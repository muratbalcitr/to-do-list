package com.murat.todolist.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.murat.todolist.model.LoginUser;

 @Database(entities = {LoginUser.class}, version = 2, exportSchema = false)

public abstract class LoginDatabase  extends RoomDatabase {


    public static final String DATABASE_NAME = "login_database";

     public static final String TABLE_LOGIN = "login";
    private static LoginDatabase instance=null;
    Context context;
    public abstract LoginDAO loginDAO();


    public static LoginDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (LoginDatabase.class) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            LoginDatabase.class, DATABASE_NAME)
                             .allowMainThreadQueries()
                            .build();

            }
        }
        return instance;
    }
     public static void destroyInstance()
     {
         instance = null;
     }

}