package com.example.goalapp.activities;

import static java.lang.String.valueOf;

import android.content.Context;
import android.util.Log;

import com.example.goalapp.database.DatenBankManager;

import java.util.ArrayList;
import java.util.List;

public class KartenVerwaltung {

    private Context ctx;

    private int stapelID;
    private int setID;
    private int randomStufe;     // Wählt die Stufe aus 1 = Schwer, 3 = Sicher -> 1 und 2 werden wahrscheinlicher sein.
    private ArrayList<Integer> kartenListe_1 = new ArrayList<Integer>();
    private ArrayList<Integer> kartenListe_2 = new ArrayList<Integer>();
    private ArrayList<Integer> kartenListe_3 = new ArrayList<Integer>();

    public KartenVerwaltung(int stapelID, int setID, Context ctx){
        this.ctx = ctx;
        this.stapelID = stapelID;
        this.setID = setID;

        DatenBankManager db = new DatenBankManager(ctx);
        kartenListe_1 = db.waehleKarten_1(stapelID,setID);
        kartenListe_2 = db.waehleKarten_2(stapelID,setID);
        kartenListe_3 = db.waehleKarten_3(stapelID,setID);
    }

    private void bestimmeRandomStufe(){
        int[] array = {1,1,1,2,2,3};    // P(1) = 0,5 | P(2) = 0,33 | P(3) = 1/6
        int auswahl = (int) ((Math.random() * (6 - 1)) + 1);
        randomStufe = array[auswahl];
    }

    public int getRandomKartenID(){
        // Würfel eine Stufe und schaue, ob die Liste dieser Stufe leer ist...
        while(true){
            bestimmeRandomStufe();
            switch (randomStufe){
                case 1 :
                    if(!kartenListe_1.isEmpty()) {
                        int auswahl = (int) ((Math.random() * (kartenListe_1.size() - 1)));
                        return kartenListe_1.get(auswahl);
                    }
                    break;
                case 2 :
                    if(!kartenListe_2.isEmpty()) {
                        int auswahl = (int) ((Math.random() * (kartenListe_1.size() - 1)));
                        return kartenListe_2.get(auswahl);
                    }
                    break;
                case 3 :
                    if(!kartenListe_3.isEmpty()) {
                        int auswahl = (int) ((Math.random() * (kartenListe_1.size() - 1)));
                        return kartenListe_3.get(auswahl);
                    }
                    break;
            }
        }
    }

}
