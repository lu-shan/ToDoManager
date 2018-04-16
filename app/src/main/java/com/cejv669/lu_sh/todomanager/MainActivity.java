package com.cejv669.lu_sh.todomanager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public final static String stringToActAdd = "message"; // pass this message to ActivityAddRecord
    // set shared Preferences
    public boolean showCompleted;
    public boolean showReminder;
    public String sortingOrder;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private ListView lv;
    private static ListAdapter adapter ;
    DatabaseManager dm;
    FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the Preferences
        SharedPreferences prefs = this.getSharedPreferences("com.cejv669.lu_sh.todomanager", Context.MODE_PRIVATE);
        // if there is none, set default Preferences
        if (prefs.getString("sortingOrder",sortingOrder)==null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            showCompleted = true;
            showReminder = true;
            sortingOrder = "Ascend By Date";
            editor.putBoolean("showCompleted", showCompleted);
            editor.putBoolean("showReminder", showReminder);
            editor.putString("sortingOrder", sortingOrder);
            editor.commit();
        } else {   // if the preference exist, read the preference
            showCompleted = prefs.getBoolean("showCompleted", showCompleted);
            showReminder = prefs.getBoolean("showReminder", showReminder);
            sortingOrder = prefs.getString("sortingOrder", sortingOrder);
        }

        lv = findViewById(R.id.listview_todo); // list view

        registerForContextMenu(lv); // register list view for context menu

        dm = new DatabaseManager(this);

        // setup the sidebar
        NavigationView mNavigationView = findViewById(R.id.navView);
        if (mNavigationView!=null){
            mNavigationView.setNavigationItemSelectedListener(this);
        }

        mDrawerLayout=findViewById(R.id.drawerlayout);
        mToggle=new ActionBarDrawerToggle(this, mDrawerLayout,R.string.open,R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);

        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fabAdd = findViewById(R.id.fab_add);


        updateUI();



    }

    private void updateUI() {
        lv = findViewById(R.id.listview_todo);

        DatabaseManager dm = new DatabaseManager(this);

        ArrayList<ToDoTask> todoList = dm.getAllToDosAL();

        adapter = new ListAdapter(getApplicationContext(),todoList);

        lv.setAdapter(adapter);


    }


    // set the menu item on sidebar
    @Override
    public boolean onNavigationItemSelected(MenuItem item){

        int selectedId=item.getItemId();
        if (R.id.nav_export == selectedId){ // export to csv
            exportCSV();
        }
        if (R.id.nav_settings == selectedId){ //go to settings
            setting();
        }
        if (R.id.nav_exit == selectedId){ // exit program
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }

        return super.onOptionsItemSelected(item);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // add option menu item (groupID, itemID, order, text)
        menu.add(0, 1, 1, menuIconWithText(getResources().getDrawable(R.mipmap.ic_add_black_24dp), getResources().getString(R.string.menu_add)));
        menu.add(0, 2, 2, menuIconWithText(getResources().getDrawable(R.mipmap.ic_view_list_black_24dp), getResources().getString(R.string.menu_showarchive)));
        menu.add(0, 3, 3, menuIconWithText(getResources().getDrawable(R.mipmap.ic_report_black_24dp), getResources().getString(R.string.menu_report)));
        menu.add(0, 4, 4, menuIconWithText(getResources().getDrawable(R.mipmap.ic_delete_forever_black_24dp), getResources().getString(R.string.menu_deleteall)));
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // when click the sidebar icon
        if (mToggle.onOptionsItemSelected(item)){
            return true;
        }

        // Handle item selection
        switch (item.getItemId()) {
            case 1: // add task
                addRecord();
                return true;
            case 2: // go to the ActivityArchived showing archive list
                showArchive();
                return true;
            case 3: // show the report for todo list
                showReport();
                return true;
            case 4:  // delete all todo tasks, show the AlertDialog
                deleteList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //this method add the icon image to the CharSequence, thus the text of menu item can have icons.
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

        if (v.getId() == R.id.listview_todo) {
            ListView lv = (ListView) v;

            // add context menu item (groupID, itemID, order, text)
            menu.add(0, 1, 1, menuIconWithText(getResources().getDrawable(R.mipmap.ic_done_black_24dp), getResources().getString(R.string.menu_complete)));
            menu.add(0, 2, 2, menuIconWithText(getResources().getDrawable(R.mipmap.ic_edit_black_24dp), getResources().getString(R.string.menu_edit)));
            menu.add(0, 3, 3, menuIconWithText(getResources().getDrawable(R.mipmap.ic_share_black_24dp), getResources().getString(R.string.menu_share)));
            menu.add(0, 4, 4, menuIconWithText(getResources().getDrawable(R.mipmap.ic_archive_black_24dp), getResources().getString(R.string.menu_archive)));
            menu.add(0, 5, 5, menuIconWithText(getResources().getDrawable(R.mipmap.ic_clear_black_24dp), getResources().getString(R.string.menu_delete)));


        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem mi) {
        ToDoTask todoItem = new ToDoTask(); // create a new ToDoTask object
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) mi.getMenuInfo();
        final ToDoTask item = adapter.getItem(acmi.position); //get corresponding record from the position id of the selected item from listview

        switch (mi.getItemId()) {
            case 1: // set the task as completed

                todoItem.setId(item.getId()); // set the new ToDoTask object's id as the same as the selected item
                todoItem.setCompleted(1);
                dm.updateToDo(todoItem); // change the selected item's completed as 1 (it is completes)
                updateUI();
                toastMsg("Task "+todoItem.getId()+" is completed.");
                break;

            case 2: // edit task
                editRecord(item.getId());
                break;
            case 3:  // share task
                String shareMSG = "TODO: "+item.getTodo()+", Deadline: "+item.getDateTime()+", Priority: "+item.getPriority(); // share the task as a text message
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, shareMSG);

                startActivity(Intent.createChooser(share, "Share TODO to:"));

                break;
            case 4: // archive task
                toastMsg(dm.taskArchive(item.getId())); // add the selected item to archive_table and delete it from todo_table
                updateUI();
                break;
            case 5:  //delete the task record from database, show the AlertDialog
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("DELETE");
                alert.setMessage("Delete \""+ item.getTodo() +"\" ?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) { // click Yes to delete
                        dm.deleteToDo(item.getId());
                        updateUI();
                        toastMsg("TaskID="+item.getId()+" is deleted.");

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


    // go to ActivityAddRecord, add record
    public void addRecord() {
        Intent intent = new Intent(this, ActivityAddRecord.class);
        intent.putExtra(stringToActAdd, "ADD");
        startActivityForResult(intent,10);

    }

    // go to ActivityAddRecord, edit record
    public void editRecord(int id) {
        Intent intent = new Intent(this, ActivityAddRecord.class);
        intent.putExtra(stringToActAdd, "EDIT");
        intent.putExtra(stringToActAdd+"_id", id);
        startActivityForResult(intent,20);

    }

    // go to ActivitySettings, change custom settings
    public void setting() {
        Intent intent = new Intent(this, ActivitySettings.class);

        startActivityForResult(intent,30);

    }

    // delete all records in todo list, show the AlertDialog
    public void deleteList() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("DELETE");
        alert.setMessage("Delete all ToDo records ?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { // click Yes to delete
                int deletedRecords=dm.deleteToDoList();
                updateUI();
                toastMsg("Deleted "+deletedRecords+" Records.");

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

    // go to ActivityArchived, show archive list
    public void showArchive(){
        Intent intent = new Intent(this, ActivityArchived.class);

        startActivityForResult(intent,40);
    }

    // go to ActivityExport, export to csv
    public void exportCSV() {
        Intent intent = new Intent(this, ActivityExport.class);

        startActivityForResult(intent,50);

    }

    // show report message
    public void showReport() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("REPORT");
        String message =dm.reportToDo();
        alert.setMessage(message);
        alert.setNeutralButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==10){ // add record result
            if (resultCode == Activity.RESULT_OK) {
                toastMsg("Add Record");


            }
            if (resultCode == Activity.RESULT_CANCELED){
                toastMsg("User Cancelled");

            }
        }

        if (requestCode==20){ // edit record result
            if (resultCode == Activity.RESULT_OK) {
                toastMsg("Edit Record");

            }
            if (resultCode == Activity.RESULT_CANCELED){
                toastMsg("user cancelled");
            }
        }

        if (requestCode==30){ // change settings result
            if (resultCode == Activity.RESULT_OK) {
                toastMsg("Set settings");

            }

        }

        if (requestCode==40){ // show archive result
            if (resultCode == Activity.RESULT_OK) {
                toastMsg("Return");

            }

        }

        if (requestCode==50){ // export result
            if (resultCode == Activity.RESULT_OK) {
                toastMsg("Return");

            }

        }
        updateUI();
    }

    private void toastMsg(String msg) {
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
    }

    // click fab to add record
    public void btnAdd(View view) {
        addRecord();

    }


}
