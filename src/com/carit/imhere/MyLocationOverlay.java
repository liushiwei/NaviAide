package com.carit.imhere;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.carit.imhere.test.MockProvider;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MyLocationOverlay extends com.google.android.maps.MyLocationOverlay {

    private MapMode mContext;
    public MyLocationOverlay(Context context, MapView mapView) {
        super(context, mapView);
        mContext = (MapMode)context;
    }

    @Override
    public synchronized void onLocationChanged(Location location) {
        if(location!=null && MockProvider.MODK_PROVIDER.equals(location.getProvider()))
        super.onLocationChanged(location);
    }

    @Override
    public boolean onTap(GeoPoint arg0, MapView arg1) {
        Log.e("MyLocation", "onTap x="+arg0.getLatitudeE6()+ " y = "+arg0.getLongitudeE6());
        if(getMyLocation()!=null){
        int size = 30;
        Point out=new Point();
        arg1.getProjection().toPixels(getMyLocation(), out);    
        Rect rect = new Rect(out.x-size,out.y-size,out.x+size,out.y+size);
        arg1.getProjection().toPixels(arg0, out);
        if(rect.contains(out.x, out.y)){
            Log.e("MyLocation", "click my location ");
            View popView = mContext.getPopView();
            popView.setVisibility(View.GONE);
            TextView title_TextView = (TextView) popView.findViewById(R.id.poi1).findViewById(R.id.ImageButton01);
            title_TextView.setText(mContext.getString(R.string.myself_location));
            title_TextView = (TextView) popView.findViewById(R.id.poi1).findViewById(R.id.TextView02);
            title_TextView.setText("");
            OverlayItem overlayItem = new OverlayItem(getMyLocation(),mContext.getString(R.string.myself_location) ,
                    "");
            popView.findViewById(R.id.poi1).findViewById(R.id.ImageButtonLeft).setTag(overlayItem);
            popView.findViewById(R.id.poi1).findViewById(R.id.ImageButtonRight).setTag(null);
            MapView.LayoutParams params = (MapView.LayoutParams) popView.getLayoutParams();
            params.x = 0;// Y轴偏移
            params.y = -5;// Y轴偏移
            GeoPoint point = getMyLocation();
            params.point = point;
            //mMapCtrl.animateTo(point);
            arg1.updateViewLayout(mContext.getPopView(), params);
            mContext.getPopView().setVisibility(View.VISIBLE);
        }else{
            //mContext.getPopView().setVisibility(View.GONE) ;  
        }
        }
        return super.onTap(arg0, arg1);
    }
    
    

    
    

}
