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
        String spellDicFilename = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/dictionary.txt";
        testDT = new SpellingDictionary(spellDicFilename);
    }

    @Test
    public void testProcessString() throws Exception {

        // processes strings as expected
        String actual = testDT.processString("you bettr recognize aav or iss it recognise?");
        assertEquals("you or it recognise",actual);

        String actualSecond = testDT.processString("it's tough bng a ttalg legggend take tz from moi");
        assertEquals("its tough a take from moi",actualSecond);

        // process removes numbers
        String actualThird = testDT.processString("345 455 55 73");
        assertEquals("",actualThird);

        // process leaves #hashtags unchanged
        String actualFourth = testDT.processString("i want a BMW #greatcar");
        assertEquals("i want a BMW #greatcar",actualFourth);

        // process leaves #hashtags unchanged with multiple #hashtags in the text while still removing misspelt words
        String actualFifth = testDT.processString("i want a BMW #greatcar it's a nehgt of a car #birthdayprezzyforme");
        assertEquals("i want a BMW #greatcar its a of a car #birthdayprezzyforme",actualFifth);
    }
}