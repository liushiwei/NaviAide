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
           case R.id.cafe:
               it = new Intent(getBaseContext(),MapMode.class);
               it.putExtra("hotkey", MapMode.CAFE);
               startActivity(it);
               break;
           case R.id.car_repair:
               it = new Intent(getBaseContext(),MapMode.class);
               it.putExtra("hotkey", MapMode.CAR_REPAIR);
               startActivity(it);
               break;
           case R.id.hotel:
               it = new Intent(getBaseContext(),MapMode.class);
               it.putExtra("hotkey", MapMode.LODGING);
               startActivity(it);
               break;
               
           case R.id.car_dealer:
               it = new Intent(getBaseContext(),MapMode.class);
               it.putExtra("hotkey", MapMode.CAR_DEALER);
               startActivity(it);
               break;
           case R.id.shopping:
               it = new Intent(getBaseContext(),MapMode.class);
               it.putExtra("hotkey", MapMode.SHOPPING);
               startActivity(it);
               break;
           case R.id.medical:
               it = new Intent(getBaseContext(),MapMode.class);
               it.putExtra("hotkey", MapMode.MEDICAL);
               startActivity(it);
               break;
           case R.id.traffic:
               it = new Intent(getBaseContext(),MapMode.class);
               it.putExtra("hotkey", MapMode.TRAFFIC);
               startActivity(it);
               break;
           case R.id.car_route:
               it = new Intent(getBaseContext(),RouteActivity.class);
               startActivity(it);
               break;
        }
    }
        
}
