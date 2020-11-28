package com.Joemo.wavylinething;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import java.lang.Runnable;
import java.lang.Math;
import com.jnoise.opensimplexnoise.OpenSimplexNoise;
import android.view.InputEvent;

public class MyCanvas extends View {
    Paint paint;
    Rect rect;
    float heightPos;
    float xScroll;
    float xPadding = 150;
    float yPadding = 300;

    int noLines = 15;

    float hA = 1.4f;
    float hB = 2f;
    float hC = 0.8f;

    float vA=1;
    float vB=2f;
    float vC=5;


    int resolution = 300;
    float frequency = 1;
    Canvas canvRef;

    public MyCanvas(Context context) {
        super(context);
        setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE);

        loop.run();

        paint = new Paint();
        rect = new Rect();
        heightPos = 0;

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float maxX = canvRef.getWidth() - canvRef.getWidth()/8f;
        float maxY = canvRef.getHeight() - canvRef.getHeight()/4f;;
        float x = event.getX();
        float y = event.getY();
        System.out.println(y);
        x -= canvRef.getWidth()/8f;
        y -= canvRef.getHeight()/4f;

        x /= canvRef.getWidth() * 3f / 4f;
        y /= canvRef.getHeight() / 2;

        hB = 4*x;
        vB = 15*y;



        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvRef = canvas;
        super.onDraw(canvas);
        xPadding = canvRef.getWidth()/8f;
        yPadding = canvRef.getHeight()/4f;
        paint.setColor(Color.LTGRAY);
        paint.setStrokeWidth(10);
        canvRef.drawColor(Color.BLACK);
        OpenSimplexNoise noise = new OpenSimplexNoise();
        for (int k = 0; k < noLines; k++) {
            float yHeight = yPadding + (canvRef.getHeight() - yPadding * 2) * (float)k / noLines;


            float[] pts = new float[resolution * 4];
            pts[0] = xPadding;
            pts[1] = (yHeight + calcAmpMod(k, vA, vB, vC)*calcAmpMod(0, hA,hB,hC) * (float)noise.eval((float) xScroll, yHeight/400f)*160);//Math.sin(xScroll) * 30);
            for (int i = 2; i < resolution * 4 - 2; i += 4) {
                for (int j = 0; j < 4; j += 2) {
                    pts[i + j] = xPadding + ((float) canvRef.getWidth() - 2 * xPadding) / resolution * (float) i / 4;
                    pts[i + 1 + j] = (float)(yHeight + calcAmpMod(k, vA, vB, vC)*calcAmpMod((float) i / resolution,hA,hB,hC)*(float)noise.eval((float) i / resolution * frequency + xScroll, yHeight/400f)*160);//Math.sin((float) i / resolution * frequency + xScroll) * 30);
                }
            }
            pts[resolution*4-2] = pts[resolution*4-4];
            pts[resolution*4-1] = pts[resolution*4-3];
            canvRef.drawLines(pts, paint);
        }


    }
    float calcAmpMod(float x, float a, float b, float c) {
        return (float)(a*Math.exp(-((x-b)*(x-b) / c/c)));
    }
    Handler handler = new Handler(Looper.getMainLooper());
    Runnable loop = new Runnable(){
        public void run(){

            xScroll += 0.005f;
            invalidate(); //will trigger the onDraw
            handler.postDelayed(this,1); //in 5 sec player0 will move again
        }
    };
}
