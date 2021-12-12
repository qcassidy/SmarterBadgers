package com.example.smarterbadgers;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        achieve = (ImageView) getView().findViewById(R.id.imageView8);
//        achieve.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v){
//                openDialog();
//            }
//        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        //View view2 = inflater.inflate(R.layout.fragment_profile, container, false);
        achieveOne = (ImageView) view.findViewById(R.id.imageView8);
        achieveTwo = (ImageView) view.findViewById(R.id.imageView9);
        achieveThree = (ImageView) view.findViewById(R.id.imageView);
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
}