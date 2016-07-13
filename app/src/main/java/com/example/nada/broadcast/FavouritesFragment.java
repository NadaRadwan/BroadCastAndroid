package com.example.nada.broadcast;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.Map;

public class FavouritesFragment extends Fragment {

    SharedPreferences sharedPreferences;
    protected ArrayList<String> recordings = new ArrayList<>();
    protected ArrayList<Recording> recordingsLongDesc = new ArrayList<>();
    protected ArrayAdapter<String> adapter; //used to populate the list view
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

    public FavouritesFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment FavouritesFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static FavouritesFragment newInstance(String param1, String param2) {
//        FavouritesFragment fragment = new FavouritesFragment();
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
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this.getContext());
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        recordings.clear(); //to clear the list everytime onCreateView is called (to avoid duplicate entries)

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        if (savedInstanceState != null) {
            return getView() ;
        }

        // querying database to get all recordings in this specific category
        Firebase favRef = new Firebase("https://broadcast11.firebaseio.com/favourites/");
        Query queryRef = favRef.orderByChild("email").equalTo(sharedPreferences.getString("userEmail","")); //looking for user with specified email address
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                Map<String, Object> favourite = (Map<String, Object>) snapshot.getValue();
                //adding to list
                String recTitle = favourite.get("favourite").toString();
                recordings.add(recTitle);
                adapter = new ArrayAdapter<>(
                        getActivity(),
                        android.R.layout.simple_list_item_1,
                        recordings);

                ListView l = (ListView) view.findViewById(R.id.favouritesList);
                l.setAdapter(adapter);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {System.out.println("The read failed: " + firebaseError.getMessage());}

            // Get the data on a post that has been removed
            @Override
            public void onChildRemoved(DataSnapshot snapshot) {}

            // Get the data on a post that has changed
            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {}

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildKey){}
        }); //end of query
        //detecting which recording from the recording list is pressed
        // ListView on item selected listener.

        ListView li= (ListView) view.findViewById(R.id.favouritesList);
        li.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String recordingName=((TextView) view).getText().toString();
                System.out.println("recodingName is "+recordingName);

            //we need the recFullDescription
                // querying database to get all recordings in this specific category
                Firebase userRef = new Firebase("https://broadcast11.firebaseio.com/recordings/");
                Query queryRef = userRef.orderByChild("title").equalTo(recordingName); //looking for user with specified email address
                queryRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                        Map<String, Object> recording = (Map<String, Object>) snapshot.getValue();
                        //adding to list
                        Recording r=new Recording(recording.get("title").toString(), recording.get("filename").toString(), recording.get("email").toString(), recording.get("category").toString(), recording.get("description").toString());

                        String recFullDescription=r.longDescription();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                        ((Home) getActivity()).listening = new ListeningFragment();
//                ListeningFragment listening = new ListeningFragment();
                        Bundle info = new Bundle();
//                info.putString("description", recordingDesc.substring(recordingDesc.indexOf("/"), recordingDesc.indexOf("p")+1));
                        info.putString("description", recFullDescription);
                        ((Home) getActivity()).listening.setArguments(info);
//                listening.setArguments(info);

                        transaction.replace(R.id.fragcontent, ((Home) getActivity()).listening);
//                transaction.replace(R.id.fragcontent, listening);
                        transaction.addToBackStack(null);
                        transaction.commit();

                    }
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {System.out.println("The read failed: " + firebaseError.getMessage());}

                    // Get the data on a post that has been removed
                    @Override
                    public void onChildRemoved(DataSnapshot snapshot) {}

                    // Get the data on a post that has changed
                    @Override
                    public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {}

                    @Override
                    public void onChildMoved(DataSnapshot snapshot, String previousChildKey){}
                }); //end of query

            }
        });


        return view;
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
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
