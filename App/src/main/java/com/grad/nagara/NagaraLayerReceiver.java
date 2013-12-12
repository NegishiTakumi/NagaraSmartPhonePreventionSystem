package com.grad.nagara;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by CST on 13/12/09.
 */
public class NagaraLayerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(MainActivity.TAG, "intent : " + intent.getAction());
        Log.v(MainActivity.TAG,
                "data string : " + intent.getDataString());

        Log.v(MainActivity.TAG, "intent : " + intent.getAction());
        Log.v(MainActivity.TAG,
                "data string : " + intent.getDataString());
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)
                || (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED) && intent
                .getDataString().equals(
                        "package:com.graduate.graduationapplication.main_activity"))) {
            Log.v(MainActivity.TAG, "send intent");
            context.stopService(new Intent(context,
                    NagaraLayerService.class));
            context.startService(new Intent(context,
                    NagaraLayerService.class));
        }
    }
}
