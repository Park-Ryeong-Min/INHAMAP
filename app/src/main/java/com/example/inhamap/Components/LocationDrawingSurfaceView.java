package com.example.inhamap.Components;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.inhamap.Threads.DrawOnSurfaceViewThread;

public class LocationDrawingSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    public DrawOnSurfaceViewThread thread;

    public LocationDrawingSurfaceView(Context context){
        super(context);
        init();
    }

    public LocationDrawingSurfaceView(Context context, AttributeSet attr){
        super(context, attr);
        init();
    }

    private void init(){
        getHolder().addCallback(this);
        this.thread = new DrawOnSurfaceViewThread();
        getHolder().addCallback(this);
        this.setZOrderMediaOverlay(true);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("SURFACE_VIEW", "Surface created.");
        //this.thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e("SURFACE_VIEW", "Surface changed.");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("SURFACE_VIEW", "Surface destroyed.");
    }


}
