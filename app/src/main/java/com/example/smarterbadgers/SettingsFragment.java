package com.example.smarterbadgers;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static String usernameKey;
    public static String userString = "Badger";
    TextView username;
//    SQLiteDatabase sqLiteDatabase = getActivity().openOrCreateDatabase("assignments",Context.MODE_PRIVATE, null);
//    private DBHelper notificationHelper = new DBHelper(sqLiteDatabase);


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        usernameKey = "username";

        EditTextPreference enteredUser = findPreference("username");

        try {
            userString = enteredUser.getText().toString();
        } catch (NullPointerException e) {
            userString = "Badger";
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.edit().putString("username", userString).apply();
        Log.i("this is the username", userString);

       // ArrayList<Integer> notifID = notificationHelper.getAssignmentNotificationIDs();




    }

//    SwitchPreferenceCompat notifToggle = (SwitchPreferenceCompat) findPreference("notification");
//
//    public SwitchPreferenceCompat getNotifToggle() {
//        return notifToggle;
//    }
//    notifToggle.setOnCheckedChangeListener
//    notifToggle

//    SettingsFragment test;
//    Preference test =



}