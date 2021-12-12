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
    TextView username;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        usernameKey = "username";

        EditTextPreference enteredUser = findPreference("username");
        String userString = enteredUser.getText().toString();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.edit().putString("name", userString).apply();
        Log.i("tag", userString);


//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.example.smarterbadgers", Context.MODE_PRIVATE);
//        sharedPreferences.edit().putString("username", user).apply();

    }

    //@Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_profile, container, false);
////        username = (TextView) view.findViewById(R.id.textView);
////        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.example.smarterbadgers", Context.MODE_PRIVATE);
////        String editUser = sharedPreferences.getString("username", "");
////        username.setText("Welcome " + editUser + "!");
//
//        return view;
//    }
}