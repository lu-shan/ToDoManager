package com.cejv669.lu_sh.todomanager;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by lushan on 2018-03-28.
 */

public class ArchiveListAdapter extends ArrayAdapter<ToDoTask> implements View.OnClickListener {

    private ArrayList<ToDoTask> dataSet;
    Context mContext;
    int mResource;
    private int lastPosition = -1;


    // View lookup cache
    private static class ViewHolder {

        TextView txtId;
        TextView txtTodo;
        TextView txtDate;
        TextView txtPriority;
        TextView txtCompleted;


    }

    public ArchiveListAdapter(Context context, ArrayList<ToDoTask> data) {
        super(context, R.layout.archivelist_row, data);
        this.dataSet = data;
        this.mContext = context;

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
            convertView = inflater.inflate(R.layout.archivelist_row, null);
            viewHolder = new ViewHolder();
            viewHolder.txtId = (TextView) convertView.findViewById(R.id.taska_id);
            viewHolder.txtTodo = (TextView) convertView.findViewById(R.id.taska_title);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.taska_date);
            viewHolder.txtPriority = (TextView) convertView.findViewById(R.id.taska_priority);
            viewHolder.txtCompleted = (TextView) convertView.findViewById(R.id.taska_complete);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        lastPosition = position;


        viewHolder.txtTodo.setText(dataModel.getTodo().toString());
        viewHolder.txtDate.setText(dataModel.getDateTime().toString());
        viewHolder.txtPriority.setText(dataModel.getPriority().toString());

        if (dataModel.getCompleted() == 1) {
            viewHolder.txtCompleted.setText("Yes"); // if the task is completed show "Yes"
        } else {
            viewHolder.txtCompleted.setText("No"); // if the task is incomplete show "No".
        }
        //viewHolder.txtTodo.setOnClickListener(this); // register the task name for click listener
        //viewHolder.txtTodo.setTag(position); // get the position id of the checke item from the listview.

        return convertView;
    }

    // TODO: multiple selection of list view
    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object = getItem(position);
        ToDoTask dataModel = (ToDoTask) object;

        switch (v.getId()) {
            case R.id.taska_title:


               break;
        }

    }
}
