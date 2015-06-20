package twittercorpus;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.Assert.*;

/**
 * Created by Andrew on 19/06/15.
 */
public class PriceLabelCorpusImplTest {

    private PriceLabelCorpus plCorpus;
    private String plFilename;

    @Before
    public void setUp() throws Exception {
        plFilename = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/BMW Price Data - April 2015.txt";
        plCorpus = new PriceLabelCorpusImpl(plFilename);
        plCorpus.extractPriceDataFromFile(plFilename);
    }

    @Test
    public void testGetPriceMap() throws Exception {
        assertFalse(plCorpus.getPriceMap().isEmpty());
    }

    @Test
    public void testSetPriceMap() throws Exception {
        // not necessary?
    }

    @Test
    public void testGetFileName() throws Exception {
        // not necessary?
    }

    @Test
    public void testSetFileName() throws Exception {
        // not necessary?
    }

    @Test
    public void testExtractPriceDataFromFile() throws Exception {

        // search on data entry: 30/04/15 16:35	106.1	106.1	106.1	106.1	0.0403	0.0584	-0.0181
        LocalDateTime localPlTS = LocalDateTime.of(2015,4,30,16,35);
        ZonedDateTime plTS = ZonedDateTime.of(localPlTS, ZoneId.of("Europe/London"));
        assertTrue(plCorpus.getPriceMap().get(plTS).getClosingSharePrice() - 106.1 < 0.0000001);
    }
}