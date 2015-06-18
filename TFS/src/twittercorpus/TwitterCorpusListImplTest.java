package twittercorpus;

import org.junit.Before;
import org.junit.Test;

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
    }

    @Test
    public void testGetUsernameEquivalenceToken() throws Exception {
        tCorpus.extractTweetsFromFile(tFilename);
    }

    @Test
    public void testGetLinkEquivalenceToken() throws Exception {

    }

    @Test
    public void testGetCorpus() throws Exception {

    }

    @Test
    public void testSetCorpus() throws Exception {

    }

    @Test
    public void testGetFileName() throws Exception {

    }

    @Test
    public void testSetFileName() throws Exception {

    }

    @Test
    public void testGetFirstTweet() throws Exception {

    }

    @Test
    public void testSetFirstTweet() throws Exception {

    }

    @Test
    public void testExtractTweetsFromFile() throws Exception {

    }

    @Test
    public void testGetMonthNum() throws Exception {

    }

    @Test
    public void testLabelCorpus() throws Exception {

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