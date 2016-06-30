package com.example.nada.broadcast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Nada on 2016-06-21.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    //database name
    private static final String DATABASE_NAME="database";
    //table names
    private static final String USERS_TABLE="users";
//    static final String colUserID="userID";
//    static final String colName="userName";
//    static final String colPassword="userPassword";
    private  static final String UPLOADS_TABLE="uploads";
//    static final String colUploadsID="uploadID"; //primary key
//    static final String colFile="uploadFileName";
//    static final String colCategory="uploadCategory";
//    static final String colUser="uploadUserID";


    //constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null,DATABASE_VERSION);
    }

    //This method is invoked when the database does not exist on the disk,
    //it’s executed only once on the same device the first time the application is run on the device.
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+USERS_TABLE+
                "(UserId TEXT(100)," +
                " UserName TEXT(100)," +
                " UserPassword TEXT(100));");
        Log.d("CREATE TABLE","Create Table Successfully");
    }

    //we want to upgrade the database by changing the schema, add new tables or change column data types.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+USERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+UPLOADS_TABLE);
        onCreate(db);
    }

    //inserting row in table
    public long addUser(User u) {
        if(this.getUser(u.getId()) !=null){ //a user with this userId already exists
            return -2;
        }
        try{
            SQLiteDatabase db;
            db=this.getWritableDatabase();//Write Data
            ContentValues cv = new ContentValues();
            cv.put("UserId", u.getId());
            cv.put("UserName", u.getName());
            cv.put("UserPassword", u.getPassword());
            long rowIndex=db.insert(USERS_TABLE, null, cv); //second attr provides the name of nullable column name to explicitly insert a NULL into in the case where your values is empty.
            db.close();
            Log.d("ADD USER","Added user successfully");
            return rowIndex;
        }catch(Exception e){
            return -1;
        }

    }

    //finding a user by user id
    public User getUser(String id){
        //rawQuery is used for querying the db using sql
        //Cursor provides random read-write access to the result set returned by a database query.
        try{
            SQLiteDatabase db = this.getReadableDatabase();//read data
            Cursor res =  db.rawQuery( "SELECT * FROM users WHERE userID LIKE '%"+id+"%'", null );//third param is a signal to cancel the operation in progress, or null if none
            if(!res.moveToFirst()){ //user not found
                return null;
            }
            res.moveToFirst(); //move to first row
            //return a user object with values obtained from the record in the table
            Log.d("Retrieve User","Retrieved user successfully");
            return new User(res.getString(res.getColumnIndex("UserId")),
                    res.getString(res.getColumnIndex("UserName")),
                    res.getString(res.getColumnIndex("UserPassword")));
        }catch (Exception e){
            return null;
        }
    }

}

