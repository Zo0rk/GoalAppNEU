package com.example.goalapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
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
        String stapelId = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
        String stapelName = cursor.getString(cursor.getColumnIndexOrThrow("STAPEL_NAME"));
        String setId = cursor.getString(cursor.getColumnIndexOrThrow("SET_ID"));

        DatenBankManager db = new DatenBankManager(context);
        int count = db.countKartenInSetUndStapel(setId, stapelId);

        TextView blueNumber = view.findViewById(R.id.blueNumber);
        blueNumber.setText(""+count);

        TextView name = view.findViewById(R.id.stapelListenElementName);
        name.setText(stapelName);
    }

    @Override
    public long getItemId(int position) {
        if (getCursor() != null) {
            // Bewegen Sie den Cursor zur gewünschten Position
            getCursor().moveToPosition(position);

            // Hier können Sie die ID aus der Datenbank abrufen
            long itemId = getCursor().getInt(getCursor().getColumnIndexOrThrow("_id"));
            Log.d("Debug", "Stapel ID: "+itemId);
            return itemId;
        }
        return super.getItemId(position);
    }
}
