package twittercorpus;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.Assert.*;

/**
 * Created by Andrew on 18/06/15.
 */
public class TwitterCorpusListImplTest {

    private TwitterCorpus tCorpus;
    private String tFilename;

    @Before
    public void setUp() throws Exception {
        tFilename = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/feb-15-bmw.txt";
        tCorpus = new TwitterCorpusListImpl(tFilename);
        tCorpus.extractTweetsFromFile(tFilename);
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
        // not necessary?
    }

    @Test
    public void testSetCorpus() throws Exception {
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

    /**
     * first line should match with "Sat Jan 31 23:02:00 2015	#dautoperfection detailed ready for sale 15 bmw 640i $87k http://t.co/wwba75rzpn"
     * second line should match with "Sat Jan 31 23:08:00 2015	rt @mickgeo3: black bmw  convertible hfz 7935 stolen in creeper burglary, glengoland w. belfast in last hour. pls rt and keep an eye out fo?"
     * @throws Exception
     */
    @Test
    public void testExtractTweetsFromFile() throws Exception {

        // test first tweet in list
        assertEquals(tCorpus.getCorpus().get(0).getTweetText(),"#dautoperfection detailed ready for sale 15 bmw 640i $87k http://t.co/wwba75rzpn");

        // test second tweet in list
        LocalDateTime localExpectedTS = LocalDateTime.of(2015,1,31,23,8,0);
        ZonedDateTime expectedTS = ZonedDateTime.of(localExpectedTS, ZoneId.of("Europe/London"));
        assertEquals(tCorpus.getCorpus().get(1).getTimeStamp(),expectedTS);
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
        PriceLabelCorpus
        tCorpus.labelCorpus();
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