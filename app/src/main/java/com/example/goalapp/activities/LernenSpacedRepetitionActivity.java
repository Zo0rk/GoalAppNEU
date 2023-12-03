package com.example.goalapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.goalapp.models.Karte;
import com.example.goalapp.R;
import com.example.goalapp.database.DatenBankManager;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class LernenSpacedRepetitionActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    private TextView textViewQuestion;
    private TextView textViewAnswer;
    private Button buttonShowAnswer;
    private LinearLayout buttonsContainer;
    private ImageButton buttonSettings;
    private int stapelID;
    private int setID;
    private int aktuellekarteID;
    private DatenBankManager datenBankManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lernen_spaced_repetition);

        stapelID = getIntent().getIntExtra("STAPEL_ID",0);
        setID = getIntent().getIntExtra("SET_ID",0);
        int color = getIntent().getIntExtra("SET_COLOR",0);
        datenBankManager = new DatenBankManager(this);
        // Verknüpfung der Views mit den entsprechenden IDs aus dem Layout
        textViewQuestion = findViewById(R.id.textViewQuestion);
        textViewAnswer = findViewById(R.id.textViewAnswer);
        buttonShowAnswer = findViewById(R.id.buttonShowAnswer);
        buttonsContainer = findViewById(R.id.buttonsContainer);
        buttonSettings = findViewById(R.id.buttonSettings);
        // Buttons für Bewertung der Karte
        Button buttonAgain = findViewById(R.id.buttonAgain);
        Button buttonHard = findViewById(R.id.buttonHard);
        Button buttonGood = findViewById(R.id.buttonGood);
        Button buttonEasy = findViewById(R.id.buttonEasy);

        // Setze initialen Text für Frage und Antwort
        textViewQuestion.setText("Was ist die Hauptstadt von Deutschland?");
        textViewAnswer.setText("Berlin");

        // Button zum Anzeigen der Antwort
        buttonShowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Antwort anzeigen und Buttons für Bewertung sichtbar machen
                textViewAnswer.setVisibility(View.VISIBLE);
                buttonsContainer.setVisibility(View.VISIBLE);
                buttonShowAnswer.setVisibility(View.INVISIBLE);
            }
        });



        // TODO: Implementiere die Logik für die Bewertung der Karte

        // Einstellungen-Button
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Implementiere die Logik für die Einstellungen
            }
        });

        buttonEasy.setOnClickListener(view -> {
            updateKarteStatus(4);
            loadNextQuestion();
        });

        buttonGood.setOnClickListener(view -> {
            updateKarteStatus(3);
            loadNextQuestion();
        });

        buttonHard.setOnClickListener(view -> {
            updateKarteStatus(2);
            loadNextQuestion();
        });

        buttonAgain.setOnClickListener(view -> {
            updateKarteStatus(1);
            loadNextQuestion();
        });

        loadNextQuestion();
    }
    private void loadNextQuestion() {
/*        List<Karte> zuLernendeKarten = datenBankManager.waehleZuLernendeKarten();
        if(!zuLernendeKarten.isEmpty()) {
            Karte nextCard = zuLernendeKarten.get(0);
            aktuellekarteID = nextCard.getKarteId();
            textViewQuestion.setText(nextCard.getFrage());
            textViewAnswer.setText(nextCard.getAntwort());*/
        Karte zuLernendeKarte = datenBankManager.waehleAeltesteFaelligeKarte(stapelID, setID);
        if(zuLernendeKarte != null) {
            aktuellekarteID = zuLernendeKarte.getKarteId();
            textViewQuestion.setText(zuLernendeKarte.getFrage());
            textViewAnswer.setText(zuLernendeKarte.getAntwort());

            textViewAnswer.setVisibility(View.INVISIBLE);
            buttonsContainer.setVisibility(View.GONE);
            buttonShowAnswer.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "Alle Karten für heute wurden gelernt", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    // 1 - nochmal, 2 - schwer, 3 - gut, 4 - einfach
    private void updateKarteStatus(int difficulty) {
        DatenBankManager db = new DatenBankManager(this);
        db.updateKarteStatus(aktuellekarteID, stapelID, setID, difficulty);
        db.close();
    }

    @Override
    protected void onDestroy() {
        datenBankManager.close();
        super.onDestroy();
    }
}