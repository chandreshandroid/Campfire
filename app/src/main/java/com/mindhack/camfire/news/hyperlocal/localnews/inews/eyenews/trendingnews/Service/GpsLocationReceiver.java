package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.widget.Toast;



public class GpsLocationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            Toast.makeText(context, "in android.location.PROVIDERS_CHANGED",
                    Toast.LENGTH_SHORT).show();
            IntentFilter filter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
            filter.addAction(Intent.ACTION_PROVIDER_CHANGED);
        }
    }
}
