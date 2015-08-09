package twittercorpus;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.Assert.*;

/**
 * Created by Andrew on 09/08/15.
 */
public class SentiWordNetImplTest {

    @Before
    public void setUp() throws Exception {
        String sentiWordNetFilename = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/SentiWordNet_3.0.txt";
        SentiWordNet swnTest = new SentiWordNetImpl(sentiWordNetFilename);

        // Tweet example Fri Jan 16 07:26:00 2015	my family doesn't own a car that's not a beemer.
        LocalDateTime lDateT = LocalDateTime.of(2015,1,16,7,26,0);
        ZonedDateTime tTime = ZonedDateTime.of(lDateT, ZoneId.of("Europe/London"));
        Tweet testTw = new TweetImpl(tTime,"my family doesn't own a car that's not a beemer.");

        swnTest.classifySentiment(testTw.getFeatures());
    }

    @Test
    public void testBuildDictionary() throws Exception {

    }

    @Test
    public void testClassifySentiment() throws Exception {

    }

    @Test
    public void testGetFeatureSentimentScore() throws Exception {

    }
}