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
//        String actualFourth = testDT.processString("i want a BMW #greatcar");
//        assertEquals("i want a BMW #greatcar",actualFourth);

        // process leaves #hashtags unchanged with multiple #hashtags in the text while still removing misspelt words
//        String actualFifth = testDT.processString("i want a BMW #greatcar it's a nehgt of a car #birthdayprezzyforme");
//        assertEquals("i want a BMW #greatcar its a of a car #birthdayprezzyforme",actualFifth);

        // additional test strings
        assertEquals("check out tools hub fan holder repair parts for bmw via bmw tools",testDT.processString("check out tools (2) waterpump hub fan holder repair parts for bmw  http://t.co/2jnm2xddhe via @ebay #bmw #tools"));

        assertEquals("ceo bmw ko be posting status like grade here i come d",testDT.processString("lol ceo yako bmw ko facebook be posting status like: grade 8 here i come :d"));

        assertEquals("i wish i could ride around that with lester aka in his lac or bimmer just jamming",testDT.processString("i wish i could ride around that westheimer with lester aka  @settle4les  in his lac or bimmer just jamming"));

        assertEquals("bought a rm bmw when he became min of higher ed not after years at what is his govt time audit",testDT.processString("#safmradio. nzimande bought a r1.5m bmw when he became min of higher ed. not after years at sacp.  what is his govt /sacp time audit?"));

        assertEquals("i will legit get more excited seeing a year old plus bmw or rather than a or",testDT.processString("i will legit get more excited seeing a 25year old plus bmw or vw rather than a lambo or ferrari"));
    }
}