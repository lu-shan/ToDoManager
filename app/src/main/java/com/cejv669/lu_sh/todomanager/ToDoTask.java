package com.cejv669.lu_sh.todomanager;

/**
 * Created by lu_sh on 2018-03-15.
 */

public class ToDoTask {

    private int id;
    private String todo;
    private String dateTime;
    private String priority;
    private int completed; // 1=yes 0=no

    public ToDoTask()    {
        super();
    }

    public ToDoTask(int id, String todo, String dateTime, String priority, int completed)    {
        super();
        this.id = id;
        this.todo = todo;
        this.dateTime = dateTime;
        this.completed = completed;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }
}
