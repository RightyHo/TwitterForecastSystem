package twittercorpus;

import java.time.ZonedDateTime;

/**
 * Created by Andrew on 16/06/15.
 */
public class BMWClosedMarketSnapshot implements PriceSnapshot {
    private ZonedDateTime timeStamp;
    private double previousDayClosePrice;
    private double previousDayCloseMACDLevel;
    private double nextDayOpenPrice;

}
