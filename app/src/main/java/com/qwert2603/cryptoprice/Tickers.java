package com.qwert2603.cryptoprice;

public class Tickers {
    final Ticker btc_usd;
    final Ticker btc_rur;
    final Ticker ltc_usd;
    final Ticker ltc_rur;
    final Ticker eth_usd;
    final Ticker eth_rur;

    public Tickers(Ticker btc_usd, Ticker btc_rur, Ticker ltc_usd, Ticker ltc_rur, Ticker eth_usd, Ticker eth_rur) {
        this.btc_usd = btc_usd;
        this.btc_rur = btc_rur;
        this.ltc_usd = ltc_usd;
        this.ltc_rur = ltc_rur;
        this.eth_usd = eth_usd;
        this.eth_rur = eth_rur;
    }

    @Override
    public String toString() {
        return "Tickers{" +
                "btc_usd=" + btc_usd +
                ", btc_rur=" + btc_rur +
                ", ltc_usd=" + ltc_usd +
                ", ltc_rur=" + ltc_rur +
                ", eth_usd=" + eth_usd +
                ", eth_rur=" + eth_rur +
                '}';
    }
}
