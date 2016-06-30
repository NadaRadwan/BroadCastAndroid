package com.example.nada.broadcast;

/**
 * Created by Nada on 2016-06-22.
 */
public class User {
    private String id;
    private String name;
    private String password;

    public User (String id, String name, String password){
        this.id=id;
        this.name=name;
        this.password=password;
    }

    public String getName(){
        return this.name;
    }

    public String getId(){
        return this.id;
    }

    public String getPassword(){
        return this.password;
    }
}
