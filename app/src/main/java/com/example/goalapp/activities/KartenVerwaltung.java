package com.example.goalapp.activities;

import android.content.Context;

import com.example.goalapp.database.DatenBankManager;

import java.util.ArrayList;
import java.util.List;

public class KartenVerwaltung {

    private Context ctx;

    private int stapelID;
    private int setID;
    private int randomStufe;     // Wählt die Stufe aus 1 = Schwer, 3 = Sicher -> 1 und 2 werden wahrscheinlicher sein.
    private int randomEintrag;   // Wählt einen zufälligen Eintrag aus der gewählten Stufe.
    private ArrayList<String> selectedKartenIDs = new ArrayList<String>();

    public KartenVerwaltung(int stapelID, int setID, Context ctx){
        this.ctx = ctx;
        this.stapelID = stapelID;
        this.setID = setID;
        randomStufe = bestimmeRandomStufe();
    }

    public int bestimmeRandomStufe(){
        int[] array = {1,1,1,2,2,3};    // P(1) = 0,5 | P(2) = 0,33 | P(3) = 1/6
        int auswahl = (int) ((Math.random() * (6 - 1)) + 1);
        return array[auswahl];
    }

    public void waehleKarten(int stapelID, int setID, int stufe){
        DatenBankManager db = new DatenBankManager(ctx);
        db.waehleKarten(stapelID,setID,randomStufe);
    }

}
