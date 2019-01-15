package com.cejv669.lu_sh.todomanager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;

public class ActivityAddRecord extends FragmentActivity {
    static EditText editTask;
    static EditText editDate;
    static final Calendar c = Calendar.getInstance();
    static int year = c.get(Calendar.YEAR);
    static int month = c.get(Calendar.MONTH);
    static int day = c.get(Calendar.DAY_OF_MONTH);
    static int hour = c.get(Calendar.HOUR_OF_DAY);
    static int minute = c.get(Calendar.MINUTE);
    String message;
    int id;
    String priority;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        message = intent.getStringExtra(MainActivity.stringToActAdd);

        TextView title = (TextView) findViewById(R.id.tv_add_title);
        editTask = (EditText) findViewById(R.id.et_todo);
        editDate = (EditText) findViewById(R.id.et_date);
        Spinner spinner2= findViewById(R.id.spinner_priority);
        Button btnAdd = (Button) findViewById(R.id.btn_add);

        if (message.equals("ADD")) {
            // set the title
            title.setText("Add Record");
            // set the EditText Deadline as the current date and time.
            editDateText(year,month,day);
            editTimeText(hour,minute);
            // set button text as "Add"
            btnAdd.setText("Add");
        }

        if (message.equals("EDIT")) {
            // set the title
            title.setText("Edit Record");

            // set button text as "Edit"
            btnAdd.setText("Edit");

            // show the record
            id = intent.getExtras().getInt(MainActivity.stringToActAdd+"_id");
            DatabaseManager dm = new DatabaseManager(this);
            ToDoTask todo = dm.getToDo(id);
            if (todo!=null) {
                editTask.setText(todo.getTodo());
                editDate.setText(todo.getDateTime());
                spinner2.setSelection(((ArrayAdapter)spinner2.getAdapter()).getPosition(todo.getPriority()));
            }


        }

        // EditText listener for picking Date and Time
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
                showDatePickerDialog(v);
            }
        });

        //Spinner listener
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                priority = item.toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); only available above Android API 26

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            //final Calendar c = Calendar.getInstance();
            //int year = c.get(Calendar.YEAR);
            //int month = c.get(Calendar.MONTH);
            //int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            editDateText(year, month, day);
        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            //final Calendar c = Calendar.getInstance();
            //int hour = c.get(Calendar.HOUR_OF_DAY);
            //int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            editTimeText(hourOfDay,minute);
        }
    }

    public static void editDateText (int year, int month, int day) { //format the Date
        String monthText;
        String dayText=String.valueOf(day);
        if (month<9){
            monthText="0"+ String.valueOf(month + 1);
        } else {
            monthText= String.valueOf(month + 1);
        }
        // in Calendar.MONTH, Jan is 0, Feb is 1,..., Dec is 11, UNDec is 12 (used in lunar calendar)
        if (day<10) {
            dayText ="0"+day;
        }
        editDate.setText(year + "-" + monthText + "-" + dayText);

    }

    public static void editTimeText (int hour, int minute) { //format the time
        String hourText = String.valueOf(hour);
        String minuteText = String.valueOf(minute);
        if (hour<10){
            hourText="0"+ hour;
        }
        if (minute<10) {
            minuteText ="0"+minute;
        }
        editDate.setText(editDate.getText() + " " + hourText + ":" + minuteText);
    }


    public void onCancel (View view){
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED,intent);
        finish();
    }

    public void onAdd (View view) {

        if (!editTask.getText().toString().equals("") ) {

            ToDoTask t = new ToDoTask();
            t.setTodo(editTask.getText().toString());
            t.setDateTime(editDate.getText().toString());
            t.setPriority(priority);
            t.setCompleted(0);

            if (editDate.getText().toString().equals("")) {
                t.setDateTime("Unknown");
            }

            DatabaseManager dm = new DatabaseManager(this);

            if (message.equals("ADD")) {
                if (dm.addToDo(t)) {

                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Add not successful", Toast.LENGTH_LONG).show();
                }
            }

            if (message.equals("EDIT")) {
                t.setId(id);
                if (dm.editToDo(t)==1) {
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Edit not successful", Toast.LENGTH_LONG).show();
                }
            }

        } else {
            Toast.makeText(getApplicationContext(), "Please input task", Toast.LENGTH_LONG).show();
        }
    }
}






