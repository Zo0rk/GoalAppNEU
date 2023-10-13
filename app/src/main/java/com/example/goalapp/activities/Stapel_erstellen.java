package com.example.goalapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goalapp.R;
import com.example.goalapp.database.DatenBankManager;


public class Stapel_erstellen extends AppCompatActivity implements View.OnClickListener {

    private EditText name;
    private EditText beschreibung;
    private Button zurueck;
    private Button speichern;
    private DatenBankManager db;
    private Intent i;
    private Intent i2;
    int setID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stapel_erstellen);

        Intent intent = getIntent();
        setID = intent.getIntExtra("SET_ID", 0);

        db = new DatenBankManager(this);
        i = new Intent(this, KarteErstellenActivity.class);
        i2 = getIntent();

        name = findViewById(R.id.nameEdit);
        beschreibung = findViewById(R.id.beschreibungEdit);

        zurueck = findViewById(R.id.zurueck);
        speichern =  findViewById(R.id.speichern);

        zurueck.setOnClickListener(this);
        speichern.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == speichern){
            if(name.getText().toString() != null || name.getText().toString().trim() != ""){
                if(beschreibung.getText().toString() != null || beschreibung.getText().toString().trim() != ""){
                    int setID = i2.getIntExtra("SET_ID",0);
                    db.insertStapel(name.getText().toString(), beschreibung.getText().toString(),db.getSetFarbe(setID), 0,setID);//setFarbe vlt über intent mitgeben
//                    i.putExtra("STAPEL_ID",db.getMaxStapelID());
//                    startActivity(i);
                    finish();
                }
            }
        }
        if(v == zurueck){
            finish();
        }
    }
}
