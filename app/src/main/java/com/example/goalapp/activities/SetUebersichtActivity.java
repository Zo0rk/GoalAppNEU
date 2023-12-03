package com.example.goalapp.activities;

import static java.lang.String.valueOf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;

import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.goalapp.R;
import com.example.goalapp.adapter.SetUebersichtCursorAdapter;
import com.example.goalapp.database.DatenBankManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SetUebersichtActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView setHeaderView;
    private TextView progressView;
    private ProgressBar mainProgressBar;
    ListView stapelListView;
    SetUebersichtCursorAdapter adapter;
    int setID;
    private int stapelID;
    private int color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_uebersicht);

        setHeaderView = findViewById(R.id.setHeader);
//        TextView setBeschreibungView = findViewById(R.id.setBeschreibung);
        progressView = findViewById(R.id.progressView);
        mainProgressBar = findViewById(R.id.MainProgressBar);
        stapelListView = findViewById(R.id.stapelListView);
        registerForContextMenu(stapelListView);

        FloatingActionButton neu = findViewById(R.id.addButton);

        setID = getIntent().getIntExtra("SET_ID", 0);
        stapelID = getIntent().getIntExtra("STAPEL_ID", 0); // siehe z. 87...
        buildUpFromDB(setID);

        // Erstellt einen neuen Stapel im Set mit entsprechender set-id
        neu.setOnClickListener(view -> {
            Intent intent2 = new Intent(this, StapelErstellenActivity.class);
            intent2.putExtra("SET_ID", setID);
            startActivity(intent2);
        });

        // Öffnet zur Zeit Kartenerstellung Activity
        stapelListView.setOnItemClickListener((adapterView, view, i, l) -> {

            stapelID = (int) adapter.getItemId(i);

            DatenBankManager db = new DatenBankManager(this);
            int anzKarten = db.getAnzKartenInStapel(stapelID, setID);
            Intent intent;

            if (anzKarten != 0) {
                intent = new Intent(this, LernenSpacedRepetitionActivity.class);
            } else {
                Log.d("anzKarten", valueOf(anzKarten));
                intent = new Intent(this, KarteErstellenActivity.class);
            }
            intent.putExtra("STAPEL_ID", stapelID);
            intent.putExtra("SET_ID", setID);
            intent.putExtra("SET_COLOR", color);
            startActivity(intent);
        });
        /*setHeaderView.setText(setHeader);
        setBeschreibungView.setText(setBeschreibung);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatenBankManager db = new DatenBankManager(this);
        Cursor cursor = db.getAllStapelFromSetID(setID);
        adapter = new SetUebersichtCursorAdapter(this, cursor);
        stapelListView.setAdapter(adapter);
        buildUpFromDB(setID);
    }

    private void buildUpFromDB(int id) {
        DatenBankManager db = new DatenBankManager(this);
        db.setSetProgress(id);
        String setHeader = db.getSetName(id);
        String setBeschreibung = db.getSetBeschreibung(id);
        String setFarbe = db.getSetFarbe(id);
        setHeaderView.setText(setHeader);
        int progress = db.getSetProgress(id);
        progressView.setText(progress + "%");
        mainProgressBar.setProgress(progress, true);
        //setBeschreibungView.setText(setBeschreibung); IST JETZT FORTSCHRITT...


//        switch(setFarbe){
//            case "red":  setHeaderView.setTextColor(Color.parseColor("#ff756b")); break;
//            case "green":  setHeaderView.setTextColor(Color.parseColor("#7afa6e")); break;
//            case "yellow":  setHeaderView.setTextColor(Color.parseColor("#fff86b")); break;
//            case "purple":  setHeaderView.setTextColor(Color.parseColor("#fa6ef8")); break;
//            case "orange":  setHeaderView.setTextColor(Color.parseColor("#ffc98f")); break;
//            case "blue":  setHeaderView.setTextColor(Color.parseColor("#6ef8fa")); break;
//        }
        int colorRes = getResources().getIdentifier(setFarbe, "color", getPackageName());
        color = ContextCompat.getColor(this, colorRes);

        mainProgressBar.setProgressTintList(ColorStateList.valueOf(color));
        setHeaderView.setTextColor(color);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // Inflater für das Kontextmenü erstellen
        getMenuInflater().inflate(R.menu.stapel_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        stapelID = (int) adapter.getItemId(info.position);
        if (item.getItemId() == R.id.newCard) {
            Intent intent = new Intent(this, KarteErstellenActivity.class);
            intent.putExtra("STAPEL_ID", stapelID);
            intent.putExtra("SET_ID", setID);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.showCards) {
            Intent intent = new Intent(this, KartenUebersichtActivity.class);
            intent.putExtra("STAPEL_ID", stapelID);
            intent.putExtra("SET_ID", setID);
            startActivity(intent);

        } else if (item.getItemId() == R.id.deleteStapel) {
            // Zeige eine Bestätigungsdialogbox an
            showDeleteConfirmationDialog(info.position);
            return true;
        } else if (item.getItemId() == R.id.stapelBearbeiten) {
            // Ändere Name
            // Schritte 1 und 2: Aufruf des Dialogs
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Neuen Namen eingeben");

            // Eingabefeld für den neuen Namen
            final EditText input = new EditText(this);
            builder.setView(input);

            // Schritte 3 und 4: Bestätigen und aktualisieren
            builder.setPositiveButton("OK", (dialog, which) -> {
                String newName = input.getText().toString().trim();
                // Hier solltest du die Logik implementieren, um den Namen des ListView-Elements zu aktualisieren
                DatenBankManager datenBankManager = new DatenBankManager(this);

                datenBankManager.updateStapelName(stapelID, setID, newName);
                recreate();
            });

            builder.setNegativeButton("Abbrechen", (dialog, which) -> dialog.cancel());

            builder.show();
        }
        return super.onContextItemSelected(item);
    }

    // Methode zur Anzeige der Bestätigungsdialogbox
    private void showDeleteConfirmationDialog(int itemPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Löschen bestätigen");
        builder.setMessage("Bist du sicher, dass du das Element löschen möchtest?");
        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatenBankManager db = new DatenBankManager(SetUebersichtActivity.this);
                db.deleteStapel((int) adapter.getItemId(itemPosition), setID); // + 1, da ListView ab 0 zählt
                Cursor cursor = db.getAllStapelFromSetID(setID);
                adapter = new SetUebersichtCursorAdapter(SetUebersichtActivity.this, cursor);
                stapelListView.setAdapter(adapter);
            }
        });
        builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Hier kannst du Aktionen ausführen, wenn der Benutzer "Nein" auswählt
            }
        });
        builder.show();
    }

    @Override
    public void onClick(View view) {

    }
}
