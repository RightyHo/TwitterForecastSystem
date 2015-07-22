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
        assertTrue(actualFeatures.contains("This,project"));
        assertTrue(actualFeatures.contains("happy,if"));
        assertFalse(actualFeatures.contains("red,herring"));
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
        assertTrue(actualNumCategories == 2);           // only POSITIVE & NEGATIVE categories should exist at this stage
    }

    // Confirm that the method sets the history queue limit correctly
    @Test
    public void testSetClassificationStorageLimit() throws Exception {
        tClassifier.setClassificationStorageLimit(500);
        assertEquals(500, tClassifier.getClassificationStorageLimit());
    }

    // confirm that method increases the count of the given feature in the given category by one
    @Test
    public void testIncrementFeature() throws Exception {
        String tFeature = "rainy,days";
        Sentiment tCategory = Sentiment.NEGATIVE;
        int actualBefore = tClassifier.fCountInCategory(tFeature,tCategory);

        // run incrementFeature method
        tClassifier.incrementFeatureCount(tFeature, tCategory);

        int actualAfter = tClassifier.fCountInCategory(tFeature,tCategory);
        assert(actualAfter - 1 == actualBefore);
    }

    // confirm that method decreases the count of the given feature in the given category by one
    @Test
    public void testDecrementFeature() throws Exception {

        // test on a feature with initial count of 1 in the category
        String tFeature = "I,will";
        Sentiment tCategory = Sentiment.NEGATIVE;
        int actualBefore = tClassifier.fCountInCategory(tFeature,tCategory);

        // test on a feature with initial count of 2 in the category
        String tFeatureB = "very,good";
        Sentiment tCategoryB = Sentiment.POSITIVE;
        int actualBeforeB = tClassifier.fCountInCategory(tFeatureB,tCategoryB);

        // run incrementFeature method
        tClassifier.decrementFeatureCount(tFeature,tCategory);
        tClassifier.decrementFeatureCount(tFeatureB,tCategoryB);

        int actualAfter = tClassifier.fCountInCategory(tFeature,tCategory);
        assert(actualAfter + 1 == actualBefore);

        int actualAfterB = tClassifier.fCountInCategory(tFeatureB,tCategoryB);
        assert(actualAfterB + 1 == actualBeforeB);
    }

    // confirm that method increases the count of the given category by one
    @Test
    public void testIncrementCategory() throws Exception {
        Sentiment tCategory = Sentiment.NEGATIVE;
        int actualBefore = tClassifier.getCategoryCount(tCategory);

        // run incrementCategory method
        tClassifier.incrementCategory(tCategory);

        int actualAfter = tClassifier.getCategoryCount(tCategory);
        assert(actualAfter - 1 == actualBefore);
    }

    // confirm that method decreases the count of the given category by one
    @Test
    public void testDecrementCategory() throws Exception {
        Sentiment tCategory = Sentiment.NEGATIVE;
        int actualBefore = tClassifier.getCategoryCount(tCategory);

        // run incrementCategory method
        tClassifier.decrementCategory(tCategory);

        int actualAfter = tClassifier.getCategoryCount(tCategory);
        assert(actualAfter + 1 == actualBefore);
    }

    // confirm that method returns the number of times the given feature appears in the given category
    @Test
    public void testFCountInCategory() throws Exception {
        String tFeatureA = "sentiment,analysis";        // appears once in test history under positive category
        String tFeatureB = "very,good";                 // appears twice in test history under positive category
        String tFeatureC = "red,herring";               // never appears in test history under positive category
        Sentiment tCategory = Sentiment.POSITIVE;
        int expectedFeatureCountA = 1;
        int expectedFeatureCountB = 2;
        int expectedFeatureCountC = 0;
        assertEquals(expectedFeatureCountA,tClassifier.fCountInCategory(tFeatureA,tCategory));
        assertEquals(expectedFeatureCountB, tClassifier.fCountInCategory(tFeatureB, tCategory));
        assertEquals(expectedFeatureCountC,tClassifier.fCountInCategory(tFeatureC,tCategory));
    }

    // confirm that method returns the total number of features in the given category
    @Test
    public void testGetCategoryCount() throws Exception {
        Sentiment tCategory = Sentiment.NEGATIVE;
        int expectedCategoryCount = 17;
        assertEquals(expectedCategoryCount,tClassifier.getCategoryCount(tCategory));
    }

    // confirm that method returns the likelihood P(f|c) of a feature f occurring given that we know the occurrence is in category c
    // Posterior Probability P(A|B) - the probability of event A (feature f occurring) given that event B has occurred (category c)
    // Formula:  Feature Count in Category / Total number of features in the given category
    @Test
    public void testCalcFeatureLikelihood() throws Exception {
        String tFeatureA = "sentiment,analysis";        // appears once in test history under positive category and doesn't appear in any other category
        String tFeatureB = "very,good";                 // appears twice in test history under positive category and doesn't appear in any other category
        String tFeatureC = "red,herring";               // never appears in test history under positive category and doesn't appear in any other category either
        String tFeatureD = "I,will";                    // appears once in test history under positive category and also appears once in the negative category
        // Formula:  Feature Count in Category / Total number of features in the given category
        double expectedTotalFeaturesInPositiveCategory = 24.0;
        double expectedProbPositiveA = 1.0 / expectedTotalFeaturesInPositiveCategory;
        double expectedProbPositiveB = 2.0 / expectedTotalFeaturesInPositiveCategory;
        double expectedProbPositiveC = 0.0 / expectedTotalFeaturesInPositiveCategory;
        double expectedProbPositiveD = 1.0 / expectedTotalFeaturesInPositiveCategory;
        assertTrue(Math.abs(expectedProbPositiveA - tClassifier.calcFeatureLikelihood(tFeatureA,Sentiment.POSITIVE)) < 0.000000001);
        assertTrue(Math.abs(expectedProbPositiveB - tClassifier.calcFeatureLikelihood(tFeatureB,Sentiment.POSITIVE)) < 0.000000001);
        assertTrue(Math.abs(expectedProbPositiveC - tClassifier.calcFeatureLikelihood(tFeatureC,Sentiment.POSITIVE)) < 0.000000001);
        assertTrue(Math.abs(expectedProbPositiveD - tClassifier.calcFeatureLikelihood(tFeatureD, Sentiment.POSITIVE)) < 0.000000001);
    }

    // confirm that method returns the weighted average probability that the given feature belongs to the given category
    // Formula:  Weighted average of [P(f \ c)]
    // Initially let weight be 1.0 and assumedProbability be 0.5
    @Test
    public void testCalcFeatureWeightedAverage() throws Exception {
        double assumeProb = 0.5;
        double weighting = 1.0;

        int numOccurrencesOfFeature = 1;
        String tFeature = "project,is";
        Sentiment tSenti = Sentiment.POSITIVE;
        double tLikelihood = tClassifier.calcFeatureLikelihood(tFeature, tSenti);
        double actualWAProb = tClassifier.calcFeatureWeightedAverage(tFeature, tSenti, weighting, assumeProb);
        double expectedWAProb = (assumeProb * weighting + numOccurrencesOfFeature * tLikelihood) / (numOccurrencesOfFeature + weighting);
        assertTrue(Math.abs(expectedWAProb - tClassifier.calcFeatureWeightedAverage(tFeature, tSenti, weighting, assumeProb)) < 0.000000001);
//        System.out.println("The likelihood of feature - " + tFeature + " given the probability of category " + tSenti + " is: " + tLikelihood);
//        System.out.println("Feature - " + tFeature + " has a weighted average probability that it belongs in the " + tSenti + " category of: " + actualWAProb);

        numOccurrencesOfFeature = 2;
        tFeature = "very,good";
        tSenti = Sentiment.POSITIVE;
        tLikelihood = tClassifier.calcFeatureLikelihood(tFeature, tSenti);
        actualWAProb = tClassifier.calcFeatureWeightedAverage(tFeature, tSenti, weighting, assumeProb);
        expectedWAProb = (assumeProb * weighting + numOccurrencesOfFeature * tLikelihood) / (numOccurrencesOfFeature + weighting);
        assertTrue(Math.abs(expectedWAProb - tClassifier.calcFeatureWeightedAverage(tFeature, tSenti, weighting, assumeProb)) < 0.000000001);
//        System.out.println("The likelihood of feature - " + tFeature + " given the probability of category " + tSenti + " is: " + tLikelihood);
//        System.out.println("Feature - " + tFeature + " has a weighted average probability that it belongs in the " + tSenti + " category of: " + actualWAProb);

        numOccurrencesOfFeature = 1;
        tFeature = "interesting,subject";
        tSenti = Sentiment.NEGATIVE;
        tLikelihood = tClassifier.calcFeatureLikelihood(tFeature, tSenti);
        actualWAProb = tClassifier.calcFeatureWeightedAverage(tFeature, tSenti, weighting, assumeProb);
        expectedWAProb = (assumeProb * weighting + numOccurrencesOfFeature * tLikelihood) / (numOccurrencesOfFeature + weighting);
        assertTrue(Math.abs(expectedWAProb - tClassifier.calcFeatureWeightedAverage(tFeature, tSenti, weighting, assumeProb)) < 0.000000001);
//        System.out.println("The likelihood of feature - " + tFeature + " given the probability of category " + tSenti + " is: " + tLikelihood);
//        System.out.println("Feature - " + tFeature + " has a weighted average probability that it belongs in the " + tSenti + " category of: " + actualWAProb);
    }

    // confirm that method returns the product of all feature probabilities: [PRODUCT OF (PROBABILITY(feature_i|category)]
    // *** NEED TO MAKE THE UNDERLYING METHOD PUBLIC TEMPORARILY DURING TESTING ***
    @Test
    public void testCalcProductOfFeatureProbs() throws Exception {
        List<String> tFeatureLst = new ArrayList<>();
        tFeatureLst.add("the,report");
        tFeatureLst.add("generates,very");
        tFeatureLst.add("rainy,days");
        Sentiment tSenti = Sentiment.POSITIVE;
        double featProbA = tClassifier.calcFeatureWeightedAverage("the,report", tSenti, 1.0, 0.5);
        double featProbB = tClassifier.calcFeatureWeightedAverage("generates,very", tSenti, 1.0, 0.5);
        double featProbC = tClassifier.calcFeatureWeightedAverage("rainy,days", tSenti, 1.0, 0.5);
        double actualProductOfProbs = tClassifier.calcProductOfFeatureProbs(tFeatureLst,tSenti);
        assertTrue(Math.abs((featProbA * featProbB * featProbC) - actualProductOfProbs) < 0.000000001);
    }

    // confirm that method calculates the probability that the features belong in the given category
    // Formula: (total number of features in the given category / total number distinct categories contained in the classifier memory) * product of all feature probabilities
    // Formula: (getCategoryCount / getTotalNumCategories) * calcProductOfFeatureProbs
    // *** NEED TO MAKE THE UNDERLYING METHOD PUBLIC TEMPORARILY DURING TESTING ***
    @Test
    public void testProbabilityFeatureInCategory() throws Exception {
        List<String> tFeatureLst = new ArrayList<>();
        tFeatureLst.add("the,report");
        tFeatureLst.add("generates,very");
        tFeatureLst.add("rainy,days");
        Sentiment tSenti = Sentiment.POSITIVE;
        double actualProb = tClassifier.probabilityFeatureInCategory(tFeatureLst,tSenti);
        double expectedProb = ((double) tClassifier.getCategoryCount(tSenti) / (double) tClassifier.getTotalNumCategories()) * tClassifier.calcProductOfFeatureProbs(tFeatureLst,tSenti);
        assertTrue(Math.abs(expectedProb - actualProb) < 0.000000001);
    }

    // confirm that method returns a sorted list of classifications ordered by the probability that the given set of features belongs to each category
    // *** NEED TO MAKE THE UNDERLYING METHOD PUBLIC TEMPORARILY DURING TESTING ***
    @Test
    public void testCategoryOrderOfProbability() throws Exception {
        List<String> tFeatureLst = new ArrayList<>();
        tFeatureLst.add("the,report");
        tFeatureLst.add("generates,very");
        tFeatureLst.add("rainy,days");
        List<Classification> actualOrder = tClassifier.categoryOrderOfProbability(tFeatureLst);
        List<Classification> expectedOrder = new ArrayList<>();
        Classification positiveClass = new ClassificationImpl(tFeatureLst,Sentiment.POSITIVE,tClassifier.probabilityFeatureInCategory(tFeatureLst,Sentiment.POSITIVE));
        Classification negativeClass = new ClassificationImpl(tFeatureLst,Sentiment.NEGATIVE,tClassifier.probabilityFeatureInCategory(tFeatureLst,Sentiment.NEGATIVE));
        if(positiveClass.getClassificationCertainty() >= negativeClass.getClassificationCertainty()){
            expectedOrder.add(positiveClass);
            expectedOrder.add(negativeClass);
        } else {
            expectedOrder.add(negativeClass);
            expectedOrder.add(positiveClass);
        }
        // test sentiment category order is correct
        assertEquals(expectedOrder.get(0).getSentimentCategory(),actualOrder.get(0).getSentimentCategory());
        assertEquals(expectedOrder.get(1).getSentimentCategory(),actualOrder.get(1).getSentimentCategory());

        // test classification certainty order is correct
        assert(Math.abs(expectedOrder.get(0).getClassificationCertainty() - actualOrder.get(0).getClassificationCertainty()) < 0.000000001);
        assert(Math.abs(expectedOrder.get(1).getClassificationCertainty() - actualOrder.get(1).getClassificationCertainty()) < 0.000000001);
        assert(actualOrder.get(0).getClassificationCertainty() > actualOrder.get(1).getClassificationCertainty());
    }

    // confirm that method trains the classifier by displaying that the given features resulted in the given
    @Test
    public void testLearn() throws Exception {
        assertFalse(tClassifier.getFeatures().contains("red,herring"));
        List<String> addListFeatures = new ArrayList<>();
        addListFeatures.add("red,herring");
        int preLearn = tClassifier.getCategoryCount(Sentiment.NEGATIVE);

        Classification addClass = new ClassificationImpl(addListFeatures,Sentiment.NEGATIVE,0.3);
        tClassifier.learn(addClass);

        assertTrue(tClassifier.getFeatures().contains("red,herring"));
        assertTrue(tClassifier.getCategoryCount(Sentiment.NEGATIVE) - 1 == preLearn);
    }

    // confirm that method returns the most likely category for the given features based upon the knowledge learnt from training on historic classifications.
    @Test
    public void testClassify() throws Exception {
        List<String> tFeatureLst = new ArrayList<>();
        tFeatureLst.add("the,report");
        tFeatureLst.add("generates,very");
        tFeatureLst.add("rainy,days");
        List<Classification> orderList = tClassifier.categoryOrderOfProbability(tFeatureLst);
        Classification expectedClass = null;
        for(Classification c : orderList){
            if(expectedClass == null){
                expectedClass = c;
            } else if(c.getClassificationCertainty() > expectedClass.getClassificationCertainty()) {
                expectedClass = c;
            }
        }
        assertEquals(expectedClass.getSentimentCategory(),tClassifier.classify(tFeatureLst).getSentimentCategory());
        assert(Math.abs(expectedClass.getClassificationCertainty() - tClassifier.classify(tFeatureLst).getClassificationCertainty()) < 0.000000001);
    }
}