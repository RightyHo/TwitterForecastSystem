package twittercorpus;

import java.time.ZonedDateTime;

/**
 * Created by Andrew on 16/06/15.
 */
public class BMWPriceSnapshot implements PriceSnapshot {

    private ZonedDateTime timeStamp;
    private double openingSharePrice;
    private double openingMACDLevel;        // do i want the MACD level or the difference between this level and the signal level?
    private double closingSharePrice;

    public BMWPriceSnapshot(ZonedDateTime timeStamp, double openingSharePrice, double openingMACDLevel, double closingSharePrice) {
        this.timeStamp = timeStamp;
        this.openingSharePrice = openingSharePrice;
        this.openingMACDLevel = openingMACDLevel;
        this.closingSharePrice = closingSharePrice;
    }

    public ZonedDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(ZonedDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getOpeningSharePrice() {
        return openingSharePrice;
    }

    public void setOpeningSharePrice(double openingSharePrice) {
        this.openingSharePrice = openingSharePrice;
    }

    public double getOpeningMACDLevel() {
        return openingMACDLevel;
    }

    public void setOpeningMACDLevel(double openingMACDLevel) {
        this.openingMACDLevel = openingMACDLevel;
    }

    public double getClosingSharePrice() {
        return closingSharePrice;
    }

    public void setClosingSharePrice(double closingSharePrice) {
        this.closingSharePrice = closingSharePrice;
    }
}
