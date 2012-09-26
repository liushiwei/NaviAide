package com.carit.imhere;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class RailOverlay extends Overlay {

    private Point leftTop = new Point();
    private Point rightBelow= new Point();
    private Paint paint;
    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        if (!shadow) {
            paint = new Paint();
            paint.setColor(Color.BLUE);
            paint.setAlpha(150);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(4);
            canvas.drawRect(new Rect(leftTop.x, leftTop.y, rightBelow.x, rightBelow.y), paint); 
            
        }
        super.draw(canvas, mapView, shadow);
    }
    public Point getLeftTop() {
        return leftTop;
    }
    public void setLeftTop(Point leftTop) {
        this.leftTop = leftTop;
    }
    public Point getRightBelow() {
        return rightBelow;
    }
    public void setRightBelow(Point rightBelow) {
        this.rightBelow = rightBelow;
    }
    
    
    
    
    
    

  
}
