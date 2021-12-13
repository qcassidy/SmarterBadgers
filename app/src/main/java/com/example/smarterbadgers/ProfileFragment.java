package com.example.smarterbadgers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.EditTextPreference;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }
    private ImageView achieveOne;
    private ImageView achieveTwo;
    private ImageView achieveThree;
    private ImageView achieveFour;
    private ImageView achieveFive;
    private ImageView achieveSix;

    TextView username;
    TextView timer;
    TextView assignment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        //View view2 = inflater.inflate(R.layout.fragment_profile, container, false);

        username = (TextView) view.findViewById(R.id.userText);
        timer = (TextView) view.findViewById(R.id.time);
        assignment = (TextView) view.findViewById(R.id.assignFinish);
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("com.example.smarterbadgers", Context.MODE_PRIVATE);
        int recordedTime = sharedPreferences.getInt("timestudied", 0);
        int finishedAssign = sharedPreferences.getInt(Assignment.COMPLETED_ASSIGNMENT_PREFERENCE_KEY, 0);
        String timeString = String.valueOf(recordedTime);

        String editUser = sharedPreferences.getString("name", "Badger");
        Log.i("this is  on profile", editUser);

//        EditTextPreference enteredUser = (EditTextPreference) findPreference("username");
//        String userString = enteredUser.getText().toString();

        assignment.setText("Assignments Finished: "+ finishedAssign);
        timer.setText("Total Study Time: " + timeString + " minutes");
        //Log.i("number", "recordedTime " + recordedTime);
        username.setText("Hello " + SettingsFragment.userString + "!");


        achieveOne = (ImageView) view.findViewById(R.id.imageView8);
        achieveTwo = (ImageView) view.findViewById(R.id.imageView9);
        achieveThree = (ImageView) view.findViewById(R.id.imageView);
        achieveFour = (ImageView) view.findViewById(R.id.imageView11);
        achieveFive = (ImageView) view.findViewById(R.id.imageView12);
        achieveSix = (ImageView) view.findViewById(R.id.imageView10);

        //username = (TextView) view.findViewById(R.id.textView);
        //username.setText("Hello "+ "");

        achieveOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                openDialog();
            }
        });
        achieveTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                openDialogTwo();
            }
        });
        achieveThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                openDialogThree();
            }
        });
        achieveFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                openDialogFour();
            }
        });
        achieveFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                openDialogFive();
            }
        });
        achieveSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                openDialogSix();
            }
        });
        return view;
    }

    public void openDialog(){
        AchievementDialog exampleDialog = new AchievementDialog();
        exampleDialog.show(getActivity().getSupportFragmentManager(), "example dialog");
    }

    public void openDialogTwo(){
        DialogTwo achievementTwo = new DialogTwo();
        achievementTwo.show(getActivity().getSupportFragmentManager(), "example dialog");
    }

    public void openDialogThree(){
        DialogThree achievementThree = new DialogThree();
        achievementThree.show(getActivity().getSupportFragmentManager(), "example dialog");
    }
    public void openDialogFour(){
        DialogFour achievementFour = new DialogFour();
        achievementFour.show(getActivity().getSupportFragmentManager(), "example dialog");
    }
    public void openDialogFive(){
        DialogFive achievementFive = new DialogFive();
        achievementFive.show(getActivity().getSupportFragmentManager(), "example dialog");
    }
    public void openDialogSix(){
        DialogSix achievementSix = new DialogSix();
        achievementSix.show(getActivity().getSupportFragmentManager(), "example dialog");
    }
}