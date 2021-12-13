package com.example.smarterbadgers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class AchievementDialog extends AppCompatDialogFragment {
    private ImageView testone;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("com.example.smarterbadgers", Context.MODE_PRIVATE);
        int recordedTime = sharedPreferences.getInt("timestudied", 0);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(recordedTime >= 1) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Calendar c = Calendar.getInstance();
            String date = sdf.format(c.getTime());
            builder.setTitle("Study 25 Minutes!")
                    .setMessage("Date Obtained: " +  date + "\n\nCongratulations!!!")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
        }else{
            builder.setTitle("Study 25 Minutes!")
                    .setMessage("Date Obtained: not yet obtained")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
        }
        return builder.create();
    }
}
