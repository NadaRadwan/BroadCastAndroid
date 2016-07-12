package com.example.nada.broadcast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.Map;

public class RegisterPage extends AppCompatActivity {

    Firebase dbRef;
    boolean complete; //verify all fields have been entered and are within bounds
    SharedPreferences sharedPreferences; //keep track of email address of active users


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        //referencing the db
        dbRef=new Firebase("https://broadcast11.firebaseio.com/");
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
    }

    public void register(View view) {

        final EditText email = (EditText) findViewById(R.id.email); //need to be declared final in order to be used by the inner class Firebase.ValueResultHandler
        final EditText userName = (EditText) findViewById(R.id.userName);
        final EditText password = (EditText) findViewById(R.id.password);
        final EditText rePassword = (EditText) findViewById(R.id.rePassword);

        complete=true;//all fields are verified

        //check if email has been entered
        if (email.getText().toString().length() == 0) {
            complete=false;
            email.setError(getString(R.string.error_field_required));
        }

        //check if userName has been entered and if it has been used before
        if (userName.getText().toString().length() == 0) { //if nothing was entered
            complete=false;
            userName.setError(getString(R.string.error_field_required));
        }else{
            //check if userName has been previously found in the database
//            Firebase userRef = new Firebase("https://broadcast11.firebaseio.com/users/");
//            // Attach an listener to read the data at our users reference
//            userRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot snapshot) {
//                    for (DataSnapshot userSnapshot: snapshot.getChildren()) {
//                        if (userSnapshot.getKey().equals(userName.getText().toString())) {
//                            complete=false;
//                            userName.setError("User name already taken!");
//                        }
//                    }
//                }
//                @Override
//                public void onCancelled(FirebaseError firebaseError) {
//                    System.out.println("The read failed: " + firebaseError.getMessage());
//                }
//            });
//            if(!complete){
//                complete=false;}
        }

        //check if password has been entered and is of appropriate length
        if (password.getText().toString().length()<5) {
            complete = false;
            if (password.getText().toString().length() == 0) { //if nothing is entered
                password.setError(getString(R.string.error_field_required));
            }else {
                password.setError(getString(R.string.error_invalid_password));//if too short
            }
        }

        //check is rePassword has been entered
        if (rePassword.getText().toString().length() == 0) {
            complete = false;
            rePassword.setError(getString(R.string.error_field_required));
        }


        if (complete) { //all fields entered
            //check if password and re-entered password match
            if (password.getText().toString().equals(rePassword.getText().toString())) {

                   //creating entry in users table  storing userName, email and rating
                   //primary key is userName not email!
                           Firebase userRef = dbRef.child("users").child(userName.getText().toString());
                           User u=new User(email.getText().toString());
                           userRef.setValue(u);

                   //creating the user in firebase section that does authentication upon login
                dbRef.createUser(email.getText().toString(), password.getText().toString(), new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        System.out.println("Successfully created user account with user id: " + result.get("uid"));
                        Toast.makeText(getApplicationContext(), "Successfully registered user account!", Toast.LENGTH_SHORT).show();

                        //login the user
                        dbRef.authWithPassword(email.getText().toString(), password.getText().toString(), new Firebase.AuthResultHandler() {

                            @Override
                            public void onAuthenticated(AuthData authData) {
                                System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                                Log.d("loginActivity","logging in after registering");

                                //keep track of email and userName of active user
                                SharedPreferences.Editor e=sharedPreferences.edit();
                                e.putString("userEmail", email.getText().toString());
                                e.apply();


                                if(getIntent().hasExtra("Intent")){
                                    Bundle intentinfo = getIntent().getExtras();

                                    switch (intentinfo.getString("Intent")){
                                        case "record":
                                            Intent record = new Intent(RegisterPage.this, Record.class);
                                            startActivity(record);
                                            break;
                                        case "favourites":
                                            Intent favourite=new Intent(RegisterPage.this, Home.class);
                                            favourite.putExtra("fragmentNav", "favourites");
                                            startActivity(favourite);
                                            break;
                                        case "profile":
                                            Intent profile = new Intent(RegisterPage.this, UserProfile.class); //create a new intent that creates a new activity and allows us to pass parameters between the current activity and the created activity
                                            startActivity(profile); //navigates to the next page (userProfile)
                                            break;
                                    }
                                }
                            }

                            @Override
                            public void onAuthenticationError(FirebaseError firebaseError) {
                                //there will never be an authentication error since the user is registering
                            }
                        });



//                        //navigate to user profile page
//                        Intent i = new Intent(RegisterPage.this, UserProfile.class); //create a new intent that creates a new activity and allows us to pass parameters between the current activity and the created activity
//                        //sending the email used to login
//                        i.putExtra("email", email.getText().toString());
//                        startActivity(i); //navigates to the next page (user profile)
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        // there was an error
                        Toast.makeText(getApplicationContext(), "\"Email address already taken or of invalid format!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "The entered passwords do not match!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
