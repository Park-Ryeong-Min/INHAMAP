package com.example.inhamap.Threads;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;


public class ToastInThread {

    private final Handler handler;
    private Context context;

    public ToastInThread(Context context){
        context = context;
        handler = new Handler(context.getMainLooper());
    }

    public void showToastMessage(String text){
        final String t = text;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, t, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void runOnUiThread(Runnable r){
        handler.post(r);
    }

}
