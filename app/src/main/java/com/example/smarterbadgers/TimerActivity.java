package com.example.smarterbadgers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TimerActivity extends AppCompatActivity {

    private TextView timerTextView;
    private Button startButton;
    private Button stopButton;

    private CountDownTimer countDownTimer;
    private long timeLeftMilliseconds = 600000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        timerTextView = findViewById(R.id.timer);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

    }

    public void startOnClick(View view) {
        startButton.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.VISIBLE);
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
                timerTextView.setText("10:00");
            }
        }.start();
    }


    public void stopOnClick(View view) {
        countDownTimer.cancel();
        timerTextView.setText("10:00");
        startButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
    }
}