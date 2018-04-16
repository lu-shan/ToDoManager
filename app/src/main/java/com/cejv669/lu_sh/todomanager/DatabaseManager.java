package com.cejv669.lu_sh.todomanager;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

/**
 * Created by lushan on 2018-03-16.
 */

public class DatabaseManager extends SQLiteOpenHelper {
    // Logcat tag
    private static final String LOG = "DatabaseHelper";
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "todoDB";
    // Table Names
    private static final String TABLE_TODO = "todo";
    private static final String TABLE_ARCHIVE = "archive";
    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_TODO = "todo";
    private static final String KEY_DATETIME = "datetime";
    private static final String KEY_COMPLETED = "completed";
    private static final String KEY_PRIORITY = "priority";

    // Table Create Statements
    // todo table create statement
    String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_TODO + " ("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_TODO + " TEXT,"
            + KEY_DATETIME + " TEXT,"
            + KEY_PRIORITY + " TEXT,"
            + KEY_COMPLETED + " INT" + ")";

    // archive table create statement
    String CREATE_ARCHIVE_TABLE = "CREATE TABLE " + TABLE_ARCHIVE + " ("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_TODO + " TEXT,"
            + KEY_DATETIME + " TEXT,"
            + KEY_PRIORITY + " TEXT,"
            + KEY_COMPLETED + " INT" + ")";

    // shared preference values
    boolean showCompleted;
    String sortingOrder;

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // read the Preferences
        SharedPreferences prefs = context.getSharedPreferences("com.cejv669.lu_sh.todomanager", Context.MODE_PRIVATE);
        showCompleted=prefs.getBoolean("showCompleted",showCompleted);
        sortingOrder=prefs.getString("sortingOrder","");
    }

    // Create tables

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_ARCHIVE_TABLE);
        db.execSQL(CREATE_TODO_TABLE);

    }

    // Upgrade DB

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARCHIVE);
        // create new tables
        onCreate(db);
    }

    public boolean addToDo(ToDoTask todo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_TODO, todo.getTodo());
        values.put(KEY_DATETIME, todo.getDateTime());
        values.put(KEY_PRIORITY, todo.getPriority());
        values.put(KEY_COMPLETED, todo.getCompleted());

        long result = db.insert(TABLE_TODO, null, values);
        db.close();

        if (result==-1) {
            return false;
        }else {
            return true;
        }
    }

    public int updateToDo(ToDoTask todo) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_COMPLETED, todo.getCompleted());

        int records= db.update(TABLE_TODO, values, KEY_ID + " = ?",
                new String[] {String.valueOf(todo.getId())});
        db.close();
        return records;

    }


    /**
     * find all tasks in todo table ordering by the sorting order
     * @return
     */
    public ArrayList<ToDoTask> getAllToDosAL() {



        ArrayList<ToDoTask> todoList = new ArrayList<ToDoTask>();

        String selectQuery = "SELECT * FROM " + TABLE_TODO;

        if (showCompleted == false)
        {
            selectQuery = selectQuery + " WHERE completed = 0";
        }

        switch (sortingOrder) {
            case "Ascend By Date":
                selectQuery = selectQuery + " ORDER BY " + KEY_DATETIME + " ASC;";
                break;
            case "Descend By Date":
                selectQuery = selectQuery + " ORDER BY " + KEY_DATETIME + " DESC;";
                break;
            case "Priority":
                selectQuery = selectQuery + " ORDER BY " + KEY_PRIORITY + " DESC;";
                break;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery(selectQuery, null);

        if(c.moveToFirst()) {
            do {
                ToDoTask todo = new ToDoTask();
                todo.setId(Integer.parseInt(c.getString(0)));
                todo.setTodo(c.getString(1));
                todo.setDateTime(c.getString(2));
                todo.setPriority(c.getString(3));
                todo.setCompleted(c.getInt(4));


                todoList.add(todo);

            } while (c.moveToNext());
        }

        c.close();
        db.close();

        return todoList;
    }

    /**
     * Return all the data from database
     * @return
     */
    public Cursor getData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TODO;
        Cursor data = db.rawQuery(query, null);
        return data;

    }

    /**
     * Return only the ID that maches the name passed in
     * @param name
     * @return
     */
    public Cursor getItemID (String name){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + KEY_ID + " FROM " + TABLE_TODO + " WHERE " + KEY_TODO + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public int editToDo(ToDoTask newToDo){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TODO, newToDo.getTodo());
        values.put(KEY_DATETIME, newToDo.getDateTime());
        values.put(KEY_PRIORITY, newToDo.getPriority());

        int record= db.update(TABLE_TODO, values, KEY_ID + " = ?",
                new String[] {String.valueOf(newToDo.getId())});
        db.close();
        return record;
    }

    public void deleteToDo(int id){ //delete todo task by id
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODO, KEY_ID+"=?", new String[]{String.valueOf(id)});
        db.close();

    }

    public ToDoTask getToDo (int id){ // find todo task by id
        SQLiteDatabase db= this.getReadableDatabase();
        Cursor cur=db.query(TABLE_TODO,
                new String[]{KEY_ID, KEY_TODO, KEY_DATETIME, KEY_PRIORITY, KEY_COMPLETED},
                KEY_ID+"=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cur!=null){
            cur.moveToFirst();
            ToDoTask todo= new ToDoTask(Integer.parseInt(cur.getString(0)),
                    cur.getString(1),
                    cur.getString(2),
                    cur.getString(3),
                    (Integer.parseInt(cur.getString(4))));
            cur.close();
            db.close();
            return todo;

        }
        cur.close();
        db.close();
        return null;

    }



    public int deleteToDoList (){ //delete all todolist
        SQLiteDatabase db= this.getWritableDatabase();
        int deletedRecords=db.delete(TABLE_TODO, "1", null);
        db.close();
        return deletedRecords;
    }

    public int deleteArchiveList (){ //delete all archivelist
        SQLiteDatabase db= this.getWritableDatabase();
        int deletedRecords=db.delete(TABLE_ARCHIVE, "1", null);
        db.close();
        return deletedRecords;
    }

    public void deleteArchive(int id){ //delete the archive record by id
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ARCHIVE, KEY_ID+"=?", new String[]{String.valueOf(id)});
        db.close();

    }


    public ArrayList<ToDoTask> getAllArchiveAL(){ // find all archive records
        ArrayList<ToDoTask> archiveList = new ArrayList<ToDoTask>();

        String selectQuery = "SELECT * FROM " + TABLE_ARCHIVE + " ORDER BY " + KEY_DATETIME + " ASC;";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery(selectQuery, null);

        if(c.moveToFirst()) {
            do {
                ToDoTask todo = new ToDoTask();
                todo.setId(Integer.parseInt(c.getString(0)));
                todo.setTodo(c.getString(1));
                todo.setDateTime(c.getString(2));
                todo.setPriority(c.getString(3));
                todo.setCompleted(c.getInt(4));


                archiveList.add(todo);

            } while (c.moveToNext());
        }

        c.close();
        db.close();

        return archiveList;
    }


    /**
     * Archive a todo record by id
     * @param id
     * @return
     */
    public String taskArchive(int id) {
        ToDoTask task;
        SQLiteDatabase db = this.getWritableDatabase();
        //find the record from todo table in database by id
        Cursor cur=db.query(TABLE_TODO,
                new String[]{KEY_ID, KEY_TODO, KEY_DATETIME, KEY_PRIORITY, KEY_COMPLETED},
                KEY_ID+"=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cur!=null){
            cur.moveToFirst();
            task= new ToDoTask(Integer.parseInt(cur.getString(0)),
                    cur.getString(1),
                    cur.getString(2),
                    cur.getString(3),
                    (Integer.parseInt(cur.getString(4))));
        //copy the record to archive table
            ContentValues values = new ContentValues();

            values.put(KEY_TODO, task.getTodo());
            values.put(KEY_DATETIME, task.getDateTime());
            values.put(KEY_PRIORITY, task.getPriority());
            values.put(KEY_COMPLETED, task.getCompleted());

            long result = db.insert(TABLE_ARCHIVE, null, values);
            if (result == -1) {
                cur.close();
                db.close();
                return "can't add to archive";
            } else {
                // if copy is successful, delete the record from todo table
                db.delete(TABLE_TODO, KEY_ID+"=?", new String[]{String.valueOf(id)});
                cur.close();
                db.close();
                return "archived";
            }
        }else {
            cur.close();
            db.close();
            return "can't find record";
        }

    }

    /**
     * Unarchive a record by id
     * @param id
     * @return
     */
    public String taskUnarchive(int id) {
        ToDoTask task;
        SQLiteDatabase db = this.getWritableDatabase();
        // find the record from archive table by id
        Cursor cur=db.query(TABLE_ARCHIVE,
                new String[]{KEY_ID, KEY_TODO, KEY_DATETIME, KEY_PRIORITY, KEY_COMPLETED},
                KEY_ID+"=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cur!=null){
            cur.moveToFirst();
            task= new ToDoTask(Integer.parseInt(cur.getString(0)),
                    cur.getString(1),
                    cur.getString(2),
                    cur.getString(3),
                    (Integer.parseInt(cur.getString(4))));
        //copy the record to todo table
            ContentValues values = new ContentValues();

            values.put(KEY_TODO, task.getTodo());
            values.put(KEY_DATETIME, task.getDateTime());
            values.put(KEY_PRIORITY, task.getPriority());
            values.put(KEY_COMPLETED, task.getCompleted());

            long result = db.insert(TABLE_TODO, null, values);
            if (result == -1) {
                cur.close();
                db.close();
                return "can't add to ToDo";
            } else {
                // if copy is successful, delete it from archive table
                db.delete(TABLE_ARCHIVE, KEY_ID+"=?", new String[]{String.valueOf(id)});
                cur.close();
                db.close();
                return "unarchived";
            }
        }else {
            cur.close();
            db.close();
            return "can't find record";
        }

    }

    // Show the number of tasks and incomplete tasks in todo table
    public String reportToDo(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM "+ TABLE_TODO + " ;",null);
        int total = c.getCount();
        c = db.rawQuery("SELECT * FROM "+ TABLE_TODO + " WHERE "+KEY_COMPLETED+" =0;",null);
        int incomplete = c.getCount();
        c.close();
        db.close();
        return "You have "+total+" tasks in the ToDo List.\n"+incomplete+" tasks are imcomplete.";
    }

    // Show the number of tasks and incomplete tasks in archive table
    public String reportArchive(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM "+ TABLE_ARCHIVE + " ;",null);
        int total = c.getCount();
        c = db.rawQuery("SELECT * FROM "+ TABLE_ARCHIVE + " WHERE "+KEY_COMPLETED+" =0;",null);
        int incomplete = c.getCount();
        c.close();
        db.close();
        return total+" Archived Records.\n"+incomplete+" Imcomplete Tasks.";
    }





}
