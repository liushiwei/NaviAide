package com.carit.imhere;

import android.content.Context;
import android.location.Location;

import com.carit.imhere.test.MockProvider;
import com.google.android.maps.MapView;

public class MyLocationOverlay extends com.google.android.maps.MyLocationOverlay {

    public MyLocationOverlay(Context context, MapView mapView) {
        super(context, mapView);
        // TODO Auto-generated constructor stub
    }

    @Override
    public synchronized void onLocationChanged(Location location) {
        if(location!=null && MockProvider.MODK_PROVIDER.equals(location.getProvider()))
        super.onLocationChanged(location);
    }

    
    

}
