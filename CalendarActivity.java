package com.example.taskmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.annotation.NonNull;

// ...

public class CalendarActivity extends Activity {
    public static final String EXTRA_DATE = "com.example.myapp.DATE";
//Will create calander activity, and also call saved data
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Calanderveiw
        setContentView(R.layout.calendar_view);

        CalendarView calendarView = findViewById(R.id.calendar_view);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            //Selecting a day will change the the tasks veiwed?
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Intent intent = new Intent();
                String date = dayOfMonth + "-" + (month + 1) + "-" + year;
                intent.putExtra(EXTRA_DATE, date);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
// Gos back to the menu
        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}
