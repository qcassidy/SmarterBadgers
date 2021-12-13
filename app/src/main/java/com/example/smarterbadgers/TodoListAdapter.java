package com.example.smarterbadgers;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.Locale;

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
    boolean needLaterDays = false;
    boolean needEarlierDays = false;
    RecyclerView recyclerView;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final View view;
        private final NestedScrollView nestedScrollView;
        private final LinearLayout nestedLinearLayout;
        private final LinearLayout dividerLinearLayout;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            this.view = view;
            textView = (TextView) view.findViewById(R.id.textView);
            nestedScrollView = (NestedScrollView) view.findViewById(R.id.todoListNestedScrollView);
            nestedLinearLayout = nestedScrollView.findViewById(R.id.NestedLinearLayout);
            dividerLinearLayout = view.findViewById(R.id.WeekDividerLinearLayout);
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

        public LinearLayout getDividerLinearLayout() {
            return dividerLinearLayout;
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

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void updateDay(ArrayList<Integer[]> updatedDays) {
        for (int i = 0; i < updatedDays.size(); i++) {
            Integer[] mdy = updatedDays.get(i);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, mdy[2]);
            calendar.set(Calendar.DAY_OF_MONTH, mdy[1]);
            calendar.set(Calendar.MONTH, mdy[0]);
            int position = getPositionOfDate(new int[] {calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.YEAR)});


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

        // handle the case where a new date is added earlier than the earliest year on the todoList
        int yearDifference = mdy[2] - minYear;
        if (yearDifference < 0) {
            ArrayList<Day> earlierDays = dbHelper.getAssignmentsFromYearRange(minYear + yearDifference, minYear - 1);
            days.addAll(0, earlierDays);
            this.notifyItemRangeInserted(0, earlierDays.size());
            minYear += yearDifference;
        }

        // handle the case where a new date is added later than the latest year on the todolist
        int yearDifferenceMax = mdy[2] - maxYear;
        if (yearDifferenceMax > 0) {
            int prevDaysSize = days.size();
            ArrayList<Day> laterDays = dbHelper.getAssignmentsFromYearRange(maxYear + 1, mdy[2]);
            days.addAll(laterDays);
            this.notifyItemRangeInserted(prevDaysSize, laterDays.size());
            maxYear += yearDifferenceMax;
        }

        int offset = 0;
        for (int j = 0; j < mdy[2] - minYear; j++) {
            if (mdy[2] - minYear < 0) {
                Log.d("position of date", "min year is greater than year of date. Something went wrong.");
            }
            offset += DBHelper.getNumberOfDaysInYear(minYear + j);
        }
        int position = offset + calendar.get(Calendar.DAY_OF_YEAR) - 1;

        Log.d("getPosition", "position: " + position + " " + mdy[0] + "/" + mdy[1] + "/" + mdy[2] + " " + days.get(position));
        return position;
    }

    public void clearDatabase() {
        dbHelper.clearDatabase();
        days = dbHelper.getAssignmentsFromYearRange(minYear, maxYear);
        notifyDataSetChanged();
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

    public void addNextYear() {
        int oldSize = days.size();
        ArrayList<Day> newDays = dbHelper.getAssignmentsFromYear(++maxYear);
        days.addAll(newDays);
        notifyItemRangeInserted(oldSize, newDays.size());
    }

    public void addPrevYear() {
        ArrayList<Day> newDays = dbHelper.getAssignmentsFromYear(--minYear);
        days.addAll(0, newDays);
        notifyItemRangeInserted(0, newDays.size());
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
            needLaterDays = true;
            //notifyItemRangeInserted(days.size(), newDays.size());
        }
        else {
            needLaterDays = false;
        }

        if (position < 50) {
            needEarlierDays = true;
        }
        else {
            needEarlierDays = false;
        }




        Day currDay = days.get(position);
        ArrayList<Assignment> assignments = currDay.getAssignments();
        NestedScrollView nestedScrollView = viewHolder.getNestedScrollView();

        TextView dateTextView = viewHolder.getTextView();
        dateTextView.setText(currDay.toString());
        //dateTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plannerFragment.createAssignment(new int[] {currDay.getCalendar().get(Calendar.MONTH), currDay.getCalendar().get(Calendar.DAY_OF_MONTH), currDay.getCalendar().get(Calendar.YEAR)});
            }
        });
        /*dateTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return onTextTouch(dateTextView, motionEvent);
            }
        });

         */
        viewHolder.getNestedLinearLayout().removeAllViews();

        if (onRunOfEmptyDays) {
            return;
        }

        // sort assignments to display in chronological order
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            assignments.sort((assignment1, assignment2) -> {
                if (assignment1.getDueHour() != assignment2.getDueHour()) {
                    return assignment1.getDueHour() - assignment2.getDueHour();
                }
                else {
                    return assignment1.getDueMin() - assignment2.getDueMin();
                }
           } );
        }

        // create TextView for each assignment and set OnClickListener that gives edit and delete options
        for (int i = 0; i < assignments.size(); i++) {
            Assignment assignment = assignments.get(i);
            LinearLayout linearLayout = new LinearLayout(nestedScrollView.getContext());
            linearLayout.setLayoutParams((new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)));
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            TextView assignmentNameTextView = new TextView(linearLayout.getContext());
            assignmentNameTextView.setText("\t\t\t\t\t\t\t\t\t\t\t\t\u2022 " + assignment.toString());
            //textView.setGravity(Gravity.CENTER_HORIZONTAL);
            assignmentNameTextView.setLayoutParams((new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)));


            // strike through text if assignment is completed
            assignmentNameTextView.setPaintFlags(assignmentNameTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            if (assignment.getCompleted()) {
                assignmentNameTextView.setPaintFlags(assignmentNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            assignmentNameTextView.setTextSize(16);

            linearLayout.addView(assignmentNameTextView);


            // go to edit or delete dialog
            assignmentNameTextView.setOnLongClickListener(new View.OnLongClickListener() {
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

            final boolean[] expanded = {false};
            // change background color on touch
            assignmentNameTextView.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return onTextTouch(assignmentNameTextView, motionEvent);
                }
            });

            if (assignment.getExpanded()) {
                expandAssignment(assignment, linearLayout, assignmentNameTextView, currDay.getDayOfWeek());
            }

            assignmentNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!assignment.getExpanded()) {
                        expandAssignment(assignment, linearLayout, assignmentNameTextView, currDay.getDayOfWeek());
                    }
                    else {
                        linearLayout.removeAllViews();
                        linearLayout.addView(assignmentNameTextView);
                        assignment.toggleExpanded();
                    }

                }


            });

            // go to edit assignment activity or delete assignment

            viewHolder.getNestedLinearLayout().addView(linearLayout);
        }

        LinearLayout dLinearLayout = viewHolder.getDividerLinearLayout();
        if (currDay.getDayOfWeek() == 1) {
            dLinearLayout.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            dLinearLayout.setVisibility(View.VISIBLE);
            TextView weekRangeText = dLinearLayout.findViewById(R.id.WeekDividerMiddleText);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(currDay.getCalendar().getTimeInMillis());
            String firstDate = (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR);
            calendar.add(Calendar.DAY_OF_YEAR, 6);
            String secondDate = String.format(Locale.getDefault(), "%d/%d/%d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR));

            weekRangeText.setText(String.format(Locale.getDefault(), "%s - %s", firstDate, secondDate));
        }
        else {
            dLinearLayout.setVisibility(View.GONE);
            dLinearLayout.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }


        //viewHolder.getNestedLinearLayout().addView(linearLayout);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return days.size();
    }

    public void expandAssignment(Assignment assignment, LinearLayout linearLayout, TextView assignmentNameTextView, int dayOfWeek) {

        // desc text view
        TextView assignmentDescText = new TextView(linearLayout.getContext());
        assignmentDescText.setText("\n" + assignment.getDescription() + "\n");
        assignmentDescText.setLayoutParams((new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)));
        assignmentDescText.setGravity(Gravity.CENTER_HORIZONTAL);

        // duetime text view
        int minute = assignment.getDueMin();
        int hour = assignment.getDueHour();
        String twoDigitMinute = Integer.toString(minute);
        if (minute < 10) {
            twoDigitMinute = String.format(Locale.getDefault(), "%d%d", 0, minute);
        }

        String morningOrNight = "am";
        int displayHour = hour;
        if (hour > 12) {
            morningOrNight = "pm";
            displayHour -= 12;
        }
        if (hour == 0) {
            displayHour = 12;
        }

        TextView assignmentDueTimeText = new TextView(linearLayout.getContext());
        assignmentDueTimeText.setText("Due at: " + displayHour + ":" + twoDigitMinute + morningOrNight);
        assignmentDueTimeText.setLayoutParams((new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)));
        assignmentDueTimeText.setGravity(Gravity.CENTER_HORIZONTAL);

        // create complete button
        Button completeButton = new Button(linearLayout.getContext());
        String buttonText = "Mark Assignment Complete";
        //completeButton.setBackgroundColor(Color.GREEN);
        if (assignment.getCompleted()) {
            buttonText = "Unmark Assignment Complete";
            //completeButton.setBackgroundColor(Color.RED);
            assignmentNameTextView.setPaintFlags(assignmentNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            assignmentNameTextView.setPaintFlags(assignmentNameTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
        completeButton.setText(buttonText);
        completeButton.setLayoutParams((new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)));
        completeButton.setGravity(Gravity.CENTER_HORIZONTAL);

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markAssignmentComplete(assignment);
                String buttonText = "Mark Assignment Complete";
                //completeButton.setBackgroundColor(Color.GREEN);
                if (assignment.getCompleted()) {
                    buttonText = "Unmark Assignment Complete";
                //    completeButton.setBackgroundColor(Color.RED);
                    assignmentNameTextView.setPaintFlags(assignmentNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                else {
                    assignmentNameTextView.setPaintFlags(assignmentNameTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
                completeButton.setText(buttonText);

                dbHelper.updateAssignment(assignment);
                Log.d("completed", "" + assignment.getCompleted());
            }
        });

        linearLayout.addView(assignmentDescText);
        linearLayout.addView(assignmentDueTimeText);
        linearLayout.addView(completeButton);

        if (dayOfWeek == 6) {
        }

        assignmentNameTextView.setBackgroundColor(Color.LTGRAY);
        assignment.toggleExpanded();
    }

    private void markAssignmentComplete(Assignment assignment) {
        assignment.setCompleted(!assignment.getCompleted());

        // update number of assignments completed
        SharedPreferences sharedPref = plannerFragment.getActivity().getSharedPreferences("com.example.smarterbadgers", Context.MODE_PRIVATE);
        int numAssignmentsCompleted = sharedPref.getInt(Assignment.COMPLETED_ASSIGNMENT_PREFERENCE_KEY, 0);
        SharedPreferences.Editor editor = sharedPref.edit();

        if (assignment.getCompleted()) {
            editor.putInt(Assignment.COMPLETED_ASSIGNMENT_PREFERENCE_KEY, numAssignmentsCompleted + 1);
        }
        else {
            editor.putInt(Assignment.COMPLETED_ASSIGNMENT_PREFERENCE_KEY, numAssignmentsCompleted - 1);
        }
        editor.apply();

        Log.d("completed assignments", "" + sharedPref.getInt(Assignment.COMPLETED_ASSIGNMENT_PREFERENCE_KEY, 0));

        dbHelper.updateAssignment(assignment);
    }

    private boolean onTextTouch(TextView assignmentNameTextView, MotionEvent motionEvent) {
        if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
            assignmentNameTextView.setBackgroundColor(Color.LTGRAY);
        } else {
            if (MotionEvent.ACTION_UP == motionEvent.getAction())  {
                assignmentNameTextView.setBackgroundColor(Color.WHITE);
            } else if (MotionEvent.ACTION_MOVE == motionEvent.getAction()) {
                //textView.setBackgroundColor(Color.WHITE);
            } else if (MotionEvent.ACTION_CANCEL == motionEvent.getAction()) {
                assignmentNameTextView.setBackgroundColor(Color.WHITE);
            }
        }
        return false;

    }
}