
package com.carit.imhere;

/**
 * @author rongfzh
 * @version 1.0.0  
 */
import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.ItemizedOverlay.OnFocusChangeListener;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

@SuppressWarnings("rawtypes")
public class SearchLocationOverlay extends ItemizedOverlay implements 
         OnFocusChangeListener {

    private MapMode mContext;

    private MapView mMapView;

    private Handler mHandler;

    private MapController mMapCtrl;

    private int level = 0;

    private View mPopView;

    private Drawable mDrawable;

    private List<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

    private int mCurrentIndex;

    public SearchLocationOverlay(MapMode context, MapView mapView, MapController mapCtrl,
            Drawable drawable) {
        super(boundCenterBottom(drawable));
        mContext = context;
        mMapView = mapView;
        mMapCtrl = mapCtrl;
        setOnFocusChangeListener(this);
        // 初始化气泡,并设置为不可见
        mPopView = View.inflate(mContext, R.layout.popup_nobtn, null);
        mMapView.addView(mPopView, new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT,
                MapView.LayoutParams.WRAP_CONTENT, null, MapView.LayoutParams.BOTTOM_CENTER));
        mPopView.setVisibility(View.GONE);
        mDrawable = drawable;
       
        populate(); // Add this
    }

  
   

    

    @Override
    protected OverlayItem createItem(int i) {
        // TODO Auto-generated method stub
        return mOverlays.get(i);
    }

    @Override
    public int size() {
        // TODO Auto-generated method stub
        return mOverlays.size();
    }
    
    public void addOverlayItem(OverlayItem item){
        MapView.LayoutParams params = (MapView.LayoutParams) mPopView.getLayoutParams();
        params.x = mDrawable.getBounds().centerX();// Y轴偏移
        params.y = -mDrawable.getBounds().height();// Y轴偏移
        GeoPoint point = item.getPoint();
        params.point = point;
        mMapCtrl.animateTo(point);
        mOverlays.add(item);
        mCurrentIndex = mOverlays.size() - 1;
        ((TextView)mPopView.findViewById(R.id.ImageButton01)).setText(item.getTitle());
        mPopView.findViewById(R.id.TextView02).setVisibility(View.GONE);
        populate();
        mMapView.updateViewLayout(mPopView, params);
        mPopView.setVisibility(View.VISIBLE);
        Log.e("LongPressOverlay", "point = " + point.getLatitudeE6() + "," + point.getLongitudeE6());
    }


    @Override
    public void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus) {
        if (newFocus != null) {
            mPopView.setVisibility(View.GONE);
            MapView.LayoutParams params = (MapView.LayoutParams) mPopView.getLayoutParams();
            params.x = mDrawable.getBounds().centerX();// Y轴偏移
            params.y = -mDrawable.getBounds().height();// Y轴偏移
            GeoPoint point = newFocus.getPoint();
            params.point = point;
            mMapCtrl.animateTo(point);
            mCurrentIndex = mOverlays.indexOf(newFocus);
            populate();
            mMapView.updateViewLayout(mPopView, params);
            mPopView.setVisibility(View.VISIBLE);
        }
    }

}
