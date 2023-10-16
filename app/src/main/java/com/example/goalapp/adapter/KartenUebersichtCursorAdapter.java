package com.example.goalapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.goalapp.R;


public class KartenUebersichtCursorAdapter extends CursorAdapter {

    Context context;
    public KartenUebersichtCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.karte_listenelement, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        String frage = cursor.getString(cursor.getColumnIndexOrThrow("KARTE_FRAGE"));
        String antwort = cursor.getString(cursor.getColumnIndexOrThrow("KARTE_ANTWORT"));

        TextView idText = view.findViewById(R.id.frageID);
        TextView frageText = view.findViewById(R.id.frageUebersicht);
        TextView antwortText = view.findViewById(R.id.antwortUebersicht);

        idText.setText(""+id);
        frageText.setText(frage);
        antwortText.setText(antwort);


    }
}
