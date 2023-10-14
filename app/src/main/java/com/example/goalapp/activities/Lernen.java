package com.example.goalapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goalapp.R;
import com.example.goalapp.database.DatenBankManager;

public class Lernen extends AppCompatActivity implements View.OnClickListener{

    private TextView stapelName;
    private ProgressBar progressBar;
    private TextView status;
    private TextView frage;
    private Button antwortAnzeigen;
    private Button unsicher;
    private Button mittel;
    private Button sicher;

    // DB-Zugriffe usw................................................................................................................
    private DatenBankManager db;
    private int stapelID;
    private int setID;
    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lernen);

        db = new DatenBankManager(this);
        i = getIntent();
        stapelID = i.getIntExtra("STAPEL_ID",0);
        setID = i.getIntExtra("SET_ID",0);

        stapelName = findViewById(R.id.lernenKapitel);
        progressBar = findViewById(R.id.mainProgressBar);
        status = findViewById(R.id.status);
        frage = findViewById(R.id.frageView);
        antwortAnzeigen = findViewById(R.id.antwortAnzeigen);
        unsicher = findViewById(R.id.unsicher);
        mittel = findViewById(R.id.mittel);
        sicher = findViewById(R.id.sicher);

        stapelName.setText(db.getStapelName(stapelID,setID).toString()); // StapelName Methode erstellen....


    }

    @Override
    public void onClick(View v) {

    }
}
