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
    private boolean graduated;
    private final double intervalmodifier = 1.5; // Konstante
    private int againInterval;
    private int hardInterval;
    private int goodInterval;
    private int easyInterval;

    // Konstruktor
    public Karte(int karteId, String frage, String antwort, int status, int stapelId, int setId,
                 long letztesLerndatum, long naechstesLerndatum, double easinessFactor, int interval, boolean graduated) {
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
        this.graduated = graduated;

        selectIntervals();
    }

    public void selectIntervals() {
        if(!graduated) {
            againInterval = 60000;
            hardInterval = 60000 * 3;
            goodInterval = 60000 * 10;
            easyInterval = 60000 * 60 * 24;
        } else {
            againInterval = 60000; // 1 min
            hardInterval = 60000 * 10; // 10 min
            goodInterval = 60000 * 60 * 24; // 1 tag
            easyInterval = 60000 * 60 * 24 * 4; // 4 Tage
        }
    }

    public void updateInterval(int grade) {
        selectIntervals();
            switch (grade) {
                case 1:
                    interval = againInterval;
                    graduated = false;
                    break;
                case 2:
                    interval = hardInterval;
                    break;
                case 3:
                    interval = goodInterval;
                    graduated = true;
                    break;
                case 4:
                    interval = easyInterval;
                    graduated = true;
                    break;
            }
            updateDates();
        }


    public void updateEasinessFactor(int grade) {
        easinessFactor = easinessFactor + 0.1 - (4 - grade) * (0.08 + (4 - grade) * 0.02);
        if (easinessFactor < 1.3) {
            easinessFactor = 1.3; // Mindestwert für den Easiness-Faktor
        }
    }

    public void updateDates() {
        letztesLerndatum = System.currentTimeMillis();
        // damit evtl nicht immer die selbe karte kommt
        if(naechstesLerndatum> System.currentTimeMillis())
            naechstesLerndatum += interval;
        else{
            naechstesLerndatum = System.currentTimeMillis() + interval;
        }
    }

    public static String fromMillisecondsToMinOrDays(int milliseconds) {
        if(milliseconds >= (24*60*60000)){
            return(milliseconds/(24*60*60000) + " Tage");
        }
        else{
            return (milliseconds/60000 + " Min");
        }
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
    public boolean isGraduated() {
        return graduated;
    }

    public void setGraduated(boolean graduated) {
        this.graduated = graduated;
    }

    public double getIntervalmodifier() {
        return intervalmodifier;
    }

    public int getAgainInterval() {
        return againInterval;
    }

    public void setAgainInterval(int againInterval) {
        this.againInterval = againInterval;
    }

    public int getHardInterval() {
        return hardInterval;
    }

    public void setHardInterval(int hardInterval) {
        this.hardInterval = hardInterval;
    }

    public int getGoodInterval() {
        return goodInterval;
    }

    public void setGoodInterval(int goodInterval) {
        this.goodInterval = goodInterval;
    }

    public int getEasyInterval() {
        return easyInterval;
    }

    public void setEasyInterval(int easyInterval) {
        this.easyInterval = easyInterval;
    }
}
