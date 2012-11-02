
package com.carit.imhere;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.ItemizedOverlay.OnFocusChangeListener;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

@SuppressWarnings("rawtypes")
public class ParkItemizedOverlay extends ItemizedOverlay implements OnFocusChangeListener,
        OnClickListener {
    private static final String TAG = "ParkItemizedOverlay";

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

    private final int[] mPopItem = {
            R.id.poi1, R.id.poi2, R.id.poi3, R.id.poi4
    };

    public ParkItemizedOverlay(Drawable defaultMarker) {
        super(boundCenterBottom(defaultMarker));
    }

    public ParkItemizedOverlay(Drawable defaultMarker, Context context, MapView mapView,
            View popView, MapController mapCtrl) {
        super(boundCenterBottom(defaultMarker));
        itemDrawable = defaultMarker;
        mContext = (MapMode) context;
        setOnFocusChangeListener(this);
        layout_x = itemDrawable.getBounds().centerX();
        layout_y = -itemDrawable.getBounds().height();
        mMapView = mapView;
        mPopView = popView;
        mMapCtrl = mapCtrl;
        mPopView.findViewById(R.id.poi1).findViewById(R.id.ImageButtonRight).setOnClickListener(mContext);
        mPopView.findViewById(R.id.poi2).findViewById(R.id.ImageButtonRight).setOnClickListener(mContext);
        mPopView.findViewById(R.id.poi3).findViewById(R.id.ImageButtonRight).setOnClickListener(mContext);
        mPopView.findViewById(R.id.poi4).findViewById(R.id.ImageButtonRight).setOnClickListener(mContext);
        mPopView.findViewById(R.id.poi1).findViewById(R.id.ImageButtonLeft).setOnClickListener(mContext);
        mPopView.findViewById(R.id.poi2).findViewById(R.id.ImageButtonLeft).setOnClickListener(mContext);
        mPopView.findViewById(R.id.poi3).findViewById(R.id.ImageButtonLeft).setOnClickListener(mContext);
        mPopView.findViewById(R.id.poi4).findViewById(R.id.ImageButtonLeft).setOnClickListener(mContext);

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

    public void cleanOverlayItem() {
        overlays.clear();
        populate();
    }



    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus) {
        if (null != newFocus) {
            // Log.d(TAG , "centerY : " + itemDrawable.getBounds().centerY() +
            // "; centerX :" + itemDrawable.getBounds().centerX());
            List<OverlayItem> list = new ArrayList<OverlayItem>();
            getNearItem(overlay, newFocus, list);
            MapView.LayoutParams params = (MapView.LayoutParams) mPopView.getLayoutParams();
            params.x = this.layout_x;// Y轴偏移
            params.y = this.layout_y;// Y轴偏移
            point = newFocus.getPoint();
            params.point = point;
            mMapCtrl.animateTo(point);
            mPopView.setVisibility(View.GONE);
            mPopView.findViewById(R.id.poi1).setVisibility(View.GONE);
            mPopView.findViewById(R.id.poi2).setVisibility(View.GONE);
            mPopView.findViewById(R.id.poi3).setVisibility(View.GONE);
            mPopView.findViewById(R.id.poi4).setVisibility(View.GONE);
            
            if (list.size() > 1) {
                for (int i = 0; i < list.size() && i < 4; i++) {
                    View tmp = mPopView.findViewById(mPopItem[i]);
                    tmp.findViewById(R.id.ImageButtonRight).setTag(list.get(i).getPoint());
                    tmp.findViewById(R.id.ImageButtonLeft).setTag(list.get(i));
                    tmp.setVisibility(View.VISIBLE);
                    TextView title_TextView = (TextView) tmp.findViewById(R.id.ImageButton01);
                    title_TextView.setText(list.get(i).getTitle());
                    TextView desc_TextView = (TextView) tmp.findViewById(R.id.TextView02);
                    if (null == list.get(i).getSnippet() || "".equals(list.get(i).getSnippet())) {
                        desc_TextView.setVisibility(View.GONE);
                    } else {
                        desc = list.get(i).getSnippet();
                        desc_TextView.setText(desc);
                        desc_TextView.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                mPopView.findViewById(R.id.poi1).setVisibility(View.VISIBLE);
                mPopView.findViewById(R.id.poi1).findViewById(R.id.ImageButtonRight).setTag(point);
                mPopView.findViewById(R.id.poi1).findViewById(R.id.ImageButtonLeft).setTag(newFocus);
                TextView title_TextView = (TextView) mPopView.findViewById(R.id.ImageButton01);
                title_TextView.setText(newFocus.getTitle());
                TextView desc_TextView = (TextView) mPopView.findViewById(R.id.TextView02);
                if (null == newFocus.getSnippet() || "".equals(newFocus.getSnippet())) {
                    desc_TextView.setVisibility(View.GONE);
                } else {
                    desc = newFocus.getSnippet();
                    desc_TextView.setText(desc);
                    desc_TextView.setVisibility(View.VISIBLE);
                }
            }

            mMapView.updateViewLayout(mPopView, params);
            mPopView.setVisibility(View.VISIBLE);
        } else {
            mPopView.setVisibility(View.GONE);
        }
    }

    private void getNearItem(ItemizedOverlay overlay, OverlayItem newFocus, List<OverlayItem> items) {
        Projection projection = mMapView.getProjection();
        Point out = new Point();
        int size = 30;
        projection.toPixels(newFocus.getPoint(), out);
        Rect rect = new Rect(out.x - size, out.y - size, out.x + size, out.y + size);
       
        items.add(newFocus);
        for (int i = 0; i < overlay.size(); i++) {
            OverlayItem item = overlay.getItem(i);
            projection.toPixels(item.getPoint(), out);
            if (rect.contains(out.x, out.y) && !newFocus.equals(item)) {
                items.add(item);
            }
        }

    }
    
    

}
