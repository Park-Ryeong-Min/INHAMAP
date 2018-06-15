package com.example.inhamap.Runnables;

import com.example.inhamap.Commons.GlobalApplication;

public class StringPassBetweenThread implements Runnable {

    private String text;

    public StringPassBetweenThread(String text){
        this.text = text;
    }

    public void setText(String t){
        this.text = t;
    }

    @Override
    public void run() {
        GlobalApplication.mainLowerTextView.setText(this.text);
    }
}
