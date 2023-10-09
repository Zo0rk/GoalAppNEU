package com.example.goalapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;



public class Stapel_erstellen extends AppCompatActivity implements View.OnClickListener {

    private EditText name;
    private EditText beschreibung;

    private Button zurueck;
    private Button speichern;

    private datenBankManager db;
    private Intent i;
    private Intent i2;
    private Intent i3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stapel_erstellen);

        db = new datenBankManager(this);
        i = new Intent(this, KarteErstellen.class);
        i2 = getIntent();
        i3 = new Intent(this, Setuebersicht.class);

        name = (EditText) findViewById(R.id.nameEdit);
        beschreibung = (EditText) findViewById(R.id.beschreibungEdit);

        zurueck = (Button) findViewById(R.id.zurueck);
        speichern = (Button) findViewById(R.id.speichern);

        zurueck.setOnClickListener(this);
        speichern.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == speichern){
            if(name.getText().toString() != null || name.getText().toString().trim() != ""){
                if(beschreibung.getText().toString() != null || beschreibung.getText().toString().trim() != ""){
                    int setID = i2.getIntExtra("SET_ID",0);
                    db.insertStapel(name.getText().toString(), beschreibung.getText().toString(),db.getSetFarbe(setID),0);
                    i.putExtra("STAPEL_ID",db.getMaxStapelID());
                    startActivity(i);
                }
            }
        }
        if(v == zurueck){

        }
    }
}