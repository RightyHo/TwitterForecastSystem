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
        // ADD CODE
    }

    @Test
    public void testGetCategories() throws Exception {
        Set<Sentiment> actualCategories = tClassifier.getCategories();
        // ADD CODE
    }

    @Test
    public void testGetTotalNumCategories() throws Exception {
        int actualNumCategories = tClassifier.getTotalNumCategories();
        // ADD CODE
    }

    @Test
    public void testSetClassificationStorageLimit() throws Exception {
        tClassifier.setClassificationStorageLimit(500);
        assertEquals(500,tClassifier.getClassificationStorageLimit());
    }

    // confirm that method increases the count of the given feature in the given category by one
    @Test
    public void testIncrementFeature() throws Exception {
        String tFeature = "ADD A VALID FEATURE STRING HERE";                // ADD CODE
        Sentiment tCategory = Sentiment.NEGATIVE;                           // ADJUST AS NECESSARY
        int actualBefore = tClassifier.fCountInCategory(tFeature,tCategory);

        // run incrementFeature method
        tClassifier.incrementFeature(tFeature,tCategory);

        int actualAfter = tClassifier.fCountInCategory(tFeature,tCategory);
        assert(actualAfter - 1 == actualBefore);
    }

    // confirm that method decreases the count of the given feature in the given category by one
    @Test
    public void testDecrementFeature() throws Exception {
        String tFeature = "ADD A VALID FEATURE STRING HERE";                // ADD CODE
        Sentiment tCategory = Sentiment.NEGATIVE;                           // ADJUST AS NECESSARY
        int actualBefore = tClassifier.fCountInCategory(tFeature,tCategory);

        // run incrementFeature method
        tClassifier.decrementFeature(tFeature,tCategory);

        int actualAfter = tClassifier.fCountInCategory(tFeature,tCategory);
        assert(actualAfter + 1 == actualBefore);
    }

    // confirm that method increases the count of the given category by one
    @Test
    public void testIncrementCategory() throws Exception {
        Sentiment tCategory = Sentiment.NEGATIVE;                           // ADJUST AS NECESSARY
        int actualBefore = tClassifier.getCategoryCount(tCategory);

        // run incrementCategory method
        tClassifier.incrementCategory(tCategory);

        int actualAfter = tClassifier.getCategoryCount(tCategory);
        assert(actualAfter - 1 == actualBefore);
    }

    // confirm that method decreases the count of the given category by one
    @Test
    public void testDecrementCategory() throws Exception {
        Sentiment tCategory = Sentiment.NEGATIVE;                           // ADJUST AS NECESSARY
        int actualBefore = tClassifier.getCategoryCount(tCategory);

        // run incrementCategory method
        tClassifier.decrementCategory(tCategory);

        int actualAfter = tClassifier.getCategoryCount(tCategory);
        assert(actualAfter + 1 == actualBefore);
    }

    // confirm that method returns the number of times the given feature appears in the given category
    @Test
    public void testFCountInCategory() throws Exception {
        String tFeature = "ADD A VALID FEATURE STRING HERE";                // ADD CODE
        Sentiment tCategory = Sentiment.NEGATIVE;                           // ADJUST AS NECESSARY
        int expectedFeatureCount = 20;                                      // ADJUST AS NECESSARY
        assertEquals(expectedFeatureCount,tClassifier.fCountInCategory(tFeature,tCategory));
    }

    // confirm that method returns the total number of features in the given category
    @Test
    public void testGetCategoryCount() throws Exception {
        Sentiment tCategory = Sentiment.NEGATIVE;                           // ADJUST AS NECESSARY
        int expectedCategoryCount = 200;                                    // ADJUST AS NECESSARY
        assertEquals(expectedCategoryCount,tClassifier.getCategoryCount(tCategory));
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