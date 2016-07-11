package com.example.nada.broadcast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.io.IOException;
import java.util.Map;


///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link BrowseFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link BrowseFragment#newInstance} factory method to
// * create an instance of this fragment.
// */

public class BrowseFragment extends Fragment implements View.OnClickListener{
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


    public BrowseFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment BrowseFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static BrowseFragment newInstance(String param1, String param2) {
//        BrowseFragment fragment = new BrowseFragment();
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
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_browse, container, false);


        Button music = (Button) view.findViewById(R.id.music);
        music.setOnClickListener(this);

        Button news = (Button) view.findViewById(R.id.news);
        news.setOnClickListener(this);

        Button comedy = (Button) view.findViewById(R.id.comedy);
        comedy.setOnClickListener(this);

        Button podcast = (Button) view.findViewById(R.id.podcast);
        podcast.setOnClickListener(this);

        Button blog = (Button) view.findViewById(R.id.blog);
        blog.setOnClickListener(this);

        return view;
    }

    public void onClick(View v){
        String categ=""; //will definetly be overwritten upon selcting the category
        switch (v.getId()){
            case R.id.music:
                categ="Music";
                break;

            case R.id.news:
                categ="News";
                break;

            case R.id.comedy:
                categ="Comedy";
                break;

            case R.id.podcast:
                categ="Podcast";
                break;

            case R.id.blog:
                categ="Blog";
                break;
        }

        //changing the fragment rather than starting a new activity:
        BrowseCategoryFragment browsecategory = new BrowseCategoryFragment();
        Bundle info = new Bundle();
        info.putString("category", categ);
        browsecategory.setArguments(info);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragcontent, browsecategory);
        transaction.addToBackStack(null);
        transaction.commit();

        //navigate to Browse Category
//        Intent i=new Intent(getActivity(), BrowseCategory.class);
//        i.putExtra("category",categ);
//        startActivity(i);
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
