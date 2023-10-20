package com.example.goalapp.activities;

import static java.lang.String.valueOf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goalapp.R;
import com.example.goalapp.adapter.MainUebersichtCursorAdapter;
import com.example.goalapp.adapter.SetUebersichtCursorAdapter;
import com.example.goalapp.database.DatenBankManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SetUebersichtActivity extends AppCompatActivity implements View.OnClickListener {
    private int progress;
    private TextView setHeaderView;
    private String setHeader;
    private TextView setBeschreibungView;
    private TextView progressView;
    private String setBeschreibung;
    private String setFarbe;
    private ProgressBar mainProgressBar;
    private int mainProgress;

    ListView stapelListView;
    private FloatingActionButton neu;

    SetUebersichtCursorAdapter adapter;
    int setID;
    private int stapelID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_uebersicht);

        setHeaderView = findViewById(R.id.setHeader);
        setBeschreibungView = findViewById(R.id.setBeschreibung);
        progressView = findViewById(R.id.progressView);
        mainProgressBar = findViewById(R.id.MainProgressBar);
        stapelListView = findViewById(R.id.stapelListView);
        registerForContextMenu(stapelListView);

        neu = findViewById(R.id.addButton);

        setID = getIntent().getIntExtra("SET_ID", 0);
        stapelID = getIntent().getIntExtra("STAPEL_ID",0); // siehe z. 87...
        buildUpFromDB(setID);

        // Erstellt einen neuen Stapel im Set mit entsprechender set-id
        neu.setOnClickListener(view -> {
            Intent intent2 = new Intent(this,Stapel_erstellen.class);
            intent2.putExtra("SET_ID",setID);
            startActivity(intent2);
        });

        // Öffnet zur Zeit Kartenerstellung Activity
        stapelListView.setOnItemClickListener((adapterView, view, i, l) -> {

            stapelID = (int) adapter.getItemId(i);

            DatenBankManager db = new DatenBankManager(this);
            int anzKarten = db.getAnzKartenInStapel(stapelID,setID);
            Intent intent;

            if(anzKarten != 0) {
                intent = new Intent(this, Lernen.class);
            }
            else{
                Log.d("anzKarten",valueOf(anzKarten));
                intent = new Intent(this, KarteErstellenActivity.class);
            }

            intent.putExtra("STAPEL_ID", stapelID);
            intent.putExtra("SET_ID", setID);
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
    }

    private void buildUpFromDB(int id){
        DatenBankManager db = new DatenBankManager(this);
        setHeader = db.getSetName(id);
        setBeschreibung = db.getSetBeschreibung(id);
        setFarbe = db.getSetFarbe(id);
        setHeaderView.setText(setHeader);
        progress = db.getSetProgress(id);
        progress = 20;
        progressView.setText(valueOf(progress) + "%");
        mainProgressBar.setProgress(progress,true);
        //setBeschreibungView.setText(setBeschreibung); IST JETZT FORTSCHRITT...

        switch(setFarbe){
            case "red":  setHeaderView.setTextColor(Color.parseColor("#ff756b")); break;
            case "green":  setHeaderView.setTextColor(Color.parseColor("#7afa6e")); break;
            case "yellow":  setHeaderView.setTextColor(Color.parseColor("#fff86b")); break;
            case "purple":  setHeaderView.setTextColor(Color.parseColor("#fa6ef8")); break;
            case "orange":  setHeaderView.setTextColor(Color.parseColor("#ffc98f")); break;
            case "blue":  setHeaderView.setTextColor(Color.parseColor("#6ef8fa")); break;
        }
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
        if(item.getItemId() == R.id.newCard) {
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

        } else if (item.getItemId() == R.id.delete) {
            // Zeige eine Bestätigungsdialogbox an
            showDeleteConfirmationDialog(info.position);
            return true;
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
