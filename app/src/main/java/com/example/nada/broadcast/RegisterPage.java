package com.example.nada.broadcast;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterPage extends AppCompatActivity {

    DatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        //creating an instance of the Broadcast database
        myDB = new DatabaseHelper(this);
    }

    public void register(View view) {

        EditText userId = (EditText) findViewById(R.id.userId);
        EditText userName = (EditText) findViewById(R.id.userName);
        EditText password = (EditText) findViewById(R.id.password);
        EditText rePassword = (EditText) findViewById(R.id.rePassword);

        //checking that all fields have been entered
        boolean complete = true; //check if all fields are entered

        if (userId.getText().toString().length()<5) {
            complete = false;
            if (userId.getText().toString().length() == 0) { //if nothing was entered
                userId.setError(getString(R.string.error_field_required));
            }else {
                userId.setError(getString(R.string.error_userId)); //if to short
            }
        }

        if (userName.getText().toString().length() == 0) {
            complete = false;
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

                User u = new User(userId.getText().toString(), userName.getText().toString(), password.getText().toString());
                if (myDB.addUser(u) == -2) { //a user with this userId exists
                    Toast.makeText(getApplicationContext(), "A user with this user ID already exists!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Successfully registered!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(RegisterPage.this, HomePage.class); //create a new intent that creates a new activity and allows us to pass parameters between the current activity and the created activity
                    i.putExtra("user", userName.getText().toString());
                    // i.putExtra("user", userId.getText().toString());
                    startActivity(i); //navigates to the next page (summary)
                }
            } else {
                Toast.makeText(getApplicationContext(), "The entered passwords do not match!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
