package twittercorpus;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Andrew on 28/06/15.
 */
public class SpellingDictionaryTest {

    private DictionaryTranslator testDT = null;

    @Before
    public void setUp() throws Exception {
        testDT = new SpellingDictionary();
    }

    @Test
    public void testProcessString() throws Exception {

        // processes strings as expected
        String actual = testDT.processString("you bettr recognize aav or iss it recognise?");
        assertEquals("you or it recognise?",actual);

        String actualSecond = testDT.processString("it's tough bng a ttalg legggend take tz from moi");
        assertEquals("it's tough a take from moi",actualSecond);

        // process leaves numbers unchanged
        String actualThird = testDT.processString("345 455 55 73");
        assertEquals("345 455 55 73",actualThird);

        // process leaves #hashtags unchanged
        String actualFourth = testDT.processString("i want a BMW #greatcar");
        assertEquals("i want a BMW #greatcar",actualFourth);

    }
}