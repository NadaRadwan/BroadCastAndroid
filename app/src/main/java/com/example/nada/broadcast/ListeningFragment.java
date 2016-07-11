package com.example.nada.broadcast;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.io.IOException;


///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link ListeningFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link ListeningFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class ListeningFragment extends Fragment implements View.OnClickListener{
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listening, container, false);

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

        return view;
    }


    //deal with all click events within fragment
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.playAudioButton:
                playAudio();
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
    public void playAudio(){
        MediaPlayer player;
        player = new MediaPlayer();

//        FragmentManager.BackStackEntry backEntry = getFragmentManager().getBackStackEntryAt(getActivity().getFragmentManager().getBackStackEntryCount()-1);
//        String str=backEntry.getName().toLowerCase();
        //Fragment fragment=getFragmentManager().findFragmentByTag(str);

        if(getArguments().getString("filename") != null) {
            try {
                player.setDataSource(getArguments().getString("filename").toString()); //playing the extracted file name
                player.prepare();
                player.start();
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
        if (inFavourites()){
            //implement remove from favourites
        }
        else{
            //implement add to favourites
        }
    }

    //checks if current audio file is already in user's favourites
    public boolean inFavourites(){
        //to implement
        return false;
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
