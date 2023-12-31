package com.example.goalapp.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.goalapp.R;
import com.example.goalapp.database.DatenBankManager;

import java.util.Map;

public class MainUebersichtCursorAdapter extends CursorAdapter {

    Context context;

    public MainUebersichtCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.context = context;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflates the custom list item layout
        return LayoutInflater.from(context).inflate(R.layout.set_listelement, parent, false);
}

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Retrieve Data from cursor
        DatenBankManager db = new DatenBankManager(context);


        String setName = cursor.getString(cursor.getColumnIndexOrThrow("SET_NAME"));
        String setFarbe = cursor.getString(cursor.getColumnIndexOrThrow("SET_FARBE"));
        String setStatus = cursor.getString(cursor.getColumnIndexOrThrow("SET_STATUS"));

        // Find the TextViews and ProgressBar in the layout
        TextView nameTextView = view.findViewById(R.id.set_listenelement_name);
        ProgressBar setProgressBar = view.findViewById(R.id.STAPEL_FORTSCHRITT_ANZEIGE);
        nameTextView.setText(setName);

        int colorRes = context.getResources().getIdentifier(setFarbe, "color", context.getPackageName());
        int textColor = ContextCompat.getColor(context, colorRes);
        nameTextView.setTextColor(textColor);

        int progress = db.calculateProgress(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
        setProgressBar.setProgress(progress);
        setProgressBar.setProgressTintList(ColorStateList.valueOf(textColor));
    }

    @Override
    public long getItemId(int position) {
        if (getCursor() != null) {
            // Bewegen Sie den Cursor zur gewünschten Position
            getCursor().moveToPosition(position);

            // Hier können Sie die ID aus der Datenbank abrufen
            long itemId = getCursor().getInt(getCursor().getColumnIndexOrThrow("_id"));
            Log.d("Debug", "Set ID: "+itemId);
            return itemId;
        }
        return super.getItemId(position);
    }
}
