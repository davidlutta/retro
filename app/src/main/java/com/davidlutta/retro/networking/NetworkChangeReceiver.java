package com.davidlutta.retro.networking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ServiceManager serviceManager = new ServiceManager(context);
        if (!serviceManager.isNetworkAvailable()) {
            Toast.makeText(context, "No Internet Connection\nFetching data from cache", Toast.LENGTH_LONG).show();
        }
    }
}
