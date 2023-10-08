package com.example.goalapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class KarteErstellen extends AppCompatActivity implements View.OnClickListener {

    String frage;
    String antwort;
    int stapel;     // Muss realisiert werden!
    int status;     // Zu beginn immer 1 -> [1-SCHLECHT] .. [2-MITTEL] .. [3-SICHER]

    // DATENBANK......................................
    public datenBankManager db;

    // UI-ELEMENTE....................................
    private Button fertig;
    private Button speichern;
    private EditText frageEdit;
    private EditText antwortEdit;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karte_erstellen);

        fertig = findViewById(R.id.fertig);
        speichern = findViewById(R.id.speichern);
        antwortEdit = findViewById(R.id.antwortEdit);
        frageEdit = findViewById(R.id.frageEdit);

        toast = Toast.makeText(this, "✓ Karte hinzugefügt!", Toast.LENGTH_SHORT);

        fertig.setOnClickListener(this);
        speichern.setOnClickListener(this);

        db = new datenBankManager(this);
    }

    @Override
    public void onClick(View v) {
        if(v == speichern){

            frage = frageEdit.getText().toString();
            antwort = antwortEdit.getText().toString();

            if(!(frage == null || antwort == null || frage.trim().equals("") || antwort.trim().equals(""))){
                db.insertKarte(frage,antwort,1,1);
                toast.show();
                antwortEdit.getText().clear();
                frageEdit.getText().clear();
            }
        }

        if(v == fertig){
            // Muss noch implementiert werden -> zurück auf die Übersicht...
        }
    }
}