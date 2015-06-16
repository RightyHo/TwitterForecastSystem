package twittercorpus;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Andrew on 16/06/15.
 */
public class TwitterCorpusMapImplTest {

    private TwitterCorpus tCorpus;
    private String tFilename;

    @Before
    public void setUp() throws Exception {
        tFilename = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/feb-15-bmw.txt";
        tCorpus = new TwitterCorpusMapImpl(tFilename);

    }

    @Test
    public void testExtractTweetsFromFile() throws Exception {
        tCorpus.extractTweetsFromFile(tFilename);
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