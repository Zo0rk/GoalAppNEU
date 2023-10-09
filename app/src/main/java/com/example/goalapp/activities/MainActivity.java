package com.example.goalapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goalapp.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button newButton;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newButton = findViewById(R.id.newButton);
        newButton.setOnClickListener(this);

        i = new Intent(this, SetErzeugen.class);
    }

    @Override
    public void onClick(View v) {
        if(v == newButton){
            startActivity(i);
        }
    }
}