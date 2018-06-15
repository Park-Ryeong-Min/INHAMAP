package com.example.inhamap.Runnables;

import android.content.Context;
import android.widget.Toast;

public class ToastMessageFromOtherThread implements Runnable {

    private String toastMessage;
    private Context context;

    public ToastMessageFromOtherThread(Context context){
        this.context = context;
        this.toastMessage = "";
    }

    public void setToastMessage(String text){
        this.toastMessage = text;
    }

    @Override
    public void run() {
        Toast.makeText(context, this.toastMessage, Toast.LENGTH_LONG).show();
    }
}
