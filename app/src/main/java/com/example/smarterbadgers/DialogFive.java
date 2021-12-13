package com.example.smarterbadgers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class DialogFive extends AppCompatDialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("com.example.smarterbadgers", Context.MODE_PRIVATE);
        int numAssign = sharedPreferences.getInt("completed_assignment_preference_key", 0);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(numAssign >= 10) {

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Calendar c = Calendar.getInstance();
            String date = sdf.format(c.getTime());
            builder.setTitle("Finished 10 Assignments!")
                    .setMessage("Date Obtained:\n " +  date + "\n\nYou've unlocked cheesy pizza!\n\nCongratulations!!!")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
        }else{
            builder.setTitle("Finished 10 Assignments!")
                    .setMessage("Date Obtained: not yet obtained\n\nFinish 10 assignments to unlock cheesy pizza!")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
        }
        return builder.create();
    }
}

