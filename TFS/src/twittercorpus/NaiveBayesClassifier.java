package twittercorpus;

import java.util.*;

/**
 * Created by Andrew on 07/07/15.
 * Concrete class implements Classifier Interface
 */
public class NaiveBayesClassifier implements Classifier {

    //Limit on the number of classifications that are stored in the learnt history - thus implementing a forgetful learning strategy
    private int classificationStorageLimit;

    // The classifiers trained memory - a list of historic classifications
    private List<Classification> classificationHistoryQueue;

    // Map of the total number of occurences of each feature
    Map<String,Integer> featureTotalCount;

    // Map that pairs each category with the total number of features they contain
    Map<Sentiment,Integer> categoryTotalCount;

    // Map connecting a feature to the number of times it appears in each category
    Map<Sentiment,Map<String,Integer>> featureAppearanceCategoryCount;

    /**
     * Constructor returns a Classifier with no memory, all fields initialised, and the memory queue storage limit set by the parameter
     * @param classificationStorageLimit
     */
    public NaiveBayesClassifier(int classificationStorageLimit){
        this.classificationStorageLimit = classificationStorageLimit;
        classificationHistoryQueue = new ArrayList<>();
        featureTotalCount = new HashMap<>();
        categoryTotalCount = new HashMap<>();
        featureAppearanceCategoryCount = new HashMap<>();
    }

    /**
     * Getter
     * @return
     */
    public int getClassificationStorageLimit(){
        return classificationStorageLimit;
    }

    /**
     * Returns the set of all features the classifier contains
     * @return
     */
    public Set<String> getFeatures(){
        Set<String> result = new HashSet<>();
        Iterator<Classification> classIt = classificationHistoryQueue.iterator();
        while(classIt.hasNext()){
            Iterator<String> stringIterator = classIt.next().getListOfFeatures().iterator();
            while(stringIterator.hasNext()){
                result.add(stringIterator.next());
            }
        }
        return result;
    }

    /**
     * returns the set of all categories contained in the classifier memory
     * @return
     */
    public Set<Sentiment> getCategories(){
        return categoryTotalCount.keySet();
    }

    /**
     * returns the total number distinct categories contained in the classifier memory
     * @return
     */
    public int getTotalNumCategories(){
        return categoryTotalCount.keySet().size();
    }

    /**
     * Setter
     * @param numClassificationsRetained
     */
    public void setClassificationStorageLimit(int numClassificationsRetained){
        this.classificationStorageLimit = numClassificationsRetained;
    }

    /**
     * Increases the count of the given feature in the given category by one
     * @param feature
     * @param sentimentCategory
     */
    public void incrementFeature(String feature,Sentiment sentimentCategory){
        return;
    }

    /**
     * Decreases the count of the given feature in the given category by one
     * @param feature
     * @param sentimentCategory
     */
    public void decrementFeature(String feature,Sentiment sentimentCategory){
        return;
    }

    /**
     * Increases the count of the given category by one
     * @param sentimentCategory
     */
    public void incrementCategory(Sentiment sentimentCategory){
        return;
    }

    /**
     * Decreases the count of the given category by one
     * @param sentimentCategory
     */
    public void decrementCategory(Sentiment sentimentCategory){
        return;
    }

    /**
     * Returns the number of times the given feature appears in the given category
     * @param feature
     * @param sentimentCategory
     * @return
     */
    public int fCountInCategory(String feature,Sentiment sentimentCategory){
        return 0;
    }

    /**
     * Returns the total number of features in the given category
     * @param sentimentCategory
     * @return
     */
    public int getCategoryCount(Sentiment sentimentCategory){
        return 0;
    }

    /**
     * Returns the probability that the given feature belongs in the given category
     * @param feature
     * @param sentimentCategory
     * @return
     */
    public double calcFeatureProbability(String feature,Sentiment sentimentCategory){
        return 0.0;
    }

    /**
     * Returns the weighted average probability that the given feature belongs to the given category
     * @param feature
     * @param sentimentCategory
     * @param weight - default to 1.0
     * @param assumedProbability - default to 0.5
     * @return
     */
    public double calcFeatureWeightedAverage(String feature,Sentiment sentimentCategory,double weight,double assumedProbability){
        return 0.0;
    }

    /**
     * Trains the classifier by displaying that the given features resulted in the given
     * category classification.
     * @param classification
     */
    public void learn(Classification classification){
        // check that there is room in the history queue, if not then delete the oldest classification to make room
        if(classificationHistoryQueue.size() >= classificationStorageLimit){

        } else {
            classificationHistoryQueue.add(classification);
            Iterator<String> stringIterator = classification.getListOfFeatures().iterator();
            while (stringIterator.hasNext()){
                incrementFeature(stringIterator.next(),classification.getSentimentCategory());
                incrementCategory(classification.getSentimentCategory());
            }
        }
    }

    /**
     * Returns the most likely category for the given features based upon the knowledge
     * learnt from training on historic classifications.
     * @param features
     * @return
     */
    public Classification classify(List<String> features){
        return null;
    }

    /**
     * Returns the product of all feature probabilities: [PRODUCT OF (PROBABILITY(feature_i|category)]
     * @param features
     * @param sentimentCategory
     * @return
     */
    private double calcProductOfFeatureProbs(List<String> features,Sentiment sentimentCategory) {
        return 0.0;
    }

    /**
     * Calculates the probability that the features belong in the given category
     * @param features
     * @param sentimentCategory
     * @return
     */
    private double probabilityFeatureInCategory(List<String> features,Sentiment sentimentCategory){
        return 0.0;
    }

    /**
     * Returns a sorted list of classifications ordered by the probability that the given set of features belongs to each category
     * @param features
     * @return
     */
    private List<Classification> categoryOrderOfProbability(List<String> features){
        return null;
    }
}
