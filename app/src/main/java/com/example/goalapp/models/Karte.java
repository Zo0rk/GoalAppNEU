package com.example.goalapp.models;

import java.time.LocalDate;

public class Karte implements Comparable<Karte> {
    private int karteId;
    private String frage;
    private String antwort;
    private int status;
    private int stapelId;
    private int setId;
    private long letztesLerndatum;
    private long naechstesLerndatum;
    private double easinessFactor;
    private int interval;
    private final double intervalmodifier = 1.5; //Konstante

    // Konstruktor
    public Karte(int karteId, String frage, String antwort, int status, int stapelId, int setId, long letztesLerndatum, long naechstesLerndatum, double easinessFactor, int interval) {
        this.karteId = karteId;
        this.frage = frage;
        this.antwort = antwort;
        this.status = status;
        this.stapelId = stapelId;
        this.setId = setId;
        this.letztesLerndatum = letztesLerndatum;
        this.naechstesLerndatum = naechstesLerndatum;
        this.easinessFactor = easinessFactor;
        this.interval = interval;
    }

    public void updateEasinessFactor(double grade) {
        easinessFactor = easinessFactor + 0.1 - (4 - grade) * (0.08 + (4 - grade) * 0.02);
        if (easinessFactor < 1.3) {
            easinessFactor = 1.3; // Mindestwert für den Easiness-Faktor
        }
        if (grade > 1) {
            updateInterval(true);
        } else {
            updateInterval(false);
        }


    }
    public void updateInterval(boolean useIntervalmodifier) {
        if(useIntervalmodifier) {
            interval = (int) (interval * intervalmodifier * easinessFactor);
        } else {
            interval = (int) (interval * easinessFactor);
        }
        updateDates();
    }

    public void updateDates() {
        letztesLerndatum = System.currentTimeMillis();
        naechstesLerndatum = letztesLerndatum + interval;
    }
    
    // Getter und Setter für alle Felder

    public double getEasinessFactor() {
        return easinessFactor;
    }

    public void setEasinessFactor(double easinessFactor) {
        this.easinessFactor = easinessFactor;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

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
