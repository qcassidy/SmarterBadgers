package com.example.smarterbadgers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TimerActivity extends AppCompatActivity {

    private TextView timerTextView;
    private Button startButton;
    private Button stopButton;
    private Button breakButton;
    private Button endBreakButton;
    private TextView enterMinTextView;
    private EditText editTextNumber;

    private CountDownTimer countDownTimer;
    private long timeLeftMilliseconds = 600000;
    private long timeLeftBreakMilliseconds = 300000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        timerTextView = findViewById(R.id.timer);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        breakButton = findViewById(R.id.breakButton);
        endBreakButton = findViewById(R.id.endBreakButton);
        enterMinTextView = findViewById(R.id.enterMinutes);
        editTextNumber = findViewById(R.id.editTextNumber);

    }

    public void startOnClick(View view) {
        startButton.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.VISIBLE);
        breakButton.setVisibility(View.VISIBLE);
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
            }

            @Override
            public void onFinish() {
                startButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.INVISIBLE);
                breakButton.setVisibility(View.INVISIBLE);
                timerTextView.setVisibility(View.INVISIBLE);
                enterMinTextView.setVisibility(View.VISIBLE);
                editTextNumber.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    public void stopOnClick(View view) {
        countDownTimer.cancel();
        timerTextView.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
        breakButton.setVisibility(View.INVISIBLE);
        enterMinTextView.setVisibility(View.VISIBLE);
        editTextNumber.setVisibility(View.VISIBLE);
    }

    public void breakOnClick(View view) {
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
            }

            @Override
            public void onFinish() {
                timeLeftBreakMilliseconds = 300000;
                breakButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.VISIBLE);
                endBreakButton.setVisibility(View.INVISIBLE);
                startTimer();
            }
        }.start();
    }

    public void endBreakOnClick(View view) {
        countDownTimer.cancel();
        timeLeftBreakMilliseconds = 300000;
        breakButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.VISIBLE);
        endBreakButton.setVisibility(View.INVISIBLE);
        startTimer();
    }

}