package com.carit.imhere;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.ItemizedOverlay.OnFocusChangeListener;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

@SuppressWarnings("rawtypes")
public class TrackOverlay extends ItemizedOverlay implements OnFocusChangeListener{
    private List<Location> mLocations;

    //private Directions mDirections;

    private Paint paint;
    
    private Drawable mPin;
    
    private Bitmap mLocationPin;
    
    private MapView mMapView;
    private MapController mMapCtrl;
    private View mPopView;
    
    private List<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
    private Point mLastPoint;
  

    public TrackOverlay(List<Location> locations,final List<OverlayItem> overlays,Drawable pin) {
        super(boundCenterBottom(pin));
        this.mLocations = locations;
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setAlpha(150);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(4);
        mOverlays = overlays;
        
    }
    
    public TrackOverlay(List<Location> locations,MapView mapView, View popView,MapController mapCtrl,Drawable pin) {
        super(boundCenterBottom(pin));
        this.mLocations = locations;
        paint = new Paint();
        setOnFocusChangeListener(this);
        paint.setColor(Color.BLUE);
        paint.setAlpha(150);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(4);
        mPin = pin;
        mPopView = popView;
        mMapView = mapView;
        mMapCtrl = mapCtrl;
        mLocationPin =  BitmapFactory.decodeResource(mapView.getResources(), R.drawable.icon_locr_light);
        mLastPoint = new Point(0,0);
        populate(); // Add this
    }
    
    public TrackOverlay(Location  start_location,MapView mapView, View popView,MapController mapCtrl,Drawable pin) {
        super(boundCenterBottom(pin));
        this.mLocations = new ArrayList<Location>();
        mLocations.add(start_location);
        paint = new Paint();
        setOnFocusChangeListener(this);
        paint.setColor(Color.BLUE);
        paint.setAlpha(150);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(4);
        mPin = pin;
        mPopView = popView;
        mMapView = mapView;
        mMapCtrl = mapCtrl;
        mLocationPin =  BitmapFactory.decodeResource(mapView.getResources(), R.drawable.icon_locr_light);
        mLastPoint = new Point(0,0);
        populate(); // Add this
    }

    /*public PathOverlay(final Directions directions,Drawable pin) {
        super(boundCenterBottom(pin));
        mDirections = directions;
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setAlpha(150);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);  
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(4);
        if (mDirections != null && mDirections.getRoutes() != null) {
            Log.e("MapMode", mDirections.getStatus());
            this.points = decodePoly(mDirections.getRoutes()[0].getOverview_polyline().getPoints());
            
        }
    }*/

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        if(shadow){
            Projection proj = mapView.getProjection();

            /*Clear the old path at first*/
            Path path = new Path();
            /* The first tap */
            Point tempPoint = new Point();
            for(int i=0;i<mLocations.size();i++){
                GeoPoint point = new GeoPoint((int)(mLocations.get(i).getLatitude()*1E6),(int)(mLocations.get(i).getLongitude()*1E6));
                proj.toPixels(point, tempPoint);
                if(i<1){
                    path.moveTo(tempPoint.x, tempPoint.y);
                }
                else{
                    path.lineTo(tempPoint.x, tempPoint.y);
                }
                if(mLocations.size()>2 && (i==mLocations.size()-2)){
                    mLastPoint = new Point(tempPoint);
                }
            }
            /* If indeed is a polygon just close the perimeter */
            canvas.drawPath(path, paint);
//            Bitmap bmp =mPin.
            canvas.drawBitmap(mLocationPin, tempPoint.x-mLocationPin.getWidth()/2, tempPoint.y-mLocationPin.getHeight()/2, paint);
//            double x=Math.atan2(mLastPoint.x-tempPoint.x,mLastPoint.y-tempPoint.y);
//            //Math.atan2();
//            x=x*180/Math.PI;
//            if(x>0)
//                x=180+x;
//            else
//                x=-x;
//            Log.e("TrackOverlay", "org x = "+x);
            /*double x =0;
            Matrix matrix=new Matrix();
            if(mLocations.size()>2)
            x = gps2d(mLocations.get(mLocations.size()-2).getLatitude(), mLocations.get(mLocations.size()-2).getLongitude(),mLocations.get(mLocations.size()-1).getLatitude(), mLocations.get(mLocations.size()-1).getLongitude());
            //matrix.setRotate((float) x);
            matrix.setRotate((float) x);
            Bitmap dstbmp=Bitmap.createBitmap(mLocationPin,0,0,mLocationPin.getWidth(),
                    mLocationPin.getHeight(),matrix,true);
            //canvas.drawColor(Color.BLACK); 
            Log.e("TrackOverlay", "x = "+x);
            canvas.drawBitmap(dstbmp, tempPoint.x-mLocationPin.getWidth()/2, tempPoint.y-mLocationPin.getHeight()/2, paint);
         */   
            
        }
        super.draw(canvas, mapView, shadow);
        
    }


    @Override
    protected OverlayItem createItem(int i) {
        return mOverlays.get(i);
    }

    @Override
    public int size() {
        return mOverlays.size();
    }
    
    public void addOverlay(OverlayItem item) {
        mOverlays.add(item);
        populate();
    }

    public void removeOverlay(int location) {
        mOverlays.remove(location);
    }
    
    public List<OverlayItem> getOverlays() {
        return mOverlays;
    }

    public void setOverlays(List<OverlayItem> overlays) {
        this.mOverlays = overlays;
        populate();
    }

    public List<Location> getLocations() {
        return mLocations;
    }

    public void setLocations(List<Location> locations) {
        this.mLocations = locations;
    }
    
    public void addLocation(Location location){
        this.mLocations.add(location);
        mMapCtrl.animateTo(new GeoPoint((int)(location.getLatitude()*1E6),(int)(location.getLongitude()*1E6)));
        populate();
    }

    @Override
    public void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus) {
        if (null != newFocus) {
            MapView.LayoutParams params = (MapView.LayoutParams) mPopView.getLayoutParams();
            params.x = mPin.getBounds().centerX();//Y轴偏移
            params.y = - mPin.getBounds().height();//Y轴偏移
            params.point = newFocus.getPoint();
            mMapCtrl.animateTo(newFocus.getPoint());
            //mPopView.findViewById(R.id.ImageButtonRight).setTag(newFocus.getPoint());
            TextView title_TextView = (TextView) mPopView.findViewById(R.id.ImageButton01);
            title_TextView.setText(Html.fromHtml(newFocus.getTitle()));
            TextView desc_TextView = (TextView) mPopView.findViewById(R.id.TextView02);
            if(null == newFocus.getSnippet() || "".equals(newFocus.getSnippet())){
                desc_TextView.setVisibility(View.GONE);
            }else{
                desc_TextView.setText(newFocus.getSnippet());
                desc_TextView.setVisibility(View.VISIBLE);
            }
            mMapView.updateViewLayout(mPopView, params);
            mPopView.setVisibility(View.VISIBLE);
        }else{
            mPopView.setVisibility(View.GONE);
        }
        
    }
    
    /*private double gps2d(double lat_a, double lng_a, double lat_b, double lng_b) {
        double d = 0;
        lat_a=lat_a*Math.PI/180;
        lng_a=lng_a*Math.PI/180;
        lat_b=lat_b*Math.PI/180;
        lng_b=lng_b*Math.PI/180;
       
         d=Math.sin(lat_a)*Math.sin(lat_b)+Math.cos(lat_a)*Math.cos(lat_b)*Math.cos(lng_b-lng_a);
        d=Math.sqrt(1-d*d);
        d=Math.cos(lat_b)*Math.sin(lng_b-lng_a)/d;
        d=Math.asin(d)*180/Math.PI;
       
//      d = Math.round(d*10000);
        return d;
     }*/

}
