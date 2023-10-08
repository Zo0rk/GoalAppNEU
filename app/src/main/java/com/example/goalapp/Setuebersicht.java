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
    private Color color;

    private void buildUpFromDB(int id){
        setHeader = db.getSetName(id);
        setHeaderView.setTextColor(476188);
        setBeschreibung = db.getSetBeschreibung(id);
        setFarbe = db.getSetFarbe(id);
        setHeaderView.setText(setHeader);
        progress = db.getSetProgress(id);
        progressView.setText(valueOf(progress) + "%");
        //setBeschreibungView.setText(setBeschreibung); IST JETZT FORTSCHRITT...
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
