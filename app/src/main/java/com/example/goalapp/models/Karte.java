package com.example.goalapp.models;

public class Karte implements Comparable<Karte> {
    private int karteId;
    private String frage;
    private String antwort;
    private int status;
    private int stapelId;
    private int setId;
    private long letztesLerndatum;
    private long naechstesLerndatum;

    // Konstruktor
    public Karte(int karteId, String frage, String antwort, int status, int stapelId, int setId, long letztesLerndatum, long naechstesLerndatum) {
        this.karteId = karteId;
        this.frage = frage;
        this.antwort = antwort;
        this.status = status;
        this.stapelId = stapelId;
        this.setId = setId;
        this.letztesLerndatum = letztesLerndatum;
        this.naechstesLerndatum = naechstesLerndatum;
    }

    // Getter und Setter für alle Felder

    public int getKarteId() {
        return karteId;
    }

    public void setKarteId(int karteId) {
        this.karteId = karteId;
    }

    public String getFrage() {
        return frage;
    }

    public void setFrage(String frage) {
        this.frage = frage;
    }

    public String getAntwort() {
        return antwort;
    }

    public void setAntwort(String antwort) {
        this.antwort = antwort;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStapelId() {
        return stapelId;
    }

    public void setStapelId(int stapelId) {
        this.stapelId = stapelId;
    }

    public int getSetId() {
        return setId;
    }

    public void setSetId(int setId) {
        this.setId = setId;
    }

    public long getLetztesLerndatum() {
        return letztesLerndatum;
    }

    public void setLetztesLerndatum(long letztesLerndatum) {
        this.letztesLerndatum = letztesLerndatum;
    }

    public long getNaechstesLerndatum() {
        return naechstesLerndatum;
    }

    public void setNaechstesLerndatum(long naechstesLerndatum) {
        this.naechstesLerndatum = naechstesLerndatum;
    }

    @Override
    public int compareTo(Karte other) {
        return Long.compare(this.naechstesLerndatum, other.naechstesLerndatum);
    }
}
