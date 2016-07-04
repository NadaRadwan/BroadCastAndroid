package com.example.nada.broadcast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

public class RegisterPage extends AppCompatActivity {

    Firebase dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        //referencing the db
        dbRef=new Firebase("https://broadcast11.firebaseio.com/");
    }

    public void register(View view) {

        final EditText email = (EditText) findViewById(R.id.email); //need to be declared final in order to be used by the inner class Firebase.ValueResultHandler
        final EditText userName = (EditText) findViewById(R.id.userName);
        final EditText password = (EditText) findViewById(R.id.password);
        final EditText rePassword = (EditText) findViewById(R.id.rePassword);

        //checking that all fields have been entered
        boolean complete = true; //check if all fields are entered

        if (email.getText().toString().length() == 0) { //if nothing was entered
            complete=false;
            email.setError(getString(R.string.error_field_required));
        }

        if (userName.getText().toString().length() == 0) { //if nothing was entered
            complete=false;
            userName.setError(getString(R.string.error_field_required));
        }

        if (password.getText().toString().length()<5) {
            complete = false;
            if (password.getText().toString().length() == 0) { //if nothing is entered
                password.setError(getString(R.string.error_field_required));
            }else {
                password.setError(getString(R.string.error_invalid_password));//if too short
            }
        }

        if (rePassword.getText().toString().length() == 0) {
            complete = false;
            rePassword.setError(getString(R.string.error_field_required));
        }

        if (complete) { //all fields entered
            //check if password and re-entered password match
            if (password.getText().toString().equals(rePassword.getText().toString())) {

                   //creating entry in users table  storing userName and rating
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

                        //navigate to user profile page
                        Intent i = new Intent(RegisterPage.this, UserProfile.class); //create a new intent that creates a new activity and allows us to pass parameters between the current activity and the created activity
                        //sending the email used to login
                        i.putExtra("email", email.getText().toString());
                        startActivity(i); //navigates to the next page (user profile)
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        // there was an error
                        Toast.makeText(getApplicationContext(), "\"Email address taken or of invalid format!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "The entered passwords do not match!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
