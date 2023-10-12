package com.example.goalapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.goalapp.R;
import com.example.goalapp.database.DatenBankManager;

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
        DatenBankManager db = new DatenBankManager(context);

        String stapelId = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
        String stapelName = cursor.getString(cursor.getColumnIndexOrThrow("STAPEL_NAME"));

        int count = db.countKarten(Integer.parseInt(stapelId));
        TextView blueNumber = view.findViewById(R.id.blueNumber);
        blueNumber.setText(""+count);

        TextView name = view.findViewById(R.id.stapelListenElementName);
        name.setText(stapelName);
    }
}
