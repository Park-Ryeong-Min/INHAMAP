package com.example.inhamap.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.inhamap.Threads.DrawPathOnSurfaceViewThread;

/**
 * Created by myown on 2018. 4. 28..
 */

public class PathDrawingSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Context context;
    private SurfaceHolder holder;
    private Canvas canvas;
    private DrawPathOnSurfaceViewThread thread;

    public PathDrawingSurfaceView(Context context){
        super(context);
        init(context);
    }

    public PathDrawingSurfaceView(Context context, AttributeSet attr){
        super(context, attr);
        init(context);
    }

    private void init(Context context){
        Log.e("SURFACE_VIEW", "View created.");
        this.context = context;
        holder = getHolder();
        getHolder().addCallback(this);
        this.setZOrderOnTop(true);
        this.holder.setFormat(PixelFormat.TRANSPARENT);
        //this.thread = new DrawPathOnSurfaceViewThread(this.holder, this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e("SURFACE_VIEW", "Surface changed.");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("SURFACE_VIEW", "Surface destroyed.");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("SURFACE_VIEW", "Surface created.");
        this.canvas = holder.lockCanvas();
        //this.setWillNotDraw(false); // required
        this.holder.unlockCanvasAndPost(this.canvas);
        thread.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.e("SURFACE_VIEW", "On draw method called.");
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        if(canvas == null){
            Log.e("ERROR", "canvas is null");
        }
        if(paint == null){
            Log.e("ERROR", "paint is null");
        }
        canvas.drawLine(10f, 10f, 100f, 100f, paint);
        canvas.drawColor(Color.CYAN);
    }

    public Canvas getCanvas(){
        return this.canvas;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setWillNotDraw(false);
    }
}
