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
        String actual = testAD.processString("this one should apply because FOMO is a common abbreviation!");
        assertEquals("this one should apply because fear of missing out is a common abbreviation!",actual);

        String actualTwo = testAD.processString("this one should apply because FOMO is a common abbreviation!");
        assertEquals("this one should apply because fear of missing out is a common abbreviation!",actualTwo);
    }
}