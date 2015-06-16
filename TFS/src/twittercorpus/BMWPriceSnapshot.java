package twittercorpus;

import java.time.ZonedDateTime;

/**
 * Created by Andrew on 16/06/15.
 */
public class BMWPriceSnapshot implements PriceSnapshot {

    private ZonedDateTime timeStamp;
    private double openingSharePrice;
    private double openingMACDLevel;
    private double price20MinsLater;

}
