package com.example.goalapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class setAdapter extends CursorAdapter {

    LayoutInflater meinLayoutInflater;
    int itemLayout;
    String[] from;
    int[] to;

    public setAdapter(Context ctx, int itemLayout, Cursor c, String[] from, int[] to, int flags) {
        super(ctx, c, flags);
        meinLayoutInflater = LayoutInflater.from(ctx);
        this.itemLayout = itemLayout;
        this.from = from;
        this.to = to;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = meinLayoutInflater.inflate(itemLayout, parent, false);
        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {
        String farbe = c.getString(c.getColumnIndexOrThrow(from[0]));
        TextView textView1 = (TextView) v.findViewById(to[0]);
        textView1.setText(farbe);

        String stapelName = c.getString(c.getColumnIndexOrThrow(from[1]));
        TextView textView2 = (TextView) v.findViewById(to[1]);
        textView2.setText(stapelName);

        int stapelStatus = c.getInt(c.getColumnIndexOrThrow(from[2]));
        ProgressBar progressBar = (ProgressBar) v.findViewById(to[2]);
        progressBar.setProgress(stapelStatus);
    }
}
