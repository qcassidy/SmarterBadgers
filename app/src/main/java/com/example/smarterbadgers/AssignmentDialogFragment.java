package com.example.smarterbadgers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AssignmentDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AssignmentDialogFragment extends AppCompatDialogFragment {

    public static final String EDIT_ASSIGNMENT = "edit_assignment";
    public static final String DELETE_ASSIGNMENT = "delete_assignment";
    private Button editButton;
    private Button deleteButton;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AssignmentDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AssignmentDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AssignmentDialogFragment newInstance(String param1, String param2) {
        AssignmentDialogFragment fragment = new AssignmentDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    @Override
    public @NonNull
    Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new AppCompatDialog(requireContext());
        dialog.setCanceledOnTouchOutside(true);
        dialog.create();
        return dialog;
    }

    public static String TAG = "AssignmentDialog";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("DialogFragment", "inflating view");
        // Inflate the layout for this fragment
        View inflated = inflater.inflate(R.layout.fragment_assignment_dialog, container, false);

        editButton = inflated.findViewById(R.id.EditAssignmentButton);
        deleteButton = inflated.findViewById(R.id.DeleteAssignmentButton);
        editButton.setOnClickListener(this::onEditButtonClick);
        deleteButton.setOnClickListener(this::onDeleteButtonClick);


        return inflated;
    }

    public void onEditButtonClick(View view) {
        Bundle result = new Bundle();
        result.putString("bundleKey", EDIT_ASSIGNMENT);
        getParentFragmentManager().setFragmentResult("requestKey", result);
    }

    private void onDeleteButtonClick(View view) {
        Bundle result = new Bundle();
        result.putString("bundleKey", DELETE_ASSIGNMENT);
        getParentFragmentManager().setFragmentResult("requestKey", result);
    }
}