package com.qwert2603.cryptoprice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(MainActivity.KEY_SERVICE_ON, false)) {
            context.startService(new Intent(context, RequestService.class));
        }
    }
}
