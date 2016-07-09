package com.example.nada.broadcast;

/**
 * Created by Nada on 2016-07-08.
 */
public class Recording {
    String filename;
    String email;
    String category;
    String description;
    int rating;

    public Recording(String fileName, String email, String category, String description) {
        this.filename=fileName;
        this.email=email;
        this.category=category;
        this.description=description;
        this.rating = 0;
    }

    public String getFilename(){return this.filename;}

    public String getEmail(){return this.email;}

    public int getRating() {
        return this.rating;
    }

    public String getCategory() {
        return this.category;
    }

    public String getDescription() {
        return this.description;
    }

    public String completeDescription() {
        return  "Email of user:" + this.email + "\n"
                + "Category:" + this.category + "\n"
                + "Description:" + this.description + "\n"
                + "Rating:" + this.rating;
    }

    public String shortDescription() {
        return  "File: "+this.filename+"\n"
                +"Rating:" + this.rating;
    }
}