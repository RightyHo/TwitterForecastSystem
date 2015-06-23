package twittercorpus;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Andrew on 18/06/15.
 */
public class TwitterCorpusListImplTest {

    private TwitterCorpus tCorpus;
    private String tFilename;
    private String plFilename;

    @Before
    public void setUp() throws Exception {
        tFilename = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/Test Twitter Corpus Sample.txt";
        tCorpus = new TwitterCorpusListImpl(tFilename);
        tCorpus.extractTweetsFromFile(tFilename);
        plFilename = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/Test Price Data Sample.txt";
    }

    @Test
    public void testGetUsernameEquivalenceToken() throws Exception {
        assertTrue(tCorpus.getUsernameEquivalenceToken().equals("USERNAME"));
    }

    @Test
    public void testGetLinkEquivalenceToken() throws Exception {
        assertTrue(tCorpus.getLinkEquivalenceToken().equals("LINK"));
    }

    @Test
    public void testGetCorpus() throws Exception {
        assertFalse(tCorpus.getCorpus().isEmpty());
    }

    @Test
    public void testSetCorpus() throws Exception {
        tCorpus.setCorpus(new ArrayList<>());
        assertTrue(tCorpus.getCorpus().isEmpty());
    }

    @Test
    public void testGetFileName() throws Exception {
        assertEquals("/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/Test Twitter Corpus Sample.txt",tCorpus.getFileName());
    }

    @Test
    public void testSetFileName() throws Exception {
        tCorpus.setFileName("this/file/does/not/exist.txt");
        assertEquals("this/file/does/not/exist.txt",tCorpus.getFileName());
    }

    /**
     * first line should match with "Fri Jan 16 00:04:00 2015	#usedcars used 2003 bmw z4 2.5i in gainesville, fl 32601 for sale at autoflex llc http://t.co/fxbqiiu6rr http://t.co/rok2iyoh0c"
     * second line should match with "Fri Jan 16 00:18:00 2015	a new beemer http://t.co/uaff540vdf"
     * @throws Exception
     */
    @Test
    public void testExtractTweetsFromFile() throws Exception {

        // test first tweet in list
        assertEquals(tCorpus.getCorpus().get(0).getTweetText(),"#usedcars used 2003 bmw z4 2.5i in gainesville, fl 32601 for sale at autoflex llc http://t.co/fxbqiiu6rr http://t.co/rok2iyoh0c");

        // test second tweet in list
        LocalDateTime localExpectedTS = LocalDateTime.of(2015,1,16,0,18,0);
        ZonedDateTime expectedTS = ZonedDateTime.of(localExpectedTS, ZoneId.of("Europe/London"));
        assertEquals(expectedTS,tCorpus.getCorpus().get(1).getTimeStamp());
    }

    @Test
    public void testGetMonthNum() throws Exception {
        assertTrue(tCorpus.getMonthNum("apr") == 4);
        assertTrue(tCorpus.getMonthNum("Oct") == 10);
        assertTrue(tCorpus.getMonthNum("auG") == 8);
    }

//    * labels the tweet by setting the price snapshot that corresponds to the timestamp of the tweet as well as
//    * setting the sentiment value of the tweet and marking the isLabelled flag as true.
//    public void labelCorpus(PriceLabelCorpus labels)
    @Test
    public void testLabelCorpus() throws Exception {
        PriceLabelCorpus plCorpus = new PriceLabelCorpusImpl(plFilename);
        plCorpus.extractPriceDataFromFile(plFilename);
        tCorpus.labelCorpus(plCorpus);

        // test the first tweet in the corpus is labelled correctly:
        // Fri Jan 16 00:04:00 2015	#usedcars used 2003 bmw z4 2.5i in gainesville, fl 32601 for sale at autoflex llc http://t.co/fxbqiiu6rr http://t.co/rok2iyoh0c
        // out of market hours so initial snapshot should be of market close on Thur Jan 15 16:35:
        // 15/01/2015 16:35	91.07	91.07	91.07	91.07	0.0563	0.0481	0.0081
        // and post Tweet snapshot should be of market open on Fri Jan 16 8:00:
        // 16/01/2015 08:00	90.7	90.7	90.6	90.61	0.0213	0.0428	-0.0215
        assertTrue(Math.abs(tCorpus.getCorpus().get(0).getInitialSnapshot().getClosingSharePrice() - 91.07) < 0.0000000000000001);
        assertTrue(Math.abs(tCorpus.getCorpus().get(0).getPostTweetSnapshot().getClosingSharePrice() - 90.61) < 0.0000000000000001);

        // test the 50th tweet in the corpus is labelled correctly:
        // Fri Jan 16 09:58:00 2015	i posted 11 photos on facebook in the album "2015 bmw i8 coupe" http://t.co/4mtrbecs7c
        // during market hours so initial snapshot should be Fri Jan 16 09:57
        // 16/01/2015 09:57	91.2	91.25	91.2	91.23	0.0169	-0.0102	0.0271
        // and post Tweet snapshot should be Fri Jan 16 10:18
        // 16/01/2015 10:18	91.32	91.35	91.32	91.35	0.037	0.0366	0.0004
        assertTrue(Math.abs(tCorpus.getCorpus().get(0).getInitialSnapshot().getClosingSharePrice() - 91.23) < 0.0000000000000001);
        assertTrue(Math.abs(tCorpus.getCorpus().get(0).getPostTweetSnapshot().getClosingSharePrice() - 91.35) < 0.0000000000000001);
    }

    @Test
    public void testRemoveRetweets() throws Exception {

    }

    @Test
    public void testReplaceLinks() throws Exception {

    }

    @Test
    public void testReplaceUsernames() throws Exception {

    }

    @Test
    public void testTranslateAbbreviations() throws Exception {

    }

    @Test
    public void testCheckSpelling() throws Exception {

    }
}