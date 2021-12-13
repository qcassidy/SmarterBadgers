package com.example.smarterbadgers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimerFragment extends Fragment {
    TimerFragment timerFragment;
    View view;

    private TextView timerTextView;
    private Button startButton;
    private Button stopButton;
    private Button breakButton;
    private Button endBreakButton;
    private Button libraryButton;
    private TextView enterMinTextView;
    private EditText editTextNumber;

    private CountDownTimer countDownTimer;
    private long timeLeftMilliseconds = 600000;
    private long timeLeftBreakMilliseconds = 300000;
    private int breaksRemaining;
    private int secStudied = 0;
    private boolean running = false;

    private NotificationManagerCompat notificationManager;



    public TimerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TimerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimerFragment newInstance(String param1, String param2) {
        TimerFragment fragment = new TimerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        breaksRemaining = ((MainActivity)getActivity()).getBreaks();
        notificationManager = NotificationManagerCompat.from(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_timer, container, false);
        timerFragment = new TimerFragment();


        timerTextView = view.findViewById(R.id.timer);
        startButton = view.findViewById(R.id.startButton);
        startButton.setOnClickListener(this::startOnClick);

        stopButton = view.findViewById(R.id.stopButton);
        stopButton.setOnClickListener(this::stopOnClick);

        breakButton = view.findViewById(R.id.breakButton);
        breakButton.setText(String.format("Breaks Remaining: %d",breaksRemaining));
        breakButton.setOnClickListener(this::breakOnClick);

        endBreakButton = view.findViewById(R.id.endBreakButton);
        endBreakButton.setOnClickListener(this::endBreakOnClick);

        libraryButton = view.findViewById(R.id.libraryButton);
        libraryButton.setOnClickListener(this::goToMap);

        enterMinTextView = view.findViewById(R.id.enterMinutes);
        editTextNumber = view.findViewById(R.id.editTextNumber);

        return view;
    }

    public void goToMap(View view) {
        Intent intent = new Intent(getActivity(), MapActivity.class);
        startActivity(intent);
    }

    public void startOnClick(View view) {
        startButton.setVisibility(View.INVISIBLE);
        libraryButton.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.VISIBLE);
        if(breaksRemaining > 0) {
            breakButton.setVisibility(View.VISIBLE);
        }
        timeLeftMilliseconds = (long)(Integer.parseInt(editTextNumber.getText().toString())) * 60000;
        int min = (int) timeLeftMilliseconds/1000 /60;
        int sec = (int) timeLeftMilliseconds/1000 %60;
        String timeLeftString = String.format("%02d:%02d", min, sec);
        timerTextView.setText(timeLeftString);
        enterMinTextView.setVisibility(View.INVISIBLE);
        editTextNumber.setVisibility(View.INVISIBLE);
        timerTextView.setVisibility(View.VISIBLE);
        startTimer();
    }

    public void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftMilliseconds, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftMilliseconds = l;
                int min = (int) timeLeftMilliseconds/1000 /60;
                int sec = (int) timeLeftMilliseconds/1000 %60;
                String timeLeftString = String.format("%02d:%02d", min, sec);
                timerTextView.setText(timeLeftString);
                secStudied++;
                running = true;
            }

            @Override
            public void onFinish() {
                startButton.setVisibility(View.VISIBLE);
                libraryButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.INVISIBLE);
                breakButton.setVisibility(View.INVISIBLE);
                timerTextView.setVisibility(View.INVISIBLE);
                enterMinTextView.setVisibility(View.VISIBLE);
                editTextNumber.setVisibility(View.VISIBLE);
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.example.smarterbadgers",Context.MODE_PRIVATE);
                int sharedint = sharedPreferences.getInt("timestudied", 0);
                sharedint += (secStudied/60);
                secStudied %= 60;
                sharedPreferences.edit().putInt("timestudied",sharedint).apply();
                running = false;
            }
        }.start();
    }

    public void stopOnClick(View view) {
        int min = (int)timeLeftMilliseconds/60000;
        int sec = (int)timeLeftMilliseconds/1000%60;
        String message = String.format("You only have %02d:%02d left!",min,sec);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Are you sure you want to stop studying?")
                .setMessage(message)
                .setNeutralButton("Continue Studying", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("End Timer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        countDownTimer.cancel();
                        timerTextView.setVisibility(View.INVISIBLE);
                        startButton.setVisibility(View.VISIBLE);
                        libraryButton.setVisibility(View.VISIBLE);
                        stopButton.setVisibility(View.INVISIBLE);
                        breakButton.setVisibility(View.INVISIBLE);
                        enterMinTextView.setVisibility(View.VISIBLE);
                        editTextNumber.setVisibility(View.VISIBLE);
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.example.smarterbadgers",Context.MODE_PRIVATE);
                        int sharedint = sharedPreferences.getInt("timestudied", 0);
                        sharedint += (secStudied/60);
                        secStudied %= 60;
                        sharedPreferences.edit().putInt("timestudied",sharedint).apply();
                        running = false;
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void breakOnClick(View view) {
        if(breaksRemaining == 0){
            return;
        }
        breaksRemaining --;
        ((MainActivity)getActivity()).setBreaks(breaksRemaining);
        breakButton.setText(String.format("Breaks Remaining: %d",breaksRemaining));
        countDownTimer.cancel();
        breakButton.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
        endBreakButton.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(timeLeftBreakMilliseconds, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftBreakMilliseconds = l;
                int min = (int) timeLeftBreakMilliseconds/1000/60;
                int sec = (int) timeLeftBreakMilliseconds/1000%60;
                String timeLeftBreakString = String.format("%02d:%02d", min, sec);
                timerTextView.setText(timeLeftBreakString);
                running = false;
            }

            @Override
            public void onFinish() {
                timeLeftBreakMilliseconds = 300000;
                breakButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.VISIBLE);
                endBreakButton.setVisibility(View.INVISIBLE);
                startTimer();
                running = true;
            }
        }.start();
    }


    public void endBreakOnClick(View view) {
        countDownTimer.cancel();
        timeLeftBreakMilliseconds = 300000;
        if (breaksRemaining > 0) {
            breakButton.setVisibility(View.VISIBLE);
        }
        stopButton.setVisibility(View.VISIBLE);
        endBreakButton.setVisibility(View.INVISIBLE);
        running = true;
        startTimer();
    }

    @Override
    public void onStop() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.example.smarterbadgers",Context.MODE_PRIVATE);
        int sharedint = sharedPreferences.getInt("timestudied", 0);
        sharedint += (secStudied/60);
        secStudied %= 60;
        sharedPreferences.edit().putInt("timestudied",sharedint).apply();

        if (running == true) {
            Intent activityIntent = new Intent(getActivity(), MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(getActivity(), 0,
                    activityIntent, 0);
            Notification notification = new NotificationCompat.Builder(getActivity(),"timer_channel")
                    .setContentTitle("You left while the timer was running!")
                    .setContentText("Come back and study more!")
                    .setSmallIcon(R.drawable.ic_timer)
                    .setColor(Color.RED)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build();
            notificationManager.notify(1,notification);
        }
        super.onStop();
    }
}