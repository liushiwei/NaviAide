package com.carit.imhere;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.ItemizedOverlay.OnFocusChangeListener;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

@SuppressWarnings("rawtypes")
public class ParkItemizedOverlay extends ItemizedOverlay implements OnFocusChangeListener,OnClickListener {
    private static final String TAG = "MItemizedOverlay";
    private List<OverlayItem> overlays = new ArrayList<OverlayItem>();
    private MapMode mContext;
    private GeoPoint point = null;
    private String desc = "";
    private String car_title = "";
    private int layout_x = 0; // 用于设置popview 相对某个位置向x轴偏移
    private int layout_y = -30; // 用于设置popview 相对某个位置向x轴偏移
    
    private MapView mMapView;
    private MapController mMapCtrl;
    private View mPopView;
    
    private Drawable itemDrawable;
    
    public ParkItemizedOverlay(Drawable defaultMarker) {
        super(boundCenterBottom(defaultMarker));
    }
    
    public ParkItemizedOverlay(Drawable defaultMarker, Context context, MapView mapView, View popView, MapController mapCtrl) {
        super(boundCenterBottom(defaultMarker));
        itemDrawable = defaultMarker;
        mContext = (MapMode) context;
        setOnFocusChangeListener(this);
        layout_x = itemDrawable.getBounds().centerX();
        layout_y = - itemDrawable.getBounds().height();
        mMapView =  mapView;
        mPopView = popView;
        mMapCtrl = mapCtrl;

    }

    @Override
    protected OverlayItem createItem(int i) {
        return overlays.get(i);
    }

    @Override
    public int size() {
        return overlays.size();
    }

    public void addOverlay(OverlayItem item) {
        overlays.add(item);
        populate();
    }

    public void removeOverlay(int location) {
        overlays.remove(location);
    }

    @Override
    public boolean onTap(GeoPoint p, MapView mapView) {
        return super.onTap(p, mapView);
    }

    @Override
    protected boolean onTap(int index) {
        return super.onTap(index);
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        super.draw(canvas, mapView, shadow);
    }

    
    public void onClick(View v) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus) {
        Log.d(TAG , "item focus changed!");
        if (null != newFocus) {
            Log.d(TAG , "centerY : " + itemDrawable.getBounds().centerY() + "; centerX :" + itemDrawable.getBounds().centerX());
            Log.d(TAG , " height : " + itemDrawable.getBounds().height());
            MapView.LayoutParams params = (MapView.LayoutParams) mPopView.getLayoutParams();
            params.x = this.layout_x;//Y轴偏移
            params.y = this.layout_y;//Y轴偏移
            point = newFocus.getPoint();
            params.point = point;
            mMapCtrl.animateTo(point);
            mPopView.findViewById(R.id.ImageButtonRight).setTag(point);
            TextView title_TextView = (TextView) mPopView.findViewById(R.id.ImageButton01);
            title_TextView.setText(newFocus.getTitle());
            TextView desc_TextView = (TextView) mPopView.findViewById(R.id.TextView02);
            if(null == newFocus.getSnippet() || "".equals(newFocus.getSnippet())){
                desc_TextView.setVisibility(View.GONE);
            }else{
                desc = newFocus.getSnippet();
                desc_TextView.setText(desc);
                desc_TextView.setVisibility(View.VISIBLE);
            }
            
            mMapView.updateViewLayout(mPopView, params);
            mPopView.setVisibility(View.VISIBLE);
        }else{
            mPopView.setVisibility(View.GONE);
        }
    }
    
  
}
