package com.example.nada.broadcast;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    protected ArrayList<String> recordings = new ArrayList<>();


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
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse_category, container, false);


        // querying database to get all recordings in this specific category
        Firebase userRef = new Firebase("https://broadcast11.firebaseio.com/recordings/");
        Query queryRef = userRef.orderByChild("category").equalTo(getArguments().getString("category").toString()); //looking for user with specified email address
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                Map<String, Object> recording = (Map<String, Object>) snapshot.getValue();
                //adding to list
                Recording r=new Recording(recording.get("filename").toString(), recording.get("email").toString(), recording.get("category").toString(), recording.get("description").toString());
                recordings.add("Title: "+snapshot.getKey()+"\n"+r.shortDescription()+"\n");

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_list_item_1,
                        recordings);

                ListView l = (ListView) getActivity().findViewById(R.id.recordingsList);
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
        ListView l= (ListView) view.findViewById(R.id.recordingsList);
        l.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                ((Home) getActivity()).listening = new ListeningFragment();
//                ListeningFragment listening = new ListeningFragment();
                Bundle info = new Bundle();
                String recordingDesc=((TextView) view).getText().toString();
                info.putString("filename", recordingDesc.substring(recordingDesc.indexOf("/"), recordingDesc.indexOf("p")+1));
                ((Home) getActivity()).listening.setArguments(info);
//                listening.setArguments(info);

                transaction.replace(R.id.fragcontent, ((Home) getActivity()).listening);
//                transaction.replace(R.id.fragcontent, listening);
                transaction.addToBackStack(null);
                transaction.commit();


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
}