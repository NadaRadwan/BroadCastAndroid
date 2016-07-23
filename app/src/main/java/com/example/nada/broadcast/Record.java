package com.example.nada.broadcast;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.app.AlertDialog;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.io.IOException;

public class Record extends AppCompatActivity {

//    private CoordinatorLayout coordinatorLayout;

    private static MediaRecorder recorder;
    private static MediaPlayer player;

    private static String filename;
    private static ImageButton stopButton;
    private static ImageButton playButton;
    private static ImageButton recordButton;

    public String file;
    public boolean hasRecorded;


    private boolean isRecording = false;
    Firebase dbRef; //reference to the database
    SharedPreferences sharedPreferences;
    public static int numRec=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        hasRecorded = false;

        //referencing the db
        dbRef=new Firebase("https://broadcast11.firebaseio.com/");

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        stopButton = (ImageButton) findViewById(R.id.stopButton);
        playButton = (ImageButton) findViewById(R.id.playButton);
        recordButton = (ImageButton) findViewById(R.id.recordButton);

        file = "myaudio"+(numRec++)+".3gp";

        filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + file;
       // filename=android.os.Environment.getExternalStorageDirectory()+"/myaudio.3gp";

    }

    @Override
    public void onBackPressed() {
        if (hasRecorded){
            testdialog();
        }
        else{
            super.onBackPressed();
        }
    }

    public void goback(){
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (hasRecorded){
                    testdialog();
                }
                else{
                    super.onBackPressed();
                }
                return(true);
        }

        return(super.onOptionsItemSelected(item));
    }

    //check if file exists. To remove this method, it does not work to the desired purpose.
    public boolean checkfile(){
        String[] files =  Environment.getExternalStorageDirectory().list();
        Boolean result = false;

        for(int i = 0; i < files.length; i++ ){
            if (files[i].equalsIgnoreCase(file)){
                result = true;
                break;
            }
        }

        return result;
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

    //start and stop recording
    public void startRecording(View view) throws IOException{

        if (!isRecording){
            hasRecorded = true;//realistically, should this be placed inside the if?

            Toast.makeText(getApplicationContext(), "Started Recording", Toast.LENGTH_SHORT).show();
            recordButton.setImageResource(R.drawable.recordpressed);

            isRecording = true;
            stopButton.setEnabled(true);
            playButton.setEnabled(false);

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
        else {
            Toast.makeText(getApplicationContext(), "Stopped Recording", Toast.LENGTH_SHORT).show();
            stopButton.setEnabled(false);
            playButton.setEnabled(true);

            recordButton.setImageResource(R.drawable.record);

                recordButton.setEnabled(false);
                recorder.stop();
                recorder.release();
                recorder = null; //free memory space?
                isRecording = false;

            LinearLayout playcontrols = (LinearLayout) findViewById(R.id.playcontrols);
            playcontrols.setVisibility(View.VISIBLE);
        }



    }

    public void stopPlaying(View view){

        stopButton.setEnabled(false);
        playButton.setEnabled(true);

//        if (isRecording){
//            recordButton.setEnabled(false);
//            recorder.stop();
//            recorder.release();
//            recorder = null; //free memory space?
//            isRecording = false;
//        }
//        else {
            player.release();
            player = null;
            recordButton.setEnabled(true);
//        }
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
        String category = ((Spinner) findViewById(R.id.category)).getSelectedItem().toString();
        String description = ((EditText) findViewById(R.id.description)).getText().toString();

        if (recordingName.isEmpty()){
            ((EditText) findViewById(R.id.recordingName)).setError("Please specify the recording title");
        }else if(description.isEmpty()){
            ((EditText) findViewById(R.id.description)).setError("Please enter a description");
        }
        else if(!hasRecorded){
            Toast.makeText(getApplicationContext(), "Nothing Recorded. Please record before uploading.", Toast.LENGTH_LONG).show();
        }
        else{
            //creating entry in recordings table  storing recordingName, recording, userName, rating, category and description
            //primary key is recordingName!
            Firebase recordingRef = dbRef.child("recordings").child(recordingName);
            Recording r=new Recording(recordingName, filename, sharedPreferences.getString("userEmail",""), category, description); //PASS CORRECT USERNAME
            recordingRef.setValue(r);
            Toast.makeText(getApplicationContext(), "Successfully uploaded", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
    }

    //dialog to make sure user wants to overrite their recording
    public void testdialog(){
        AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(Record.this);
        //alertDialog1.setTitle("You will lose your recording i");

        alertDialog1.setMessage("You will lose your recording if you go back. Are you sure you want to go back?")
                .setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        goback();
                    }
                })
                .setNegativeButton("Stay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        alertDialog1.create();
        alertDialog1.show();
    }

//
//    public void stopPlaying(View view) {
//        player.release();
//        player = null;
//    }

}
