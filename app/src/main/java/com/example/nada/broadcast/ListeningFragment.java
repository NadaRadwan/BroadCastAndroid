package com.example.nada.broadcast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link ListeningFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link ListeningFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class ListeningFragment extends Fragment implements View.OnClickListener{

    Firebase dbRef; //reference to the database
    SharedPreferences sharedpreferences;


    public ListeningFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment ListeningFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static ListeningFragment newInstance(String param1, String param2) {
//        ListeningFragment fragment = new ListeningFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbRef=new Firebase("https://broadcast11.firebaseio.com/");
        sharedpreferences= PreferenceManager.getDefaultSharedPreferences(this.getContext());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listening, container, false);


        final TextView description = (TextView) view.findViewById(R.id.recordingDesc);
        try{ //null pointer exception throw if description is not passed which is the case when nothing is currently playing
            String recTitle=getArguments().getString("recTitle");

            //fetching the recording with the specific name from the database
            final Firebase userRef = new Firebase("https://broadcast11.firebaseio.com/recordings/");
            final Query queryRef = userRef.orderByChild("title").equalTo(recTitle); //looking for recording with specified title
            queryRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                    // Inflate the layout for this fragment
                    View view = inflater.inflate(R.layout.fragment_listening, container, false);//always inflate so as to allow access to xml file elements
                    Map<String, Object> recording = (Map<String, Object>) snapshot.getValue();
                    Recording r=new Recording(recording.get("title").toString(), recording.get("filename").toString(), recording.get("email").toString(), recording.get("category").toString(), recording.get("description").toString());
                    description.setText(r.displayOnForm());
                    final TextView rrating = (TextView) getView().findViewById(R.id.RecordingRating);
                    rrating.setText("rating: "+recording.get("rating").toString());
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {System.out.println("The read failed: " + firebaseError.getMessage());}

                @Override
                public void onChildRemoved(DataSnapshot snapshot) {}

                @Override
                public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
                }

                @Override
                public void onChildMoved(DataSnapshot snapshot, String previousChildKey){}
            }); //end of query

        }catch (Exception e){

            setNavBar();
            ImageButton homebutton = (ImageButton) getActivity().findViewById(R.id.buttonhome);
            homebutton.setImageResource(R.drawable.ic_home_blue);

            Toast.makeText(getContext(), "Nothing currently playing!", Toast.LENGTH_SHORT).show();
            FragmentTransaction tran=getActivity().getSupportFragmentManager().beginTransaction();
            tran.replace(R.id.fragcontent, ((Home)getActivity()).browse );
            tran.addToBackStack(null);
            tran.commit();
        }



        //set onclick listeners
        ImageButton playAudioButton = (ImageButton) view.findViewById(R.id.playAudioButton);
        playAudioButton.setOnClickListener(this);

        ImageButton pauseAudioButton = (ImageButton) view.findViewById(R.id.pauseAudioButton);
        pauseAudioButton.setOnClickListener(this);

        ImageButton addFavButton = (ImageButton) view.findViewById(R.id.addFavButton);
        addFavButton.setOnClickListener(this);

        //set image of favourites icon depending on whether if it is user's favourites or not
        if(inFavourites()) {
            addFavButton.setImageResource(R.drawable.ic_removefavourites);
        }
        else{
            addFavButton.setImageResource(R.drawable.ic_addfavourites);
        }

        RatingBar rr = (RatingBar) view.findViewById(R.id.ratingBar);
        rr.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rate,
                                        boolean fromUser) {
                final float rating=rate;
                System.out.println(String.valueOf(rating));

                String fileDescription=getArguments().getString("description");
                final String recName=fileDescription.substring(7,fileDescription.indexOf(";"));

                // querying database to get recording with the specific name
                final Firebase userRef = new Firebase("https://broadcast11.firebaseio.com/recordings/");
                final Query queryRef = userRef.orderByChild("title").equalTo(recName); //looking for recording with specified title
                queryRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                        Map<String, Object> recording = (Map<String, Object>) snapshot.getValue();
                        //updating the rating
                        Firebase updateRef = new Firebase("https://broadcast11.firebaseio.com/recordings/"+recording.get("title"));

                        float oldNumRaters=Integer.parseInt(recording.get("numRaters").toString());
                        recording.put("numRaters",Long.toString((Long.parseLong(recording.get("numRaters").toString())+1)));
                        float newNumRaters=Float.parseFloat(recording.get("numRaters").toString());
                        float oldRating=Float.parseFloat(recording.get("rating").toString());
                        float newRating= (float)rating;
                        float updatedRating= oldRating*(oldNumRaters/newNumRaters)+newRating*(1/newNumRaters);
                        double toInsert=0.5*Math.round(updatedRating/0.5);
                        System.out.println(updatedRating);
                        System.out.println(toInsert);
                        recording.put("rating", Double.toString(toInsert));
                        updateRef.updateChildren(recording);

                        //update description
                    }
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {System.out.println("The read failed: " + firebaseError.getMessage());}

                    // Get the data on a post that has been removed
                    @Override
                    public void onChildRemoved(DataSnapshot snapshot) {}

                    // Get the data on a post that has changed
                    @Override
                    public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
                        System.out.println("inside onChildChanged which is inside ListeningFragment");
                        //////////////////////////////////////////////////////////////////////////////////
                        String recTitle=getArguments().getString("recTitle");
//
                        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        //fetching the recording with the specific name from the database
                        final Firebase userRef = new Firebase("https://broadcast11.firebaseio.com/recordings/");
                        final Query queryRef = userRef.orderByChild("title").equalTo(recTitle); //looking for recording with specified title
                        queryRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot snapshot, String previousChild) {

                                View view = inflater.inflate(R.layout.fragment_listening, container, false);
                                TextView rrating = (TextView) view.findViewById(R.id.RecordingRating);

                                Map<String, Object> recording = (Map<String, Object>) snapshot.getValue();
                                Recording r=new Recording(recording.get("title").toString(), recording.get("filename").toString(), recording.get("email").toString(), recording.get("category").toString(), recording.get("description").toString());
                                rrating.setText("rating: "+recording.get("rating").toString());
                            }
                            @Override
                            public void onCancelled(FirebaseError firebaseError) {System.out.println("The read failed: " + firebaseError.getMessage());}

                            // Get the data on a post that has been removed
                            @Override
                            public void onChildRemoved(DataSnapshot snapshot) {}

                            // Get the data on a post that has changed
                            @Override
                            public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
                            }

                            @Override
                            public void onChildMoved(DataSnapshot snapshot, String previousChildKey){}
                        }); //end of query
                        /////////////////////////////////////////////////////////////////////////////////

                    }

                    @Override
                    public void onChildMoved(DataSnapshot snapshot, String previousChildKey){}
                }); //end of query


            }
        });

        return view;
    }


    //deal with all click events within fragment
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.playAudioButton:
                playAudio(v);
                break;
            case R.id.pauseAudioButton:
                pauseAudio();
                break;
            case R.id.addFavButton:
                addRemoveFavourites(v);
                break;
        }
    }

    //play audio
    public void playAudio(View view){

        final MediaPlayer player;
        player = new MediaPlayer();

//        FragmentManager.BackStackEntry backEntry = getFragmentManager().getBackStackEntryAt(getActivity().getFragmentManager().getBackStackEntryCount()-1);
//        String str=backEntry.getName().toLowerCase();
        //Fragment fragment=getFragmentManager().findFragmentByTag(str);
        String fileDescription=getArguments().getString("description");
        String fileName=fileDescription.substring(fileDescription.indexOf("/"));
        if(fileName != null) {
            try {
                player.setDataSource(fileName);//playing the extracted file name

//                SeekBar playingbar = (SeekBar) getActivity().findViewById(R.id.playingbar);
//                playingbar.setMax(player.getDuration());

                player.prepare();
                player.start();

//                final Handler mHandler = new Handler();
////Make sure you update Seekbar on UI thread
//                getActivity().runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        if(player != null){
//                            int mCurrentPosition = player.getCurrentPosition() / 1000;
//                            playingbar.setProgress(mCurrentPosition);
//                        }
//                        mHandler.postDelayed(this, 1000);
//                    }
//                });
            } catch (IOException e) {
                Log.e("AudioTest", "prepare() failed");
            }
        }



    }

    //pauses current audio file
    public void pauseAudio(){
        //to implement
    }

    //adds or removes current file to or from user's favourites
    public void addRemoveFavourites(View v){
        String s=getArguments().getString("description");

        if (inFavourites()){
            //implement remove from favourites
        }
        else{

            if(sharedpreferences.getString("userEmail","").equals("")){
                Toast.makeText(getContext(), "You should log in", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(getActivity(), LoginActivity.class);
                //i.putExtra("Intent", "listen");
                startActivity(i);
            }else {
                Toast.makeText(getContext(), "Successfully added to favourites", Toast.LENGTH_SHORT).show();

                //adding recording to favourites
                final Firebase userRef = new Firebase("https://broadcast11.firebaseio.com/users/");
                final Query queryRef = userRef.orderByChild("email").equalTo(sharedpreferences.getString("userEmail", "")); //looking for user with specified email address
                queryRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                        String name = snapshot.getKey();
                        final Firebase favRef = new Firebase("https://broadcast11.firebaseio.com/favourites/");
                        Map<String, Object> user = (Map<String, Object>) snapshot.getValue();

                        //getting title of recording
                        String fileDescription = getArguments().getString("description");
                        String recName = fileDescription.substring(7, fileDescription.indexOf(";"));

                        //adding fto favourites
                        Map<String, String> post1 = new HashMap<String, String>();
                        post1.put("email", sharedpreferences.getString("userEmail", ""));
                        post1.put("favourite", recName);
                        favRef.push().setValue(post1);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }

                    // Get the data on a post that has been removed
                    @Override
                    public void onChildRemoved(DataSnapshot snapshot) {
                    }

                    // Get the data on a post that has changed
                    @Override
                    public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {}

                    @Override
                    public void onChildMoved(DataSnapshot snapshot, String previousChildKey) {
                    }
                });
            }
        }
    }

    //checks if current audio file is already in user's favourites
    public boolean inFavourites(){
        return false;
        //getting title of recording
//        String fileDescription=getArguments().getString("description");
//        final String recName=fileDescription.substring(7,fileDescription.indexOf(";"));
//
//
//        //check if userName has been previously found in the database
//            Firebase favRef = new Firebase("https://broadcast11.firebaseio.com/favourites/");
//            // Attach an listener to read the data at our users reference
//            favRef.addValueEventListener(new ValueEventListener() {
//
//                @Override
//                public void onDataChange(DataSnapshot snapshot) {
//                    for (DataSnapshot userSnapshot: snapshot.getChildren()) {
//                        Map<String, Object> favourite = (Map<String, Object>) snapshot.getValue();
//                        if (favourite.get("favourite").equals(recName) && favourite.get("email").equals(sharedpreferences.getString("userEmail",""))){
//                            return false;
//                        }
//                    }
//                }
//                @Override
//                public void onCancelled(FirebaseError firebaseError) {
//                    System.out.println("The read failed: " + firebaseError.getMessage());
//                }
//            });
//
//
//
//        return false;
    }

    public void setNavBar(){
        ImageButton homebutton = (ImageButton) getActivity().findViewById(R.id.buttonhome);
        homebutton.setImageResource(R.drawable.ic_home);

        ImageButton nowplayingbutton = (ImageButton) getActivity().findViewById(R.id.buttonnp);
        nowplayingbutton.setImageResource(R.drawable.ic_nowplaying);

        ImageButton favouritebutton = (ImageButton) getActivity().findViewById(R.id.buttonfav);
        favouritebutton.setImageResource(R.drawable.ic_favourites);

        ImageButton profilebutton = (ImageButton) getActivity().findViewById(R.id.buttonprofile);
        profilebutton.setImageResource(R.drawable.ic_profile);
    }



    //    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p/>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
