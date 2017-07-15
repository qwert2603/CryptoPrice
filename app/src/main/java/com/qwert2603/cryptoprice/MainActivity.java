package com.qwert2603.cryptoprice;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends Activity {

    public static final String KEY_SERVICE_ON = "KEY_SERVICE_ON";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Switch switchCompat = findViewById(R.id.enable_Switch);
        switchCompat.setChecked(sharedPreferences.getBoolean(KEY_SERVICE_ON, false));
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(MainActivity.this, RequestService.class);
                if (isChecked) {
                    startService(intent);
                } else {
                    stopService(intent);
                }
                sharedPreferences.edit().putBoolean(KEY_SERVICE_ON, isChecked).apply();
            }
        });
    }
}
