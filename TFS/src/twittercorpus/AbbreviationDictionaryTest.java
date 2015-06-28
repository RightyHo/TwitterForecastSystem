package twittercorpus;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Andrew on 28/06/15.
 */
public class AbbreviationDictionaryTest {

    private DictionaryTranslator testAD = null;

    @Before
    public void setUp() throws Exception {
        testAD = new AbbreviationDictionary();
    }

    @Test
    public void testProcessString() throws Exception {
        String actual = testAD.processString("this string should change because FOMO is a common abbreviation!");
        assertEquals("this string should change because fear of missing out is a common abbreviation!",actual);

        String actualTwo = testAD.processString("this string should change because prt is a common abbreviation!");
        assertEquals("this string should change because please retweet is a common abbreviation!",actualTwo);

        String actualThree = testAD.processString("this string should change because Twitterfly is a common abbreviation!");
        assertEquals("this string should change because A social butterfly on Twitter is a common abbreviation!",actualThree);

        String actualFour = testAD.processString("this string should NOT change because JRTY is an abbreviation that I just made up!");
        assertEquals("this string should NOT change because JRTY is an abbreviation that I just made up!",actualFour);
    }
}