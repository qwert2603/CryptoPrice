package com.qwert2603.cryptoprice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class RequestService extends Service {

    private static final boolean HAS_OREO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;

    public static void makeStart(Context context) {
        LogUtils.d("RequestService makeStart");
        Intent intent = new Intent(context, RequestService.class);
        if (HAS_OREO) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    private static final String[] PAIRS = {
            "btc_usd",
            "btc_rur",

            "ltc_btc",
            "ltc_usd",

            "eth_btc",
            "eth_usd",

            "dsh_btc",
            "dsh_usd",

            "bch_btc",
            "bch_usd",

            "nmc_btc",
            "nvc_btc",
            "ppc_btc",

//            "ltc_rur",
//            "usd_rur",
//            "btc_eur",
//            "ltc_eur",
//            "nmc_usd",
//            "nvc_usd",
//            "eur_usd",
//            "eur_rur",
//            "ppc_usd",
//            "dsh_rur",
//            "dsh_eur",
//            "dsh_ltc",
//            "dsh_eth",
//            "eth_eur",
//            "eth_ltc",
//            "eth_rur",
    };

    private static final int NOTIFICATION_ID = 1;

    private static final String CHANNEL_ID = "channel_cr_pr";

    private volatile boolean isDestroyed = false;

    private final Runnable loadRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (isDestroyed) break;

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("https://wex.nz/api/3/ticker/");
                for (String pair : PAIRS) {
                    stringBuilder.append(pair).append('-');
                }
                stringBuilder.append("?ignore_invalid=1");

                final String url = stringBuilder.toString();
                LogUtils.d("RequestService " + RequestService.this.hashCode() + " request to " + url);
                try (InputStream inputStream = new URL(url).openStream()) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = bufferedReader.readLine();
                    LogUtils.d("RequestService " + RequestService.this.hashCode() + " response is " + line);
                    JSONObject jsonObject = new JSONObject(line);
                    double[] lastPrices = new double[PAIRS.length];
                    for (int i = 0; i < PAIRS.length; i++) {
                        lastPrices[i] = jsonObject.getJSONObject(PAIRS[i]).getDouble("last");
                    }
                    if (!isDestroyed) showNotification(lastPrices);
                } catch (Exception ignored) {
                }

                try {
                    Thread.sleep(2000);
                } catch (Exception ignored) {
                }
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LogUtils.d("RequestService " + hashCode() + " onCreate");

        if (HAS_OREO) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "current price", NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableVibration(false);
            notificationChannel.enableLights(false);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        startForeground(NOTIFICATION_ID, (HAS_OREO ? new Notification.Builder(this, CHANNEL_ID) : new Notification.Builder(this))
                .setSmallIcon(R.drawable.icon)
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
        LogUtils.d("RequestService " + hashCode() + " onDestroy");

        isDestroyed = true;
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(MainActivity.KEY_SERVICE_ON, false)
                .apply();
        super.onDestroy();
    }

    private void showNotification(double[] lastPrices) {
        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
        StringBuilder stringBuilder = new StringBuilder();
        List<Integer> newLines = Arrays.asList(1, 3, 5, 7, 9, 10, 11, 12);
        for (int i = 0; i < PAIRS.length; i++) {
            stringBuilder
                    .append(PAIRS[i])
                    .append(' ')
                    .append(lastPrices[i])
                    .append(newLines.contains(i) ? '\n' : ' ');
        }
        bigTextStyle.bigText(stringBuilder.toString());

        startForeground(NOTIFICATION_ID, (HAS_OREO ? new Notification.Builder(this, CHANNEL_ID) : new Notification.Builder(this))
                .setSmallIcon(R.drawable.icon)
                .setTicker(getString(R.string.app_name))
                .setColor(0x007B00)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(PAIRS[0] + " " + lastPrices[0] + " " + PAIRS[1] + " " + lastPrices[1])
                .setStyle(bigTextStyle)
                .setOngoing(true)
                .setShowWhen(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0))
                .build()
        );
    }
}
