package twittercorpus;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * Created by Andrew on 19/06/15.
 */
public class PriceLabelCorpusImplTest {

    private PriceLabelCorpus plCorpus;
    private String plFilename;

    @Before
    public void setUp() throws Exception {
        plFilename = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/Test Price Data Sample.txt";
        plCorpus = new PriceLabelCorpusImpl(plFilename);
        plCorpus.extractPriceDataFromFile(plFilename);
    }

    @Test
    public void testGetPriceMap() throws Exception {
        assertFalse(plCorpus.getPriceMap().isEmpty());
    }

    @Test
    public void testSetPriceMap() throws Exception {
        plCorpus.setPriceMap(new HashMap<>());
        assertTrue(plCorpus.getPriceMap().isEmpty());
    }

    @Test
    public void testGetFileName() throws Exception {
        assertEquals("/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/Test Price Data Sample.txt",plCorpus.getFileName());
    }

    @Test
    public void testSetFileName() throws Exception {
        plCorpus.setFileName("this/file/does/not/exist.txt");
        assertEquals("this/file/does/not/exist.txt",plCorpus.getFileName());
    }

    @Test
    public void testExtractPriceDataFromFile() throws Exception {

        // search on data entry: 30/04/15 16:35	106.1	106.1	106.1	106.1	0.0403	0.0584	-0.0181
        LocalDateTime localPlTS = LocalDateTime.of(2015,4,30,16,35,0);
        ZonedDateTime plTS = ZonedDateTime.of(localPlTS, ZoneId.of("Europe/London"));
        assertNotNull(plCorpus.getPriceMap().get(plTS));
        if(plCorpus.getPriceMap().get(plTS) != null)
            assertTrue(Math.abs(plCorpus.getPriceMap().get(plTS).getClosingSharePrice() - 106.1) < 0.0000001);

        // search on data entry:  07/04/15 08:32	115.65	115.7	115.5	115.6	0.0443	0.0049	0.0394
        localPlTS = LocalDateTime.of(2015,4,7,8,32,0);
        plTS = ZonedDateTime.of(localPlTS, ZoneId.of("Europe/London"));
        assertNotNull(plCorpus.getPriceMap().get(plTS));
        if(plCorpus.getPriceMap().get(plTS) != null)
            assertTrue(Math.abs(plCorpus.getPriceMap().get(plTS).getOpeningMACDDirectionSignal() - 0.0394) < 0.0000001);
    }

}