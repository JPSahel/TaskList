package com.example.taskmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

public class TaskDbHelper extends SQLiteOpenHelper {

    // USING SQL LITE IN ORDER TO STORE A TABLE FOR TASKS.

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TaskManager.db";

    //Creates Table with 4 entries, The task ID, the Title, The date and the status (completed or not completed)
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TaskContract.TaskEntry.TABLE_NAME + " (" +
                    TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY," +
                    TaskContract.TaskEntry.COLUMN_NAME_TITLE + " TEXT," +
                    TaskContract.TaskEntry.COLUMN_NAME_DATE + " TEXT," +
                    TaskContract.TaskEntry.COLUMN_NAME_STATUS + " INTEGER)";

    //This will make sure the data is stored and a new table isnt created if the table already exists
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE_NAME;

    //This allows the object to be shared without issues
    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //This is the function Oncreate which which creates the database while building the app
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }


    // for updating SQL
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }


    //Creating TaskContract Class
    // This adds a new task to the database
    public void addTask(String title, String date) {
        //This is a function from sql database called getwritabledatabase which brings the entries for the database to the code
        SQLiteDatabase db = this.getWritableDatabase();

        //This allows the values to be put into collumns of the table
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_NAME_TITLE, title);
        values.put(TaskContract.TaskEntry.COLUMN_NAME_DATE, date);
        values.put(TaskContract.TaskEntry.COLUMN_NAME_STATUS, 0); // 0: not completed, 1: completed

        // Insert the new row
        db.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);
    }

    // Deleting a task
    public void deleteTask(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        //Selects the entry for the specific task
        String selection = TaskContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        //deletes
        db.delete(TaskContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
    }

    // Updating a task's completion status
    public void updateTaskStatus(long id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //Gets Status of selected stats
        values.put(TaskContract.TaskEntry.COLUMN_NAME_STATUS, status);
        String selection = TaskContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        //Changes the Status
        db.update(TaskContract.TaskEntry.TABLE_NAME, values, selection, selectionArgs);
    }


    // Selecting Date for task using cursor which allows the users button click to change the value.
    public Cursor getTasksByDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        //Gets Date from the specific task
        String selection = TaskContract.TaskEntry.COLUMN_NAME_DATE + " = ?";
        String[] selectionArgs = { date };
        // show incomplete tasks first
        String sortOrder = TaskContract.TaskEntry.COLUMN_NAME_STATUS + " ASC";
        //Returns the value of the date
        return db.query(TaskContract.TaskEntry.TABLE_NAME, null, selection, selectionArgs, null, null, sortOrder);
    }
    //Method added to show task for current day
    // Switching days will be accesible by going into Calander selecting another day and going back to the menu, that will show the task for the selected day.
    public Cursor getTasksForToday() {
        //Code needed in order for java to recognize date selected by calender
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        //Returns the collumn and day
        return getReadableDatabase()
                .query(
                        TaskContract.TaskEntry.TABLE_NAME,
                        null,
                        TaskContract.TaskEntry.COLUMN_NAME_DATE + " = ?",
                        new String[]{currentDate},
                        null,
                        null,
                        null
                );
    }


    // Getting all tasks
    //By default at the main menu it will show all tasks
    public Cursor getAllTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sortOrder = TaskContract.TaskEntry.COLUMN_NAME_STATUS + " ASC"; // show incomplete tasks first
        return db.query(TaskContract.TaskEntry.TABLE_NAME, null, null, null, null, null, sortOrder);
    }
}
