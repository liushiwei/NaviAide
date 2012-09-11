package com.carit.imhere.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.google.android.maps.GeoPoint;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TestProvider {
    private static TestProvider privader;
    private LocationManager mLocationManager;
    private TestProvider(){
        
    }
    
    public static TestProvider getInstance(){
        if(privader==null)
            privader = new TestProvider();
        return privader;
            
    }
    
    public static void init(LocationManager gpsLocationManager,LocationListener listener){
        String mocLocationProvider = LocationManager.NETWORK_PROVIDER;
        gpsLocationManager.addTestProvider(mocLocationProvider, false, false,
        false, false, true, true, true, 0, 5);
        gpsLocationManager.setTestProviderEnabled(mocLocationProvider, true);
        gpsLocationManager.requestLocationUpdates(mocLocationProvider, 0, 0, listener);
    }
    
    public static void generateGpsFile(List<GeoPoint> points){
        
        try {
            FileOutputStream out = new FileOutputStream(new File("/sdcard/media/gps.json"));
            Gson gson = new Gson();
            String point = gson.toJson(points);
            out.write(point.getBytes());
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void startProvider(){
       
        Thread thread = new Thread(){

            @Override
            public void run() {
                try {
//                    FileInputStream input = new FileInputStream(new File("/sdcard/media/gps.json"));
//                    StringBuffer content = new StringBuffer();
//                    byte [] buffer = new byte[100];
//                    while(input.read(buffer)==100){
//                        content.append(new String(buffer));
//                    }
//                    content.append(new String(buffer));
                    FileReader reader = new FileReader(new File("/sdcard/media/gps.json"));
                    List<GeoPoint> points ;
                    Gson gson = new Gson();
                    points = gson.fromJson(reader, new TypeToken<List<GeoPoint>>(){}.getType());
                    for(GeoPoint point :points){
                        Location location = new Location(LocationManager.NETWORK_PROVIDER);
                        location.setLatitude(point.getLatitudeE6()/1E6);
                        location.setLongitude(point.getLongitudeE6()/1E6);
                        location.setTime(System.currentTimeMillis());
                        location.setAltitude(100);
                        mLocationManager.setTestProviderLocation(LocationManager.NETWORK_PROVIDER, location);
                        sleep(1000);
                    }
                    
                    
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
               
                super.run();
            }
            
        };
        thread.start();
    }
    
    public void setLocation(Location location){
        mLocationManager.setTestProviderLocation(LocationManager.NETWORK_PROVIDER, location);
    }
    
    public TestProvider init(LocationManager gpsLocationManager){
        mLocationManager = gpsLocationManager;
        if(privader==null)
            privader = new TestProvider();
        return privader;
    }

}
