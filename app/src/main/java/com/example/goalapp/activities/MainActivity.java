package com.example.goalapp.activities;

import static java.lang.String.valueOf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.goalapp.adapter.MainUebersichtCursorAdapter;
import com.example.goalapp.R;
import com.example.goalapp.database.DatenBankManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity  {

    FloatingActionButton newButton;
    ListView listView;
    MainUebersichtCursorAdapter adapter;
    DatenBankManager db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        newButton = findViewById(R.id.newButton);
        listView = findViewById(R.id.setUebersichtListView);
        //Auswahlfeld für löschen und bearbeiten bei längeren drücken
        registerForContextMenu(listView);

        newButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, SetErzeugenActivity.class);
            startActivity(intent);
        });

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            //Rufe SetUebersichtActivity auf
            //brauche id des geklickten Sets
            //Anschließend wird die Übersicht des Set mittels ID geöffnet...
            //Dazu benötigen wir nun die ID:
            Intent intent = new Intent(this, SetUebersichtActivity.class);
            int id = (int) adapter.getItemId(i);
            intent.putExtra("SET_ID", id);
            startActivity(intent);
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Erstelle oder erhalte eine Cursor mit den Daten aus der Datenbank
        db = new DatenBankManager(this);
        Cursor cursor = db.getAllSets();
        adapter = new MainUebersichtCursorAdapter(this, cursor);
        listView.setAdapter(adapter);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // Inflater für das Kontextmenü erstellen
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    // Menü, das bei längeren Drücken eines Listenelementes erscheint
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int selectedItemPosition = info.position;
        if(item.getItemId() == R.id.edit) {
            // Öffne Activity, die das ausgewählte Set bearbeiten soll
            int selectedItemID = (int) adapter.getItemId(selectedItemPosition);
            Intent intent = new Intent(this, SetBearbetienActivity.class);
            intent.putExtra("SET_ID", selectedItemID);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.delete) {
            // Zeige eine Bestätigungsdialogbox an
            showDeleteConfirmationDialog(selectedItemPosition);
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
                DatenBankManager db = new DatenBankManager(MainActivity.this);
                db.deleteSetWithID((int) adapter.getItemId(itemPosition)); // + 1, da ListView ab 0 zählt
                Cursor cursor = db.getAllSets();
                adapter = new MainUebersichtCursorAdapter(MainActivity.this, cursor);
                listView.setAdapter(adapter);
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
}