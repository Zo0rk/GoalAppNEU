package com.example.goalapp.activities;

import static java.lang.String.valueOf;

import android.content.Intent;
import android.os.Bundle;
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
        progressText = findViewById(R.id.progressText);
        frage = findViewById(R.id.frageView);
        antwortAnzeigen = findViewById(R.id.antwortAnzeigen);
        unsicher = findViewById(R.id.unsicher);
        mittel = findViewById(R.id.mittel);
        sicher = findViewById(R.id.sicher);

        // Hier wird die karte per Zufallsalgorithmus gewählt......................................................................

        int randomStufe;     // Wählt die Stufe aus 1 = Schwer, 3 = Sicher -> 1 und 2 werden wahrscheinlicher sein.
        int randomEintrag;  // Wählt einen zufälligen Eintrag aus der gewählten Stufe.

        randomStufe = bestimmeRandomStufe();

        // Hier wird die Ansicht der ausgewählten karte aufgebaut..................................................................

        stapelName.setText(db.getStapelName(stapelID,setID).toString()); // StapelName Methode erstellen....
        progressBar.setProgress(db.getStapelStatus(stapelID,setID),true);
        progressText.setText(" ~ " + valueOf(db.getStapelStatus(stapelID,setID))+"%");
        status.setText("< " + valueOf(db.getAnzSichereKartenInStapel(stapelID,setID)) + " Sicher beantwortet >");



    }

    public int bestimmeRandomStufe(){
        int[] array = {1,1,1,2,2,3};
        int auswahl = (int) ((Math.random() * (6 - 1)) + 1);
        return array[auswahl];
    }

    public int bestimmeRandomEintrag(){
        return 0;
    }

    @Override
    public void onClick(View v) {

    }
}
