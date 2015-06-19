package twittercorpus;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Created by Andrew on 16/06/15.
 */
public interface PriceLabelCorpus {

    Map<ZonedDateTime, PriceSnapshot> getPriceMap();

    void setPriceMap(Map<ZonedDateTime, PriceSnapshot> priceMap);

    String getFileName();

    void setFileName(String fileName);

    void extractPriceDataFromFile(String fileName);


}
