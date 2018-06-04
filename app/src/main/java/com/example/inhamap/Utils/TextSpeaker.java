package com.example.inhamap.Utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class TextSpeaker implements TextToSpeech.OnInitListener {

    TextToSpeech tts;
    String text;

    public TextSpeaker(Context context, String text){
        tts = new TextToSpeech(context, this);
        this.text = text;
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            tts.setLanguage(Locale.KOREA);
            tts.setPitch(3);
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            while(tts.isSpeaking());
        }
    }

}
