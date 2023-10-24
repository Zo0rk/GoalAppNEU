package com.example.goalapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goalapp.R;
import com.example.goalapp.database.DatenBankManager;

public class KarteErstellenActivity extends AppCompatActivity implements View.OnClickListener {

    private String frage;
    private String antwort;
    private int stapel_id = 9999;     // Zu welchem Stapel gehören die karten?
    private int set_id = 9999;
    private int status;     // Zu beginn immer 1 -> [1-SCHLECHT] .. [2-MITTEL] .. [3-SICHER]

    // DATENBANK......................................
    private DatenBankManager db;

    // UI-ELEMENTE....................................
    private Button fertig;
    private Button hinzufuegen;
    private EditText frageEdit;
    private EditText antwortEdit;
    private Toast toast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karte_erstellen);
        stapel_id = getIntent().getIntExtra("STAPEL_ID", 9999); //nicht 0 sonst kommt es evtl in den Falschen Stapel
        set_id = getIntent().getIntExtra("SET_ID", 9999);

        fertig = findViewById(R.id.fertig);
        hinzufuegen = findViewById(R.id.speichern);
        antwortEdit = findViewById(R.id.antwortEdit);
        frageEdit = findViewById(R.id.frageEdit);

        fertig.setOnClickListener(this);
        hinzufuegen.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if(v == hinzufuegen){

            frage = frageEdit.getText().toString();
            antwort = antwortEdit.getText().toString();

            if(!(frage == null || antwort == null || frage.trim().equals("") || antwort.trim().equals(""))){
                DatenBankManager db = new DatenBankManager(this);
                db.insertKarte(frage, antwort, stapel_id, set_id,1);

                toast = Toast.makeText(this, "✓ Karte hinzugefügt! ", Toast.LENGTH_SHORT);
                toast.show();

                antwortEdit.getText().clear();
                frageEdit.getText().clear();
                startActivity(new Intent(this, KarteErstellenActivity.class)
                        .putExtra("SET_ID", set_id).putExtra("STAPEL_ID", stapel_id));
                finish();
            }
        }

        if(v == fertig){
            // Muss noch implementiert werden -> zurück auf die Übersicht...
            finish();
        }
    }
}