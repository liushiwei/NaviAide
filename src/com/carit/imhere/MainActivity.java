package com.carit.imhere;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void onClick(View view){
        Intent it = null;
        switch(view.getId()){
            case R.id.atmwz:
                it = new Intent();
                it.setClassName("com.findatmwemi", "com.findatmwemi.main");
                startActivity(it);
                break;
            case R.id.bddt:
                it = new Intent();
                it.setClassName("com.baidu.BaiduMap", "com.baidu.BaiduMap.BaiduMap");
                startActivity(it);
                break;
            case R.id.changxing:
                it = new Intent();
                it.setClassName("net.loopu.express", "net.loopu.express.WelcomeActivity");
                startActivity(it);
                break;
            case R.id.gdcyh:
                it = new Intent();
                it.setClassName("com.autonavi.cvc.app.aac", "com.autonavi.cvc.app.aac.ui.actvy.ActvySplash");
                startActivity(it);
                break;
            case R.id.hbgj:
                it = new Intent();
                it.setClassName("com.flightmanager.view", "com.flightmanager.view.Loading");
                startActivity(it);
                break;
            case R.id.hlzh:
                it = new Intent();
                it.setClassName("com.umetrip.android.msky", "com.umetrip.android.msky.activity.SplashActivity");
                startActivity(it);
                break;
            case R.id.jddr:
                it = new Intent();
                it.setClassName("cn.ikamobile.hotelfinder", "cn.ikamobile.hotelfinder.HotelFinderActivity");
                startActivity(it);
                break;
            case R.id.kaka:
                it = new Intent();
                it.setClassName("cn.mucang.kaka.android", "cn.mucang.kaka.android.Login");
                startActivity(it);
                break;
            case R.id.sslk:
                it = new Intent();
                it.setClassName("com.wxcs", "com.wxcs.wxcs");
                startActivity(it);
                break;
            case R.id.czjt:
                it = new Intent();
                it.setClassName("com.heiwen.carinjt.activity", "com.heiwen.carinjt.activity.Main");
                startActivity(it);
                break;
           case R.id.lrts:
                it = new Intent();
                it.setClassName("bubei.tingshu", "bubei.tingshu.ui.Home");
                startActivity(it);
                break;
           case R.id.bdyy:
                it = new Intent();
                it.setClassName("com.ting.mp3.android", "com.ting.mp3.android.SplashActivity");
                startActivity(it);
                break;
            /*
             * case R.id.bddt:
                it = new Intent();
                it.setClassName("com.baidu.BaiduMap", "com.baidu.BaiduMap.BaiduMap");
                startActivity(it);
                break;
            case R.id.bddt:
                it = new Intent();
                it.setClassName("com.baidu.BaiduMap", "com.baidu.BaiduMap.BaiduMap");
                startActivity(it);
                break;*/
           case R.id.parking:
               it = new Intent(getBaseContext(),MapMode.class);
               it.putExtra("hotkey", MapMode.PARKING);
               startActivity(it);
               break;
           case R.id.bank:
               it = new Intent(getBaseContext(),MapMode.class);
               it.putExtra("hotkey", MapMode.BANK);
               startActivity(it);
               break;
           case R.id.gas_station:
               it = new Intent(getBaseContext(),MapMode.class);
               it.putExtra("hotkey", MapMode.GAS_STATION);
               startActivity(it);
               break;
           case R.id.food:
               it = new Intent(getBaseContext(),MapMode.class);
               it.putExtra("hotkey", MapMode.FOOD);
               startActivity(it);
               break;
           case R.id.coffee:
               it = new Intent(getBaseContext(),MapMode.class);
               it.putExtra("hotkey", MapMode.CAR_REPAIR);
               startActivity(it);
               break;
           case R.id.sport:
               it = new Intent(getBaseContext(),MapMode.class);
               it.putExtra("hotkey", MapMode.CAR_WASH);
               startActivity(it);
               break;
        }
    }
        
}
