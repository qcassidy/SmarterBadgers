package com.example.smarterbadgers;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * This is used to create elements that go inside the TodoListRecyclerView
 */
public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.ViewHolder> {

    DBHelper dbHelper;
    ArrayList<Day> days;
    int[] mdy;
    int currYear;
    boolean expandAll = false;
    PlannerFragment plannerFragment;
    ArrayList<Day> latestRunOfEmptyDays;
    boolean onRunOfEmptyDays;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final View view;
        private final NestedScrollView nestedScrollView;
        private final LinearLayout nestedLinearLayout;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            this.view = view;
            textView = (TextView) view.findViewById(R.id.textView);
            nestedScrollView = (NestedScrollView) view.findViewById(R.id.todoListNestedScrollView);
            nestedLinearLayout = nestedScrollView.findViewById(R.id.NestedLinearLayout);
        }

        public TextView getTextView() {
            return textView;
        }

        public NestedScrollView getNestedScrollView() {
            return nestedScrollView;
        }

        public LinearLayout getNestedLinearLayout() {
            return nestedLinearLayout;
        }

        public View getView() {
            return view;
        }

        public ItemDetailsLookup.ItemDetails getItemDetails() {
            ItemDetailsLookup.ItemDetails itemDetails = new ItemDetailsLookup.ItemDetails() {
                @Override
                public int getPosition() {
                    Log.d("ViewHolder", "position: " + getAdapterPosition());
                    return getAdapterPosition();
                }

                @Nullable
                @Override
                public Object getSelectionKey() {
                    return null;
                }
            };
            return itemDetails;
        }

        public boolean changeActivated() {
            boolean newActivatedStatus;
            view.setActivated(newActivatedStatus = !view.isActivated());
            Log.d("view", "view.isActivated(): " + view.isActivated());
            return newActivatedStatus;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param firstDay string that represents the first day to be used in RecyclerView
     *                 USE FORMAT: "MM/DD/YYYY"
     *                 by RecyclerView.
     */
    public TodoListAdapter(String firstDay, DBHelper dbHelper, PlannerFragment plannerFragment) {
        dbHelper = dbHelper;

        String[] mdy = firstDay.split("/");
        int m = Integer.valueOf(mdy[0]);
        int d = Integer.valueOf(mdy[1]);
        int y = Integer.valueOf(mdy[2]);

        this.plannerFragment = plannerFragment;

        currYear = y;

        this.mdy = new int[]{m, d, y};

        //days = new ArrayList<>();
        days = dbHelper.getAssignmentsFromYear(y);
        this.dbHelper = dbHelper;

        latestRunOfEmptyDays = new ArrayList<>();
    }

    public void updateDay(ArrayList<Integer[]> updatedDays) {
        for (int i = 0; i < updatedDays.size(); i++) {
            Integer[] mdy = updatedDays.get(i);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, mdy[2]);
            calendar.set(Calendar.DAY_OF_MONTH, mdy[1]);
            calendar.set(Calendar.MONTH, mdy[0]);

            int offset = 0;
            for (int j = 0; j < mdy[2] - this.mdy[2]; j++) {
                GregorianCalendar gregorianCalendar = (GregorianCalendar) GregorianCalendar.getInstance();

                if (gregorianCalendar.isLeapYear(this.mdy[2] + j)) {
                    offset += 366;
                } else {
                    offset += 365;
                }
            }
            Day currDay = days.get(offset + calendar.get(Calendar.DAY_OF_YEAR) - 1);

            currDay.setAssignments(dbHelper.getAssignmentsFromDay((new int[]{mdy[0], mdy[1], mdy[2]})));

            this.notifyItemChanged(calendar.get(Calendar.DAY_OF_YEAR) - 1);
            Log.d("todoList", "item changed");
            PlannerActivity.logAssignments(currDay.getAssignments());
        }
    }

    public int getPositionOfDate(int[] mdy) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, mdy[2]);
        calendar.set(Calendar.DAY_OF_MONTH, mdy[1]);
        calendar.set(Calendar.MONTH, mdy[0]);

        return calendar.get(calendar.DAY_OF_YEAR) - 1;
    }

    public void removeAssignment(Assignment assignment) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, assignment.getDueYear());
        calendar.set(Calendar.DAY_OF_MONTH, assignment.getDueDay());
        calendar.set(Calendar.MONTH, assignment.getDueMonth());

        int offset = 0;
        for (int j = 0; j < mdy[2] - this.mdy[2]; j++) {
            GregorianCalendar gregorianCalendar = (GregorianCalendar) GregorianCalendar.getInstance();

            if (gregorianCalendar.isLeapYear(this.mdy[2] + j)) {
                offset += 366;
            } else {
                offset += 365;
            }
        }
        int position = offset + calendar.get(Calendar.DAY_OF_YEAR) - 1;

        Day currDay = days.get(position);
        currDay.removeAssignment(assignment);

        this.notifyItemChanged(position);
    }

    public class AssignmentEditView extends View {

        public AssignmentEditView(Context context) {
            super(context);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View currView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.todo_list_item, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(currView);

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        Log.d("days", "" + days.size());
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        if (days.size() - position < 50) {
            days.addAll(this.dbHelper.getAssignmentsFromYear(++currYear));
        }

        ArrayList<Assignment> assignments = days.get(position).getAssignments();
        NestedScrollView nestedScrollView = viewHolder.getNestedScrollView();

/*        if (assignments.size() == 0) {
            viewHolder.getTextView().setText("");
            onRunOfEmptyDays = true;
            latestRunOfEmptyDays.add(days.get(position));

            if (position + 1 >= days.size() || days.get(position + 1).getAssignments().size() != 0) {
                viewHolder.getTextView().setText(String.format(Locale.getDefault(), "%s - %s",
                        days.get(0).toString(), days.get(days.size() - 1)));

                onRunOfEmptyDays = false;

                latestRunOfEmptyDays = new ArrayList<>();
                return;
            }

            viewHolder.getNestedLinearLayout().removeAllViews();
            return;
        }*/


        viewHolder.getTextView().setText(days.get(position).toString());
        viewHolder.getNestedLinearLayout().removeAllViews();

        if (onRunOfEmptyDays) {
            return;
        }
        // create TextView for each assignment and set OnClickListener that gives edit and delete options
        for (int i = 0; i < assignments.size(); i++) {
            Assignment assignment = assignments.get(i);
            TextView textView = new TextView(nestedScrollView.getContext());
            textView.setText(assignment.toString());
            textView.setGravity(Gravity.CENTER_HORIZONTAL);

            // go to edit or delete
            textView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    FragmentManager childFragmentManager = plannerFragment.getChildFragmentManager();
                    childFragmentManager.setFragmentResultListener("requestKey", plannerFragment, new FragmentResultListener() {
                        @Override
                        public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                            // We use a String here, but any type that can be put in a Bundle is supported
                            String result = bundle.getString("bundleKey");

                            Log.d("DialogFragment", result);

                            if (result == AssignmentDialogFragment.EDIT_ASSIGNMENT) {
                                plannerFragment.editAssignment(assignment);
                            } else if (result == AssignmentDialogFragment.DELETE_ASSIGNMENT) {
                                // todo dialog to confirm a delete
                                plannerFragment.deleteAssignment(assignment);
                            }

                        }
                    });

                    AssignmentDialogFragment assignmentDialogFragment = new AssignmentDialogFragment();
                    assignmentDialogFragment.show(childFragmentManager, AssignmentDialogFragment.TAG);

                    return true;
                }
            });

            // change background color on touch
            textView.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                        textView.setBackgroundColor(Color.LTGRAY);
                        return false;
                    } else {
                        if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                            textView.setBackgroundColor(Color.WHITE);
                        } else if (MotionEvent.ACTION_MOVE == motionEvent.getAction()) {
                            //textView.setBackgroundColor(Color.WHITE);
                        } else if (MotionEvent.ACTION_CANCEL == motionEvent.getAction()) {
                            textView.setBackgroundColor(Color.WHITE);
                        }
                        return false;
                    }
                }
            });

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }


            });

            // go to edit assignment activity or delete assignment

            viewHolder.getNestedLinearLayout().addView(textView);
        }

       /* // update array adapter with new data
        MyArrayAdapter arrayAdapter = (MyArrayAdapter) viewHolder.getListView().getAdapter();
        arrayAdapter.clear(); // possibly add something to day so it doesn't have to clear and re-add unchanged datasets
        arrayAdapter.notifyDataSetChanged();
        arrayAdapter.addAll(assignments);
        arrayAdapter.notifyDataSetChanged();*/
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return days.size();
    }
}