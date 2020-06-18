package com.darryncampbell.datawedge_component_sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String decodedData = intent.getStringExtra("com.symbol.datawedge.data_string");
                String decodedLabelType = intent.getStringExtra("com.symbol.datawedge.label_type");
                Toast.makeText(context.getApplicationContext(), "" + decodedData + " [" + decodedLabelType + "]", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
