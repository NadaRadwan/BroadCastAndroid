package com.example.nada.broadcast;

import com.firebase.client.Firebase;

/**
 * Created by Nada on 2016-07-02.
 */
public class Broadcast extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
