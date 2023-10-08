package com.example.goalapp;

import static java.lang.String.valueOf;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Setuebersicht extends AppCompatActivity {

    private int id;
    private Intent i;
    private datenBankManager db;

    private int progress;
    private TextView setHeaderView;
    private String setHeader;
    private TextView setBeschreibungView;
    private TextView progressView;
    private String setBeschreibung;
    private String setFarbe;
    private ProgressBar mainProgressBar;
    private int mainProgress;
    private int setID; //Dazu da, um die Stapel mit gleicher ID in das Set zu laden...
    private ListView setListe;

    private void buildUpFromDB(int id){
        setHeader = db.getSetName(id);
        setBeschreibung = db.getSetBeschreibung(id);
        setFarbe = db.getSetFarbe(id);
        setHeaderView.setText(setHeader);
        progress = db.getSetProgress(id);
        progressView.setText(valueOf(progress) + "%");
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_uebersicht);

        setHeaderView = (TextView) findViewById(R.id.setHeader);
        setBeschreibungView = (TextView) findViewById(R.id.setBeschreibung);
        progressView = (TextView) findViewById(R.id.progressView);

        i = getIntent();
        id = i.getIntExtra("SET_ID",0);
        db = new datenBankManager(this);
        buildUpFromDB(id);



        /*setHeaderView.setText(setHeader);
        setBeschreibungView.setText(setBeschreibung);*/

        //LISTE FÜLLEN MIT STAPELN ÜBER SET_ID....
        //mainProgress aus dem Inhalten der Liste berechnen...
        //mainProgress auf die ProgressBar anwenden...

    }

}
