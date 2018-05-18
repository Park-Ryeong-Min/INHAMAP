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

import com.example.inhamap.Activities.MainActivity;
import com.example.inhamap.Commons.GlobalApplication;
import com.example.inhamap.Models.AdjacentEdge;
import com.example.inhamap.Models.EdgeList;
import com.example.inhamap.Models.NodeItem;
import com.example.inhamap.R;
import com.example.inhamap.Utils.ValueConverter;

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
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inSampleSize = 20;
        this.locationIcon = BitmapFactory.decodeResource(res, R.drawable.my_location_icon, option);

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
            //canvas.drawBitmap(locationIcon, locationX, locationY, new Paint());
        }else{
            //canvas.drawBitmap(locationIcon, dipToPixels(this.context, GlobalApplication.myLocationLeft), dipToPixels(this.context, GlobalApplication.myLocationTop), new Paint());
            //NodeItem tmp = ValueConverter.getNearestNodeItem(GlobalApplication.myLocationLatitude, GlobalApplication.myLocationLongitude);
            //float l = tmp.getMarginLeft();
            //float t = tmp.getMarginTop();
            Log.e("DRAWING_VIEW", Float.toString(GlobalApplication.myLocationLeft) + " , " + Float.toString(GlobalApplication.myLocationTop));
            canvas.drawBitmap(locationIcon, dipToPixels(this.context, GlobalApplication.myLocationLeft), dipToPixels(this.context, GlobalApplication.myLocationTop), new Paint());
            for(int i = 0; i < MainActivity.imageButtons.size(); i++){
                NodeImageButton btn = MainActivity.imageButtons.get(i);
                if(btn.getNodeID() == GlobalApplication.myLocationNodeID){
                    Log.e("FOCUS","request focus");
                    btn.requestFocus();
                }
            }
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

    public void drawLocation(float left, float top){
        Log.e("MY_LOCATION" , Float.toString(left) + " , " + Float.toString(top));
        this.drawLocation = true;
        this.locationX = dipToPixels(this.context, left) - 50f;
        this.locationY = dipToPixels(this.context, top) - 50f;
        invalidate();
    }

    public boolean isEdgeDraw(){
        return this.drawEdges;
    }
}
