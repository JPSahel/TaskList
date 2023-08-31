package com.example.taskmanager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Request Date grabs result from Calander activity
    private static final int REQUEST_DATE = 1;
    private TaskDbHelper dbHelper;
    private TaskAdapter taskAdapter;

    //Launches Activity
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
    //defines expected result from activity
            new ActivityResultContracts.StartActivityForResult(),
            //this calls back the result
            new ActivityResultCallback<ActivityResult>() {
                @Override
                //
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String date = data.getStringExtra(CalendarActivity.EXTRA_DATE);
                            taskAdapter.swapCursor(dbHelper.getTasksByDate(date));
                        }
                    } else {
                        taskAdapter.swapCursor(dbHelper.getTasksForToday());
                    }

                }
            }
    );

    @Override
    //This calls TaskDbhelper and taskadapterclass to the recycle veiwer
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TaskDbHelper(this);
        RecyclerView taskList = findViewById(R.id.task_list);
        taskList.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(dbHelper.getTasksForToday());
        taskList.setAdapter(taskAdapter);



        Button addTaskButton = findViewById(R.id.add_task_button);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialog_add_task);

                final EditText taskNameInput = dialog.findViewById(R.id.task_name_input);
                final DatePicker taskDatePicker = dialog.findViewById(R.id.task_date_picker);
                Button addButton = dialog.findViewById(R.id.add_button);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String taskName = taskNameInput.getText().toString();
                        String taskDate = taskDatePicker.getDayOfMonth() + "-" +
                                (taskDatePicker.getMonth() + 1) + "-" +
                                taskDatePicker.getYear();
                        dbHelper.addTask(taskName, taskDate);
                        taskAdapter.swapCursor(dbHelper.getAllTasks()); // refresh the cursor
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        Button calendarViewButton = findViewById(R.id.calendar_view_button);
        calendarViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                activityResultLauncher.launch(intent);
            }
        });
    }

    private class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
        private Cursor cursor;

        public TaskAdapter(Cursor cursor) {
            this.cursor = cursor;
        }

        public void swapCursor(Cursor newCursor) {
            if (cursor != null) {
                cursor.close();
            }

            cursor = newCursor;

            if (newCursor != null) {
                notifyDataSetChanged();
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public CheckBox checkBox;
            public TextView name;
            public TextView status;
            public Button deleteButton;

            public ViewHolder(View itemView) {
                super(itemView);
                checkBox = itemView.findViewById(R.id.task_checkbox);
                name = itemView.findViewById(R.id.task_name);
                status = itemView.findViewById(R.id.task_status);
                deleteButton = itemView.findViewById(R.id.delete_button);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.item_task, parent, false);
            return new ViewHolder(itemView);
        }
//Code for the veiwholder and Cursor
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (cursor.moveToPosition(position)) {
                final long id = cursor.getLong(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry._ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_NAME_TITLE));
                int status = cursor.getInt(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_NAME_STATUS));
                holder.name.setText(title);
                holder.checkBox.setChecked(status == 1);
                holder.status.setText(status == 1 ? "Completed" : "Not completed");
                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        dbHelper.updateTaskStatus(id, isChecked ? 1 : 0);
                    }
                });
                holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbHelper.deleteTask(id);
                        swapCursor(dbHelper.getAllTasks()); // refresh the cursor
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return cursor.getCount();
        }

        // I want to admit I dont really understand my own code too much, I just worked until it worked.
    }


}
