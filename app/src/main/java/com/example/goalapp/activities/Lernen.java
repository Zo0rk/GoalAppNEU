package com.example.goalapp.activities;

import static java.lang.String.valueOf;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
    private TextView progressText;
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

    private int randomKartenID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lernen);

        db = new DatenBankManager(this);
        i = getIntent();
        stapelID = i.getIntExtra("STAPEL_ID",0);
        setID = i.getIntExtra("SET_ID",0);
        int color = i.getIntExtra("SET_COLOR",0);

        KartenVerwaltung kv = new KartenVerwaltung(stapelID,setID,this);

        stapelName = findViewById(R.id.lernenKapitel);
        progressBar = findViewById(R.id.mainProgressBar);
        status = findViewById(R.id.status);
        progressText = findViewById(R.id.progressText);
        frage = findViewById(R.id.frageView);
        antwortAnzeigen = findViewById(R.id.antwortAnzeigen);
        unsicher = findViewById(R.id.unsicher);
        mittel = findViewById(R.id.mittel);
        sicher = findViewById(R.id.sicher);

        unsicher.setVisibility(View.INVISIBLE);
        mittel.setVisibility(View.INVISIBLE);
        sicher.setVisibility(View.INVISIBLE);

        antwortAnzeigen.setOnClickListener(this);
        unsicher.setOnClickListener(this);
        mittel.setOnClickListener(this);
        sicher.setOnClickListener(this);

        // Hier wird die karte per Zufallsalgorithmus gewählt......................................................................

        randomKartenID = kv.getRandomKartenID();

        // Hier wird die Ansicht der ausgewählten karte aufgebaut..................................................................

        stapelName.setText(db.getStapelName(stapelID,setID).toString()); // StapelName Methode erstellen....
        progressBar.setProgress(db.getStapelStatus(stapelID,setID));
        progressBar.setProgressTintList(ColorStateList.valueOf(color));
        progressText.setText(" ~ " + valueOf(db.getStapelStatus(stapelID,setID))+"%");
        status.setText("< " + valueOf(db.getAnzSichereKartenInStapel(stapelID,setID)) + " / " + db.getAnzKartenInStapel(stapelID,setID) + " Sicher beantwortet >");
        frage.setText(db.getFrage(setID,stapelID, randomKartenID));


    }

    private void zeigeAntwort(int kartenID){
        Dialog antw = new Dialog(this);
        antw.setContentView(R.layout.antwort_popup); // Das Layout für das Popup-Fenster setzen

        // Hier können Sie auf die Views im Popup-Fenster zugreifen und deren Inhalt festlegen
        TextView antwortText = antw.findViewById(R.id.antwortText);
        // Hier können Sie den Text oder Inhalt ändern, basierend auf der KartenID oder anderen Informationen
        antwortText.setText(db.getAntwort(setID,stapelID,kartenID));

        antw.show(); // Das Popup-Fenster anzeigen
        unsicher.setVisibility(View.VISIBLE);
        mittel.setVisibility(View.VISIBLE);
        sicher.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if(v == antwortAnzeigen){
            zeigeAntwort(randomKartenID);
        }
        if(v == unsicher){
            db.setStatus(setID,stapelID,randomKartenID,1);
            progressBar.setProgress(db.getStapelStatus(stapelID,setID),true);
            progressBar.setBackgroundColor(1);
            this.recreate();
        }
        if(v == mittel){
            db.setStatus(setID,stapelID,randomKartenID,2);
            progressBar.setProgress(db.getStapelStatus(stapelID,setID),true);
            this.recreate();
        }
        if(v == sicher){
            db.setStatus(setID,stapelID,randomKartenID,3);
            progressBar.setProgress(db.getStapelStatus(stapelID,setID),true);
            this.recreate();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("BACK","BACK");
        db.getStapelStatus(stapelID,setID); // Damit beim zurückkehrern auch wieder die Progressbar stimmt...
        db.setSetProgress(setID);
        Log.d("SET------",valueOf(setID));
    }

}
