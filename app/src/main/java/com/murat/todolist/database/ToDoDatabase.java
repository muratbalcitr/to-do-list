package com.murat.todolist.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.murat.todolist.model.ToDo;


@androidx.room.Database(entities = {ToDo.class}, version = 2, exportSchema = false)

public abstract class ToDoDatabase extends RoomDatabase {


    public static final String DATABASE_NAME = "todo_database";

    public static final String TABLE_NAME = "todo_list";
    private static ToDoDatabase instance;

    public abstract TodoDAO daoAccess();

    public static ToDoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), ToDoDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();

        }
        return instance;
    }

}
