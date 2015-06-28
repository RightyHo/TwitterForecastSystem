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

        // SCENARIO 1: TWEET IS PUBLISHED OUTSIDE OF MARKET HOURS - test the first tweet in the corpus is labelled correctly:
        // Fri Jan 16 00:04:00 2015	#usedcars used 2003 bmw z4 2.5i in gainesville, fl 32601 for sale at autoflex llc http://t.co/fxbqiiu6rr http://t.co/rok2iyoh0c
        // Initial snapshot should be of market close on Thur Jan 15 16:35:
        // 15/01/2015 16:35	91.07	91.07	91.07	91.07	0.0563	0.0481	0.0081
        // and post Tweet snapshot should be of market open on Fri Jan 16 8:00:
        // 16/01/2015 08:00	90.7	90.7	90.6	90.61	0.0213	0.0428	-0.0215
        assertTrue(Math.abs(tCorpus.getCorpus().get(0).getInitialSnapshot().getClosingSharePrice() - 91.07) < 0.0000000000000001);
        assertTrue(Math.abs(tCorpus.getCorpus().get(0).getPostTweetSnapshot().getClosingSharePrice() - 90.61) < 0.0000000000000001);

        // SCENARIO 2: TWEET IS PUBLISHED DURING MARKET HOURS - test the 50th tweet in the corpus is labelled correctly:
        // Fri Jan 16 09:58:00 2015	i posted 11 photos on facebook in the album "2015 bmw i8 coupe" http://t.co/4mtrbecs7c
        // so initial snapshot should be Fri Jan 16 09:57
        // 16/01/2015 09:57	91.2	91.25	91.2	91.23	0.0169	-0.0102	0.0271
        // and post Tweet snapshot should be Fri Jan 16 10:18
        // 16/01/2015 10:18	91.32	91.35	91.32	91.35	0.037	0.0366	0.0004
        assertTrue(Math.abs(tCorpus.getCorpus().get(49).getInitialSnapshot().getClosingSharePrice() - 91.23) < 0.0000000000000001);
        assertTrue(Math.abs(tCorpus.getCorpus().get(49).getPostTweetSnapshot().getClosingSharePrice() - 91.35) < 0.0000000000000001);

        // SCENARIO 3: TWEET IS PUBLISHED DURING WEEKEND - test a tweet published on a Saturday in the corpus is labelled correctly:
        // Sat Jan 17 15:35:00 2015	vintage bmw tool kit wrench by heyco tools, germany - 13mm x 12mm - 13 12 mm http://t.co/i8bgjncgn5 http://t.co/wmfzr7flqh
        // so initial snapshot should be Fri Jan 16 16:35
        // 16/01/2015 16:35	93.59	93.59	93.59	93.59	0.1025	0.1523	-0.0498
        // and post Tweet snapshot should be Mon Jan 19 08:00
        // 19/01/2015 08:00	94.05	94.25	94.03	94.05	0.1134	0.1445	-0.0311
        assertTrue(Math.abs(tCorpus.getCorpus().get(238).getInitialSnapshot().getClosingSharePrice() - 93.59) < 0.0000000000000001);
        assertTrue(Math.abs(tCorpus.getCorpus().get(238).getPostTweetSnapshot().getClosingSharePrice() - 94.05) < 0.0000000000000001);

        // SCENARIO 4: TWEET IS PUBLISHED DURING WEEKEND - test a tweet published on a Sunday in the corpus is labelled correctly:
        // Sun Jan 18 11:41:00 2015	android 7" car dvd player gps u4 full hd 1080p for bmw 5 series e39 e53 x5 530 http://t.co/59tkbj9ahm http://t.co/hq9ugqz1xm
        // so initial snapshot should be Fri Jan 16 16:35
        // 16/01/2015 16:35	93.59	93.59	93.59	93.59	0.1025	0.1523	-0.0498
        // and post Tweet snapshot should be Mon Jan 19 08:00
        // 19/01/2015 08:00	94.05	94.25	94.03	94.05	0.1134	0.1445	-0.0311
        assertTrue(Math.abs(tCorpus.getCorpus().get(343).getInitialSnapshot().getClosingSharePrice() - 93.59) < 0.0000000000000001);
        assertTrue(Math.abs(tCorpus.getCorpus().get(343).getPostTweetSnapshot().getClosingSharePrice() - 94.05) < 0.0000000000000001);

        // SCENARIO 5: TWEET IS PUBLISHED DURING 5 MINUTE END OF DAY AUCTION - test a tweet published during the end of day auction during which there is no price print between 16:29 and 16:35
        // Tue Jan 20 16:33:00 2015	new genuine bmw e36 (93-99) impact strip rear bumper trim lt oem + warranty http://t.co/yywgidoksg http://t.co/q8eutedegt
        // so initial snapshot should be Tue Jan 20 16:29
        // 20/01/2015 16:29	94.62	94.7	94.62	94.68	-0.0155	-0.0044	-0.0111
        // and post Tweet snapshot should be Wed Jan 21 08:00 because 20 minutes aftr the tweet is published the market will be closed
        // 21/01/2015 08:00	94.49	94.5	94.48	94.49	-0.027	-0.01	-0.0171
        assertTrue(Math.abs(tCorpus.getCorpus().get(663).getInitialSnapshot().getClosingSharePrice() - 94.68) < 0.0000000000000001);
        assertTrue(Math.abs(tCorpus.getCorpus().get(663).getPostTweetSnapshot().getClosingSharePrice() - 94.49) < 0.0000000000000001);

    }

    @Test
    public void testRemoveRetweets() throws Exception {

        // test that retweets are present in the corpus prior to running the removeRetweets method:

        // Fri Jan 16 01:58:00 2015	rt @bmw: packed with innovations ? the #bmwi8 and #bmwi3: gorilla-glas, ologic-technology and bmw laserlight. @bmwi http://t.co/yshqauivo9
        Tweet retweetA = tCorpus.getCorpus().get(4);

        // Fri Jan 16 02:31:00 2015	rt @johnspatricc: ? http://t.co/iivulyxuc5 674 designed to resist ak-47s: tony abbotts new bulletproof bmw #tonyabbott deputy prime mi? htt?
        Tweet retweetB = tCorpus.getCorpus().get(9);

        // Fri Jan 16 03:23:00 2015	rt @khalidkarim: 2014...audi, bmw &amp; merc sold 0.7 cars for every 1000 citizens of the world.malaysia lagged the global average with only 0.?
        Tweet retweetC = tCorpus.getCorpus().get(13);

        assertTrue(tCorpus.getCorpus().contains(retweetA));
        assertTrue(tCorpus.getCorpus().contains(retweetB));
        assertTrue(tCorpus.getCorpus().contains(retweetC));

        // test that some normal tweets are present in the corpus prior to running the removeRetweets method as a control:

        // Fri Jan 16 02:55:00 2015	led angel eye halo light bmw 3-series e90 e91 06-08 oem http://t.co/ewij4o6ecf http://t.co/efjsry3q66
        Tweet normalTweetA = tCorpus.getCorpus().get(11);

        // Fri Jan 16 03:46:00 2015	set of o2 oxygen sensor combo package 234-4672 234-4683 for bmw 323i 528i z3 x5 http://t.co/rxhzwpatvy http://t.co/0qbfagomeo
        Tweet normalTweetB = tCorpus.getCorpus().get(15);

        assertTrue(tCorpus.getCorpus().contains(normalTweetA));
        assertTrue(tCorpus.getCorpus().contains(normalTweetB));

        // remove retweets from the corpus
        int totalBeforeRemoval = tCorpus.getCorpus().size();
        tCorpus.removeRetweets();
        int totalAfterRemoval = tCorpus.getCorpus().size();

        // test that the corpus of tweets reduces in size when we remove retweets from the corpus
        assertTrue(totalAfterRemoval < totalBeforeRemoval);

        // test that retweets are no longer in the corpus:

        // Fri Jan 16 01:58:00 2015	rt @bmw: packed with innovations ? the #bmwi8 and #bmwi3: gorilla-glas, ologic-technology and bmw laserlight. @bmwi http://t.co/yshqauivo9
        assertFalse(tCorpus.getCorpus().contains(retweetA));
        // Fri Jan 16 02:31:00 2015	rt @johnspatricc: ? http://t.co/iivulyxuc5 674 designed to resist ak-47s: tony abbotts new bulletproof bmw #tonyabbott deputy prime mi? htt?
        assertFalse(tCorpus.getCorpus().contains(retweetB));
        // Fri Jan 16 03:23:00 2015	rt @khalidkarim: 2014...audi, bmw &amp; merc sold 0.7 cars for every 1000 citizens of the world.malaysia lagged the global average with only 0.?
        assertFalse(tCorpus.getCorpus().contains(retweetC));

        // test that the normal tweets are still present in the corpus after running the removeRetweets method as a control:

        // Fri Jan 16 02:55:00 2015	led angel eye halo light bmw 3-series e90 e91 06-08 oem http://t.co/ewij4o6ecf http://t.co/efjsry3q66
        assertTrue(tCorpus.getCorpus().contains(normalTweetA));
        // Fri Jan 16 03:46:00 2015	set of o2 oxygen sensor combo package 234-4672 234-4683 for bmw 323i 528i z3 x5 http://t.co/rxhzwpatvy http://t.co/0qbfagomeo
        assertTrue(tCorpus.getCorpus().contains(normalTweetB));
    }

    @Test
    public void testReplaceLinks() throws Exception {

        // test that tweets contain URL links prior to running the replaceLinks method:

        // Fri Jan 16 01:58:00 2015	rt @bmw: packed with innovations ? the #bmwi8 and #bmwi3: gorilla-glas, ologic-technology and bmw laserlight. @bmwi http://t.co/yshqauivo9
        Tweet urlTweetA = tCorpus.getCorpus().get(4);

        // Fri Jan 16 02:31:00 2015	rt @johnspatricc: ? http://t.co/iivulyxuc5 674 designed to resist ak-47s: tony abbotts new bulletproof bmw #tonyabbott deputy prime mi? htt?
        Tweet urlTweetB = tCorpus.getCorpus().get(9);

        // Fri Jan 16 02:55:00 2015	led angel eye halo light bmw 3-series e90 e91 06-08 oem http://t.co/ewij4o6ecf http://t.co/efjsry3q66
        Tweet urlTweetC = tCorpus.getCorpus().get(11);

        assertTrue(urlTweetA.getTweetText().contains("http://"));
        assertTrue(urlTweetB.getTweetText().contains("http://"));
        assertTrue(urlTweetC.getTweetText().contains("http://"));

        // test that tweet does not contain URL links prior to running the replaceLinks method as a control:

        // Fri Jan 16 03:23:00 2015	rt @khalidkarim: 2014...audi, bmw &amp; merc sold 0.7 cars for every 1000 citizens of the world.malaysia lagged the global average with only 0.?
        Tweet unlinkedTweet = tCorpus.getCorpus().get(13);

        assertFalse(unlinkedTweet.getTweetText().contains("http://"));

        // replace links found in the corpus with an equivalence token
        tCorpus.replaceLinks();

        // test that the URL links have been replaced by the equivalence token in the tweets that previously contained links:
        assertTrue(urlTweetA.getTweetText().contains("LINK"));
        assertFalse(urlTweetA.getTweetText().contains("http://"));
        assertTrue(urlTweetB.getTweetText().contains("LINK"));
        assertFalse(urlTweetB.getTweetText().contains("http://"));
        assertTrue(urlTweetC.getTweetText().contains("LINK"));
        assertFalse(urlTweetC.getTweetText().contains("http://"));

        // test that tweet which originally did not contain URL links prior to running the replaceLinks method is unchanged as a control:
        assertFalse(unlinkedTweet.getTweetText().contains("LINK"));
        assertFalse(unlinkedTweet.getTweetText().contains("http://"));
    }

    @Test
    public void testReplaceUsernames() throws Exception {

        // test that tweets contain "usernames" prior to running the replaceUsernames method:

        // Fri Jan 16 01:58:00 2015	rt @bmw: packed with innovations ? the #bmwi8 and #bmwi3: gorilla-glas, ologic-technology and bmw laserlight. @bmwi http://t.co/yshqauivo9
        Tweet usernameTweetA = tCorpus.getCorpus().get(4);

        // Fri Jan 16 02:31:00 2015	rt @johnspatricc: ? http://t.co/iivulyxuc5 674 designed to resist ak-47s: tony abbotts new bulletproof bmw #tonyabbott deputy prime mi? htt?
        Tweet usernameTweetB = tCorpus.getCorpus().get(9);

        // Fri Jan 16 03:23:00 2015	rt @khalidkarim: 2014...audi, bmw &amp; merc sold 0.7 cars for every 1000 citizens of the world.malaysia lagged the global average with only 0.?
        Tweet usernameTweetC = tCorpus.getCorpus().get(13);

        assertTrue(usernameTweetA.getTweetText().contains("@"));
        assertTrue(usernameTweetB.getTweetText().contains("@"));
        assertTrue(usernameTweetC.getTweetText().contains("@"));

        // test that tweet does not contain a "username" prior to running the replaceUsernames method as a control:

        // Fri Jan 16 02:55:00 2015	led angel eye halo light bmw 3-series e90 e91 06-08 oem http://t.co/ewij4o6ecf http://t.co/efjsry3q66
        Tweet noNameTweet = tCorpus.getCorpus().get(11);

        assertFalse(noNameTweet.getTweetText().contains("@"));

        // replace links found in the corpus with an equivalence token
        tCorpus.replaceUsernames();

        // test that the URL links have been replaced by the equivalence token in the tweets that previously contained links:
        assertTrue(usernameTweetA.getTweetText().contains("USERNAME"));
        assertFalse(usernameTweetA.getTweetText().contains("@"));
        assertTrue(usernameTweetB.getTweetText().contains("USERNAME"));
        assertFalse(usernameTweetB.getTweetText().contains("@"));
        assertTrue(usernameTweetC.getTweetText().contains("USERNAME"));
        assertFalse(usernameTweetC.getTweetText().contains("@"));

        // test that tweet which originally did not contain URL links prior to running the replaceLinks method is unchanged as a control:
        assertFalse(noNameTweet.getTweetText().contains("USERNAME"));
        assertFalse(noNameTweet.getTweetText().contains("@"));
    }

    @Test
    public void testTranslateAbbreviations() throws Exception {
        // check the tweet of the text is as expected before we run the translateAbbreviations method on the corpus
        // Tweet #592 Tue Jan 20 02:16:00 2015	@jd_bimmer vroom vroom to my casa with da food.
        assertEquals("@jd_bimmer vroom vroom to my casa with da food.",tCorpus.getCorpus().get(591).getTweetText());

        // check the tweet of the text is as expected before we run the translateAbbreviations method on the corpus
        // Tweet #436 Mon Jan 19 03:37:00 2015	rt @bimmerella: @willyginpdx @helpfulatheist3 @ydanasmithdutra @hostileurbanist love u guys! ?????
        assertEquals("rt @bimmerella: @willyginpdx @helpfulatheist3 @ydanasmithdutra @hostileurbanist love u guys! ?????",tCorpus.getCorpus().get(435).getTweetText());

        DictionaryTranslator testAbbrev = new AbbreviationDictionary();
        tCorpus.translateAbbreviations(testAbbrev);

        // check the tweet of the text is as expected after we run the translateAbbreviations method on the corpus
        // Tweet #592 Tue Jan 20 02:16:00 2015	@jd_bimmer vroom vroom to my casa with da food. --> 'da' should be converted to 'the'
        assertEquals("@jd_bimmer vroom vroom to my casa with the food.",tCorpus.getCorpus().get(591).getTweetText());

        // check the tweet of the text is as expected after we run the translateAbbreviations method on the corpus
        // Tweet #436 Mon Jan 19 03:37:00 2015	rt @bimmerella: @willyginpdx @helpfulatheist3 @ydanasmithdutra @hostileurbanist love u guys! ????? --> 'u' should be converted to 'you'
        assertEquals("rt @bimmerella: @willyginpdx @helpfulatheist3 @ydanasmithdutra @hostileurbanist love you guys! ?????",tCorpus.getCorpus().get(435).getTweetText());
    }

    @Test
    public void testCheckSpelling() throws Exception {
        DictionaryTranslator testSpell = new SpellingDictionary();
        tCorpus.checkSpelling(testSpell);

    }
}