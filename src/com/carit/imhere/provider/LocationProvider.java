package com.carit.imhere.provider;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class LocationProvider extends ContentProvider{
    private static final String TAG = "LocationProvider";
    private static final String DATABASE_NAME = "location.db";
    private static final int DATABASE_VERSION = 3;
    private static final String LOCATION_TABLE = "location_table";
    
    private static HashMap<String, String> sLocationTableProjectionMap;
    private DatabaseHelper mOpenHelper;
    private static final UriMatcher sUriMatcher;
    private static final int LOCATION_TABLE_NO = 0;
    private static final int LOCATION_TABLE_ID = 1;
    @Override
    public boolean onCreate() {
        Log.e(TAG, "Database Create!");
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(LOCATION_TABLE);

        switch (sUriMatcher.match(uri)) {
        case LOCATION_TABLE_NO:
            qb.setProjectionMap(sLocationTableProjectionMap);
            break;

        case LOCATION_TABLE_ID:
            qb.setProjectionMap(sLocationTableProjectionMap);
            qb.appendWhere(LocationTable._ID + "=" + uri.getPathSegments().get(1));
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = LocationTable.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case LOCATION_TABLE_NO:
                return LocationTable.CONTENT_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (sUriMatcher.match(uri) != LOCATION_TABLE_NO) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues value;
        if (values != null) {
            value = new ContentValues(values);
        } else {
            value = new ContentValues();
        }


        // Make sure that the fields are all set
        


        


        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        try{
        long rowId = db.insert(LOCATION_TABLE, LocationTable.TIME, value);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(LocationTable.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        //throw new SQLException("Failed to insert row into " + uri);
        }catch (SQLiteConstraintException e) {
           e.printStackTrace();
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case LOCATION_TABLE_NO:
            count = db.delete(LOCATION_TABLE, selection, selectionArgs);
            break;

        case LOCATION_TABLE_ID:
            String callLogId = uri.getPathSegments().get(1);
            count = db.delete(LOCATION_TABLE, LocationTable._ID + "=" + callLogId
                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.e(TAG, "Database Create!");
            db.execSQL("CREATE TABLE location_table ("
                    +LocationTable._ID+" INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    +"time INTEGER NOT NULL,"
                    +"provider TEXT,"
                    +"lat TEXT,"
                    +"lng TEXT," 
                    +"altitude TEXT," 
                    +"speed TEXT," 
                    +"bearing TEXT," 
                    +"accuracy TEXT" 
                    +");");
             db.execSQL("CREATE UNIQUE INDEX [time] ON [location_table] ([time]);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }
    
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(LocationTable.AUTHORITY, LOCATION_TABLE, LOCATION_TABLE_NO);
        sUriMatcher.addURI(LocationTable.AUTHORITY, LOCATION_TABLE+"/#", LOCATION_TABLE_ID);

        sLocationTableProjectionMap = new HashMap<String, String>();
        sLocationTableProjectionMap.put(LocationTable._ID, LocationTable._ID);
        sLocationTableProjectionMap.put(LocationTable.ACCURACY,LocationTable.ACCURACY);
        sLocationTableProjectionMap.put(LocationTable.TIME,LocationTable.TIME);
        sLocationTableProjectionMap.put(LocationTable.ALTITUDE,LocationTable.ALTITUDE);
        sLocationTableProjectionMap.put(LocationTable.BEARING,LocationTable.BEARING);
        sLocationTableProjectionMap.put(LocationTable.LAT,LocationTable.LAT);
        sLocationTableProjectionMap.put(LocationTable.LNG,LocationTable.LNG);
        sLocationTableProjectionMap.put(LocationTable.PROVIDER,LocationTable.PROVIDER);
        sLocationTableProjectionMap.put(LocationTable.SPEED,LocationTable.SPEED);
    }

}
