package twittercorpus;

import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by Andrew on 08/07/15.
 */
public class NaiveBayesClassifierTest {

    private Classifier tClassifier;

    @Before
    public void setUp() throws Exception {
        tClassifier = new NaiveBayesClassifier(1000);
    }

    @Test
    public void testGetClassificationStorageLimit() throws Exception {
        int actual = tClassifier.getClassificationStorageLimit();
        assertEquals(1000,actual);
    }

    @Test
    public void testGetFeatures() throws Exception {
        Set<String> actualFeatures = tClassifier.getFeatures();
    }

    @Test
    public void testGetCategories() throws Exception {
        Set<Sentiment> actualCategories = tClassifier.getCategories();
    }

    @Test
    public void testGetTotalNumCategories() throws Exception {
        int actualNumCategories = tClassifier.getTotalNumCategories();
    }

    @Test
    public void testSetClassificationStorageLimit() throws Exception {
        tClassifier.setClassificationStorageLimit(500);
        assertEquals(500,tClassifier.getClassificationStorageLimit());
    }

    @Test
    public void testIncrementFeature() throws Exception {

    }

    @Test
    public void testDecrementFeature() throws Exception {

    }

    @Test
    public void testIncrementCategory() throws Exception {

    }

    @Test
    public void testDecrementCategory() throws Exception {

    }

    @Test
    public void testFCountInCategory() throws Exception {

    }

    @Test
    public void testGetCategoryCount() throws Exception {

    }

    @Test
    public void testCalcFeatureProbability() throws Exception {

    }

    @Test
    public void testCalcFeatureWeightedAverage() throws Exception {

    }

    @Test
    public void testLearn() throws Exception {

    }

    @Test
    public void testClassify() throws Exception {

    }
}