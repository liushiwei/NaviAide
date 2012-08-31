package com.carit.imhere;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WiFiInfoManager {
	private static final String TAG = "WiFiInfoManager";
	private Context context; 
	 
    public WiFiInfoManager(Context context) { 
        super(); 
        this.context = context; 
    } 
 
    public String getWifiInfo() { 
        WifiManager manager = (WifiManager) context 
                .getSystemService(Context.WIFI_SERVICE); 
        String mac = manager.getConnectionInfo().getBSSID(); 
        Log.i(TAG, "WIFI MAC is:" + mac); 
        return mac; 
    } 
    
    public Location getWIFILocation() {	
    	return getWIFILocation(getWifiInfo());
    }
    
    public static Location getWIFILocation(String mac) { 
        if (mac == null) { 
            Log.i(TAG, "mac is null."); 
            return null; 
        } 
        DefaultHttpClient client = new DefaultHttpClient(); 
        HttpPost post = new HttpPost("http://www.google.com/loc/json"); 
        JSONObject holder = new JSONObject(); 
        try { 
            holder.put("version", "1.1.0"); 
            holder.put("host", "maps.google.com"); 
 
            JSONObject data; 
            JSONArray array = new JSONArray(); 
            if (mac != null && mac.trim().length() > 0) { 
                data = new JSONObject(); 
                data.put("mac_address", mac); 
                data.put("signal_strength", 8); 
                data.put("age", 0); 
                array.put(data); 
            } 
            holder.put("wifi_towers", array); 
            Log.i(TAG, "request json:" + holder.toString()); 
            StringEntity se = new StringEntity(holder.toString()); 
            post.setEntity(se); 
            HttpResponse resp = client.execute(post); 
            int state = resp.getStatusLine().getStatusCode(); 
            if (state == HttpStatus.SC_OK) { 
                HttpEntity entity = resp.getEntity(); 
                if (entity != null) { 
                    BufferedReader br = new BufferedReader( 
                            new InputStreamReader(entity.getContent())); 
                    StringBuffer sb = new StringBuffer(); 
                    String resute = ""; 
                    while ((resute = br.readLine()) != null) { 
                        sb.append(resute); 
                    } 
                    br.close(); 
 
                    Log.i(TAG, "response json:" + sb.toString()); 
                    data = new JSONObject(sb.toString()); 
                    data = (JSONObject) data.get("location"); 
 
                    Location loc = new Location( 
                            android.location.LocationManager.NETWORK_PROVIDER); 
                    loc.setLatitude((Double) data.get("latitude")); 
                    loc.setLongitude((Double) data.get("longitude")); 
                    loc.setAccuracy(Float.parseFloat(data.get("accuracy") 
                            .toString())); 
                    loc.setTime(System.currentTimeMillis()); 
                    return loc; 
                } else { 
                    return null; 
                } 
            } else { 
                Log.v(TAG, state + ""); 
                return null; 
            } 
        } catch (Exception e) { 
            Log.e(TAG, e.getMessage()); 
            return null; 
        } 
    }
}