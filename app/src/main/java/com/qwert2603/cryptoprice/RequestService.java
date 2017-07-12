package com.qwert2603.cryptoprice;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestService extends Service {

    private Disposable disposable;

    private static Rest rest = new Retrofit.Builder()
            .baseUrl("https://btc-e.nz/api/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(Rest.class);

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

        disposable = Observable
                .interval(2, TimeUnit.SECONDS)
                .flatMapMaybe(new Function<Long, MaybeSource<Tickers>>() {
                    @Override
                    public MaybeSource<Tickers> apply(@NonNull Long aLong) throws Exception {
                        return rest.getTickers(1)
                                .toMaybe()
                                .onErrorResumeNext(new Function<Throwable, MaybeSource<? extends Tickers>>() {
                                    @Override
                                    public MaybeSource<? extends Tickers> apply(@NonNull Throwable throwable) throws Exception {
                                        Log.e("AASSDD", "error", throwable);
                                        return Maybe.empty();
                                    }
                                });
                    }
                })
                .subscribe(new Consumer<Tickers>() {
                    @Override
                    public void accept(@NonNull Tickers tickers) throws Exception {
                        Log.d("AASSDD", tickers.toString());
                        showNotification(tickers);
                    }
                });
    }

    @Override
    public void onDestroy() {
        if (disposable != null) disposable.dispose();
        super.onDestroy();
    }

    private void showNotification(Tickers tickers) {
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(
                "btc_usd\t" + tickers.btc_usd.getLast() + "\n"
                        + "btc_rur\t\t" + tickers.btc_rur.getLast() + "\n"
                        + "ltc_usd\t\t" + tickers.ltc_usd.getLast() + "\n"
                        + "ltc_rur\t\t" + tickers.ltc_rur.getLast() + "\n"
                        + "eth_usd\t" + tickers.eth_usd.getLast() + "\n"
                        + "eth_rur\t\t" + tickers.eth_rur.getLast()
        );

        startForeground(1, new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon)
                .setTicker(getString(R.string.app_name))
                .setContentTitle(getString(R.string.app_name))
                .setStyle(bigTextStyle)
                .setOngoing(true)
                .setShowWhen(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0))
                .build()
        );
    }
}
