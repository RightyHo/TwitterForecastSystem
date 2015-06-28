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
        String actual = testDT.processString("you bettr recognize aav or iss it recognise?");
        assertEquals("you or it recognise?",actual);
    }
}