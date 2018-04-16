package com.cejv669.lu_sh.todomanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by lushan on 2018-03-16.
 */

public class ListAdapter extends ArrayAdapter<ToDoTask> implements View.OnClickListener{

    private ArrayList<ToDoTask> dataSet;
    Context mContext;
    private int lastPosition = -1;

    // shared preference values
    boolean showReminder;


    // View lookup cache
    private static class ViewHolder {

        TextView txtId;
        TextView txtTodo;
        TextView txtDate;
        TextView txtPriority;
        ImageView imgCompleted;


    }

    public ListAdapter(Context context, ArrayList<ToDoTask> data) {
        super(context,R.layout.listview_row, data);
        this.dataSet = data;
        this.mContext = context;

        // read the Preferences
        SharedPreferences prefs = context.getSharedPreferences("com.cejv669.lu_sh.todomanager", Context.MODE_PRIVATE);
        showReminder=prefs.getBoolean("showReminder",showReminder);

        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ToDoTask dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        LayoutInflater inflater = LayoutInflater.from(getContext());

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_row, null);
            viewHolder = new ViewHolder();
            viewHolder.txtId = (TextView) convertView.findViewById(R.id.task_id);
            viewHolder.txtTodo = (TextView) convertView.findViewById(R.id.task_title);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.task_date);
            viewHolder.txtPriority = (TextView) convertView.findViewById(R.id.task_priority);
            viewHolder.imgCompleted = (ImageView) convertView.findViewById(R.id.imgView_completed);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        lastPosition = position;


        viewHolder.txtTodo.setText(dataModel.getTodo().toString());
        viewHolder.txtDate.setText(dataModel.getDateTime().toString());
        viewHolder.txtPriority.setText(dataModel.getPriority().toString());
        switch (dataModel.getPriority().toString()){
            case "Low":
                viewHolder.txtPriority.setTextColor(Color.parseColor("#007819")); // green
                break;
            case "Normal":
                viewHolder.txtPriority.setTextColor(Color.parseColor("#FFAB00")); //orange
                break;
            case "Urgent":
                viewHolder.txtPriority.setTextColor(Color.parseColor("#D43500")); //red
                break;
            default:
                break;

        }
        if (dataModel.getCompleted()==1){
            viewHolder.imgCompleted.setImageResource(R.mipmap.ic_check_box_black_24dp); // if the task is completed show the image as checked

        } else {
            viewHolder.imgCompleted.setImageResource(R.mipmap.ic_check_box_outline_blank_black_24dp); // if the task is incomplete show the image as unchecked.

        }
        viewHolder.txtDate.setBackground(viewHolder.txtTodo.getBackground()); // default color for deadline

        if (showReminder) {
            if (dataModel.getCompleted()==1){
                viewHolder.txtDate.setBackground(viewHolder.txtTodo.getBackground()); // default color for deadline
            } else {
                if (dataModel.getDateTime().toString().compareTo(currentTime(1))<0){ // if the task is due in 24 hours, change background color of Date as yellow.
                    viewHolder.txtDate.setBackgroundColor(Color.parseColor("#FFDA9B"));
                }
                if (dataModel.getDateTime().toString().compareTo(currentTime(0))<0){ // if the task was overdue, change background color of Date as red.
                    viewHolder.txtDate.setBackgroundColor(Color.parseColor("#D4A08F"));
                }
            }
        }

        viewHolder.imgCompleted.setOnClickListener(this); // register the check image for click listener
        viewHolder.imgCompleted.setTag(position); // get the position id of the checke item from the listview.

        return convertView;
    }

    @Override
    public void onClick(View v) { // onclick listener for the checkbox image

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        ToDoTask dataModel=(ToDoTask)object;

        switch (v.getId()) {
            case R.id.imgView_completed:

                DatabaseManager dbm = new DatabaseManager(getContext());

                if (dataModel.getCompleted() == 0) {

                    ToDoTask t = new ToDoTask(dataModel.getId(), null, null, null, 1); // if the task is incomplete, click to check the box
                    dbm.updateToDo(t);
                } else {
                    ToDoTask t = new ToDoTask(dataModel.getId(), null, null, null, 0); // if the task is completed, click to uncheck the box
                    dbm.updateToDo(t);
                }

                ArrayList<ToDoTask> todoList = dbm.getAllToDosAL();

                this.dataSet.clear();
                this.dataSet.addAll(todoList);
                this.notifyDataSetChanged();
                break;
        }

    }

    // get the current datetime (dayFromNow=0) or the datetime in 24 hours (dayFromNow=1)
    public String currentTime(int dayFromNow) {
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, dayFromNow);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        String monthText = String.valueOf(month);
        String dayText=String.valueOf(day);
        if (month<9){
            monthText="0"+ (month + 1);
        }
        if (day<10) {
            dayText ="0"+day;
        }
        String hourText = String.valueOf(hour);
        String minuteText = String.valueOf(minute);
        if (hour<10){
            hourText="0"+ hour;
        }
        if (minute<10) {
            minuteText ="0"+minute;
        }
        return year + "-" + monthText + "-" + dayText + " " + hourText + ":" + minuteText;
    }
}

