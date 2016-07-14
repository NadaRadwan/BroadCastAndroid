package com.example.nada.broadcast;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;


///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link BrowseCategoryFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link BrowseCategoryFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class BrowseCategoryFragment extends Fragment{

    protected ArrayList<Recording> recordings = new ArrayList<>();
    protected ArrayList<Recording> recordingsLongDesc = new ArrayList<>();
    protected RecordingAdapter adapter; //used to populate the list view

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    private OnFragmentInteractionListener mListener;

    public BrowseCategoryFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment BrowseCategoryFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static BrowseCategoryFragment newInstance(String param1, String param2) {
//        BrowseCategoryFragment fragment = new BrowseCategoryFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //NOTE: THE QUERY MUST BE IN ONCREATE BECAUSE IF IT IS IN ONCREATEVIEW, IT IS EXECUTED EVERYTIME WE NAVUAGTE TO THE FRAGMENT WHICH DUPLICATED THE ENTRIES IN THE LIST

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        recordings.clear();//to clear the list eveytime onCreateView is called (to avoid duplicate entries)

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_browse_category, container, false);

        // querying database to get all recordings in this specific category
        if (getActivity() == null) {
            ////to prevent calling the database when the fragment is detached from the Home activity which crashes the activity
        }else {

            Firebase userRef = new Firebase("https://broadcast11.firebaseio.com/recordings/");
            Query queryRef = userRef.orderByChild("category").equalTo(getArguments().getString("category").toString()); //looking for user with specified email address
            queryRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                    Map<String, Object> recording = (Map<String, Object>) snapshot.getValue();
                    //adding to list
                    Recording r = new Recording(recording.get("title").toString(), recording.get("filename").toString(), recording.get("email").toString(), recording.get("category").toString(), recording.get("description").toString());
                    recordings.add(r);
                    recordingsLongDesc.add(r);

                    // ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    adapter = new RecordingAdapter(
                            getActivity(),
                            R.layout.listitem,
                            recordings);

                    ListView l = (ListView) view.findViewById(R.id.recordingsList);
                    l.setAdapter(adapter);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }

                // Get the data on a post that has been removed
                @Override
                public void onChildRemoved(DataSnapshot snapshot) {
                }

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                // Get the data on a post that has changed
                @Override
                public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
                }

                @Override
                public void onChildMoved(DataSnapshot snapshot, String previousChildKey) {
                }
            }); //end of query
            //detecting which recording from the recording list is pressed
            // ListView on item selected listener.

            ListView l = (ListView) view.findViewById(R.id.recordingsList);

            l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    String recFullDescription = "";
                    String recordingDesc = ((TextView)((LinearLayout) view).getChildAt(0)).getText().toString();
                    System.out.println("recodingDesc is " + recordingDesc);
                    String title = recordingDesc;
                   // String title = recordingDesc.substring(7, (recordingDesc.length()-1));
                    System.out.println("fileName is " + title);
                    for (int i = 0; i < recordingsLongDesc.size(); i++) {
                        if (recordingsLongDesc.get(i).getTitle().equals(title)) {
                            recFullDescription = recordingsLongDesc.get(i).longDescription();
                        }
                    }

                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                    setNavBar();
                    ImageButton nowplayingbutton = (ImageButton) getActivity().findViewById(R.id.buttonnp);
                    nowplayingbutton.setImageResource(R.drawable.ic_nowplaying_blue);

                    ((Home) getActivity()).listening = new ListeningFragment();
                    //                ListeningFragment listening = new ListeningFragment();
                    Bundle info = new Bundle();
                    //                info.putString("description", recordingDesc.substring(recordingDesc.indexOf("/"), recordingDesc.indexOf("p")+1));
                    info.putString("description", recFullDescription);
                    info.putString("recTitle", title);
                    ((Home) getActivity()).listening.setArguments(info);
                    //                listening.setArguments(info);

                    transaction.replace(R.id.fragcontent, ((Home) getActivity()).listening);
                    //                transaction.replace(R.id.fragcontent, listening);
                    transaction.addToBackStack(null);
                    transaction.commit();


                }
            });
        }
        return view;

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
}
