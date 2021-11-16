package com.example.smarterbadgers;


import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * This is used to create elements that go inside the TodoListRecyclerView
 */
public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.ViewHolder> {

    DBHelper dbHelper;
    ArrayList<Day> days;
    int[] mdy;
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = (TextView) view.findViewById(R.id.textView);
            linearLayout = (LinearLayout) view.findViewById(R.id.AssignmentsLinearLayout);
        }

        public TextView getTextView() {
            return textView;
        }

        public LinearLayout getLinearLayout() {
            return linearLayout;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param firstDay string that represents the first day to be used in RecyclerView
     *                USE FORMAT: "MM/DD/YYYY"
     * by RecyclerView.
     */
    public TodoListAdapter(String firstDay, DBHelper dbHelper) {
        dbHelper = dbHelper;

        String[] mdy = firstDay.split("/");
        int m = Integer.valueOf(mdy[0]);
        int d = Integer.valueOf(mdy[1]);
        int y = Integer.valueOf(mdy[2]);

        this.mdy = new int[] {m,d,y};

        //days = new ArrayList<>();
        days = dbHelper.getAssignmentsFromYear(y);
        this.dbHelper = dbHelper;
    }

    public void updateDay(int mdy[]) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, mdy[2]);
        calendar.set(Calendar.DAY_OF_MONTH, mdy[1]);
        calendar.set(Calendar.MONTH, mdy[0]);
        Day currDay = days.get(calendar.get(Calendar.DAY_OF_YEAR) - 1);

        currDay.setAssignments(dbHelper.getAssignmentsFromDay(mdy));

        this.notifyItemChanged(calendar.get(Calendar.DAY_OF_YEAR) - 1);
        Log.d("todoList", "item changed");
        PlannerActivity.logAssignments(currDay.getAssignments());
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.todo_list_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element


        viewHolder.getTextView().setText(days.get(position).toString());
        ArrayList<Assignment> assignments = days.get(position).getAssignments();

        viewHolder.linearLayout.removeAllViews();
        for (int i = 0; i < assignments.size(); i++) {
            Assignment currAssignment = assignments.get(i);
            TextView currView = new TextView(viewHolder.linearLayout.getContext());
            currView.setText(currAssignment.getName() + ":\n\t" + currAssignment.getDescription() + "\n");
            viewHolder.getLinearLayout().addView(currView);

        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return days.size();
    }
}

