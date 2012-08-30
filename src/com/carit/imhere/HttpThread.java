
package com.carit.imhere;

import java.security.KeyStore;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.carit.imhere.obj.PlaceSearchResult;
import com.google.gson.Gson;

public class HttpThread extends Thread {

    private String url;

    private HttpThreadListener listener;
    
    //private PlaceSearchResult result;

    public HttpThread(String url, HttpThreadListener listener) {
        Log.e("HttpThread", "URL = "+url);
        this.url = url;
        this.listener = listener;
    }

    @Override
    public void run() {
        listener.start();
        HttpGet get = new HttpGet(url);
        String strResult = "";
        try {
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 20000);
            HttpConnectionParams.setSoTimeout(httpParameters, 20000);
            HttpClient httpClient = getNewHttpClient();
            
            HttpResponse httpResponse = null;
            httpResponse = httpClient.execute(get);

            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                strResult = EntityUtils.toString(httpResponse.getEntity());
                //decodeResult(strResult);
                listener.complete(strResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.netError(e.toString());
        }

        super.run();
    }

    private void decodeResult(String json) {
        // points.clear();
        Gson gson = new Gson();
        PlaceSearchResult result = gson.fromJson(json, PlaceSearchResult.class);
        Log.e("ParkingOverlay", result.getStatus());
        // GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6),
        // (int) (((double) lng / 1E5) * 1E6));
        // points.add(p);

    }
    public static HttpClient getNewHttpClient() {  
        try {  
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());  
            trustStore.load(null, null);  
            
            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);  
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);  
    
            HttpParams params = new BasicHttpParams();  
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);  
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);  
    
            SchemeRegistry registry = new SchemeRegistry();  
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));  
            registry.register(new Scheme("https", sf, 443));  
    
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);  
    
            return new DefaultHttpClient(ccm, params);  
        } catch (Exception e) {  
            return new DefaultHttpClient();  
        }  
    }  

}

interface HttpThreadListener {
    public void start();

    public void netError(String error);

    public void complete(String result);
}
