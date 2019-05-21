package com.qwert2603.cryptoprice;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import org.json.JSONArray;
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
            "BTCUSDT",

            "ETHUSDT", "ETHBTC",
            "BNBUSDT", "BNBBTC",
            "LTCUSDT", "LTCBTC",
            "TRXUSDT", "TRXBTC",
            "XRPUSDT", "XRPBTC",
    };

    private static int indexOfPair(String s) {
        for (int i = 0; i < PAIRS.length; i++) {
            if (s.equals(PAIRS[i])) return i;
        }
        return -1;
    }

    private static final int NOTIFICATION_ID = 1;

    private static final String CHANNEL_ID = "channel_cr_pr";

    private volatile boolean isDestroyed = false;

    private final Runnable loadRunnable = new Runnable() {
        @Override
        public void run() {
            while (!isDestroyed) {
                final String url = "https://api.binance.com/api/v3/ticker/price";
                LogUtils.d("RequestService " + RequestService.this.hashCode() + " request to " + url);
                try (InputStream inputStream = new URL(url).openStream()) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = bufferedReader.readLine();
                    LogUtils.d("RequestService " + RequestService.this.hashCode() + " response is " + line);
                    double[] lastPrices = new double[PAIRS.length];

                    JSONArray jsonArray = new JSONArray(line);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String symbol = jsonObject.getString("symbol");
                        int indexOf = indexOfPair(symbol);
                        if (indexOf >= 0) {
                            lastPrices[indexOf] = jsonObject.getDouble("price");
                        }
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
        super.onDestroy();
    }

    @SuppressLint("DefaultLocale")
    private void showNotification(double[] lastPrices) {
        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
        StringBuilder stringBuilder = new StringBuilder();
        List<Integer> newLines = Arrays.asList(0, 2, 4, 6, 8);
        for (int i = 0; i < PAIRS.length; i++) {
            stringBuilder
                    .append(PAIRS[i])
                    .append(' ')
                    .append(String.format("%f", lastPrices[i]))
                    .append(newLines.contains(i) ? '\n' : "\t\t\t");
        }
        bigTextStyle.bigText(stringBuilder.toString());

        startForeground(NOTIFICATION_ID, (HAS_OREO ? new Notification.Builder(this, CHANNEL_ID) : new Notification.Builder(this))
                .setSmallIcon(R.drawable.icon)
                .setTicker(getString(R.string.app_name))
                .setColor(0x007B00)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(PAIRS[0] + " " + lastPrices[0])
                .setStyle(bigTextStyle)
                .setOngoing(true)
                .setShowWhen(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0))
                .build()
        );
    }
}
