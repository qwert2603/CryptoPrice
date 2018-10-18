package com.qwert2603.cryptoprice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import java.util.Objects;

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) return;
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(MainActivity.KEY_SERVICE_ON, false)) {
            RequestService.makeStart(context);
        }
    }
}
