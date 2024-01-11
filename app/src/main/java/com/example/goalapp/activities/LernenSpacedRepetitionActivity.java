package com.example.goalapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.goalapp.models.Karte;
import com.example.goalapp.R;
import com.example.goalapp.database.DatenBankManager;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.zone.TzdbZoneRulesProvider;
import org.threeten.bp.zone.ZoneRulesInitializer;

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

            currentCard.selectIntervals();
            buttonAgain.setText("Nochmal \n" + Karte.fromMillisecondsToMinOrDays(currentCard.getAgainInterval()));
            buttonHard.setText("Schwer \n"+ Karte.fromMillisecondsToMinOrDays(currentCard.getHardInterval()));
            buttonGood.setText("Gut \n"+ Karte.fromMillisecondsToMinOrDays(currentCard.getGoodInterval()));
            buttonEasy.setText("Einfach \n"+ Karte.fromMillisecondsToMinOrDays(currentCard.getEasyInterval()));
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
        cardQueue = datenBankManager.waehleZuLernendeKarten(stapelID, setID);
        loadAndShowNextCard();
    }

    //Wählt die Karte aus der Warteschlange mit dem gerningsten Intervall und Easinessfaktor
    public Karte getNextCard() {
        // Sortiere die Karten nach aufsteigendem Intervall und absteigendem Easiness-Faktor.
        Collections.sort(cardQueue, (card1, card2) -> {
            if (card1.getNaechstesLerndatum() == card2.getNaechstesLerndatum()) {
                return Double.compare(card2.getInterval(), card1.getInterval());
            }
            return Double.compare(card1.getNaechstesLerndatum(), card2.getNaechstesLerndatum());
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
        card.updateInterval(grade);

        int cardID = currentCard.getKarteId();
        int interval = currentCard.getInterval();
        double easinessFactor = currentCard.getEasinessFactor();
        long letztes_lerndatum = card.getLetztesLerndatum();
        long naechstes_lerndatum = card.getNaechstesLerndatum();

        Date datum = new Date(naechstes_lerndatum);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String formattedDate = dateFormat.format(datum);
        Toast.makeText(this, "Nächste Lerndatum: " + formattedDate, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Nächstes Lerndatum: " + formattedDate, Toast.LENGTH_SHORT).show();
        datenBankManager.updateKarteIntervalAndDate(cardID, stapelID, setID, interval, easinessFactor, letztes_lerndatum, naechstes_lerndatum);

        // Findet den Unix Timestamp für das ende das aktuellen Tages heraus
        LocalDate today = LocalDate.now();
        LocalDateTime endOfDay = LocalDateTime.of(today.plusDays(1), LocalTime.MIDNIGHT);
        long endOfDayTimeStamp = endOfDay.toInstant(ZoneOffset.UTC).toEpochMilli();

        // Wenn Karte heute nicht mehr gelernt werden soll
        if(card.getNaechstesLerndatum() >= endOfDayTimeStamp) {
            cardQueue.remove(card);
        }
    }
    private void loadAndShowNextCard() {
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

        });
        builder.show();
    }
    @Override
    protected void onDestroy() {
        datenBankManager.close();
        super.onDestroy();
    }
}