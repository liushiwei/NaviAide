
package com.carit.imhere;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class PathOverlay extends Overlay {
    private List<GeoPoint> points;

   

    private Paint paint;

    public PathOverlay(List<GeoPoint> points) {
        this.points = points;
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setAlpha(150);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(4);
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        if (!shadow) {// 不是绘制shadow层
            Projection projection = mapView.getProjection();
            if (points != null && points.size() >= 2) {// 画线
                Point start = new Point();
                projection.toPixels(points.get(0), start);// 需要转换坐标
                for (int i = 1; i < points.size(); i++) {
                    Point end = new Point();
                    projection.toPixels(points.get(i), end);
                    canvas.drawLine(start.x, start.y, end.x, end.y, paint);// 绘制到canvas上即可
                    start = end;
                }
            }
        }
    }
    
    public List<GeoPoint> getPoints() {
        return points;
    }

    public void setPoints(List<GeoPoint> points) {
        this.points = null;
        this.points = points;
    }
}
