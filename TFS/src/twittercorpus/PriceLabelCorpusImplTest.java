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

    /**
     * test that the output from a couple of price snapshots extracted at random match the input data
     * @throws Exception
     */
    @Test
    public void testExtractPriceDataFromFile() throws Exception {

//        Iterator<ZonedDateTime> it = plCorpus.getPriceMap().keySet().iterator();
//        if(it.hasNext()){
//            ZonedDateTime zKey = it.next();
//            System.out.println(zKey);
//            System.out.println("day "+zKey.getDayOfMonth());
//            System.out.println("month "+zKey.getMonthValue());
//            System.out.println("year "+zKey.getYear());
//        }

        // search on data entry: 20/01/2015 15:11	94.87	94.91	94.86	94.91	0.0431	0.0191	0.024
        LocalDateTime localPlTS = LocalDateTime.of(2015,1,20,15,11,0);
        ZonedDateTime plTS = ZonedDateTime.of(localPlTS, ZoneId.of("Europe/London"));
        assertNotNull(plCorpus.getPriceMap().get(plTS));
        System.out.println(plCorpus.getPriceMap().get(plTS));
        if(plCorpus.getPriceMap().get(plTS) != null)
            assertTrue(Math.abs(plCorpus.getPriceMap().get(plTS).getClosingSharePrice() - 94.91) < 0.0000000000000001);

        // search on data entry:  19/01/2015 10:34	94.04	94.04	93.99	93.99	-0.0011	0.0001	-0.0012
        localPlTS = LocalDateTime.of(2015,1,19,10,34,0);
        plTS = ZonedDateTime.of(localPlTS, ZoneId.of("Europe/London"));
        assertNotNull(plCorpus.getPriceMap().get(plTS));
        if(plCorpus.getPriceMap().get(plTS) != null)
            assertTrue(Math.abs(plCorpus.getPriceMap().get(plTS).getOpeningMACDDirectionSignal() - (-0.0012)) < 0.0000000000000001);
    }

}