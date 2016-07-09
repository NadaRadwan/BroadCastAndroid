package com.example.nada.broadcast;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
//import com.roughike.bottombar.BottomBar;
//import com.roughike.bottombar.OnMenuTabSelectedListener;
//import android.support.design.widget.CoordinatorLayout;

import com.firebase.client.Firebase;

import java.io.IOException;

public class Record extends AppCompatActivity {

//    private CoordinatorLayout coordinatorLayout;

    private static MediaRecorder recorder;
    private static MediaPlayer player;

    private static String filename;
    private static Button stopButton;
    private static Button playButton;
    private static ImageButton recordButton;


    private boolean isRecording = false;
    Firebase dbRef; //reference to the database
    SharedPreferences sharedPreferences;
    public static int numRec=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        //referencing the db
        dbRef=new Firebase("https://broadcast11.firebaseio.com/");

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        stopButton = (Button) findViewById(R.id.stopButton);
        playButton = (Button) findViewById(R.id.playButton);
        recordButton = (ImageButton) findViewById(R.id.recordButton);

        filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myaudio"+(numRec++)+".3gp";
       // filename=android.os.Environment.getExternalStorageDirectory()+"/myaudio.3gp";
    }

//    private void onRecord(boolean start){
//        if (start){
//            startRecording();
//        }
//        else{
//            stopRecording();
//        }
//    }
//
//    private void onPlay(boolean start){
//        if (start){
//            startPlaying();
//        }
//        else{
//            stopPlaying();
//        }
//    }

    public void startRecording(View view) throws IOException{
        isRecording = true;
        stopButton.setEnabled(true);
        playButton.setEnabled(false);
        recordButton.setEnabled(false);

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(filename);

        try{
            recorder.prepare();
            recorder.start();
        }
        catch (IOException e){
            Log.e("RecordTest", "prepare() failed");
        }

    }

    public void stopRecording(View view){

        stopButton.setEnabled(false);
        playButton.setEnabled(true);

        if (isRecording){
            recordButton.setEnabled(false);
            recorder.stop();
            recorder.release();
            recorder = null; //free memory space?
            isRecording = false;
        }
        else {
            player.release();
            player = null;
            recordButton.setEnabled(true);
        }
    }

    public void startPlaying(View view) throws IOException{
        playButton.setEnabled(false);
        recordButton.setEnabled(false);
        stopButton.setEnabled(true);

        player = new MediaPlayer();
        try {
            player.setDataSource(filename);
            player.prepare();
            player.start();
        }
        catch (IOException e){
            Log.e("AudioTest", "prepare() failed");
        }
    }


    public void uploadFile(View view){
        String recordingName= ((EditText) findViewById(R.id.recordingName)).getText().toString();
        String category = ((EditText) findViewById(R.id.category)).getText().toString();
        String description = ((EditText) findViewById(R.id.description)).getText().toString();
        //creating entry in recordings table  storing recordingName, recording, userName, rating, category and description
        //primary key is recordingName!
        Firebase recordingRef = dbRef.child("recordings").child(recordingName);
        Recording r=new Recording(filename, sharedPreferences.getString("userEmail",""), category, description); //PASS CORRECT USERNAME
        recordingRef.setValue(r);

    }

//
//    public void stopPlaying(View view) {
//        player.release();
//        player = null;
//    }

}
