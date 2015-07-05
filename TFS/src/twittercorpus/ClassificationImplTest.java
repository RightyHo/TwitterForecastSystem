package twittercorpus;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Andrew on 05/07/15.
 */
public class ClassificationImplTest {

    private Classification tClass;

    @Before
    public void setUp() throws Exception {
        List<String> featureList = new ArrayList<>();
        featureList.add("This,project");
        featureList.add("project,is");
        featureList.add("is,very");
        featureList.add("very,good");
        Sentiment sentiment = Sentiment.POSITIVE;
        double certainty = 1.0;
        tClass = new ClassificationImpl(featureList,sentiment,certainty);
    }

    @Test
    public void testGetListOfFeatures() throws Exception {
        String expectedA = "This,project";
        assertEquals(expectedA,tClass.getListOfFeatures().get(0));
        String expectedB= "very,good";
        assertEquals(expectedB,tClass.getListOfFeatures().get(3));
    }

    @Test
    public void testGetSentimentCategory() throws Exception {
        assertEquals(Sentiment.POSITIVE,tClass.getSentimentCategory());
    }

    @Test
    public void testGetClassificationCertainty() throws Exception {
        assertTrue(Math.abs(1.0 - tClass.getClassificationCertainty()) < 0.00000001);
    }

    @Test
    public void testToString() throws Exception {
        String expected = "The Feature List --> [(this,project),(project,is),(is,very),(very,good)] is classified as falling in the positive category with a probability of 100%";
        assertEquals(expected,tClass.toString());
    }
}