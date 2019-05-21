package com.qwert2603.cryptoprice;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity {

    public static final String KEY_SERVICE_ON = "KEY_SERVICE_ON";

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Switch switchCompat = findViewById(R.id.enable_Switch);
        switchCompat.setChecked(sharedPreferences.getBoolean(KEY_SERVICE_ON, false));
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    RequestService.makeStart(MainActivity.this);
                } else {
                    stopService(new Intent(MainActivity.this, RequestService.class));
                }
                sharedPreferences.edit().putBoolean(KEY_SERVICE_ON, isChecked).apply();
            }
        });

        final TextView launchInfoTextView = findViewById(R.id.launchInfo_TextView);
        launchInfoTextView.setText(Html.fromHtml(getString(R.string.launch_info)));
        launchInfoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getPackageName(), null)
                );
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferences.getBoolean(KEY_SERVICE_ON, false)) {
            RequestService.makeStart(MainActivity.this);
        }
    }
}
