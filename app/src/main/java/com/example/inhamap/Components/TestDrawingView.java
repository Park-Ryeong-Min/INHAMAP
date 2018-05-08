package com.example.inhamap.Components;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.example.inhamap.Models.AdjacentEdge;
import com.example.inhamap.Models.EdgeList;
import com.example.inhamap.Models.NodeItem;
import com.example.inhamap.R;

/**
 * Created by myown on 2018. 4. 29..
 */

public class TestDrawingView extends View {

    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private float locationX;
    private float locationY;
    private boolean isDrawing = true;
    private boolean drawEdges = false;
    private boolean drawLocation = false;
    private Context context;
    private EdgeList edges;
    private Bitmap locationIcon;

    public TestDrawingView(Context context){
        super(context);
        init(context);
    }

    public TestDrawingView(Context context, AttributeSet attr){
        super(context, attr);
        init(context);
    }

    private void init(Context context){
        this.context = context;
        Resources res = getResources();
        this.locationIcon = BitmapFactory.decodeResource(res, R.drawable.node_icon_1);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.e("TEST_VIEW", "Canvas loaded.");
        if(!isDrawing){
            isDrawing = true;
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(10);
            canvas.drawLine(startX, startY, endX, endY, paint);
        }else{
            isDrawing = false;
        }
        if(drawEdges){
            Log.e("TEST_VIEW", "Drawing paths.");
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(10);
            for(int i = 0; i < this.edges.size(); i++){
                AdjacentEdge edge = this.edges.getEdge(i);
                NodeItem[] nodes = edge.getNodes();
                float sX = (float)nodes[0].getMarginLeft();
                float sY = (float)nodes[0].getMarginTop();
                float eX = (float)nodes[1].getMarginLeft();
                float eY = (float)nodes[1].getMarginTop();
                sX = dipToPixels(this.context, sX);
                sY = dipToPixels(this.context, sY);
                eX = dipToPixels(this.context, eX);
                eY = dipToPixels(this.context, eY);
                canvas.drawLine(sX, sY, eX, eY, paint);
            }
        }else{
            Log.e("TEST_VIEW", "Clear paths.");
        }
        if(drawLocation){
            canvas.drawBitmap(locationIcon, 100f, 100f, new Paint());
        }
    }

    public void drawLine(float startX, float startY, float endX, float endY){
        this.startX = dipToPixels(this.context, startX);
        this.startY = dipToPixels(this.context, startY);
        this.endX = dipToPixels(this.context, endX);
        this.endY = dipToPixels(this.context, endY);
        invalidate();
    }

    public float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public void drawEdges(EdgeList edges){
        this.edges = edges;
        drawEdges = true;
        invalidate();
    }

    public void clearEdges(){
        this.drawEdges = false;
        invalidate();
    }

    public void drawLocation(double lat, double lng){
        Log.e("MY_LOCATION" , Double.toString(lat) + " , " + Double.toString(lng));
        this.drawLocation = true;
        invalidate();
    }

    public boolean isEdgeDraw(){
        return this.drawEdges;
    }
}
