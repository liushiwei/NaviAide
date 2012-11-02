
package com.carit.imhere;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener{

    private boolean isSearchMyLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String location_name = intent.getStringExtra("search_location");
        if (location_name != null) {
            ((TextView) findViewById(R.id.label)).setText(Html.fromHtml(String.format(
                    getString(R.string.label), location_name)));
            isSearchMyLocation = false;
        } else {
            ((TextView) findViewById(R.id.label)).setText(Html.fromHtml(String.format(
                    getString(R.string.label), getString(R.string.myself_location))));
            isSearchMyLocation = true;
        }
        findViewById(R.id.btn_home).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.btn_home){
            startActivity(new Intent(Intent.ACTION_MAIN).addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addCategory(Intent.CATEGORY_HOME));
            return;
        }
        if(view.getId()==R.id.btn_back){
            finish();
            return;
        }
        Intent it = null;
        if(isSearchMyLocation){
            it = new Intent(getBaseContext(), MapMode.class);
            }else{
                it = getIntent();
                it.setClass(this, MapMode.class);
            }
        switch (view.getId()) {
            case R.id.parking:
                it.putExtra("hotkey", MapMode.PARKING);
                break;
            case R.id.bank:
                it.putExtra("hotkey", MapMode.BANK);
                break;
            case R.id.gas_station:
                it.putExtra("hotkey", MapMode.GAS_STATION);
                break;
            case R.id.food:
                it.putExtra("hotkey", MapMode.FOOD);
                break;
            case R.id.cafe:
                it.putExtra("hotkey", MapMode.CAFE);
                break;
            case R.id.car_repair:
                it.putExtra("hotkey", MapMode.CAR_REPAIR);
                break;
            case R.id.hotel:
                it.putExtra("hotkey", MapMode.LODGING);
                break;

            case R.id.car_dealer:
                it.putExtra("hotkey", MapMode.CAR_DEALER);
                break;
            case R.id.shopping:
                it.putExtra("hotkey", MapMode.SHOPPING);
                break;
            case R.id.medical:
                it.putExtra("hotkey", MapMode.MEDICAL);
                break;
            case R.id.traffic:
                it.putExtra("hotkey", MapMode.TRAFFIC);
                break;
            /*case R.id.car_route:
                it = new Intent(getBaseContext(), RouteActivity.class);
                startActivity(it);
                break;*/
        }
        startActivity(it);
        finish();
    }

}
