package twittercorpus;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Andrew on 30/06/15.
 */
public class TweetImplTest {

    private Tweet testTw;
    private String stopWordsFilename;

    @Before
    public void setUp() throws Exception {
        // Tweet example Fri Jan 16 07:26:00 2015	my family doesn't own a car that's not a beemer.
        LocalDateTime lDateT = LocalDateTime.of(2015,1,16,7,26,0);
        ZonedDateTime tTime = ZonedDateTime.of(lDateT, ZoneId.of("Europe/London"));
        testTw = new TweetImpl(tTime,"my family doesn't own a car that's not a beemer.");
        stopWordsFilename = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/TwitterForecastSystem/TFS/English Stop Words.txt";
    }

    @Test
    public void testRemoveStopWords() throws Exception {
        testTw.removeStopWords(stopWordsFilename);
        assertEquals("family doesn't car that's beemer.",testTw.getTweetText());
    }
        @Test
    public void testExtractNGramFeatures() throws Exception {

        // check whether unigram features are extracted as expected
        testTw.extractNGramFeatures(1,testTw.getTweetText());
        assertEquals("my",testTw.getFeatures().get(0));
        assertEquals("family",testTw.getFeatures().get(1));
        assertEquals("beemer.",testTw.getFeatures().get(9));
        // expect the following call to throw an IndexOutOfBoundsException...capture in a test clause
//        testTw.getFeatures().get(10);

        // check whether bigram features are extracted as expected
        testTw.extractNGramFeatures(2,testTw.getTweetText());
        assertEquals("my,family",testTw.getFeatures().get(0));
        assertEquals("family,doesn't",testTw.getFeatures().get(1));
        assertEquals("a,beemer.",testTw.getFeatures().get(8));
        // expect the following call to throw an IndexOutOfBoundsException...capture in a test clause
//        testTw.getFeatures().get(9);
    }
}