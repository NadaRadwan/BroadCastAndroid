package com.example.nada.broadcast;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

public class HomePage extends AppCompatActivity implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {

    private MediaPlayer player;
    DatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //creating an instance of the Broadcast database
        myDB = new DatabaseHelper(this);
           /*
         * extracting the retrieved values (user) and getting his name converting it to string
         */
        Bundle passedParams = getIntent().getExtras(); //bundle to retrieve the values passed from the first activity
        String name= (passedParams.getString("user").toString());
        Toast.makeText(getApplicationContext(), "Welcome "+name+"!", Toast.LENGTH_SHORT).show();
    }

    /*
  *We are inflating the menu_home xml file in java. "Inflating" a view means taking the layout XML and parsing it to create
  * the view and view group objects from the elements and their attributes specified 
  */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    /*
   *If an action from the menu is selected, the onOptionsItemSelected() method in the corresponding activity is called.
   * It receives the selected action as parameter
     *Based on this information, you can decide what to do.
   */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item); //the default implementation of the method in the super class returns false
        }
    }

    public void logout(){
        Toast.makeText(getApplicationContext(), "Successfully logged out", Toast.LENGTH_SHORT).show(); //display small window saying "settings saved"
        Intent i = new Intent(HomePage.this, LoginActivity.class); //create a new intent that creates a new activity and allows us to pass parameters between the current activity and the created activity
        startActivity(i); //navigates to the next page (summary)
    }

    public void playOttawa(View view){
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource("http://www.surfmusic.de/radio-station/cbc-1-halifax,110.html");
            player.setOnErrorListener(this);
            player.setOnPreparedListener(this);
            player.prepareAsync();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.release();
        player = null;
    }
    @Override
    public void onPrepared(MediaPlayer play) {
        play.start();
    }
    @Override
    public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
        return false;
    }

}
