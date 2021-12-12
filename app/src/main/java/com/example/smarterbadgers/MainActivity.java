package com.example.smarterbadgers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Intent for testing TimerActivity
//        Intent intent = new Intent(this, TimerActivity.class);
//        startActivity(intent);

        //Intent intent = new Intent(this, PlannerActivity.class);
        //startActivity(intent);
        BottomNavigationView bottomNavBar = findViewById(R.id.bottomnav);

        // Until other fragments are added to the bottomnavFunction onNavigationItemSelected(),
        // this will crash the app if you choose anything other than Planner
        bottomNavBar.setOnItemSelectedListener(bottomnavFunction);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(PlannerFragment.ASSIGNMENT_NOTIFICATION_CHANNEL_ID, PlannerFragment.ASSIGNMENT_NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new TimerFragment()).commit();
    }
    private NavigationBarView.OnItemSelectedListener bottomnavFunction = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.planner:
                    fragment = new PlannerFragment();
                    break;
                case R.id.timer:
                    fragment = new TimerFragment();
                    break;
                case R.id.profile:
                    fragment = new ProfileFragment();
                    break;
//                case R.id.settings:
//                    fragment = new SettingFragment();
//                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            return false;
        }
    };
}