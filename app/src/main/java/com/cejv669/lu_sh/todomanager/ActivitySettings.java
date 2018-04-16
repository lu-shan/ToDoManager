package com.cejv669.lu_sh.todomanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

public class ActivitySettings extends AppCompatActivity {
    boolean showCompleted;
    boolean showReminder;
    String sortingOrder; // Preferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // read the Preferences
        SharedPreferences prefs = this.getSharedPreferences("com.cejv669.lu_sh.todomanager", Context.MODE_PRIVATE);
        showCompleted=prefs.getBoolean("showCompleted",showCompleted);
        showReminder=prefs.getBoolean("showReminder",showReminder);
        sortingOrder=prefs.getString("sortingOrder","");

        // set the switch for showing completed task
        Switch swCompleted = findViewById(R.id.switch_show_completed);
        swCompleted.setChecked(showCompleted);
        // switch listener
        swCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                showCompleted = isChecked;
            }

        });

        // set the switch for showing deadline reminder
        Switch swReminder = findViewById(R.id.switch_show_reminder);
        swReminder.setChecked(showReminder);
        // switch listener
        swReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                showReminder = isChecked;
            }

        });

        Spinner spinner= findViewById(R.id.spinner_sort);
        spinner.setSelection(((ArrayAdapter)spinner.getAdapter()).getPosition(sortingOrder));
        //Spinner listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                sortingOrder = item.toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void onClose (View view){
        savePreference();

        Intent intent = new Intent();

        setResult(Activity.RESULT_OK,intent);
        finish();

    }

    // save the custom settings as shared preference.
    public void savePreference(){
        SharedPreferences prefs = this.getSharedPreferences("com.cejv669.lu_sh.todomanager", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("showCompleted",showCompleted);
        editor.putBoolean("showReminder",showReminder);
        editor.putString("sortingOrder",sortingOrder);
        editor.commit();
    }
}
