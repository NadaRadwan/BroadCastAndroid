package com.example.nada.broadcast;

/**
 * Created by Nada on 2016-07-03.
 */
public class User {

    String email;
    int rating;

    public User(String email){
        this.email=email;
        this.rating=0;
    }

    public String getEmail(){
        return this.email;
    }

    public int getRating(){
        return this.rating;
    }
}
