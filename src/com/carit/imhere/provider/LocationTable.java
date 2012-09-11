package com.carit.imhere.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class LocationTable implements BaseColumns{
    
    public static final String AUTHORITY = "com.carit.provider.Location";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/location_table");
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.carit.location_table";
    public static final String DEFAULT_SORT_ORDER = "time ASC";
    public static final String TIME =      "time";      
    public static final String PROVIDER =   "provider";    
    public static final String LAT =       "lat";
    public static final String LNG =       "lng";       
    public static final String ALTITUDE =  "altitude";  
    public static final String SPEED = "speed"; 
    public static final String BEARING =  "bearing";      
    public static final String ACCURACY  =  "accuracy";  

}
