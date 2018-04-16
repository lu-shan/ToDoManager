package com.cejv669.lu_sh.todomanager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ActivityExport extends AppCompatActivity {
    ArrayList<ToDoTask> al = new ArrayList<>();
    DatabaseManager dm;
    TextView exportResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        dm = new DatabaseManager(this);
        exportResult= (TextView)findViewById(R.id.tv_export_result);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private String generateCSV(ArrayList<ToDoTask> al) {
        StringBuilder sb = new StringBuilder();

        sb.append("\"ID\", \"Task\", \"Deadline\",\"Priority\",\"complete\"");
        sb.append(System.getProperty("line.separator"));

        for (ToDoTask item : al)
        {
            sb.append(item.getId());
            sb.append(", ");
            sb.append("\"");
            sb.append(item.getTodo());
            sb.append("\"");
            sb.append(", ");
            sb.append("\"");
            sb.append(item.getDateTime());
            sb.append("\"");
            sb.append(", ");
            sb.append(item.getPriority());
            sb.append(", ");
            sb.append(item.getCompleted());
            sb.append(System.getProperty("line.separator"));
        }

        return sb.toString();
    }

    /*
    Called by clicking the export todo button.
     */
    public void btnExportToDo(View view) {
        al = dm.getAllToDosAL();
        String toSave = generateCSV(al);

        if (isExternalStorageWritable()){
            File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File dir = new File(root.getAbsolutePath());
            File file = new File(dir, "mytodo.txt");

            try {

                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(toSave.getBytes());
                fileOutputStream.close();

            } catch (FileNotFoundException e) {
                //e.printStackTrace();
                Toast.makeText(getApplicationContext(), "FileNotFoundException", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                //e.printStackTrace();
                Toast.makeText(getApplicationContext(), "IOException", Toast.LENGTH_LONG).show();
            }
            exportResult.setText("ToDo List is exported to \"mytodo.txt\" file in Download folder.");
        } else {
            Toast.makeText(getApplicationContext(), "External Not Available", Toast.LENGTH_LONG).show();
        }
    }

    /*
    Called by clicking the export archive button.
     */
    public void btnExportArchive(View view) {
        al = dm.getAllArchiveAL();
        String toSave = generateCSV(al);

        if (isExternalStorageWritable()){
            File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File dir = new File(root.getAbsolutePath());
            File file = new File(dir, "myarchive.txt");

            try {

                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(toSave.getBytes());
                fileOutputStream.close();

            } catch (FileNotFoundException e) {
                //e.printStackTrace();
                Toast.makeText(getApplicationContext(), "FileNotFoundException", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                //e.printStackTrace();
                Toast.makeText(getApplicationContext(), "IOException", Toast.LENGTH_LONG).show();
            }
            exportResult.setText("Archive List is exported to \"myarchive.txt\" file in Download folder.");
        } else {
            Toast.makeText(getApplicationContext(), "External Not Available", Toast.LENGTH_LONG).show();
        }
    }

    public void btnReturn(View view) { // click return button
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK,intent);
        finish();
    }


}
