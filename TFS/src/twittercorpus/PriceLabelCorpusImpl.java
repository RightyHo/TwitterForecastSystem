package twittercorpus;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Created by Andrew on 16/06/15.
 */
public class PriceLabelCorpusImpl implements PriceLabelCorpus {

    private Map<ZonedDateTime,PriceSnapshot> priceMap;
    private String fileName;

    public void extractPriceDataFromFile(String fileName){

    }
}
