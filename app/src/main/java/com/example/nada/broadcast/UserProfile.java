package com.example.nada.broadcast;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.io.IOException;
import java.util.Map;

public class UserProfile extends AppCompatActivity implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {

    private MediaPlayer player;
    Firebase dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //connecting to the db
        dbRef=new Firebase("https://broadcast11.firebaseio.com/");

        //populating the user rating
        final TextView rating = (TextView) findViewById(R.id.rating);
        final TextView welcome = (TextView) findViewById(R.id.welcome);

        // Get a reference to our posts
        Firebase userRef = new Firebase("https://broadcast11.firebaseio.com/users/");
        Query queryRef = userRef.orderByChild("email").equalTo(getIntent().getExtras().getString("email")); //looking for user with specified email address
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                Map<String, Object> user = (Map<String, Object>) snapshot.getValue();
                rating.setText("Current Rating: " + user.get("rating"));
                welcome.setText("Welcome "+snapshot.getKey()+"!");
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
        });

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
        dbRef.unauth(); //calling unauth invalidates the user token and logs them out o the application
        Toast.makeText(getApplicationContext(), "Successfully logged out", Toast.LENGTH_SHORT).show(); //display small window saying "settings saved"
        Intent i = new Intent(UserProfile.this, Home.class); //create a new intent that creates a new activity and allows us to pass parameters between the current activity and the created activity
        startActivity(i); //navigates to the next page (summary)
    }

    public void changePassword(View view){
        if(dbRef.getAuth()!=null) { //you are still signed in
            //retrieving the user's email address
            Bundle bundle = getIntent().getExtras();
            String email = bundle.getString("email");

            EditText oldPassword = (EditText) findViewById(R.id.oldPassword);
            EditText newPassword = (EditText) findViewById(R.id.newPassword);

            boolean complete = true; //indicating whether we are ready to call reset password or not

            //checking that the old password has been entered
            if (oldPassword.getText().toString().length() == 0) {
                complete = false;
                oldPassword.setError(getString(R.string.error_field_required));
            }
            //checking that a new password has been entered and that it is at least 5 characters
            if (newPassword.getText().toString().length() < 5) {
                complete = false;
                if (newPassword.getText().toString().length() == 0) { //if nothing is entered
                    newPassword.setError(getString(R.string.error_field_required));
                } else {
                    newPassword.setError(getString(R.string.error_invalid_password));//if too short
                }
            }
            //calling the reset method
            if (complete) {
                dbRef.changePassword(email, oldPassword.getText().toString(), newPassword.getText().toString(), new Firebase.ResultHandler() {
                    @Override
                    public void onSuccess() {
                        // password changed
                        Toast.makeText(getApplicationContext(), "Password successfully reset!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        // error encountered
                        Toast.makeText(getApplicationContext(), "Incorrect old password!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }else{
            Toast.makeText(getApplicationContext(), "Hey, you are logged out!", Toast.LENGTH_SHORT).show();
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
