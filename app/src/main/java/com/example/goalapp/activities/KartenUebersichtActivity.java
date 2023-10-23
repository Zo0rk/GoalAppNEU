package com.example.goalapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.goalapp.R;
import com.example.goalapp.adapter.KartenUebersichtCursorAdapter;
import com.example.goalapp.adapter.MainUebersichtCursorAdapter;
import com.example.goalapp.database.DatenBankManager;

public class KartenUebersichtActivity extends AppCompatActivity {

    ListView listView;
    KartenUebersichtCursorAdapter adapter;
    DatenBankManager db;

    int setID, stapelID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karten_uebersicht);
        db = new DatenBankManager(this);
        listView = findViewById(R.id.kartenUebersicht);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Erstelle oder erhalte eine Cursor mit den Daten aus der Datenbank
        setID = getIntent().getIntExtra("SET_ID",9999);
        stapelID = getIntent().getIntExtra("STAPEL_ID",9999);
        Cursor cursor = db.getAllKarten(setID, stapelID);
        adapter = new KartenUebersichtCursorAdapter(this, cursor);
        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("BACK","BACK");
    }
}