package com.example.smarterbadgers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class profile extends AppCompatActivity {

    private ImageView achieve;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        achieve = findViewById(R.id.imageView8);
        achieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                openDialog();
            }
        });


    }
    public void openDialog(){
        AchievementDialog exampleDialog = new AchievementDialog();


        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }
}