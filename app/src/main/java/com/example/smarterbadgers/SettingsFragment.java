package com.example.smarterbadgers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import org.w3c.dom.Text;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static String usernameKey;
    public static String userString = "Badger";
    TextView username;

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

    }


}