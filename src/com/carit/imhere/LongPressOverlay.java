
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
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.ItemizedOverlay.OnFocusChangeListener;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

@SuppressWarnings("rawtypes")
public class LongPressOverlay extends ItemizedOverlay implements OnDoubleTapListener,
        OnGestureListener, OnClickListener, OnFocusChangeListener {

    private MapMode mContext;

    private MapView mMapView;

    private Handler mHandler;

    private MapController mMapCtrl;

    private GestureDetector gestureScanner = new GestureDetector(this);

    private int level = 0;

    private View mPopView;

    private Drawable mDrawable;

    private List<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

    private int mCurrentIndex;

    public LongPressOverlay(MapMode context, MapView mapView, MapController mapCtrl,
            Drawable drawable) {
        super(boundCenterBottom(drawable));
        mContext = context;
        mMapView = mapView;
        mMapCtrl = mapCtrl;
        setOnFocusChangeListener(this);
        // 初始化气泡,并设置为不可见
        mPopView = View.inflate(mContext, R.layout.long_press_popup, null);
        mMapView.addView(mPopView, new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT,
                MapView.LayoutParams.WRAP_CONTENT, null, MapView.LayoutParams.BOTTOM_CENTER));
        mPopView.setVisibility(View.GONE);
        mDrawable = drawable;
        ImageButton end = (ImageButton) mPopView.findViewById(R.id.btn_end);
        end.setOnClickListener(this);
        end = (ImageButton) mPopView.findViewById(R.id.btn_pass);
        end.setOnClickListener(this);
        end = (ImageButton) mPopView.findViewById(R.id.btn_delete);
        end.setOnClickListener(this);
        populate(); // Add this
    }

    @Override
    public boolean onTouchEvent(MotionEvent event, MapView mapView) {
        return gestureScanner.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        if (++level % 3 == 0) {
            mMapCtrl.zoomIn();
            level = 0;
        }
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        MapView.LayoutParams params = (MapView.LayoutParams) mPopView.getLayoutParams();
        params.x = mDrawable.getBounds().centerX();// Y轴偏移
        params.y = -mDrawable.getBounds().height();// Y轴偏移
        GeoPoint point = mMapView.getProjection().fromPixels((int) e.getX(), (int) e.getY());
        params.point = point;
        mMapCtrl.animateTo(point);
        mOverlays.add(new OverlayItem(point, "", ""));
        mCurrentIndex = mOverlays.size() - 1;
        populate();
        mMapView.updateViewLayout(mPopView, params);
        mPopView.setVisibility(View.VISIBLE);
        Log.e("LongPressOverlay", "point = " + point.getLatitudeE6() + "," + point.getLongitudeE6());
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_end:
                if (mOverlays.size() > 1) {
                    GeoPoint[] passPoint = new GeoPoint[mOverlays.size() - 1];

                    for (int i = 0, j = 0; i < mOverlays.size(); i++) {
                        if (i != mCurrentIndex) {
                            passPoint[j] = mOverlays.get(i).getPoint();
                            j++;
                        }
                    }
                    mContext.getPath(mOverlays.get(mOverlays.size() - 1).getPoint(), passPoint);
                } else
                    mContext.getPath(mOverlays.get(mOverlays.size() - 1).getPoint(), null);

                break;
            case R.id.btn_delete:
                mOverlays.remove(mCurrentIndex);
                populate();
                setLastFocusedIndex(-1);
                mMapView.invalidate();
                mPopView.setVisibility(View.GONE);
                break;
        }

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
