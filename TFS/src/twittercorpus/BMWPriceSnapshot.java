package twittercorpus;

import java.time.ZonedDateTime;

/**
 * Created by Andrew on 16/06/15.
 */
public class BMWPriceSnapshot implements PriceSnapshot {

    private ZonedDateTime timeStamp;
    private double openingSharePrice;
    private double openingMACDDirectionSignal;        // MACD Level minus Signal Level.  A positive signal indicates upward price momentum and vice versa
    private double closingSharePrice;

    public BMWPriceSnapshot(ZonedDateTime timeStamp, double openingSharePrice, double openingMACDDirectionSignal, double closingSharePrice) {
        this.timeStamp = timeStamp;
        this.openingSharePrice = openingSharePrice;
        this.openingMACDDirectionSignal = openingMACDDirectionSignal;
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

    public double getOpeningMACDDirectionSignal() {
        return openingMACDDirectionSignal;
    }

    public void setOpeningMACDDirectionSignal(double openingMACDDirectionSignal) {
        this.openingMACDDirectionSignal = openingMACDDirectionSignal;
    }

    public double getClosingSharePrice() {
        return closingSharePrice;
    }

    public void setClosingSharePrice(double closingSharePrice) {
        this.closingSharePrice = closingSharePrice;
    }
}
