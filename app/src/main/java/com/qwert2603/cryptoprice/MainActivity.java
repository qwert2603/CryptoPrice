package com.qwert2603.cryptoprice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_SERVICE_ON = "KEY_SERVICE_ON";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        SwitchCompat switchCompat = (SwitchCompat) findViewById(R.id.enable_Switch);
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
