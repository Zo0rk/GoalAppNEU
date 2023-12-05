package com.example.goalapp.database;

import static java.lang.String.valueOf;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.goalapp.models.Karte;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DatenBankManager extends SQLiteOpenHelper {

    public static final int DATENBANK_VERSION = 1;
    public static final String DATENBANK_NAMEN = "Karteikarten.db";
    public DatenBankManager(Context cxt) {
        super(cxt, DATENBANK_NAMEN, null, DATENBANK_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("PRAGMA foreign_keys=ON;");

        db.execSQL(
                "CREATE TABLE " + "STAPELSET" + " (" +
                        "SET_ID" + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "SET_NAME" + " TEXT," +
                        "SET_BESCHREIBUNG" + " TEXT," +
                        "SET_FARBE" + " TEXT," +
                        "SET_STATUS" + " INTEGER " +
                        ")"
        );
        db.execSQL(
                "CREATE TABLE " + "STAPEL" + " (" +
                        "STAPEL_ID" + " INTEGER ," +
                        "STAPEL_NAME" + " TEXT," +
                        "STAPEL_BESCHREIBUNG" + " TEXT," +
                        "STAPEL_FARBE" + " TEXT," +
                        "STAPEL_STATUS" + " INTEGER,"+
                        "SET_ID" + " INTEGER," +
                        "CONSTRAINT stapel_pk PRIMARY KEY(STAPEL_ID, SET_ID), " + // Fremdschlüssel auf STAPELSET
                        "CONSTRAINT stapel_fk FOREIGN KEY (SET_ID) REFERENCES STAPELSET(SET_ID) ON DELETE CASCADE " +
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
                        "LETZTES_LERNDATUM" + " LONG, " +
                        "NAECHSTES_LERNDATUM" + " LONG, " +
                        "CONSTRAINT karte_pk PRIMARY KEY (KARTE_ID, STAPEL_ID, SET_ID), "+
                        "CONSTRAINT karte_fk FOREIGN KEY (STAPEL_ID, SET_ID) REFERENCES STAPEL(STAPEL_ID, SET_ID) ON DELETE CASCADE" +
                         ");"
        );
    }
    // KARTE-TABLE-METHODEN------------------------------------------------------------------------------------------------------------------------
    public void updateKarte(int karteID, int stapelID, int setID, String neueFrage, String neueAntwort) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues updateValues = new ContentValues();
        updateValues.put("KARTE_FRAGE", neueFrage);
        updateValues.put("KARTE_ANTWORT", neueAntwort);

        db.update("KARTE",updateValues, "KARTE_ID = ? AND STAPEL_ID = ? AND SET_ID = ?",
                new String[]{String.valueOf(karteID), String.valueOf(stapelID), String.valueOf(setID)});
        db.close();
    }

    public void deleteKarte(int karteID, int stapelID, int setID) {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = "KARTE_ID = ? AND STAPEL_ID = ? AND SET_ID = ?";
        String[] whereArgs = {valueOf(karteID), valueOf(stapelID), valueOf(setID)};

        db.delete("KARTE", whereClause, whereArgs);
        db.close();
    }

    public ArrayList<Integer> waehleKarten_1(int stapelID, int setID){
        ArrayList<Integer> ret = new ArrayList<Integer>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT KARTE_ID FROM KARTE WHERE STAPEL_ID = " + stapelID + " AND SET_ID = " + setID + " AND KARTE_STATUS = 1",null);

        if(cursor.moveToFirst()){ // Wenn der Cursor nicht leer ist, also karten dieser Stufe vorhanden sind...
            do{
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("KARTE_ID"));
                ret.add(id);
            }while(cursor.moveToNext());
        }
        db.close();
        return ret;
    }

    public ArrayList<Integer> waehleKarten_2(int stapelID, int setID){
        ArrayList<Integer> ret = new ArrayList<Integer>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT KARTE_ID FROM KARTE WHERE STAPEL_ID = " + stapelID + " AND SET_ID = " + setID + " AND KARTE_STATUS = 2",null);

        if(cursor.moveToFirst()){ // Wenn der Cursor nicht leer ist, also karten dieser Stufe vorhanden sind...
            do{
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("KARTE_ID"));
                ret.add(id);
            }while(cursor.moveToNext());
        }
        db.close();
        return ret;
    }

    public ArrayList<Integer> waehleKarten_3(int stapelID, int setID){
        ArrayList<Integer> ret = new ArrayList<Integer>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT KARTE_ID FROM KARTE WHERE STAPEL_ID = " + stapelID + " AND SET_ID = " + setID + " AND KARTE_STATUS = 3",null);

        if(cursor.moveToFirst()){ // Wenn der Cursor nicht leer ist, also karten dieser Stufe vorhanden sind...
            do{
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("KARTE_ID"));
                ret.add(id);
            }while(cursor.moveToNext());
        }
        db.close();
        return ret;
    }

    public void insertKarte(String frage, String antwort, int stapelId, int setId, int status) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Abfrage, um den höchsten Wert von KARTE_ID für die gegebene SET_ID und STAPEL_ID zu ermitteln
        Cursor cursor = db.rawQuery("SELECT MAX(KARTE_ID) FROM KARTE WHERE SET_ID = ? AND STAPEL_ID = ?", new String[]{String.valueOf(setId), String.valueOf(stapelId)});
        int neueKarteID = 1; // Standardwert, wenn noch keine Karten für diese Kombination von SET_ID und STAPEL_ID existieren

        if (cursor.moveToFirst()) {
            neueKarteID = cursor.getInt(0) + 1; // Die neue KARTE_ID ist der höchste Wert + 1
        }
        cursor.close();

        long aktuellesDatum = System.currentTimeMillis();
        long naechstesLerndatum = aktuellesDatum; // Standardwert für naechstesLerndatum ist aktuellesDatum

        ContentValues neueZeile = new ContentValues();
        neueZeile.put("KARTE_ID", neueKarteID);
        neueZeile.put("KARTE_FRAGE", frage);
        neueZeile.put("KARTE_ANTWORT", antwort);
        neueZeile.put("KARTE_STATUS", status);
        neueZeile.put("STAPEL_ID", stapelId);
        neueZeile.put("SET_ID", setId);
        neueZeile.put("LETZTES_LERNDATUM", aktuellesDatum);
        neueZeile.put("NAECHSTES_LERNDATUM", naechstesLerndatum);

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
        db.close();
        return kartenAnzahl;
    }

    public Cursor getAllKarten(int setId, int stapelId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT KARTE_ID as _id, KARTE_FRAGE, KARTE_ANTWORT FROM KARTE " +
                "WHERE SET_ID = ? AND STAPEL_ID = ?";
        String[] selectionArgs = {String.valueOf(setId), String.valueOf(stapelId)};
        Cursor cursor = db.rawQuery(query,selectionArgs);
        if(cursor != null)
            cursor.moveToFirst();
        db.close();
        return cursor;
    }

    public String getFrage(int setID, int stapelID, int kartenID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT KARTE_FRAGE FROM KARTE WHERE SET_ID = " + setID + " AND STAPEL_ID = " + stapelID + " AND KARTE_ID = " + kartenID,null);
        cursor.moveToFirst();
        db.close();
        return cursor.getString(cursor.getColumnIndexOrThrow("KARTE_FRAGE"));
    }

    public String getAntwort(int setID, int stapelID, int kartenID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT KARTE_ANTWORT FROM KARTE WHERE SET_ID = " + setID + " AND STAPEL_ID = " + stapelID + " AND KARTE_ID = " + kartenID,null);
        cursor.moveToFirst();
        db.close();
        return cursor.getString(cursor.getColumnIndexOrThrow("KARTE_ANTWORT"));
    }

    /*public void setStatus(int setID, int stapelID, int kartenID, int status){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(" KARTE SET KARTE_STATUS = " + status + " WHERE SET_ID = " + setID + " AND STAPEL_ID = " + stapelID + " AND KARTE_ID = " + kartenID,null);
        Log.d("DB","OK");
    }*/

    public void setKarteStatus(int setID, int stapelID, int kartenID, int status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("KARTE_STATUS", status);

        String whereClause = "SET_ID = ? AND STAPEL_ID = ? AND KARTE_ID = ?";
        String[] whereArgs = {String.valueOf(setID), String.valueOf(stapelID), String.valueOf(kartenID)};

        int rowsAffected = db.update("KARTE", values, whereClause, whereArgs);

        if (rowsAffected > 0) {
            Log.d("DB", "Status der Karte erfolgreich aktualisiert");
        } else {
            Log.e("DB", "Fehler beim Aktualisieren des Kartenstatus");
        }

        db.close();
    }

    public void updateKarteStatus(long karteId, long stapelId, long setId, int bewertung) {
        SQLiteDatabase db = this.getWritableDatabase();
        long aktuellesDatum = System.currentTimeMillis();

        // Hier implementierst du deine Logik, um das neue Datum basierend auf der Bewertung zu berechnen
        long neuesDatum = berechneNeuesDatum(bewertung, aktuellesDatum);

        // SQL-Anweisung zum Aktualisieren der Lerndaten unter Verwendung von Primärschlüsseln
        String updateQuery = "UPDATE KARTE " +
                "SET LETZTES_LERNDATUM = " + aktuellesDatum +
                ", NAECHSTES_LERNDATUM = " + neuesDatum +
                " WHERE KARTE_ID = " + karteId +
                " AND STAPEL_ID = " + stapelId +
                " AND SET_ID = " + setId;

        // Führe die SQL-Anweisung aus
        db.execSQL(updateQuery);

        // Schließe die Verbindung zur Datenbank
        db.close();
    }

    private long berechneNeuesDatum(int bewertung, long aktuellesDatum) {
        // Hier implementierst du deine Logik für die Berechnung des neuen Datums basierend auf der Bewertung
        // Beispiellogik:
        switch (bewertung) {
            case 1: // nochmal
                return aktuellesDatum + TimeUnit.SECONDS.toMillis(5);
            case 2: // schwer
                return aktuellesDatum + TimeUnit.SECONDS.toMillis(10);
            case 3: // gut
                return aktuellesDatum + TimeUnit.SECONDS.toMillis(15);
            case 4: // einfach
                return aktuellesDatum + TimeUnit.SECONDS.toMillis(25);
            default:
                return aktuellesDatum; // Standardfall
        }
    }
    // Methode, um die Karte mit dem ältesten naechstesLerndatum zu erhalten
    public Karte waehleAeltesteFaelligeKarte(int stapelId, int setId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Karte aeltesteKarte = null;

        // SQL-Abfrage, um die Karte mit dem ältesten naechstesLerndatum zu erhalten
        String selectQuery = "SELECT * FROM KARTE " +
                "WHERE STAPEL_ID = ? AND SET_ID = ? " +
                "ORDER BY NAECHSTES_LERNDATUM ASC LIMIT 1";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(stapelId), String.valueOf(setId)});

        if (cursor != null && cursor.moveToFirst()) {
            // Extrahiere die Daten aus dem Cursor und erstelle ein Karte-Objekt
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("KARTE_ID"));
            String frage = cursor.getString(cursor.getColumnIndexOrThrow("KARTE_FRAGE"));
            String antwort = cursor.getString(cursor.getColumnIndexOrThrow("KARTE_ANTWORT"));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow("KARTE_STATUS"));
            long letztesLerndatum = cursor.getLong(cursor.getColumnIndexOrThrow("LETZTES_LERNDATUM"));
            long naechstesLerndatum = cursor.getLong(cursor.getColumnIndexOrThrow("NAECHSTES_LERNDATUM"));

            aeltesteKarte = new Karte(id, frage, antwort, status , stapelId, setId, letztesLerndatum, naechstesLerndatum);
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();

        return aeltesteKarte;
    }

    public List<Karte> waehleZuLernendeKarten() {
        List<Karte> zuLernendeKarten = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        long aktuellesDatum = System.currentTimeMillis();
        long endeDesTagesHeuteDatum = aktuellesDatum + + TimeUnit.DAYS.toMillis(1) - 1; // bis 23:59:59 Uhr

        // SQL-Anweisung zum Auswählen von Karten, die jetzt gelernt werden sollen
        String selectQuery = "SELECT * FROM KARTE " +
                "WHERE NAECHSTES_LERNDATUM <= " + aktuellesDatum;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Daten aus dem Cursor extrahieren und in Karte-Objekte umwandeln
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("KARTE_ID"));
                String frage = cursor.getString(cursor.getColumnIndexOrThrow("KARTE_FRAGE"));
                String antwort = cursor.getString(cursor.getColumnIndexOrThrow("KARTE_ANTWORT"));
                int status = cursor.getInt(cursor.getColumnIndexOrThrow("KARTE_STATUS"));
                int stapelId = cursor.getInt(cursor.getColumnIndexOrThrow("STAPEL_ID"));
                int setId = cursor.getInt(cursor.getColumnIndexOrThrow("SET_ID"));
                long letztesLerndatum = cursor.getLong(cursor.getColumnIndexOrThrow("LETZTES_LERNDATUM"));
                long naechstesLerndatum = cursor.getLong(cursor.getColumnIndexOrThrow("NAECHSTES_LERNDATUM"));

                Karte karte = new Karte(id, frage, antwort, status, stapelId, setId, letztesLerndatum, naechstesLerndatum);
                zuLernendeKarten.add(karte);
            } while (cursor.moveToNext());

            cursor.close();
        }
        // Schließe die Verbindung zur Datenbank
        db.close();

        return zuLernendeKarten;
    }

    // STAPEL-TABLE-METHODEN------------------------------------------------------------------------------------------------------------------------
    public void insertStapel(String stapelName, String stapelBeschreibung, String stapelFarbe, int stapelStatus, int setID){
        SQLiteDatabase db = this.getWritableDatabase();

        // Abfrage, um den höchsten Wert von STAPEL_ID für die gegebene SET_ID zu ermitteln
        Cursor cursor = db.rawQuery("SELECT MAX(STAPEL_ID) FROM STAPEL WHERE SET_ID = ?", new String[]{valueOf(setID)});
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
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM KARTE WHERE STAPEL_ID = " + stapelID + " AND SET_ID = " + setID,null);
        cursor.moveToFirst();
        float summeAll = cursor.getInt(0);
        Log.d("COUNT*",valueOf(summeAll));

        cursor = db.rawQuery("SELECT COUNT(KARTE_STATUS) FROM KARTE WHERE STAPEL_ID = " + stapelID + " AND SET_ID = " + setID + " AND KARTE_STATUS = 3",null);
        cursor.moveToFirst();
        float summeStatus3 = cursor.getInt(0);
        Log.d("COUNT3",valueOf(summeStatus3));

        if(summeAll == 0){  // verhindert, dass durch 0 geteilt wird.
            return 0;
        }
        float ret = (summeStatus3 / summeAll) * 100;  // Berechnet den prozentualen Wert...
        Log.d("RET",valueOf(ret));
        if(ret < 0){
            ret = 0;
        }
        if(ret > 100){
            ret = 100;
        }
        Log.d("RET",valueOf(ret));
        int i;
        i = (int) ret;

        String updateQuery = "UPDATE STAPEL SET STAPEL_STATUS = " + i + " WHERE STAPEL_ID = " + stapelID + " AND SET_ID = " + setID;
        db.execSQL(updateQuery);
        db.close();
        return i;
    }

    public int getAnzKartenInStapel(int stapelID, int setID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d("STAPEL_ID",valueOf(stapelID));
        Log.d("SET_ID",valueOf(setID));
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM KARTE WHERE STAPEL_ID = " + stapelID + " AND SET_ID = " + setID, null);
        cursor.moveToFirst();
        int summeAll = cursor.getInt(0);
        db.close();
        return summeAll;
    }

    public int getAnzSichereKartenInStapel(int stapelID, int setID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(KARTE_STATUS) FROM KARTE WHERE STAPEL_ID = " + stapelID + " AND SET_ID = " + setID + " AND KARTE_STATUS = 3",null);
        cursor.moveToFirst();
        int summeStatus3 = cursor.getInt(0);
        db.close();
        return summeStatus3;
    }

    public void deleteStapel(int stapelID, int setID) {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = "STAPEL_ID = ? AND SET_ID = ?";
        String[] whereArgs = {valueOf(stapelID), valueOf(setID)};

        // Lösche den Stapel aus der Tabelle "STAPEL"
        db.delete("STAPEL", whereClause, whereArgs);
        db.delete("KARTE", whereClause, whereArgs);
        db.close();
    }
    public Cursor getAllStapelFromSetID(int setId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT STAPEL_ID AS _id, STAPEL_NAME, SET_ID FROM STAPEL WHERE SET_ID ="+setId, null);
        cursor.moveToFirst();
        db.close();
        return cursor;
    }

    public String getStapelName(int stapelID, int setID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT STAPEL_NAME FROM STAPEL WHERE STAPEL_ID = " + stapelID + " AND " + "SET_ID = " + setID,null);
        cursor.moveToFirst();
        String name = cursor.getString(0);
        db.close();
        return name;
    }

    public void updateStapelName(int stapelID, int setID, String stapelName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updateValues = new ContentValues();
        updateValues.put("STAPEL_NAME", stapelName);

        db.update("STAPEL", updateValues, "SET_ID = ? AND STAPEL_ID = ?", new String[]{String.valueOf(setID), String.valueOf(stapelID)});

        // Schließe die Datenbankverbindung
        db.close();
    }
    // SET-TABLE-METHODEN------------------------------------------------------------------------------------------------------------------------
    public void insertSet(String name,String beschreibung, String farbe, int setStatus){
        ContentValues neueZeile = new ContentValues();
        neueZeile.put("SET_NAME", name);
        neueZeile.put("SET_BESCHREIBUNG", beschreibung);
        neueZeile.put("SET_FARBE", farbe);
        neueZeile.put("SET_STATUS", setStatus);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("STAPELSET", null, neueZeile);
        db.close();
    }

    public Cursor getAllSets() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT SET_ID AS _id, SET_NAME, SET_BESCHREIBUNG, SET_FARBE, SET_STATUS FROM STAPELSET",null);
        if(cursor != null)
            cursor.moveToFirst();
        db.close();
        return cursor;
    }

    public int getSetID(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM STAPELSET WHERE SET_ID =" + id,null);
        cursor.moveToFirst();
        db.close();
        return cursor.getInt(0);
    }
    public int getMaxSetID(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor maxID = db.rawQuery("SELECT MAX(SET_ID) FROM STAPELSET",null);
        maxID.moveToFirst();
        int id = maxID.getInt(0);
        db.close();
        return id;
    }

    public String getSetName(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor name = db.rawQuery("SELECT SET_NAME FROM STAPELSET WHERE SET_ID = " + id,null);
        name.moveToFirst();
        String result = name.getString(0);
        db.close();
        return result;
    }

    public String getSetBeschreibung(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor beschr = db.rawQuery("SELECT SET_BESCHREIBUNG FROM STAPELSET WHERE SET_ID = " + id,null);
        beschr.moveToFirst();
        String result = beschr.getString(0);
        db.close();
        return result;
    }

    public String getSetFarbe(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor farbe = db.rawQuery("SELECT SET_FARBE FROM STAPELSET WHERE SET_ID = " + id,null);
        farbe.moveToFirst();
        String result = farbe.getString(0);
        db.close();
        return result;
    }

    public int getSetProgress(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor progress = db.rawQuery("SELECT SET_STATUS FROM STAPELSET WHERE SET_ID =" + id,null);
        Log.d("JETZT:",valueOf(id));
        progress.moveToFirst();
        int setProgress = progress.getInt(0);
        Log.d("PROGRESS:",valueOf(setProgress));
        db.close();
        return setProgress;
    }

    public void setSetProgress(int setID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM STAPEL WHERE SET_ID = " + setID,null);
        cursor.moveToFirst();
        float summeAll = cursor.getInt(0);
        Log.d("summeAll:",valueOf(summeAll));
        cursor = db.rawQuery("SELECT COUNT(*) FROM STAPEL WHERE SET_ID = " + setID + " AND STAPEL_STATUS = 100",null);
        cursor.moveToFirst();
        float summeStatus3 = cursor.getInt(0);
        Log.d("setID",valueOf(setID));
        Log.d("summeStatus3:",valueOf(summeStatus3));
        if(summeAll == 0){  // verhindert, dass durch 0 geteilt wird.
            String query = "UPDATE STAPELSET SET SET_STATUS = 0 WHERE SET_ID = " + valueOf(setID);
        }
        float ret = (summeStatus3 / summeAll) * 100;  // Berechnet den prozentualen Wert...
        if(ret < 0){
            ret = 0;
        }
        if(ret > 100){
            ret = 100;
        }
        Log.d("RET",valueOf(ret));
        int i;
        i = (int) ret;
        String query = "UPDATE STAPELSET SET SET_STATUS = " + valueOf(i) + " WHERE SET_ID = " + valueOf(setID);
        db.execSQL(query);
    }

    public void deleteSetWithID(int setID) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Lösche das Set und verknüpfte Stapel und Karten mithilfe der CASCADE-Option
        db.delete("STAPELSET", "SET_ID = ?", new String[]{valueOf(setID)});

        db.close();
    }

    public void updateSet(int id, String setName, String setBeschreibung, String selectedColor) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues updateValues = new ContentValues();
        updateValues.put("SET_NAME", setName);
        updateValues.put("SET_BESCHREIBUNG", setBeschreibung);
        updateValues.put("SET_FARBE", selectedColor);

        db.update("STAPELSET", updateValues, "SET_ID = ?", new String[]{String.valueOf(id)});

        db.close();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



}
