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
 * create an instance of this fragment.
 */
public class AssignmentDialogFragment extends AppCompatDialogFragment {

    public static final String EDIT_ASSIGNMENT = "edit_assignment";
    public static final String DELETE_ASSIGNMENT = "delete_assignment";
    private Button editButton;
    private Button deleteButton;
    protected AppCompatDialog dialog;


    public AssignmentDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public @NonNull
    Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = new AppCompatDialog(requireContext());
        dialog.setCanceledOnTouchOutside(true);
        dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
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