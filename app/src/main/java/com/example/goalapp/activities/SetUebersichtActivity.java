package com.example.goalapp.activities;

import static java.lang.String.valueOf;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goalapp.R;
import com.example.goalapp.adapter.SetUebersichtCursorAdapter;
import com.example.goalapp.adapter.setAdapter;
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
    int setID; //Dazu da, um die Stapel mit gleicher ID in das Set zu laden...
    ListView stapelListView;
    private FloatingActionButton neu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_uebersicht);

        setHeaderView = findViewById(R.id.setHeader);
        setBeschreibungView = findViewById(R.id.setBeschreibung);
        progressView = findViewById(R.id.progressView);
        mainProgressBar = findViewById(R.id.MainProgressBar);
        stapelListView = findViewById(R.id.stapelListView);
        neu = findViewById(R.id.addButton);

        Intent intent = getIntent();
        setID = intent.getIntExtra("SET_ID", 0);
        buildUpFromDB(setID);

        neu.setOnClickListener(view -> {
            Intent intent2 = new Intent(this,Stapel_erstellen.class);
            intent2.putExtra("SET_ID",setID);
            startActivity(intent2);
        });
        /*setHeaderView.setText(setHeader);
        setBeschreibungView.setText(setBeschreibung);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatenBankManager db = new DatenBankManager(this);
        Cursor cursor = db.getAllStapelFromSetID(setID);
        SetUebersichtCursorAdapter adapter = new SetUebersichtCursorAdapter(this, cursor);
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
    public void onClick(View view) {

    }
}
