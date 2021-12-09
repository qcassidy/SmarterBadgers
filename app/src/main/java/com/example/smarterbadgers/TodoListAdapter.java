package com.example.smarterbadgers;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

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
    int maxYear;
    int minYear;
    boolean expandAll = false;
    PlannerFragment plannerFragment;
    AssignmentDialogFragment assignmentDialogFragment;
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

        maxYear = y + 1;
        minYear = y - 1;

        this.mdy = new int[]{m, d, y};

        //days = new ArrayList<>();
        this.dbHelper = dbHelper;
        days = dbHelper.getAssignmentsFromYearRange(minYear, maxYear);

        latestRunOfEmptyDays = new ArrayList<>();

        plannerFragment.todoListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (RecyclerView.SCROLL_STATE_DRAGGING == newState) {
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    public void updateDay(ArrayList<Integer[]> updatedDays) {
        // todo account for days that are not currently in the recyclerview, add new days
        for (int i = 0; i < updatedDays.size(); i++) {
            Integer[] mdy = updatedDays.get(i);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, mdy[2]);
            calendar.set(Calendar.DAY_OF_MONTH, mdy[1]);
            calendar.set(Calendar.MONTH, mdy[0]);
            int position = getPositionOfDate(new int[] {calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.YEAR)});

            // handle the case where a new date is added earlier than the earliest year on the todoList
            int yearDifference = mdy[2] - minYear;
            if (yearDifference < 0) {
                ArrayList<Day> earlierDays = dbHelper.getAssignmentsFromYearRange(minYear + yearDifference, minYear - 1);
                days.addAll(0, earlierDays);
                this.notifyItemRangeInserted(0, earlierDays.size());
                minYear += yearDifference;
            }

            // handle the case where a new date is added later than the latest year on the todolist

            Day currDay = days.get(position);

            currDay.setAssignments(dbHelper.getAssignmentsFromDay((new int[]{mdy[0], mdy[1], mdy[2]})));

            this.notifyItemChanged(position);
            Log.d("todoList", "item changed " + position);
            PlannerActivity.logAssignments(currDay.getAssignments());
        }
    }

    /**
     *
     * @param mdy
     * @return returns position of date in days ArrayList, -1 if date is not in range
     */
    public int getPositionOfDate(int[] mdy) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, mdy[2]);
        calendar.set(Calendar.DAY_OF_MONTH, mdy[1]);
        calendar.set(Calendar.MONTH, mdy[0]);

        int offset = 0;
        for (int j = 0; j < mdy[2] - minYear; j++) {
            if (mdy[2] - minYear < 0) {
                return -1;
            }
            offset += DBHelper.getNumberOfDaysInYear(minYear + j);
        }
        int position = offset + calendar.get(Calendar.DAY_OF_YEAR) - 1;

        Log.d("getPosition", "position: " + position + " " + mdy[0] + "/" + mdy[1] + "/" + mdy[2]);
        return position;
    }

    public void removeAssignment(Assignment assignment) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, assignment.getDueYear());
        calendar.set(Calendar.DAY_OF_MONTH, assignment.getDueDay());
        calendar.set(Calendar.MONTH, assignment.getDueMonth());

        int position = getPositionOfDate(new int[] {assignment.getDueMonth(), assignment.getDueDay(), assignment.getDueYear()});

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
        //Log.d("days", "" + days.size());
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        if (days.size() - position < 50) {
            ArrayList<Day> newDays = this.dbHelper.getAssignmentsFromYear(++maxYear);
            days.addAll(newDays);
            notifyItemRangeInserted(days.size(), newDays.size());
        }


        ArrayList<Assignment> assignments = days.get(position).getAssignments();
        NestedScrollView nestedScrollView = viewHolder.getNestedScrollView();
        //Log.d("position", "" + position);
        //PlannerActivity.logAssignments(assignments);

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

                                AlertDialog.Builder builder = new AlertDialog.Builder(plannerFragment.getContext());
                                builder.setMessage("Are you sure you want to delete this assignment?");
                                builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        plannerFragment.deleteAssignment(assignment);
                                    }
                                });
                                builder.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();

                                    }
                                });
                                AlertDialog confirmDeleteDialog = builder.create();
                                confirmDeleteDialog.setCanceledOnTouchOutside(true);
                                confirmDeleteDialog.show();
                            }

                        }
                    });

                    assignmentDialogFragment = new AssignmentDialogFragment();
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