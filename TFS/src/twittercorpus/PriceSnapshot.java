package twittercorpus;

import java.time.ZonedDateTime;

/**
 * Created by Andrew on 16/06/15.
 */
public interface PriceSnapshot {
     ZonedDateTime getTimeStamp();

     void setTimeStamp(ZonedDateTime timeStamp);

     double getOpeningSharePrice();

     void setOpeningSharePrice(double openingSharePrice);

     double getOpeningMACDLevel();

     void setOpeningMACDLevel(double openingMACDLevel);

     double getClosingSharePrice();

     void setClosingSharePrice(double closingSharePrice);
}
