package com.example.nada.broadcast;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class BrowseCategory extends AppCompatActivity {

    protected ArrayList<String> recordings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_category);

        // querying database to get all recordings in this specific category
        Firebase userRef = new Firebase("https://broadcast11.firebaseio.com/recordings/");
        Query queryRef = userRef.orderByChild("category").equalTo(getIntent().getExtras().getString("category").toString()); //looking for user with specified email address
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                Map<String, Object> recording = (Map<String, Object>) snapshot.getValue();
                //adding to list
                Recording r=new Recording(recording.get("filename").toString(), recording.get("email").toString(), recording.get("category").toString(), recording.get("description").toString());
                recordings.add("Title: "+snapshot.getKey()+"\n"+r.shortDescription()+"\n");

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        BrowseCategory.this,
                        android.R.layout.simple_list_item_1,
                        recordings);

                ListView l= (ListView)findViewById(R.id.recordingsList);
                l.setAdapter(adapter);
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
        }); //end of query

        //detecting which recording from the recording list is pressed
        // ListView on item selected listener.
        ListView l= (ListView)findViewById(R.id.recordingsList);
        l.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

//COULDNT FIGURE OUT HOW TO NAVIGATE FROM ACTIVITY TO FRAGMENT BUT CLICKING AN AUDIO PLAYS IT!
                //navigate to Listening Fragment
//                Intent i=new Intent(getApplicationContext(), ListeningFragment.class);
//                i.putExtra("fileName", recording.get("filename").toString());
//                i.putExtra("longDescription", "Title: "+snapshot.getKey()+"\n"+r.completeDescription());
//                startActivity(i);

                //////////play the stuff
                String recordingDesc=((TextView) view).getText().toString();
                String fileName=recordingDesc.substring(recordingDesc.indexOf("/"), recordingDesc.indexOf("p")+1);
                MediaPlayer player;
                player = new MediaPlayer();
                try {
                    player.setDataSource(fileName); //playing the extracted file name
                    player.prepare();
                    player.start();
                }
                catch (IOException e){
                    Log.e("AudioTest", "prepare() failed");
                }
            }
        });
    }


}
