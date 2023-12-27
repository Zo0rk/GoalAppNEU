package com.example.goalapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.goalapp.adapter.MainUebersichtCursorAdapter;
import com.example.goalapp.models.Karte;
import com.example.goalapp.R;
import com.example.goalapp.database.DatenBankManager;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class LernenSpacedRepetitionActivity extends AppCompatActivity {


    private List<Karte> cardQueue;
    private ProgressBar progressBar;
    private TextView textViewQuestion;
    private TextView textViewAnswer;
    private EditText editTextQuestion;
    private EditText editTextAnswer;
    private Button buttonShowAnswer;
    private Button buttonApply;
    private Button buttonDiscard;
    private LinearLayout buttonsContainer;
    private ImageButton buttonSettings;
    private int stapelID;
    private int setID;
    private int aktuellekarteID;
    private DatenBankManager datenBankManager;
    private Karte currentCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lernen_spaced_repetition);

        datenBankManager = new DatenBankManager(this);
        cardQueue = datenBankManager.waehleZuLernendeKarten();

        stapelID = getIntent().getIntExtra("STAPEL_ID",0);
        setID = getIntent().getIntExtra("SET_ID",0);
        int color = getIntent().getIntExtra("SET_COLOR",0);
        datenBankManager = new DatenBankManager(this);
        // Verknüpfung der Views mit den entsprechenden IDs aus dem Layout
        textViewQuestion = findViewById(R.id.textViewQuestion);
        textViewAnswer = findViewById(R.id.textViewAnswer);
        editTextQuestion = findViewById(R.id.editTextViewQuestion);
        editTextAnswer = findViewById(R.id.editTextViewAnswer);
        buttonShowAnswer = findViewById(R.id.buttonShowAnswer);
        buttonsContainer = findViewById(R.id.buttonsContainer);
        buttonSettings = findViewById(R.id.buttonSettings);
        // Buttons für Bewertung der Karte
        Button buttonAgain = findViewById(R.id.buttonAgain);
        Button buttonHard = findViewById(R.id.buttonHard);
        Button buttonGood = findViewById(R.id.buttonGood);
        Button buttonEasy = findViewById(R.id.buttonEasy);
        buttonApply = findViewById(R.id.buttonSaveChangesCard);
        buttonDiscard = findViewById(R.id.buttonDiscardChangesCard);

        buttonApply.setOnClickListener(view -> {
            String neueFrage = String.valueOf(editTextQuestion.getText());
            String neueAntwort = String.valueOf(editTextAnswer.getText());
            datenBankManager.updateKarte(aktuellekarteID, stapelID, setID, neueFrage, neueAntwort);
            recreate();
        });
        
        buttonDiscard.setOnClickListener(view -> {
            editTextQuestion.setVisibility(View.GONE);
            editTextAnswer.setVisibility(View.GONE);

            editTextQuestion.setText(textViewQuestion.getText());
            editTextAnswer.setText(textViewAnswer.getText());

            textViewQuestion.setVisibility(View.VISIBLE);
            textViewAnswer.setVisibility(View.VISIBLE);

            buttonDiscard.setVisibility(View.GONE);
            buttonApply.setVisibility(View.GONE);
            buttonShowAnswer.setVisibility(View.VISIBLE);
        });
        buttonShowAnswer.setOnClickListener(view -> {
            // Antwort anzeigen und Buttons für Bewertung sichtbar machen
            textViewAnswer.setVisibility(View.VISIBLE);
            buttonsContainer.setVisibility(View.VISIBLE);
            buttonShowAnswer.setVisibility(View.INVISIBLE);
        });

        buttonSettings.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, view);
            popupMenu.inflate(R.menu.karten_menu);
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if(menuItem.getItemId() == R.id.editKarte) {
                    editTextQuestion.setVisibility(View.VISIBLE);
                    editTextAnswer.setVisibility(View.VISIBLE);

                    editTextQuestion.setText(textViewQuestion.getText());
                    editTextAnswer.setText(textViewAnswer.getText());

                    textViewQuestion.setVisibility(View.GONE);
                    textViewAnswer.setVisibility(View.GONE);

                    buttonDiscard.setVisibility(View.VISIBLE);
                    buttonApply.setVisibility(View.VISIBLE);
                    buttonShowAnswer.setVisibility(View.GONE);
//                    return true;
                } else if(menuItem.getItemId() == R.id.deleteKarte) {
                    // Popup um Löschen zu bestätigen
                    showDeleteConfirmationDialog();
                    // return true;
                }
                return false;
            });
            popupMenu.show();
        });

        buttonEasy.setOnClickListener(view -> {
            updateCard(currentCard, 4);
            loadAndShowNextCard();
        });

        buttonGood.setOnClickListener(view -> {
            updateCard(currentCard, 3);
            loadAndShowNextCard();;
        });

        buttonHard.setOnClickListener(view -> {
            updateCard(currentCard, 2);
            loadAndShowNextCard();
        });

        buttonAgain.setOnClickListener(view -> {
            updateCard(currentCard, 1);
            loadAndShowNextCard();
        });

        loadAndShowNextCard();
    }

    //Wählt die Karte aus der Warteschlange mit dem gerningsten Intervall und Easinessfaktor
    public Karte getNextCard() {
        // Sortiere die Karten nach aufsteigendem Intervall und absteigendem Easiness-Faktor.
        Collections.sort(cardQueue, (card1, card2) -> {
            if (card1.getInterval() == card2.getInterval()) {
                return Double.compare(card2.getEasinessFactor(), card1.getEasinessFactor());
            }
            return Double.compare(card1.getInterval(), card2.getInterval());
        });

        // Wähle die erste Karte aus der sortierten Liste.
        if (!cardQueue.isEmpty()) {
            return cardQueue.get(0);
        } else {
            return null;  // Wenn die Warteschlange leer ist, gibt es keine Karte zum Lernen.
        }
    }

    public void updateCard(Karte card, int grade) {
        card.updateEasinessFactor(grade);
        int cardID = currentCard.getKarteId();
        int interval = currentCard.getInterval();
        double easinessFactor = currentCard.getEasinessFactor();
        long letztes_lerndatum = card.getLetztesLerndatum();
        long naechstes_lerndatum = card.getNaechstesLerndatum();

        Date datum =new Date(naechstes_lerndatum);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String formattedDate = dateFormat.format(datum);

        Toast.makeText(this, "Nächstes Lerndatum: " + formattedDate, Toast.LENGTH_SHORT).show();
        datenBankManager.updateKarteIntervalAndDate(cardID, stapelID, setID, interval, easinessFactor, letztes_lerndatum, naechstes_lerndatum);

        if(grade != 1 && grade !=2) {
            if(card.getNaechstesLerndatum() >= System.currentTimeMillis()) {
                cardQueue.remove(card);
            }
        }
    }
    private void loadAndShowNextCard() {
//        Karte zuLernendeKarte = datenBankManager.waehleAeltesteFaelligeKarte(stapelID, setID);
//        if(zuLernendeKarte != null) {
//            aktuellekarteID = zuLernendeKarte.getKarteId();
//            textViewQuestion.setText(zuLernendeKarte.getFrage());
//            textViewAnswer.setText(zuLernendeKarte.getAntwort());
//
//            textViewAnswer.setVisibility(View.INVISIBLE);
//            buttonsContainer.setVisibility(View.GONE);
//            buttonShowAnswer.setVisibility(View.VISIBLE);
//        } else {
//            Toast.makeText(this, "Alle Karten für heute wurden gelernt", Toast.LENGTH_SHORT).show();
//            finish();
//        }
        Karte zuLernendeKarte = getNextCard();
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
        currentCard = zuLernendeKarte;
    }
    // 1 - nochmal, 2 - schwer, 3 - gut, 4 - einfach
//    private void updateKarteStatus(int difficulty) {
//        DatenBankManager db = new DatenBankManager(this);
//        db.updateKarteStatus(aktuellekarteID, stapelID, setID, difficulty);
//        db.close();
//    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Löschen bestätigen");
        builder.setMessage("Bist du sicher, dass du das Element löschen möchtest?");
        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                datenBankManager.deleteKarte(aktuellekarteID, stapelID, setID);
                recreate();
            }
        });
        builder.setNegativeButton("Nein", (dialog, which) -> {
            // Hier kannst du Aktionen ausführen, wenn der Benutzer "Nein" auswählt
        });
        builder.show();
    }
    @Override
    protected void onDestroy() {
        datenBankManager.close();
        super.onDestroy();
    }
}