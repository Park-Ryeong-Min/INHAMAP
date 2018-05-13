package com.example.inhamap.Fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.example.inhamap.Components.FindMyLocationButton;
import com.example.inhamap.Components.LocationDrawingSurfaceView;
import com.example.inhamap.Components.PathDrawingSurfaceView;
import com.example.inhamap.Components.TestDrawingView;
import com.example.inhamap.R;
import com.example.inhamap.Utils.AllocateImageButtonInFragment;

/**
 * Created by myown on 2018. 4. 18..
 */

public class CustomMapFragment extends Fragment implements View.OnTouchListener{

    private ScrollView vScroll;
    private HorizontalScrollView hScroll;
    private static int Xpos = 0;
    private static int Ypos = 0;
    private View view;
    private ViewGroup viewGroup;
    private RelativeLayout layout;
    private Context context;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private float curScale = 1f;

    //private FindMyLocationButton myLocationButton;
    private TestDrawingView drawingView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // fragment layout 추가
        View view = inflater.inflate(R.layout.fragment_custom_map, null);
        this.viewGroup = container;
        this.viewGroup.setWillNotDraw(false);
        this.view = view;
        this.view.setWillNotDraw(false);
        initSetting();
        return view;
    }

    private void eventSetting(){

    }

    private void initSetting(){

        layout = (RelativeLayout)view.findViewById(R.id.map_fragment_frame_layout);

        vScroll = (ScrollView)view.findViewById(R.id.map_fragment_vertical_scroll);
        hScroll = (HorizontalScrollView)view.findViewById(R.id.map_fragment_horizontal_scroll);

        vScroll.setFadingEdgeLength(0);
        hScroll.setFadingEdgeLength(0);

        vScroll.setOnTouchListener(this);
        hScroll.setOnTouchListener(this);

        //scaleGestureDetector = new ScaleGestureDetector(this.context, new MyOnScaleGestureListener());
        //gestureDetector = new GestureDetector(this.context, new MyGestureListener());

        //layout.setWillNotDraw(false);
        drawingView = (TestDrawingView)view.findViewById(R.id.map_fragment_surface_view);
        //myLocationButton = (FindMyLocationButton) view.findViewById(R.id.map_fragment_find_my_location_button);
        //myLocationButton.setDrawingView(drawingView);
        //LocationDrawingSurfaceView locationSurfaceView = (LocationDrawingSurfaceView)view.findViewById(R.id.map_fragment_location_surface_view);
        //layout.addView(tv);
        //tv.invalidate();

        AllocateImageButtonInFragment allocate = new AllocateImageButtonInFragment(getActivity(), layout);
        // attach surface view
        //PathDrawingSurfaceView sv = (PathDrawingSurfaceView)view.findViewById(R.id.map_fragment_surface_view);
        /*
        SurfaceView sv = (SurfaceView)view.findViewById(R.id.map_fragment_surface_view);
        sv.setWillNotDraw(false);
        sv.setZOrderOnTop(true);
        SurfaceHolder holder = sv.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.e("SURFACE_VIEW", "SurfaceView created.");
                Canvas canvas = holder.lockCanvas();
                Paint paint = new Paint();
                paint.setColor(Color.BLACK);
                paint.setStrokeWidth(10f);
                if(canvas == null){
                    Log.e("ERROR", "canvas is null");
                }
                if(paint == null){
                    Log.e("ERROR", "paint is null");
                }
                canvas.drawLine(10f, 10f, 100f, 100f, paint);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        */
    }

    private void scroll(int x, int y){
        hScroll.scrollBy(x, 0);
        vScroll.scrollBy(0, y);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //scaleGestureDetector.onTouchEvent(event);
        //gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Xpos = (int) event.getRawX();
                Ypos = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int Xpos2 = (int) event.getRawX();
                int Ypos2 = (int) event.getRawY();
                scroll(Xpos - Xpos2, Ypos - Ypos2);
                Xpos = Xpos2;
                Ypos = Ypos2;
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                Xpos = (int) event.getRawX();
                Ypos = (int) event.getRawY();
                break;
            }
        Xpos = (int) event.getRawX();
        Ypos = (int) event.getRawY();
        v.invalidate();
        return true;
    }

    public class MyOnScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = 1 - detector.getScaleFactor();
            //Log.e("ZOOM", Float.toString(scaleFactor));
            float prevScale = curScale;
            curScale += scaleFactor;
            if(curScale < 1f){
                curScale = 1f;
            }
            if(curScale > 1.4f){
                curScale = 1.4f;
            }
            ScaleAnimation scaleAnimation = new ScaleAnimation(1f/prevScale, 1f/curScale, 1f/prevScale, 1f/curScale, detector.getFocusX(), detector.getFocusY());
            scaleAnimation.setDuration(0);
            scaleAnimation.setFillAfter(true);
            layout.startAnimation(scaleAnimation);
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }
    }

    public class MyGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }
    }
}
