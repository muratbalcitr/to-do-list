package com.murat.todolist.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.murat.todolist.database.ToDoDatabase;

import java.io.Serializable;
@Entity(tableName = ToDoDatabase.TABLE_NAME)
public class ToDo implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name ="NAME")
    public String name;

    @ColumnInfo(name ="DESCRIPTION")
    public String desc;

    @ColumnInfo(name ="CATEGORY")
    public String category;

    @ColumnInfo(name = "DEADLINE")
    public String deadline;

    @ColumnInfo(name = "CREATEDATE")
    public String createDate;
    @ColumnInfo(name = "COMPLETE")
    public int complete;

    public ToDo() {
    }
    @Ignore
    public ToDo(int id, String name, String desc, String category) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.category = category;
    }
    @Ignore
    public ToDo(String name, String desc, String category) {
        this.name = name;
        this.desc = desc;
        this.category = category;
    }

    public ToDo(int id, String name, String desc, String category, String deadline, String status) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.category = category;
        this.deadline = deadline;
     }

    public ToDo(String name, String desc, String category, String deadline, String status) {
        this.name = name;
        this.desc = desc;
        this.category = category;
        this.deadline = deadline;
     }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getComplete() {
        return complete;
    }

    public void setComplete(int complete) {
        this.complete = complete;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }


}
