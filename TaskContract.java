package com.example.taskmanager;

import android.provider.BaseColumns;

public final class TaskContract {
// this class makes the Task Entries
    private TaskContract() {}

    public static class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "task";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_STATUS = "status";
    }
}
