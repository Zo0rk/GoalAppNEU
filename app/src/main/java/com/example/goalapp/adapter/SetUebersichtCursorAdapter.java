package com.example.goalapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.goalapp.R;

public class SetUebersichtCursorAdapter extends CursorAdapter {

    Context context;

    public SetUebersichtCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.context = context;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflates the custom list item layout
        return LayoutInflater.from(context).inflate(R.layout.stapel_listenelement, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String stapelName = cursor.getString(cursor.getColumnIndexOrThrow("STAPEL_NAME"));
        TextView textView = view.findViewById(R.id.stapelListenElementName);
        textView.setText(stapelName);
    }
}
