
package com.carit.imhere;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
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

    private boolean isPost;
    
    private Map<String, String> param;

    // private PlaceSearchResult result;
    public HttpThread(String url, HttpThreadListener listener) {
        this(url, listener, false);
    }
    
    public HttpThread(String url, HttpThreadListener listener, boolean isPost) {
        Log.e("HttpThread", "URL = " + url);
        this.url = url;
        this.listener = listener;
        this.isPost = isPost;
    }
    public HttpThread(String url, HttpThreadListener listener, Map<String, String> param) {
        Log.e("HttpThread", "URL = " + url);
        this.url = url;
        this.listener = listener;
        this.isPost = true;
        this.param = param;
    }

    @Override
    public void run() {
        listener.start();
        if (!isPost) {
            HttpGet get = new HttpGet(url);
            String strResult = "";
            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 20000);
                HttpConnectionParams.setSoTimeout(httpParameters, 20000);
                HttpClient httpClient = getNewHttpClient();
                get.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
                get.addHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3");
                HttpResponse httpResponse = null;
                httpResponse = httpClient.execute(get);

                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    strResult = EntityUtils.toString(httpResponse.getEntity());
                    // decodeResult(strResult);
                    listener.complete(strResult);
                }
            } catch (Exception e) {
                e.printStackTrace();
                listener.netError(e.toString());
            }
        }else{
            HttpPost post = new HttpPost(url);
            String strResult = "";
            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 20000);
                HttpConnectionParams.setSoTimeout(httpParameters, 20000);
                HttpClient httpClient = getNewHttpClient();
                post.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
                post.addHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3");
                List<NameValuePair> params = new ArrayList<NameValuePair>();  
                                        
                for (Entry<String, String> e: param.entrySet()) {
                    //url.append(e.getKey()).append("=").append(e.getValue()).append("&");
                    params.add(new BasicNameValuePair(e.getKey(), e.getValue())); 
                }
                post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                HttpResponse httpResponse = null;
                httpResponse = httpClient.execute(post);

                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    strResult = EntityUtils.toString(httpResponse.getEntity());
                    // decodeResult(strResult);
                    listener.complete(strResult);
                }
            } catch (Exception e) {
                e.printStackTrace();
                listener.netError(e.toString());
            }
            
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
