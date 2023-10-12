package com.example.goalapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatenBankManager extends SQLiteOpenHelper {

    public static final int DATENBANK_VERSION = 3;
    public static final String DATENBANK_NAMEN = "Karte.db";
    public DatenBankManager(Context cxt) {
        super(cxt, DATENBANK_NAMEN, null, DATENBANK_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + "STAPEL" + " (" +
                        "STAPEL_ID" + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "STAPEL_NAME" + " TEXT," +
                        "STAPEL_BESCHREIBUNG" + " TEXT," +
                        "STAPEL_FARBE" + " TEXT," +
                        "STAPEL_STATUS" + " INTEGER,"+
                        "STAPELSET_ID" + " INTEGER, " + // Fremdschlüssel auf STAPELSET
                        "FOREIGN KEY (STAPELSET_ID) REFERENCES STAPELSET(SET_ID) ON DELETE CASCADE" +
                         ")"
        );
        db.execSQL(
                "CREATE TABLE " + "KARTE" + " (" +
                        "KARTE_ID" + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "KARTE_FRAGE" + " TEXT," +
                        "KARTE_ANTWORT" + " TEXT," +
                        "STAPEL_ID" + " INTEGER," +
                        "KARTE_STATUS" + " INTEGER," +
                        "FOREIGN KEY (STAPEL_ID) REFERENCES STAPEL(STAPEL_ID) ON DELETE CASCADE" +
                         ")"
        );
        db.execSQL(
                "CREATE TABLE " + "STAPELSET" + " (" +
                        "SET_ID" + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "SET_NAME" + " TEXT," +
                        "SET_BESCHREIBUNG" + " TEXT," +
                        "SET_FARBE" + " TEXT," +
                        "SET_STATUS" + " INTEGER"
                        + ")"
        );
    }
    // KARTE-TABLE-METHODEN------------------------------------------------------------------------------------------------------------------------
    public void insertKarte(String frage, String antwort, int stapel, int status) {
        ContentValues neueZeile = new ContentValues();
        neueZeile.put("KARTE_FRAGE", frage);
        neueZeile.put("KARTE_ANTWORT", antwort);
        neueZeile.put("STAPEL_ID", stapel);
        neueZeile.put("KARTE_STATUS", status);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("KARTE", null, neueZeile);
    }

    public int countKarten(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM KARTE WHERE STAPEL_ID =" +id, null);
        int count = 0;
        if(cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }
    // STAPEL-TABLE-METHODEN------------------------------------------------------------------------------------------------------------------------
    public void insertStapel(String stapelName, String stapelBeschreibung, String stapelFarbe, int stapelStatus, int stapelsetID){
        ContentValues neueZeile = new ContentValues();
        neueZeile.put("STAPEL_NAME",stapelName);
        neueZeile.put("STAPEL_BESCHREIBUNG",stapelBeschreibung);
        neueZeile.put("STAPEL_FARBE",stapelFarbe);
        neueZeile.put("STAPEL_STATUS",stapelStatus);
        neueZeile.put("STAPELSET_ID", stapelsetID);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("STAPEL", null, neueZeile);
    }
    public int getMaxStapelID(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor maxID = db.rawQuery("SELECT MAX(STAPEL_ID) FROM STAPEL",null);
        maxID.moveToFirst();
        int id = maxID.getInt(0);
        return id;
    }

    public int getStapelID(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM STAPEL WHERE STAPEL_ID =" + id,null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public Cursor getAllStapelFromSetID(int setId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT STAPEL_ID AS _id, STAPEL_NAME FROM STAPEL WHERE STAPELSET_ID ="+setId, null);
        if(cursor!= null)
         cursor.moveToFirst();
        return cursor;
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
