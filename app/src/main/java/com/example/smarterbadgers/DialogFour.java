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
import java.util.Date;


public class DialogFour extends AppCompatDialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("com.example.smarterbadgers", Context.MODE_PRIVATE);
        int numAssign = sharedPreferences.getInt("completed_assignment_preference_key", 0);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(numAssign >= 1) {

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Calendar c = Calendar.getInstance();
            String date = sdf.format(c.getTime());            builder.setTitle("Finished 1 Assignment!")
                    .setMessage("Date Obtained:\n " +  date + "\n\nYou've unlocked stinky cheese!\n\nCongratulations!!!")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
        }else{
            builder.setTitle("Finished 1 Assignment!")
                    .setMessage("Date Obtained: not yet obtained\n\nFinish 1 assignment to unlock stinky cheese!")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
        }
        return builder.create();
    }
}

