package com.qwert2603.cryptoprice;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface Rest {
    @GET("ticker/btc_usd-btc_rur-ltc_usd-ltc_rur-eth_usd-eth_rur")
    Single<Tickers> getTickers(@Query("ignore_invalid") int ignore_invalid);
}
