package com.maum.bluetooth;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Masum Rahman on 5/26/2018.
 */
public class PaintView extends View {

    private Context context;
    public static int BRUSH_SIZE = 20;
    public static final int DEFAULT_COLOR = Color.RED;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;
    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    private ArrayList<FingerPath> paths = new ArrayList<>();
    private int currentColor;
    private View view3 ;

    private int backgroundColor = DEFAULT_BG_COLOR;
    private int strokeWidth;
    private boolean emboss;
    private boolean blur;
    private MaskFilter mEmboss;
    private MaskFilter mBlur;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    public  Command cmd = null;
    public PaintView(Context context) {
        this(context, null);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);
        mEmboss = new EmbossMaskFilter(new float[] {1, 1, 1}, 0.4f, 6, 3.5f);
        mBlur = new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL);

        prevPoint = new Points(0,0);
        curPoint = new Points(0.0,0.5);
        curLine = new StraightLine(prevPoint,curPoint);
    }

    public void init(DisplayMetrics metrics) {
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;
        float density = getResources().getDisplayMetrics().density;
        //Log.d("DEBUG", height + " " + width);
        //Log.d("DEBUG", height/density + " " + width/density );
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);


        drawLine();

        currentColor = DEFAULT_COLOR;
        strokeWidth = BRUSH_SIZE;
    }

    public void drawLine()
    {
        Bitmap bitmap = Bitmap.createBitmap(
                500, // Width
                300, // Height
                Bitmap.Config.ARGB_8888 // Config
        );

        // Initialize a new Canvas instance
        Canvas canvas = new Canvas(bitmap);

        // Draw a solid color on the canvas as background
        canvas.drawColor(Color.LTGRAY);

        // Initialize a new Paint instance to draw the line
        Paint paint = new Paint();
        // Line color
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        // Line width in pixels
        paint.setStrokeWidth(8);
        paint.setAntiAlias(true);

        // Set a pixels value to offset the line from canvas edge
        int offset = 50;

        // Draw a line on canvas at the center position
        canvas.drawLine(
                offset, // startX
                //canvas.getHeight() / 2, // startY
                100,
                canvas.getWidth() - offset, // stopX
                canvas.getHeight() / 2, // stopY
                paint // Paint
        );
        Log.d("DEBUG","line not drawn");
    }

    public void normal() {
        emboss = false;
        blur = false;
    }

    public void emboss() {
        emboss = true;
        blur = false;
    }

    public void blur() {
        emboss = false;
        blur = true;
    }

    public void clear() {
        backgroundColor = DEFAULT_BG_COLOR;
        paths.clear();
        normal();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        mCanvas.drawColor(backgroundColor);

        for (FingerPath fp : paths) {
            mPaint.setColor(fp.color);
            mPaint.setStrokeWidth(fp.strokeWidth);
            mPaint.setMaskFilter(null);

            if (fp.emboss)
                mPaint.setMaskFilter(mEmboss);
            else if (fp.blur)
                mPaint.setMaskFilter(mBlur);

            mCanvas.drawPath(fp.path, mPaint);

        }

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    private void touchStart(float x, float y) {
        //Log.d("DEBUG","--in on touchStart");
        mPath = new Path();
        FingerPath fp = new FingerPath(currentColor, emboss, blur, strokeWidth, mPath);
        paths.add(fp);

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        //Log.d("DEBUG","--in on touchMove");
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            //mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mPath.lineTo(mX,mY);
            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        //Log.d("DEBUG","--in on touchUp");
        mPath.lineTo(mX, mY);
    }

    Points prevPoint,curPoint;
    StraightLine prevLine,curLine;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        Log.d("DEBUG","distance is "+new Points(x,y).distanceFrom(curPoint));
        if(new Points(x,y).distanceFrom(curPoint) >= 0.5)
        {
            Command.lineList.add(curLine);
            prevPoint = curPoint;
            curPoint = new Points(x,y);

            prevLine = curLine;
            curLine = new StraightLine(prevPoint,curPoint);
            // Log.d("DEBUG", prevLine.toString()   );
            // Log.d("DEBUG", curLine.toString()   );
            //  Log.d("DEBUG","------Angle is "+Math.toDegrees(prevLine.calcAngleWith(curLine)));
        }



        Log.d("DEBUG",x + " " + y);
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                Log.d("DEBUG","Action Down");
                touchStart(x, y);
                Toast.makeText(context,"Touch start",Toast.LENGTH_LONG).show();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE :
                Log.d("DEBUG","Action Move");
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP :
                Log.d("DEBUG","Action Up");
                touchUp();
                invalidate();

                if(cmd==null)
                {
                    Toast.makeText(context,"cmd is null",Toast.LENGTH_LONG).show();
                }

                cmd.sendCommand();

                break;
        }

        return true;
    }
}


