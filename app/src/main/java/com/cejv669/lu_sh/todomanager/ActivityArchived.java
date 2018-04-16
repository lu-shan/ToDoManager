package com.cejv669.lu_sh.todomanager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ActivityArchived extends AppCompatActivity {

    private ListView alv;
    private ArrayList alvItem = new ArrayList<>(); // for holding list item ids
    private static ArchiveListAdapter adapter ;
    DatabaseManager dm;

    private TextView tvReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archived);

        tvReport =findViewById(R.id.tv_a_records);

        alv = findViewById(R.id.listview_archive); // list view
        registerForContextMenu(alv); // register list view for context menu

        dm = new DatabaseManager(this);
        updateUI();


    }

    private void updateUI() {
        alv = findViewById(R.id.listview_archive);

        DatabaseManager dm = new DatabaseManager(this);

        ArrayList<ToDoTask> archiveList = dm.getAllArchiveAL();

        adapter = new ArchiveListAdapter(getApplicationContext(),archiveList);

        alv.setAdapter(adapter);

        tvReport.setText(dm.reportArchive());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(0, 1, 1, menuIconWithText(getResources().getDrawable(R.mipmap.ic_keyboard_return_black_24dp), getResources().getString(R.string.menu_a_return)));
        menu.add(0, 2, 2, menuIconWithText(getResources().getDrawable(R.mipmap.ic_unarchive_black_24dp), getResources().getString(R.string.menu_a_unarchive)));
        menu.add(0, 3, 3, menuIconWithText(getResources().getDrawable(R.mipmap.ic_delete_forever_black_24dp), getResources().getString(R.string.menu_a_delete)));
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case 1:
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK,intent);
                finish();
                return true;
            case 2:
                unarchiveAll();
                return true;
            case 3:
                deleteArchiveList();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private CharSequence menuIconWithText(Drawable r, String title) {

        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.listview_archive) {
            ListView alv = (ListView) v;

            //MenuInflater inflater = getMenuInflater();
            //inflater.inflate(R.menu.context_menu, menu);
            menu.add(0, 1, 1, menuIconWithText(getResources().getDrawable(R.mipmap.ic_unarchive_black_24dp), getResources().getString(R.string.menu_unarchive)));
            menu.add(0, 2, 2, menuIconWithText(getResources().getDrawable(R.mipmap.ic_clear_black_24dp), getResources().getString(R.string.menu_delete)));

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem mi) {
        ToDoTask todoItem = new ToDoTask(); // create a new ToDoTask object
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) mi.getMenuInfo();
        final ToDoTask item = adapter.getItem(acmi.position); //get corresponding record from the position id of the selected item from listview

        switch (mi.getItemId()) {
            case 1:
                toastMsg(dm.taskUnarchive(item.getId())); // add the selected item to todo_table and delete it from archive_table
                updateUI();
                break;
            case 2: //delete the archive record from database, show AlertDialog
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("DELETE");
                alert.setMessage("Delete \""+ item.getTodo() +"\" ?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) { // click Yes to delete
                        dm.deleteArchive(item.getId());
                        updateUI();
                        toastMsg("ArchiveID=" + item.getId() + " is deleted.");

                        dialog.dismiss();
                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();

                break;
            default:
                break;

        }
        return super.onContextItemSelected(mi);
    }

    public void btnReturn(View view){ // click on return button
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK,intent);
        finish();
    }

    // unarchive all records
    public void unarchiveAll() {

        ArrayList<ToDoTask> archiveList = dm.getAllArchiveAL();
        for (ToDoTask record : archiveList){
            dm.taskUnarchive(record.getId());
        }
        updateUI();
        toastMsg(archiveList.size()+" records unarchived");

    }

    // delete all Archive records, show AlertDialog
    public void deleteArchiveList() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("DELETE");
        alert.setMessage("Delete all Archive records ?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { // click Yes to delete
                int deletedRecords=dm.deleteArchiveList();
                updateUI();
                toastMsg("Deleted "+deletedRecords+" Archive Records.");

                dialog.dismiss();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();

    }

    private void toastMsg(String msg) {
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
    }


}
