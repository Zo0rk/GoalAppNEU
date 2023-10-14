package com.example.goalapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        newButton = findViewById(R.id.newButton);
        listView = findViewById(R.id.setUebersichtListView);

        newButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, SetErzeugen.class);
            startActivity(intent);
        });

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            //Rufe SetUebersichtActivity auf
            //brauche id des geklickten Sets
            //Anschließend wird die Übersicht des Set mittels ID geöffnet...
            //Dazu benötigen wir nun die ID:
            DatenBankManager db = new DatenBankManager(this);
            Intent intent = new Intent(this, SetUebersichtActivity.class);
            int id = db.getSetID(i+1); //i+1, da Listelemente ab 0 gezählt werden
            intent.putExtra("SET_ID", id);
            startActivity(intent);
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        // Erstelle oder erhalte eine Cursor mit den Daten aus der Datenbank
        DatenBankManager db = new DatenBankManager(this);
        Cursor cursor = db.getAllSets();
        MainUebersichtCursorAdapter adapter = new MainUebersichtCursorAdapter(this, cursor);
        listView.setAdapter(adapter);

    }
}