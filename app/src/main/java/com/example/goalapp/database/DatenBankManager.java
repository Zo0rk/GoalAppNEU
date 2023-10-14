package com.example.goalapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatenBankManager extends SQLiteOpenHelper {

    public static final int DATENBANK_VERSION = 3;
    public static final String DATENBANK_NAMEN = "Karteikarten.db";
    public DatenBankManager(Context cxt) {
        super(cxt, DATENBANK_NAMEN, null, DATENBANK_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + "STAPELSET" + " (" +
                        "SET_ID" + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "SET_NAME" + " TEXT," +
                        "SET_BESCHREIBUNG" + " TEXT," +
                        "SET_FARBE" + " TEXT," +
                        "SET_STATUS" + " INTEGER"
                        + ")"
        );
        db.execSQL(
                "CREATE TABLE " + "STAPEL" + " (" +
                        "STAPEL_ID" + " INTEGER ," +
                        "STAPEL_NAME" + " TEXT," +
                        "STAPEL_BESCHREIBUNG" + " TEXT," +
                        "STAPEL_FARBE" + " TEXT," +
                        "STAPEL_STATUS" + " INTEGER,"+
                        "SET_ID" + " INTEGER," +
                        "PRIMARY KEY(STAPEL_ID, SET_ID), " + // Fremdschlüssel auf STAPELSET
                        "FOREIGN KEY (SET_ID) REFERENCES STAPELSET(SET_ID) ON DELETE CASCADE" +
                         ")"
        );
        db.execSQL(
                "CREATE TABLE " + "KARTE" + " (" +
                        "KARTE_ID" + " INTEGER," +
                        "KARTE_FRAGE" + " TEXT," +
                        "KARTE_ANTWORT" + " TEXT," +
                        "KARTE_STATUS" + " INTEGER," +
                        "STAPEL_ID" + " INTEGER," +
                        "SET_ID" + " INTEGER, " +
                        "PRIMARY KEY (KARTE_ID, STAPEL_ID, SET_ID), "+
                        "FOREIGN KEY (STAPEL_ID, SET_ID) REFERENCES STAPEL(STAPEL_ID, SET_ID) ON DELETE CASCADE" +
                         ")"
        );

    }
    // KARTE-TABLE-METHODEN------------------------------------------------------------------------------------------------------------------------
    public void insertKarte(String frage, String antwort ,int stapelId ,int setId ,int status) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Abfrage, um den höchsten Wert von KARTE_ID für die gegebene SET_ID und STAPEL_ID zu ermitteln
        Cursor cursor = db.rawQuery("SELECT MAX(KARTE_ID) FROM KARTE WHERE SET_ID = ? AND STAPEL_ID = ?", new String[]{String.valueOf(setId), String.valueOf(stapelId)});
        int neueKarteID = 1; // Standardwert, wenn noch keine Karten für diese Kombination von SET_ID und STAPEL_ID existieren

        if (cursor.moveToFirst()) {
            neueKarteID = cursor.getInt(0) + 1; // Die neue KARTE_ID ist der höchste Wert + 1
        }
        cursor.close();

        ContentValues neueZeile = new ContentValues();
        neueZeile.put("KARTE_ID", neueKarteID);
        neueZeile.put("KARTE_FRAGE", frage);
        neueZeile.put("KARTE_ANTWORT", antwort);
        neueZeile.put("KARTE_STATUS", status);
        neueZeile.put("STAPEL_ID", stapelId);
        neueZeile.put("SET_ID", setId);

        db.insert("KARTE", null, neueZeile);

        db.close();
    }

    public int countKartenInSetUndStapel(String setId, String stapelId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM Karte " +
                "WHERE SET_ID = ? AND stapel_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{setId,stapelId});
        int kartenAnzahl = 0;
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                kartenAnzahl = cursor.getInt(0);
            }
            cursor.close();
        }
        return kartenAnzahl;
    }
    // STAPEL-TABLE-METHODEN------------------------------------------------------------------------------------------------------------------------
    public void insertStapel(String stapelName, String stapelBeschreibung, String stapelFarbe, int stapelStatus, int setID){
        SQLiteDatabase db = this.getWritableDatabase();

        // Abfrage, um den höchsten Wert von STAPEL_ID für die gegebene SET_ID zu ermitteln
        Cursor cursor = db.rawQuery("SELECT MAX(STAPEL_ID) FROM STAPEL WHERE SET_ID = ?", new String[]{String.valueOf(setID)});
        int neueStapelID = 1; // Standardwert für den Fall, dass noch keine Stapel für dieses Set existieren

        if (cursor.moveToFirst()) {
            neueStapelID = cursor.getInt(0) + 1; // Die neue STAPEL_ID ist der höchste Wert + 1
        }
        cursor.close();

        ContentValues neueZeile = new ContentValues();
        neueZeile.put("STAPEL_ID", neueStapelID);
        neueZeile.put("STAPEL_NAME",stapelName);
        neueZeile.put("STAPEL_BESCHREIBUNG",stapelBeschreibung);
        neueZeile.put("STAPEL_FARBE",stapelFarbe);
        neueZeile.put("STAPEL_STATUS",stapelStatus);
        neueZeile.put("SET_ID", setID);

        db.insert("STAPEL", null, neueZeile);
        db.close();
    }

    public int getStapelStatus(int stapelID, int setID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(KARTE_STATUS) FROM KARTE WHERE STAPEL_ID = " + stapelID + " AND SET_ID = " + setID,null);
        cursor.moveToFirst();
        int summeAll = cursor.getInt(0);

        cursor = db.rawQuery("SELECT SUM(KARTE_STATUS) FROM KARTE WHERE STAPEL_ID = " + stapelID + " AND SET_ID = " + setID + " AND KARTE_STATUS = 3",null);
        cursor.moveToFirst();
        int summeStatus3 = cursor.getInt(0);

        if(summeAll == 0){  // verhindert, dass durch 0 geteilt wird.
            return 0;
        }
        int ret = (summeStatus3 / summeAll) * 100;  // Berechnet den prozentualen Wert...

        if(ret < 0){
            ret = 0;
        }
        if(ret > 100){
            ret = 100;
        }

        return ret;
    }

    public int getAnzKartenInStapel(int stapelID, int setID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(KARTE_STATUS) FROM KARTE WHERE STAPEL_ID = " + stapelID + " AND SET_ID = " + setID, null);
        cursor.moveToFirst();
        int summeAll = cursor.getInt(0);
        return summeAll;
    }

    public int getAnzSichereKartenInStapel(int stapelID, int setID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(KARTE_STATUS) FROM KARTE WHERE STAPEL_ID = " + stapelID + " AND SET_ID = " + setID + " AND KARTE_STATUS = 3",null);
        cursor.moveToFirst();
        int summeStatus3 = cursor.getInt(0);
        return summeStatus3;
    }

//    public int getMaxStapelID(){
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor maxID = db.rawQuery("SELECT MAX(STAPEL_ID) FROM STAPEL",null);
//        maxID.moveToFirst();
//        int id = maxID.getInt(0);
//        return id;
//    }

//    // Ka was ich gemacht habe????
//    public int getStapelID(int id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM STAPEL WHERE STAPEL_ID =" + id,null);
//        cursor.moveToFirst();
//        return cursor.getInt(0);
//    }

    public Cursor getAllStapelFromSetID(int setId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT STAPEL_ID AS _id, STAPEL_NAME, SET_ID FROM STAPEL WHERE SET_ID ="+setId, null);
        cursor.moveToFirst();
        return cursor;
    }

    public String getStapelName(int stapelID, int setID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT STAPEL_NAME FROM STAPEL WHERE STAPEL_ID = " + stapelID + " AND " + "SET_ID = " + setID,null);
        cursor.moveToFirst();
        String name = cursor.getString(0);
        return name;
    }
    // SET-TABLE-METHODEN------------------------------------------------------------------------------------------------------------------------
    public void insertSet(String name,String beschreibung, String farbe, int setStatus){
        ContentValues neueZeile = new ContentValues();
        neueZeile.put("SET_NAME",name);
        neueZeile.put("SET_BESCHREIBUNG",beschreibung);
        neueZeile.put("SET_FARBE",farbe);
        neueZeile.put("SET_STATUS",setStatus);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("STAPELSET", null, neueZeile);
    }

    public Cursor getAllSets() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT SET_ID AS _id, SET_NAME, SET_BESCHREIBUNG, SET_FARBE, SET_STATUS FROM STAPELSET",null);
        if(cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    public int getSetID(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM STAPELSET WHERE SET_ID =" + id,null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }
    public int getMaxSetID(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor maxID = db.rawQuery("SELECT MAX(SET_ID) FROM STAPELSET",null);
        maxID.moveToFirst();
        int id = maxID.getInt(0);
        return id;
    }

    public String getSetName(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor name = db.rawQuery("SELECT SET_NAME FROM STAPELSET WHERE SET_ID = " + id,null);
        name.moveToFirst();
        String result = name.getString(0);
        return result;
    }

    public String getSetBeschreibung(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor beschr = db.rawQuery("SELECT SET_BESCHREIBUNG FROM STAPELSET WHERE SET_ID = " + id,null);
        beschr.moveToFirst();
        String result = beschr.getString(0);
        return result;
    }

    public String getSetFarbe(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor farbe = db.rawQuery("SELECT SET_FARBE FROM STAPELSET WHERE SET_ID = " + id,null);
        farbe.moveToFirst();
        String result = farbe.getString(0);
        return result;
    }

    public int getSetProgress(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor progress = db.rawQuery("SELECT SET_STATUS FROM STAPELSET WHERE SET_ID =" + id,null);
        progress.moveToFirst();
        int setProgress = progress.getInt(0);
        return setProgress;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
