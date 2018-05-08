package com.example.inhamap.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.inhamap.Models.EdgeList;
import com.example.inhamap.Threads.DrawPathOnSurfaceViewThread;

public class LocationDrawingSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    public DrawPathOnSurfaceViewThread thread;
    private SurfaceHolder holder;
    private Context context;
    private EdgeList currentPath;

    public LocationDrawingSurfaceView(Context context){
        super(context);
        init(context);
    }

    public LocationDrawingSurfaceView(Context context, AttributeSet attr){
        super(context, attr);
        init(context);
    }

    private void init(Context context){
        this.context = context;
        this.holder = getHolder();
        getHolder().addCallback(this);
        this.thread = new DrawPathOnSurfaceViewThread(this.holder, this);
        getHolder().addCallback(this);
        this.setZOrderMediaOverlay(true);
        this.setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("SURFACE_VIEW", "Surface created.");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e("SURFACE_VIEW", "Surface changed.");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("SURFACE_VIEW", "Surface destroyed.");
    }

    public void drawingPath(EdgeList path){
        invalidate();
        this.currentPath = path;
        if(this.thread != null){
            if(this.thread.isAlive()){
                this.thread.interrupt();
                Log.e("THREAD", "Is alive.");
            }
        }else{
            Log.e("THREAD", "Thread is null");
        }
        this.thread = null;
        setThread(this.holder, this, this.context, this.currentPath);
        this.thread.start();
    }

    public void clearPath(){
        invalidate();
        Canvas c = this.holder.lockCanvas();
        c.drawColor(Color.TRANSPARENT);
    }

    private void setThread(SurfaceHolder holder, LocationDrawingSurfaceView view, Context context, EdgeList path){
        this.thread = new DrawPathOnSurfaceViewThread(holder, view);
        this.thread.setContext(context);
        this.thread.setPath(path);
    }
}
