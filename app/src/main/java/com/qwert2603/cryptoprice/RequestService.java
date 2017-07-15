package com.qwert2603.cryptoprice;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RequestService extends Service {

    public static final String[] PAIRS = {
            "btc_usd",
            "btc_rur",
            "btc_eur",
            "ltc_btc",
            "ltc_usd",
            "ltc_rur",
            "ltc_eur",
            "nmc_btc",
            "nmc_usd",
            "nvc_btc",
            "nvc_usd",
            "usd_rur",
            "eur_usd",
            "eur_rur",
            "ppc_btc",
            "ppc_usd",
            "dsh_btc",
            "dsh_usd",
            "dsh_rur",
            "dsh_eur",
            "dsh_ltc",
            "dsh_eth",
            "eth_btc",
            "eth_usd",
            "eth_eur",
            "eth_ltc",
            "eth_rur",
    };

    private volatile boolean isDestroyed = false;

    private final Runnable loadRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (isDestroyed) break;

                InputStream inputStream = null;
                try {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("https://btc-e.nz/api/3/ticker/");
                    for (String pair : PAIRS) {
                        stringBuilder.append(pair).append('-');
                    }
                    stringBuilder.append("?ignore_invalid=1");
                    Log.d("AASSDD", stringBuilder.toString());
                    inputStream = new URL(stringBuilder.toString()).openStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = bufferedReader.readLine();
                    JSONObject jsonObject = new JSONObject(line);
                    Map<String, Double> lastPrices = new HashMap<>();
                    for (String pair : PAIRS) {
                        lastPrices.put(pair, jsonObject.getJSONObject(pair).getDouble("last"));
                    }
                    if (!isDestroyed) showNotification(lastPrices);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        startForeground(1, new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon)
                .setTicker(getString(R.string.app_name))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.loading_text))
                .setOngoing(true)
                .setShowWhen(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0))
                .build()
        );

        new Thread(loadRunnable).start();
    }

    @Override
    public void onDestroy() {
        isDestroyed = true;
        super.onDestroy();
    }

    private void showNotification(Map<String, Double> lastPrices) {
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        StringBuilder stringBuilder = new StringBuilder();
        for (String pair : PAIRS) {
            stringBuilder.append(pair).append(' ').append(lastPrices.get(pair)).append('\n');
        }
        bigTextStyle.bigText(stringBuilder.toString());

        startForeground(1, new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon)
                .setTicker(getString(R.string.app_name))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(PAIRS[0] + " " + lastPrices.get(PAIRS[0]))
                .setStyle(bigTextStyle)
                .setOngoing(true)
                .setShowWhen(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0))
                .build()
        );
    }
}
