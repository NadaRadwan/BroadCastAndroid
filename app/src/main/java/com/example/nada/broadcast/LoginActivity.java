package com.example.nada.broadcast;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.Map;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     * This is a thread
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    Firebase dbRef; //instance of the database
    SharedPreferences sharedPreferences; //used to keep track of the user email throughout the login session

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //creating an instance of the database
        dbRef=new Firebase("https://broadcast11.firebaseio.com/");
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        //ANOTHER WAY TO DO THIS IS BY SETTING ONCLICK ON THE BUTTON IN THE XML FILE
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(
                new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for an entered email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for an entered password.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus(); //requests that the component gets the input focus
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password); //create thread
            mAuthTask.execute(); //start thread
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void register(View view){
        //create a new intent that creates a new activity and allows us to pass parameters between the current activity and the created activity
        Intent i = new Intent(LoginActivity.this, RegisterPage.class);
        i.putExtra("Intent", getIntent().getExtras().getString("Intent"));
        startActivity(i); //navigates to the next page (RegisterPage)
    }
    /**
     * Represents an asynchronous login/registration task used to authenticate the user.
     * AsyncTask enables proper and easy use of the UI thread.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        /*
         * This step is used to perform background computation that can take a long time.
         * The parameters of the asynchronous task are passed to this step.
         */
        @Override
        protected Boolean doInBackground(Void... params) {

            dbRef.authWithPassword(mEmail, mPassword, new Firebase.AuthResultHandler() {

                @Override
                public void onAuthenticated(AuthData authData) {
                    System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                    Log.d("loginActivity","valid password");
                    showProgress(false);//stop the progress bar

                    //keep track of email and userName to keep track of active user
                    final SharedPreferences.Editor e=sharedPreferences.edit();
                    e.putString("userEmail", mEmail);
                    e.apply();

                    //This if statement will navigate the user back to the page from which they clicked that brought them to the
                    //login. If they directly clicked login, they will be navigated to profile?
                    //I am considering changing this so the check happens in the page that the user wants to navigate to
                    //rather than this page, but for now it is as this.
                    if(getIntent().hasExtra("Intent")){
                        Bundle intentinfo = getIntent().getExtras();

                        switch (intentinfo.getString("Intent")){
                            case "record":
                                Intent record = new Intent(LoginActivity.this, Record.class);
                                startActivity(record);
                                break;
                            case "favourites":
                                Intent favourite=new Intent(LoginActivity.this, Home.class);
                                favourite.putExtra("fragmentNav", "favourites");
                                startActivity(favourite);
                                break;
                            case "profile":
                                Intent profile = new Intent(LoginActivity.this, UserProfile.class); //create a new intent that creates a new activity and allows us to pass parameters between the current activity and the created activity
                                startActivity(profile); //navigates to the next page (userProfile)
                                break;
//                            case "listen":
//                                Intent listen = new Intent(LoginActivity.this, Home.class); //create a new intent that creates a new activity and allows us to pass parameters between the current activity and the created activity
//                                listen.putExtra("fragmentNav", "listen");
//                                startActivity(listen);
//                                break;
                        }
                    }
                    else{
                        Intent i= new Intent(LoginActivity.this, Home.class); //create a new intent that creates a new activity and allows us to pass parameters between the current activity and the created activity
                        startActivity(i);

                    }

                }
                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    // there was an error
                    Log.d("loginActivity","invalid password");
                    //if we cancel mAuthTask, we will not be able to try to enter username/password multipe times
                    mAuthTask = null;//we have to set it to null so that we can retry
                    showProgress(false); //stop the progress bar

                    mEmailView.setError(getString(R.string.error_invalid_credentials));
                    mEmailView.requestFocus(); //requests that the component gets the input focus
                }
            });
            return true;
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

    }
}

