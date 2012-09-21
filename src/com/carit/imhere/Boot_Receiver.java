package com.carit.imhere;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Boot_Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent it = new Intent(Intent.ACTION_RUN);
            it.setClass(context, NaviAideService.class);
            it.putExtra("from", NaviAideService.BOOT_COMPLETED);
            context.startService(it);
        }
    }

}