package com.example.goalapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goalapp.MainCursorAdapter;
import com.example.goalapp.R;
import com.example.goalapp.database.DatenBankManager;

public class MainActivity extends AppCompatActivity  {

    Button newButton;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newButton = findViewById(R.id.newButton);
        listView = findViewById(R.id.setUebersichtListView);

        newButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, SetErzeugen.class);
            startActivity(intent);
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            //Rufe SetUebersichtActivity auf
            //brauche id des geklickten Sets
            //Anschließend wird die Übersicht des Set mittels ID geöffnet...
            //Dazu benötigen wir nun die ID:
            DatenBankManager db = new DatenBankManager(this);
            Intent intent = new Intent(this, Setuebersicht.class);
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
        MainCursorAdapter adapter = new MainCursorAdapter(this, cursor);
        listView.setAdapter(adapter);

    }
}