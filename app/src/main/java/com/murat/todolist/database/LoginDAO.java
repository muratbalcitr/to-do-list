package com.murat.todolist.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.murat.todolist.model.LoginUser;

import java.util.List;

@Dao
public interface LoginDAO {

    @Insert
    Long insertUser(LoginUser loginUser);


    @Insert
    void insertUser(List<LoginUser> loginUser);

    @Query("SELECT * FROM "+ LoginDatabase.TABLE_LOGIN+" WHERE EMAIL=:email")
    LoginUser queryUser(String email);

    @Query("SELECT * FROM "+ LoginDatabase.TABLE_LOGIN)
    LoginUser queryAllUser();

    @Query("SELECT * FROM "+ LoginDatabase.TABLE_LOGIN+" WHERE EMAIL=:email and PASSWORD =:password")
    boolean queryUser(String email,String password);


}
