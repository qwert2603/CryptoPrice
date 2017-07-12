package com.qwert2603.cryptoprice;

public class Ticker {

    private double high;
    private double low;
    private double avg;
    private double vol;
    private double volCur;
    private double last;
    private double buy;
    private double sell;
    private int updated;

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getAvg() {
        return avg;
    }

    public void setAvg(Double avg) {
        this.avg = avg;
    }

    public Double getVol() {
        return vol;
    }

    public void setVol(Double vol) {
        this.vol = vol;
    }

    public Double getVolCur() {
        return volCur;
    }

    public void setVolCur(Double volCur) {
        this.volCur = volCur;
    }

    public Double getLast() {
        return last;
    }

    public void setLast(Double last) {
        this.last = last;
    }

    public Double getBuy() {
        return buy;
    }

    public void setBuy(Double buy) {
        this.buy = buy;
    }

    public Double getSell() {
        return sell;
    }

    public void setSell(Double sell) {
        this.sell = sell;
    }

    public Integer getUpdated() {
        return updated;
    }

    public void setUpdated(Integer updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        return "Ticker{" +
                "high=" + high +
                ", low=" + low +
                ", avg=" + avg +
                ", vol=" + vol +
                ", volCur=" + volCur +
                ", last=" + last +
                ", buy=" + buy +
                ", sell=" + sell +
                ", updated=" + updated +
                '}';
    }
}