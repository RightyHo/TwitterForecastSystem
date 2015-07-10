package twittercorpus;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
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

        // test classification A
        List<String> featureListA = new ArrayList<>();
        featureListA.add("This,project");
        featureListA.add("project,is");
        featureListA.add("is,very");
        featureListA.add("very,good");
        Sentiment sentimentA = Sentiment.POSITIVE;
        double certaintyA = 0.8;
        Classification tClassificationA = new ClassificationImpl(featureListA,sentimentA,certaintyA);

        // test classification B
        List<String> featureListB = new ArrayList<>();
        featureListB.add("The,report");
        featureListB.add("report,was");
        featureListB.add("was,a");
        featureListB.add("a,great");
        featureListB.add("great,read");
        Sentiment sentimentB = Sentiment.POSITIVE;
        double certaintyB = 0.9;
        Classification tClassificationB = new ClassificationImpl(featureListB,sentimentB,certaintyB);

        // test classification C
        List<String> featureListC = new ArrayList<>();
        featureListC.add("rainy,days");
        featureListC.add("days,can");
        featureListC.add("can,be");
        featureListC.add("be,boring");
        Sentiment sentimentC = Sentiment.NEGATIVE;
        double certaintyC = 0.6;
        Classification tClassificationC = new ClassificationImpl(featureListC,sentimentC,certaintyC);

        // test classification D
        List<String> featureListD = new ArrayList<>();
        featureListD.add("sentiment,analysis");
        featureListD.add("analysis,is");
        featureListD.add("is,an");
        featureListD.add("an,interesting");
        featureListD.add("interesting,subject");
        Sentiment sentimentD = Sentiment.POSITIVE;
        double certaintyD = 0.7;
        Classification tClassificationD = new ClassificationImpl(featureListD,sentimentD,certaintyD);

        // test classification E
        List<String> featureListE = new ArrayList<>();
        featureListE.add("I,will");
        featureListE.add("will,not");
        featureListE.add("not,miss");
        featureListE.add("miss,working");
        featureListE.add("working,on");
        featureListE.add("on,assignments");
        featureListE.add("assignments,after");
        featureListE.add("after,a");
        featureListE.add("a,long");
        featureListE.add("long,day");
        featureListE.add("day,at");
        featureListE.add("at,the");
        featureListE.add("the,office");
        Sentiment sentimentE = Sentiment.NEGATIVE;
        double certaintyE = 0.5;
        Classification tClassificationE = new ClassificationImpl(featureListE,sentimentE,certaintyE);

        // test classification F
        List<String> featureListF = new ArrayList<>();
        featureListF.add("I,will");
        featureListF.add("will,be");
        featureListF.add("be,happy");
        featureListF.add("happy,if");
        featureListF.add("if,this");
        featureListF.add("this,system");
        featureListF.add("system,generates");
        featureListF.add("generates,very");
        featureListF.add("very,good");
        featureListF.add("good,results");
        Sentiment sentimentF = Sentiment.POSITIVE;
        double certaintyF = 1.0;
        Classification tClassificationF = new ClassificationImpl(featureListF,sentimentF,certaintyF);

        // train the classifier on the test classifications
        tClassifier.learn(tClassificationA);
        tClassifier.learn(tClassificationB);
        tClassifier.learn(tClassificationC);
        tClassifier.learn(tClassificationD);
        tClassifier.learn(tClassificationE);
        tClassifier.learn(tClassificationF);
    }

    @Test
    public void testGetClassificationStorageLimit() throws Exception {
        int actual = tClassifier.getClassificationStorageLimit();
        assertEquals(1000,actual);
    }

    // Confirm that the method returns the set of all features the classifier contains
    @Test
    public void testGetFeatures() throws Exception {
        Set<String> actualFeatures = tClassifier.getFeatures();
        assertTrue(actualFeatures.contains("this,project"));
        assertTrue(actualFeatures.contains("happy,if"));
        assertFalse(actualFeatures.contains("red,herring"));
        // *** CURRENTLY GETTING NULLPOINTEREXECPTION BECAUSE LEARN() METHOD HAS NOT BEEN IMPLEMENTED ***
    }

    // Confirm that the method returns the set of all categories contained in the classifier memory
    @Test
    public void testGetCategories() throws Exception {
        Set<Sentiment> actualCategories = tClassifier.getCategories();
        assertTrue(actualCategories.contains(Sentiment.NEGATIVE));
        assertTrue(actualCategories.contains(Sentiment.POSITIVE));
        assertFalse(actualCategories.contains(Sentiment.NEUTRAL));
        assertFalse(actualCategories.contains(Sentiment.UNCLASSIFIED));
    }

    // Confirm that the method returns the total number distinct categories contained in the classifier memory
    @Test
    public void testGetTotalNumCategories() throws Exception {
        int actualNumCategories = tClassifier.getTotalNumCategories();
        assertTrue(actualNumCategories == 2);
    }

    // Confirm that the method r
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

    // confirm that method returns the probability that the given feature belongs in the given category
    @Test
    public void testCalcFeatureProbability() throws Exception {
        // ADD CODE
    }

    // confirm that method returns the weighted average probability that the given feature belongs to the given category
    @Test
    public void testCalcFeatureWeightedAverage() throws Exception {
        // ADD CODE
    }

    // confirm that method trains the classifier by displaying that the given features resulted in the given
    @Test
    public void testLearn() throws Exception {
        // ADD CODE
    }

    // confirm that method returns the most likely category for the given features based upon the knowledge learnt from training on historic classifications.
    @Test
    public void testClassify() throws Exception {
        // ADD CODE
    }
}