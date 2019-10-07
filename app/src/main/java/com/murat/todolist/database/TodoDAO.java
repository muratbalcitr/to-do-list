package com.murat.todolist.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.murat.todolist.model.ToDo;

import java.util.List;

@Dao
public interface TodoDAO {

    @Insert
    Long insertTodo(ToDo todo);

    @Insert
    void insertTodo(List<ToDo> todo);


    @Query("SELECT * FROM " + ToDoDatabase.TABLE_NAME + " WHERE COMPLETE=0")
    List<ToDo> fetchAllTodos();

    @Query("SELECT * FROM " + ToDoDatabase.TABLE_NAME + " WHERE COMPLETE=1")
    List<ToDo> fetchAllCompleted();

    @Query("SELECT * FROM " + ToDoDatabase.TABLE_NAME + " WHERE category = :category")
    List<ToDo> fetchTodoListByCategory(String category);

    @Query("SELECT * FROM " + ToDoDatabase.TABLE_NAME + " WHERE COMPLETE=0 ORDER BY name  ASC")
    List<ToDo> fetchOrderNameASC();
    @Query("SELECT * FROM " + ToDoDatabase.TABLE_NAME + " WHERE COMPLETE=0 ORDER BY name  DESC")
    List<ToDo> fetchOrderNameDESC();

    @Query("SELECT * FROM " + ToDoDatabase.TABLE_NAME + " WHERE id = :todoId")
    ToDo fetchTodoListById(int todoId);


    @Query("SELECT * FROM " + ToDoDatabase.TABLE_NAME + " WHERE name = :name")
    List<ToDo> fetchTodoListByName(String name);

    @Query("SELECT  * FROM " + ToDoDatabase.TABLE_NAME + " WHERE deadline <= :deadline   order by deadline desc")
    List<ToDo> fetchToDoListByDeadline(String deadline);


    @Update
    int updateTodo(ToDo todo);

    @Query("DELETE  FROM " + ToDoDatabase.TABLE_NAME + " WHERE id=:id")
    int deleteTodo(int id);

    @Query("Update " + ToDoDatabase.TABLE_NAME + " set COMPLETE = 1 where id=:id ")
    int Complete(int id);

}
