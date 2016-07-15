package com.example.nada.broadcast;

/**
 * Created by Nada on 2016-07-08.
 */
public class Recording {
    String title;
    String filename;
    String email;
    String category;
    String description;
    int rating;
    int numRaters;

    public Recording(String title, String fileName, String email, String category, String description) {
        this.title=title;
        this.filename=fileName;
        this.email=email;
        this.category=category;
        this.description=description;
        this.rating = 0;
        this.numRaters=0;
    }

    public String getTitle(){return this.title;}

    public String getFilename(){return this.filename;}

    public String getEmail(){return this.email;}

    public int getRating() {
        return this.rating;
    }

    public int getNumRaters(){return this.numRaters;}

    public String getCategory() {
        return this.category;
    }

    public String getDescription() {
        return this.description;
    }

    public String longDescription() {
        //i took out the rating
        return  "Title: "+this.title + ";\n"
                +"Email of user: " + this.email + ";\n"
                + "Category: " + this.category + ";\n"
                + "Description: " + this.description + ";\n"
                + "File: "+this.filename;
    }

    public String shortDescription() {
        //i took out the rating
        return  "Title: "+this.title + "\n";
    }

    public String displayOnForm(){
        return  "Title: "+this.title + "\n"
                + "UserName:" +"\n"
                + "Category: " + this.category + "\n"
                + "Description: " + this.description + "\n";
    }
}