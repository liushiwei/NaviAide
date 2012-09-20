
package com.carit.imhere;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AdjustLatLng {
    private static int zoom = 18;// 地图层级
    // private static int offsetX = -270;//当前层级下X偏移
    // private static int offsetY = 1193;

    /**
     * 请求谷歌，获取X,Y偏移量
     */
    public static double[] request(double lat, double lng) {
        String path = "http://ditu.google.cn/maps/vp?spn=0.0,0.0&z=18&vp=" + lat + "," + lng;// 请求的路径
        InputStream is = null;
        ByteArrayOutputStream bao = null;
        HttpURLConnection urlConn = null;
        double[] offsetPix = new double[2];
        try {
            URL url = new URL(path.toString());
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
            is = urlConn.getInputStream();
            bao = new ByteArrayOutputStream();
            byte block[] = new byte[4096];
            int read = is.read(block);
            while (read != -1) {
                bao.write(block, 0, read);
                read = is.read(block);
            }
            String shuJu = new String(bao.toByteArray(), "utf-8");
            int x = shuJu.lastIndexOf("[");
            int y = shuJu.lastIndexOf("]");
            // System.out.print(x+","+y);
            // System.out.println(shuJu);
            if (x > 0 && y > 0) {
                String text = shuJu.substring(x + 1, y);

                int b = text.lastIndexOf(",");

                int a = text.lastIndexOf(",", b - 1);

                if (a > 0 && b > 0) {

                    String offsetPixX = text.substring(a + 2, b);

                    String offsetPixY = text.substring(b + 2);
                    // System.out.println(offsetPixX);
                    // System.out.println(offsetPixY);
                    offsetPix[0] = Double.parseDouble(offsetPixX);
                    offsetPix[1] = Double.parseDouble(offsetPixY);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (bao != null) {
                    bao.close();
                    bao = null;
                }
                if (is != null) {
                    is.close();
                    is = null;
                }
                if (urlConn != null) {
                    urlConn.disconnect();
                    urlConn = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return offsetPix;
    }

    /**
     * 经度转化成像素值
     * 
     * @param lng
     * @param zoom
     * @return
     */
    public static double lngToPixel(double lng, int zoom) {

        return (lng + 180) * (256L << zoom) / 360;

    }

    /**
     * 像素值转化为经度值
     * 
     * @param pixelX
     * @param zoom
     * @return
     */

    public static double pixelToLng(double pixelX, int zoom) {

        return pixelX * 360 / (256L << zoom) - 180;

    }

    /**
     * 纬度转化为像素值
     * 
     * @param lat
     * @param zoom
     * @return
     */

    public static double latToPixel(double lat, int zoom) {

        double siny = Math.sin(lat * Math.PI / 180);

        double y = Math.log((1 + siny) / (1 - siny));

        return (128 << zoom) * (1 - y / (2 * Math.PI));

    }

    /**
     * 像素值转化为纬度
     * 
     * @param pixelY
     * @param zoom
     * @return
     */
    public static double pixelToLat(double pixelY, int zoom) {

        double y = 2 * Math.PI * (1 - pixelY / (128 << zoom));

        double z = Math.pow(Math.E, y);

        double siny = (z - 1) / (z + 1);

        return Math.asin(siny) * 180 / Math.PI;

    }

    /**
     * 调整后的经纬度
     * 
     * @param lat
     * @param lng
     * @return
     */
    public static double[] adjustLatLng(double lat, double lng) {
        //double[] offset = request(lat, lng);
        double[] offset = {907,615};
        double latPixel = Math.round(latToPixel(lat, zoom));
        double lngPixel = Math.round(lngToPixel(lng, zoom));
        latPixel += offset[1];
        lngPixel += offset[0];
        double adjustedLat = pixelToLat(latPixel, zoom);
        double adjustedLng = pixelToLng(lngPixel, zoom);
        double[] adjustedLatLng = {
                adjustedLat, adjustedLng
        };
        return adjustedLatLng;
    }

}
