package com.example.nada.broadcast;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
//    private static final String[] DUMMY_CREDENTIALS = new String[]{
//            "admin@admin.com:admin", "admin1@admin1.com;admin1"
//    };
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

    DatabaseHelper myDB; //store instance of Broadcast database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //creating an instance of the Broadcast database
        myDB = new DatabaseHelper(this);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.userId);
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
        String userId = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(userId)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(userId)) {
            mEmailView.setError(getString(R.string.error_userId));
            focusView = mEmailView;
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
            mAuthTask = new UserLoginTask(userId, password); //create thread
            mAuthTask.execute(); //start thread
        }
    }

    private boolean isEmailValid(String userid) {
        //TODO: Replace this with your own logic
        return userid.length()>4;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
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
        Intent i = new Intent(LoginActivity.this, RegisterPage.class); //create a new intent that creates a new activity and allows us to pass parameters between the current activity and the created activity
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
            // TODO: attempt authentication against a network service.
            boolean validCred = false;
            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            User u= myDB.getUser(mEmail); //retrieving the user with the entered email

            if(u==null){ //there is no such user
                Log.d("loginActivity","there is no such user");
                return false;
            }else {
                String validPassword = u.getPassword();
                if (mPassword.equals(validPassword)) { //check if correct password was entered
                    Log.d("loginActivity","valid password");
                    return true;
                } else {
                    Log.d("loginActivity","invalid password");
                    return false;
                }
            }
        }


//
        /*
         *  invoked on the UI thread after the background computation finishes.
         *  The result of the background computation is passed to this step as a parameter (success).
         *  success indicates whether the username and password were valid
         */
        @Override
        protected void onPostExecute(final Boolean success) {
            //if we cancel mAuthTask, we will not be able to try to enter username/password multipe times
            mAuthTask = null;//we have to set it to null so that we can retry
            showProgress(false);

            if (success) {
                Intent i = new Intent(LoginActivity.this, HomePage.class); //create a new intent that creates a new activity and allows us to pass parameters between the current activity and the created activity
                i.putExtra("user", myDB.getUser(mEmailView.getText().toString()).getName());
                startActivity(i); //navigates to the next page (summary)
            } else {
                mEmailView.setError(getString(R.string.error_invalid_credentials));
                mEmailView.requestFocus(); //requests that the component gets the input focus
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

