package com.example.nada.broadcast;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class Home extends FragmentActivity {

    BrowseFragment browse = new BrowseFragment();
    ListeningFragment listening = new ListeningFragment();
    FavouritesFragment favourites = new FavouritesFragment();
    ProfileFragment profile = new ProfileFragment();
    boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        Toolbar titlebar = (Toolbar) findViewById(getResources().getIdentifier("action_bar_title", "id", "android"));
//        setSupportActionBar(titlebar);


        if (findViewById(R.id.fragcontent) != null){

            if (savedInstanceState != null) {
                return;
            }

            browse.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction().add(R.id.fragcontent, browse).commit();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.profile:
//                break;
//        }
//        return true;
//    }

    //navigates to the browse page (fragment)
    public void toBrowsePage(View view){

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragcontent, browse);
        transaction.addToBackStack(null); //this might be unnecessarily accumulating info and might slow down app. When does it pop? There is endless backs available
        transaction.commit();
    }

    //navigates to the listening page (fragment for now, will make into activity)
    public void toListeningPage(View view){

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragcontent, listening);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //navigates to the favourites page (activity)
    public void toRecordActivity (View view){



        //You'll have to get the value whether the user is logged in or not and assign it to this variable.
        //For now I initialized it so the code works.
        isLoggedIn = false;

        if (!isLoggedIn){
            Intent login = new Intent(Home.this, LoginActivity.class);

            //pass the string name of the page that the user wants to navigate to, to the login page so the login page can
            //redirect the user there after successfuly logging in. (The login page uses a switch statement)
            //to navigate to the correct page
            login.putExtra("Intent", "record");
            startActivity(login);
        }
        else{
            Intent record = new Intent(Home.this, Record.class);
            startActivity(record);
        }

    }

    //navigates to the favourites page (fragment)
    public void toFavouritesPage(View view){

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragcontent, favourites);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //navigates to the profile page (fragment)
    public void toProfilePage(View view){

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragcontent, profile);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
