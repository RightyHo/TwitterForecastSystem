package twittercorpus;

import org.junit.Before;
import org.junit.Test;

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
        int expected = tClassifier.getClassificationStorageLimit();
        assertEquals(expected,1000);
    }

    @Test
    public void testGetFeatures() throws Exception {

    }

    @Test
    public void testGetCategories() throws Exception {

    }

    @Test
    public void testGetTotalNumCategories() throws Exception {

    }

    @Test
    public void testSetClassificationStorageLimit() throws Exception {

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