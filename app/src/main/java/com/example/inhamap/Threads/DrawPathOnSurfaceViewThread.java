package com.example.inhamap.Threads;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;

import com.example.inhamap.Components.LocationDrawingSurfaceView;
import com.example.inhamap.Components.PathDrawingSurfaceView;
import com.example.inhamap.Models.AdjacentEdge;
import com.example.inhamap.Models.EdgeList;
import com.example.inhamap.Models.NodeItem;

/**
 * Created by myown on 2018. 4. 29..
 */

public class DrawPathOnSurfaceViewThread extends Thread {

    private SurfaceHolder holder;
    private LocationDrawingSurfaceView surfaceView;
    private EdgeList path;
    private Context context;

    public DrawPathOnSurfaceViewThread(SurfaceHolder holder, LocationDrawingSurfaceView view){
        Log.e("THREAD", "Thread constructor.");
        this.holder = holder;
        this.surfaceView = view;
    }

    @Override
    public void run() {
        surfaceView.invalidate();
        Canvas canvas = null;

        canvas = holder.lockCanvas();

        Log.e("TREAD", "Thread runs.");
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10);
        //canvas.drawLine(10f, 10f, 1000f, 1000f, paint);
        synchronized (holder) {
            /*
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(10);
            */
            Log.e("THREAD", "Draw path.");
            for (int i = 0; i < path.size(); i++) {
                AdjacentEdge edge = path.getEdge(i);
                NodeItem[] nodes = edge.getNodes();
                float sX = (float) nodes[0].getMarginLeft();
                float sY = (float) nodes[0].getMarginTop();
                float eX = (float) nodes[1].getMarginLeft();
                float eY = (float) nodes[1].getMarginTop();
                sX = dipToPixels(this.context, sX);
                sY = dipToPixels(this.context, sY);
                eX = dipToPixels(this.context, eX);
                eY = dipToPixels(this.context, eY);
                canvas.drawLine(sX, sY, eX, eY, paint);
            }
        }
        holder.unlockCanvasAndPost(canvas);
    }

    public void setPath(EdgeList path){
        this.path = path;
    }

    public void setContext(Context context){
        this.context = context;
    }

    public float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
}
