package com.example.smarterbadgers;


import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ListView listView;
        private final View view;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            this.view = view;
            textView = (TextView) view.findViewById(R.id.textView);
            listView = (ListView) view.findViewById(R.id.todoListView);
        }

        public TextView getTextView() {
            return textView;
        }

        public ListView getListView() {
            return listView;
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
            Log.d("view","view.isActivated(): " + view.isActivated());
            return newActivatedStatus;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param firstDay string that represents the first day to be used in RecyclerView
     *                USE FORMAT: "MM/DD/YYYY"
     * by RecyclerView.
     */
    public TodoListAdapter(String firstDay, DBHelper dbHelper, PlannerFragment plannerFragment) {
        dbHelper = dbHelper;

        String[] mdy = firstDay.split("/");
        int m = Integer.valueOf(mdy[0]);
        int d = Integer.valueOf(mdy[1]);
        int y = Integer.valueOf(mdy[2]);

        this.plannerFragment = plannerFragment;

        currYear = y;

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
        ListView listView = viewHolder.getListView();

        // show fragment for edit and delete assignment options
        listView.setOnItemClickListener(
                (AdapterView<?> parent, View view, int position, long id) -> {
                     MyArrayAdapter myArrayAdapter = (MyArrayAdapter) parent.getAdapter();

                     ViewGroup buttonGroup = new ViewGroup(view.getContext()) {
                         @Override
                         protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

                         }
                     };
                     AssignmentDialogFragment assignmentDialogFragment = new AssignmentDialogFragment();
                     assignmentDialogFragment.show(plannerFragment.getChildFragmentManager(), AssignmentDialogFragment.TAG);
        });

        // set adapter for each ViewHolder
        MyArrayAdapter myArrayAdapter = new MyArrayAdapter(viewGroup.getContext(), R.layout.assignment_list_item);
        listView.setAdapter(myArrayAdapter);

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        if (days.size() - position < 50) {
            days.addAll(this.dbHelper.getAssignmentsFromYear(++currYear));
        }

        viewHolder.getTextView().setText(days.get(position).toString());
        ArrayList<Assignment> assignments = days.get(position).getAssignments();
        PlannerActivity.logAssignments(assignments);

        MyArrayAdapter arrayAdapter = (MyArrayAdapter) viewHolder.getListView().getAdapter();
        arrayAdapter.clear(); // possibly add something to day so it doesn't have to clear and re-add unchanged datasets
        arrayAdapter.notifyDataSetChanged();
        Log.d("adapter", "position " + position + " isEmpty(): " + arrayAdapter.isEmpty());
        PlannerActivity.logAssignments(assignments);
        arrayAdapter.addAll(assignments);
        arrayAdapter.notifyDataSetChanged();
        Log.d("adapter", "" + arrayAdapter.getCount());
        //MyListAdapter currListAdapter = (MyListAdapter) currListView.getAdapter();
        //currListAdapter.setListData(assignments);
    }

    public void tryExpand(ViewHolder viewHolder, int position) {
        ArrayList<Assignment> assignments = days.get(position).getAssignments();
        if (viewHolder.getView().isActivated()) {
            for (int i = 0; i < assignments.size(); i++) {
                Assignment currAssignment = assignments.get(i);
            //    TextView currView = new TextView(viewHolder.linearLayout.getContext());
            //    currView.setText(currAssignment.getName() + ":\n\t" + currAssignment.getDescription() + "\n");
            //    viewHolder.getLinearLayout().addView(currView);
            }
        }
        else {
            //viewHolder.linearLayout.removeAllViews();
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return days.size();
    }

    public class MyArrayAdapter extends ArrayAdapter<Assignment> {

        public MyArrayAdapter(@NonNull Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.assignment_list_item, parent, false);
            }

            TextView textView = convertView.findViewById(R.id.assignmentListTextView);
            textView.setText(getItem(position).toString());

            //return super.getView(position, convertView, parent);
            return convertView;
        }

        @Override
        public void clear() {
            super.clear();
        }
    }

    public class MyDataSetObserver extends DataSetObserver {

        public MyDataSetObserver() {

        }

        @Override
        public void onChanged() {

        }
    }

}

