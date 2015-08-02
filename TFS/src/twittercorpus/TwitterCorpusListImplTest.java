package twittercorpus;

import org.junit.Before;
import org.junit.Test;

import java.time.*;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by Andrew on 18/06/15.
 */
public class TwitterCorpusListImplTest {

    private TwitterCorpus tCorpus;
    private String tFilename;
    private String plFilename;
    private String abbDicFile;
    private String spellDicFile;
    private String stopWordFile;
    private int millennium;                          // needs to be changed to 2000 if the date format of input data is 23/11/15
    private ZoneOffset timeZone;

    @Before
    public void setUp() throws Exception {
        tFilename = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/Test Twitter Corpus Sample.txt";
        abbDicFile = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/Twerminology - 100 Twitter Slang Words & Abbreviations.txt";
        spellDicFile = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/dictionary.txt";
        stopWordFile = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/English Stop Words.txt";
        timeZone = ZoneOffset.of("Z");              // set time zone for date information used in the TFS to GMT/UTC
        millennium = 0;                             // needs to be changed to 2000 if the date format of input data is 23/11/15
        int thisYear = 2015;

        // initialise set of local market holidays at the exchange on which the stock is traded
        LocalDate[] setValues = new LocalDate[]{
                LocalDate.of(thisYear,1,1),
                LocalDate.of(thisYear,4,3),
                LocalDate.of(thisYear,4,6),
                LocalDate.of(thisYear,5,1),
                LocalDate.of(thisYear,5,25),
                LocalDate.of(thisYear,12,24),
                LocalDate.of(thisYear,12,25),
                LocalDate.of(thisYear,12,31)
        };
        Set<LocalDate> marketHoliday = new HashSet<>(Arrays.asList(setValues));

        // set the market open and close times in GMT
        LocalTime bmwOpenTime = LocalTime.of(8,0,0);
        LocalTime bmwClosingTime = LocalTime.of(16,35,0);
        ZonedDateTime earliestCorpusTimeStamp = ZonedDateTime.of(thisYear, 1, 1, 0, 0, 0, 0,timeZone);
        ZonedDateTime latestCorpusTimeStamp = ZonedDateTime.of(thisYear, 3, 24, 0, 0, 0, 0, timeZone);
        tCorpus = new TwitterCorpusListImpl(tFilename,timeZone,marketHoliday,earliestCorpusTimeStamp,latestCorpusTimeStamp,bmwOpenTime,bmwClosingTime);
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
        assertEquals("/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/Test Twitter Corpus Sample.txt", tCorpus.getFileName());
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
        ZonedDateTime expectedTS = ZonedDateTime.of(localExpectedTS,timeZone);
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
        PriceLabelCorpus plCorpus = new PriceLabelCorpusImpl(plFilename,timeZone,millennium);
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
    public void testRemoveLinks() throws Exception {

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
        tCorpus.removeLinks();

        // test that the URL links have been removed in the tweets that previously contained links:
        assertFalse(urlTweetA.getTweetText().contains("http://"));
        assertFalse(urlTweetB.getTweetText().contains("http://"));
        assertFalse(urlTweetC.getTweetText().contains("http://"));

        // test that tweet which originally did not contain URL links prior to running the replaceLinks method is unchanged as a control:
        assertFalse(unlinkedTweet.getTweetText().contains("http://"));
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
    public void testRemoveUsernames() throws Exception {

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
        tCorpus.removeUsernames();

        // test that the usernames have been removed in the tweets that previously contained usernames:
        assertFalse(usernameTweetA.getTweetText().contains("@"));
        assertFalse(usernameTweetB.getTweetText().contains("@"));
        assertFalse(usernameTweetC.getTweetText().contains("@"));

        // test that tweet which originally did not contain a username prior to running the removeUsernames method is unchanged as a control:
        assertFalse(noNameTweet.getTweetText().contains("USERNAME"));
        assertFalse(noNameTweet.getTweetText().contains("@"));
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

        // test that the usernames have been replaced by the equivalence token in the tweets that previously contained usernames:
        assertTrue(usernameTweetA.getTweetText().contains("USERNAME"));
        assertFalse(usernameTweetA.getTweetText().contains("@"));
        assertTrue(usernameTweetB.getTweetText().contains("USERNAME"));
        assertFalse(usernameTweetB.getTweetText().contains("@"));
        assertTrue(usernameTweetC.getTweetText().contains("USERNAME"));
        assertFalse(usernameTweetC.getTweetText().contains("@"));

        // test that tweet which originally did not contain a username prior to running the replaceUsernames method is unchanged as a control:
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

        DictionaryTranslator testAbbrev = new AbbreviationDictionary(abbDicFile);
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

        // check the tweet of the text is as expected before we run the checkSpelling method on the corpus
        // Tweet #351 Sun Jan 18 13:09:00 2015	check out tools (2) waterpump hub fan holder repair parts for bmw  http://t.co/2jnm2xddhe via @ebay #bmw #tools
        assertEquals("check out tools (2) waterpump hub fan holder repair parts for bmw  http://t.co/2jnm2xddhe via @ebay #bmw #tools",tCorpus.getCorpus().get(350).getTweetText());

        // check the tweet of the text is as expected before we run the checkSpelling method on the corpus
        // Tweet #588 Tue Jan 20 01:50:00 2015	lol ceo yako bmw ko facebook be posting status like: grade 8 here i come :d
        assertEquals("lol ceo yako bmw ko facebook be posting status like: grade 8 here i come :d",tCorpus.getCorpus().get(587).getTweetText());

        // check the tweet of the text is as expected before we run the checkSpelling method on the corpus
        // Tweet #178 Sat Jan 17 04:48:00 2015	i wish i could ride around that westheimer with lester aka  @settle4les  in his lac or bimmer just jamming
        assertEquals("i wish i could ride around that westheimer with lester aka  @settle4les  in his lac or bimmer just jamming",tCorpus.getCorpus().get(177).getTweetText());

        // check the tweet of the text is as expected before we run the checkSpelling method on the corpus
        // Tweet #37 Fri Jan 16 07:37:00 2015	#safmradio. nzimande bought a r1.5m bmw when he became min of higher ed. not after years at sacp.  what is his govt /sacp time audit?
        assertEquals("#safmradio. nzimande bought a r1.5m bmw when he became min of higher ed. not after years at sacp.  what is his govt /sacp time audit?",tCorpus.getCorpus().get(36).getTweetText());


        // check the tweet of the text is as expected before we run the checkSpelling method on the corpus
        // Tweet #223 Sat Jan 17 12:07:00 2015	i will legit get more excited seeing a 25year old plus bmw or vw rather than a lambo or ferrari
        assertEquals("i will legit get more excited seeing a 25year old plus bmw or vw rather than a lambo or ferrari",tCorpus.getCorpus().get(222).getTweetText());

        tCorpus.replaceLinks();
        tCorpus.replaceUsernames();
        DictionaryTranslator tAbbreviations = new AbbreviationDictionary(abbDicFile);
        tCorpus.translateAbbreviations(tAbbreviations);
        DictionaryTranslator testSpell = new SpellingDictionary(spellDicFile);
        tCorpus.checkSpelling(testSpell);

        // check the tweet of the text is as expected after we run the translateAbbreviations method on the corpus
        // Tweet #351 Sun Jan 18 13:09:00 2015	check out tools (2) waterpump hub fan holder repair parts for bmw  http://t.co/2jnm2xddhe via @ebay #bmw #tools
        assertEquals("check out tools (2) hub fan holder repair parts for bmw LINK via USERNAME #bmw #tools",tCorpus.getCorpus().get(350).getTweetText());

        // check the tweet of the text is as expected after we run the translateAbbreviations method on the corpus
        // Tweet #588 Tue Jan 20 01:50:00 2015	lol ceo yako bmw ko facebook be posting status like: grade 8 here i come :d
        assertEquals("laugh out loud ceo bmw ko be posting status like: grade 8 here i come :d",tCorpus.getCorpus().get(587).getTweetText());

        // check the tweet of the text is as expected after we run the translateAbbreviations method on the corpus
        // Tweet #178 Sat Jan 17 04:48:00 2015	i wish i could ride around that westheimer with lester aka  @settle4les  in his lac or bimmer just jamming
        assertEquals("i wish i could ride around that with lester also known as USERNAME in his lac or bimmer just jamming",tCorpus.getCorpus().get(177).getTweetText());

        // *** WHY DO THE FOLLOWING TESTS FAIL???  THE PROGRAM SEEMS TO BE SHORTENING A WORD IN EACH OF THE FOLLOWING TEXTS ***
        // *** IN THE FIRST ONE THE WORD 'audit' is returned as just 't', IN THE SECOND ONE THE WORD 'get' is returned as just 't' ***

        // check the tweet of the text is as expected after we run the translateAbbreviations method on the corpus
        // Tweet #37 Fri Jan 16 07:37:00 2015	#safmradio. nzimande bought a r1.5m bmw when he became min of higher ed. not after years at sacp.  what is his govt /sacp time audit?
//        assertEquals("#safmradio. bought a r1.5m bmw when he became min of higher ed. not after years at . what is his govt / time audit?",tCorpus.getCorpus().get(36).getTweetText());

        // check the tweet of the text is as expected after we run the translateAbbreviations method on the corpus
        // Tweet #223 Sat Jan 17 12:07:00 2015	i will legit get more excited seeing a 25year old plus bmw or vw rather than a lambo or ferrari
//        assertEquals("i will legit get more excited seeing a 25year old plus bmw or rather than a or",tCorpus.getCorpus().get(222).getTweetText());
    }

    @Test
    public void testFilterOutStopWords() throws Exception {

        // *** SHOULD I REMOVE THE WORD 'NOT' FROM THE STOP WORDS LIST SINCE IT COULD HAVE BIG LOGICAL SIGNIFICANCE FOR THE SENTIMENT OF THE TEXT? ***

        // check the tweet of the text is as expected before we run the filterOutStopWords method on the corpus
        // Tweet #351 Sun Jan 18 13:09:00 2015	check out tools (2) waterpump hub fan holder repair parts for bmw  http://t.co/2jnm2xddhe via @ebay #bmw #tools
        assertEquals("check out tools (2) waterpump hub fan holder repair parts for bmw  http://t.co/2jnm2xddhe via @ebay #bmw #tools",tCorpus.getCorpus().get(350).getTweetText());

        // check the tweet of the text is as expected before we run the filterOutStopWords method on the corpus
        // Tweet #37 Fri Jan 16 07:37:00 2015	#safmradio. nzimande bought a r1.5m bmw when he became min of higher ed. not after years at sacp.  what is his govt /sacp time audit?
        assertEquals("#safmradio. nzimande bought a r1.5m bmw when he became min of higher ed. not after years at sacp.  what is his govt /sacp time audit?",tCorpus.getCorpus().get(36).getTweetText());

        tCorpus.filterOutStopWords(stopWordFile);

        // check the tweet of the text is as expected after we run the filterOutStopWords method on the corpus
        // Tweet #351 Sun Jan 18 13:09:00 2015	check out tools (2) waterpump hub fan holder repair parts for bmw  http://t.co/2jnm2xddhe via @ebay #bmw #tools
        assertEquals("check out tools (2) waterpump hub fan holder repair parts bmw http://t.co/2jnm2xddhe via @ebay #bmw #tools",tCorpus.getCorpus().get(350).getTweetText());

        // check the tweet of the text is as expected after we run the filterOutStopWords method on the corpus
        // Tweet #37 Fri Jan 16 07:37:00 2015	#safmradio. nzimande bought a r1.5m bmw when he became min of higher ed. not after years at sacp.  what is his govt /sacp time audit?
        assertEquals("#safmradio. nzimande bought r1.5m bmw became min higher ed. years sacp. govt /sacp time audit?",tCorpus.getCorpus().get(36).getTweetText());
    }

    @Test
    public void testExtractFeatures() throws Exception {

        // check the tweet of the text is as expected before we run the extractFeatures method on the corpus
        // Tweet #174 Sat Jan 17 04:10:00 2015	i'm online in my bmw with bmw connected.
        assertEquals("i'm online in my bmw with bmw connected.",tCorpus.getCorpus().get(173).getTweetText());

        // check the tweet of the text is as expected before we run the extractFeatures method on the corpus
        // Tweet #291 Sat Jan 17 23:54:00 2015	the mini cooper on the detroit car show on snapchat ??
        assertEquals("the mini cooper on the detroit car show on snapchat ??",tCorpus.getCorpus().get(290).getTweetText());

        // test using bigrams as features
        tCorpus.extractFeatures(2);

        // check the features list held by the tweet matches expectations after we run the extractFeatures(2) method on the corpus
        // Tweet #174 Sat Jan 17 04:10:00 2015	i'm online in my bmw with bmw connected.
        List<String> actualFeaturesOneBi = tCorpus.getCorpus().get(173).getFeatures();
        List<String> expectedFeaturesOneBi = new ArrayList<>();
        expectedFeaturesOneBi.add("i'm,online");
        expectedFeaturesOneBi.add("online,in");
        expectedFeaturesOneBi.add("in,my");
        expectedFeaturesOneBi.add("my,bmw");
        expectedFeaturesOneBi.add("bmw,with");
        expectedFeaturesOneBi.add("with,bmw");
        expectedFeaturesOneBi.add("bmw,connected.");
        assertEquals(expectedFeaturesOneBi,actualFeaturesOneBi);

        // check the features list held by the tweet matches expectations after we run the extractFeatures(2) method on the corpus
        // Tweet #291 Sat Jan 17 23:54:00 2015	the mini cooper on the detroit car show on snapchat ??
        List<String> actualFeaturesTwoBi = tCorpus.getCorpus().get(290).getFeatures();
        List<String> expectedFeaturesTwoBi = new ArrayList<>();
        expectedFeaturesTwoBi.add("the,mini");
        expectedFeaturesTwoBi.add("mini,cooper");
        expectedFeaturesTwoBi.add("cooper,on");
        expectedFeaturesTwoBi.add("on,the");
        expectedFeaturesTwoBi.add("the,detroit");
        expectedFeaturesTwoBi.add("detroit,car");
        expectedFeaturesTwoBi.add("car,show");
        expectedFeaturesTwoBi.add("show,on");
        expectedFeaturesTwoBi.add("on,snapchat");
        expectedFeaturesTwoBi.add("snapchat,??");
        assertEquals(expectedFeaturesTwoBi,actualFeaturesTwoBi);

        // test using unigrams as features
        tCorpus.extractFeatures(1);

        // check the features list held by the tweet matches expectations after we run the extractFeatures(1) method on the corpus
        // Tweet #174 Sat Jan 17 04:10:00 2015	i'm online in my bmw with bmw connected.
        List<String> actualFeaturesOneUni = tCorpus.getCorpus().get(173).getFeatures();
        List<String> expectedFeaturesOneUni = new ArrayList<>();
        expectedFeaturesOneUni.add("i'm");
        expectedFeaturesOneUni.add("online");
        expectedFeaturesOneUni.add("in");
        expectedFeaturesOneUni.add("my");
        expectedFeaturesOneUni.add("bmw");
        expectedFeaturesOneUni.add("with");
        expectedFeaturesOneUni.add("bmw");
        expectedFeaturesOneUni.add("connected.");
        assertEquals(expectedFeaturesOneUni, actualFeaturesOneUni);

        // check the features list held by the tweet matches expectations after we run the extractFeatures(2) method on the corpus
        // Tweet #291 Sat Jan 17 23:54:00 2015	the mini cooper on the detroit car show on snapchat ??
        List<String> actualFeaturesTwoUni = tCorpus.getCorpus().get(290).getFeatures();
        List<String> expectedFeaturesTwoUni = new ArrayList<>();
        expectedFeaturesTwoUni.add("the");
        expectedFeaturesTwoUni.add("mini");
        expectedFeaturesTwoUni.add("cooper");
        expectedFeaturesTwoUni.add("on");
        expectedFeaturesTwoUni.add("the");
        expectedFeaturesTwoUni.add("detroit");
        expectedFeaturesTwoUni.add("car");
        expectedFeaturesTwoUni.add("show");
        expectedFeaturesTwoUni.add("on");
        expectedFeaturesTwoUni.add("snapchat");
        expectedFeaturesTwoUni.add("??");
        assertEquals(expectedFeaturesTwoUni, actualFeaturesTwoUni);
    }
}