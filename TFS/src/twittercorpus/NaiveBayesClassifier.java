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

    // Map of the total number of occurrences of each feature
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
        // initialise all sentiment categories with Maps
        featureAppearanceCategoryCount.put(Sentiment.NEGATIVE,new HashMap<String,Integer>());
        featureAppearanceCategoryCount.put(Sentiment.POSITIVE,new HashMap<String,Integer>());
        featureAppearanceCategoryCount.put(Sentiment.NEUTRAL,new HashMap<String,Integer>());
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
        Map<String,Integer> mapInSelectedCategory = featureAppearanceCategoryCount.get(sentimentCategory);
        if(mapInSelectedCategory == null) throw new NullPointerException("The map in the selected category is not initialised properly");
        if(featureTotalCount.containsKey(feature)){
            // add one to existing feature count mapping
            featureTotalCount.put(feature,featureTotalCount.get(feature) + 1);
            if(mapInSelectedCategory.containsKey(feature)){
                // add one to existing feature count mapping under the given category
                int prevCount = mapInSelectedCategory.get(feature);
                mapInSelectedCategory.put(feature,prevCount + 1);
                featureAppearanceCategoryCount.put(sentimentCategory, mapInSelectedCategory);
            } else {
                // create a new feature count mapping under the given category with a count value of one
                mapInSelectedCategory.put(feature,1);
                featureAppearanceCategoryCount.put(sentimentCategory,mapInSelectedCategory);
            }
        } else {
            // create a new feature count mapping with a count value of one
            featureTotalCount.put(feature,1);
            // create a new feature count mapping under the given category with a count value of one
            mapInSelectedCategory.put(feature,1);
            featureAppearanceCategoryCount.put(sentimentCategory,mapInSelectedCategory);
        }
    }

    /**
     * Decreases the count of the given feature in the given category by one
     * @param feature
     * @param sentimentCategory
     */
    public void decrementFeature(String feature,Sentiment sentimentCategory){
        Map<String,Integer> mapInSelectedCategory = featureAppearanceCategoryCount.get(sentimentCategory);
        if(mapInSelectedCategory == null) throw new NullPointerException("The map in the selected category is not initialised properly");
        if(featureTotalCount.containsKey(feature)){
            // subtract one from existing feature count mapping or remove from the map if the count value is initially only one
            if(featureTotalCount.get(feature) == 1){
                featureTotalCount.remove(feature);
                if(mapInSelectedCategory.containsKey(feature)){
                    // remove existing feature count mapping under the given category since the initial count will be one
                    mapInSelectedCategory.remove(feature);
                    featureAppearanceCategoryCount.put(sentimentCategory,mapInSelectedCategory);
                } else {
                    throw new NoSuchElementException("The feature you tried to decrement exists in the total feature set but is not present in the category you gave!");
                }
            } else {
                // initial feature count is greater than one
                featureTotalCount.put(feature, featureTotalCount.get(feature) - 1);
                if(mapInSelectedCategory.containsKey(feature)){
                    // subtract one from feature count mapping under the given category
                    int prevCount = mapInSelectedCategory.get(feature);
                    mapInSelectedCategory.put(feature,prevCount - 1);
                    featureAppearanceCategoryCount.put(sentimentCategory,mapInSelectedCategory);
                } else {
                    throw new NoSuchElementException("The feature you tried to decrement exists in the total feature set but is not present in the category you gave!");
                }
            }
        } else {
            // the given feature doesn't appear in the total feature set
            throw new NoSuchElementException("The feature you attempted to decrement does not exist in the total feature set!");
        }
    }

    /**
     * Increases the count of the given category by one
     * @param sentimentCategory
     */
    public void incrementCategory(Sentiment sentimentCategory) {
        if (categoryTotalCount.get(sentimentCategory) == null) {
            categoryTotalCount.put(sentimentCategory, 1);
        } else {
            categoryTotalCount.put(sentimentCategory, categoryTotalCount.get(sentimentCategory) + 1);
        }
    }

    /**
     * Decreases the count of the given category by one
     * @param sentimentCategory
     */
    public void decrementCategory(Sentiment sentimentCategory) {
        if (categoryTotalCount.get(sentimentCategory) == null) {
            throw new IndexOutOfBoundsException("Cannot decrement a null category!");
        } else if (categoryTotalCount.get(sentimentCategory) < 1) {
            throw new IndexOutOfBoundsException("Cannot decrement a category count with an initial value less than one!");
        } else {
            categoryTotalCount.put(sentimentCategory, categoryTotalCount.get(sentimentCategory) - 1);
        }
    }

    /**
     * Returns the number of times the given feature appears in the given category
     * @param feature
     * @param sentimentCategory
     * @return
     */
    public int fCountInCategory(String feature,Sentiment sentimentCategory){
        Map<String,Integer> mapInSelectedCategory = featureAppearanceCategoryCount.get(sentimentCategory);
        if(mapInSelectedCategory == null) throw new NullPointerException("The map in the selected category is not initialised properly");
        if(mapInSelectedCategory.containsKey(feature)){
            return mapInSelectedCategory.get(feature);             
        } else {
            return 0;
        }
    }

    /**
     * Returns the total number of features in the given category
     * @param sentimentCategory
     * @return
     */
    public int getCategoryCount(Sentiment sentimentCategory){
        if(categoryTotalCount.get(sentimentCategory) == null){
            return 0;
        } else {
            return categoryTotalCount.get(sentimentCategory);
        }
    }

    /**
     * Returns the likelihood P(f|c) of a feature f occurring given that we know the occurrence is in category c
     * Posterior Probability P(A|B) - the probability of event A (feature f occurring) given that event B has occurred (category c)
     * Formula:  Feature Count in Category / Total number of features in the given category
     * @param feature
     * @param sentimentCategory
     * @return
     */
    public double calcFeatureLikelihood(String feature,Sentiment sentimentCategory){
        if(getCategoryCount(sentimentCategory) == 0){
            return 0.0;
        } else {
            return (double) fCountInCategory(feature,sentimentCategory) / (double) getCategoryCount(sentimentCategory);
        }
    }

    /**
     * Returns the weighted average probability that the given feature belongs to the given category
     * Formula:  Weighted average of P(f | c)
     * @param feature
     * @param sentimentCategory
     * @param weight - default to 1.0
     * @param assumedProbability - default to 0.5
     * @return
     */
    public double calcFeatureWeightedAverage(String feature,Sentiment sentimentCategory,double weight,double assumedProbability){
        double result = 0.0;
        Integer numOccurrencesOfFeature = featureTotalCount.get(feature);
        if(numOccurrencesOfFeature == null){
            numOccurrencesOfFeature = 0;
        }
        double likelihood = calcFeatureLikelihood(feature,sentimentCategory);
        result = (assumedProbability * weight + numOccurrencesOfFeature * likelihood) / (numOccurrencesOfFeature + weight);
        return result;
    }

    /**
     * Returns the product of all weighted average feature probabilities under the given category
     * Formula: [PRODUCT OF (PROBABILITY(feature_i|category)]
     * @param features
     * @param sentimentCategory
     * @return
     */
    public double calcProductOfFeatureProbs(List<String> features,Sentiment sentimentCategory) {
        if(features == null || sentimentCategory == null){
            throw new IllegalArgumentException("Null parameters passed to calcProductOfFeatureProbs method");
        } else {
            double result = 1.0;
            for(String f : features) {
                // using 1.0 as a default weighting and 0.5 as the default assumed probability for our calculations
                result *= calcFeatureWeightedAverage(f,sentimentCategory,1.0,0.5);
            }
            return result;
        }
    }

    /**
     * Calculates the probability that the features belong in the given category
     * Formula: (total number of features in the given category / total number distinct categories contained in the classifier memory) * product of all feature probabilities
     * Formula: (getCategoryCount / getTotalNumCategories) * calcProductOfFeatureProbs
     * @param features
     * @param sentimentCategory
     * @return
     */
    public double probabilityFeatureInCategory(List<String> features,Sentiment sentimentCategory){
        if(features == null || sentimentCategory == null){
            throw new IllegalArgumentException("Null parameter passed to probabilityFeatureInCategory method");
        } else {
            return (getCategoryCount(sentimentCategory) / getTotalNumCategories()) * calcProductOfFeatureProbs(features,sentimentCategory);
        }
    }

    /**
     * Returns a sorted list of classifications ordered by the probability that the given set of features belongs to each category
     * @param features
     * @return
     */
    public List<Classification> categoryOrderOfProbability(List<String> features){
        if(features == null){
            throw new IllegalArgumentException("Null parameter passed to categoryOrderOfProbability method");
        } else {
            List<Classification> result = new ArrayList<>();
            List<Double> certaintyList = new ArrayList<>();

            double negativeClassCertainty = probabilityFeatureInCategory(features, Sentiment.NEGATIVE);
            Classification negativeClass = new ClassificationImpl(features, Sentiment.NEGATIVE, negativeClassCertainty);

            double positiveClassCertainty = probabilityFeatureInCategory(features, Sentiment.POSITIVE);
            Classification positiveClass = new ClassificationImpl(features, Sentiment.POSITIVE, positiveClassCertainty);

            double neutralClassCertainty = probabilityFeatureInCategory(features, Sentiment.NEUTRAL);
            Classification neutralClass = new ClassificationImpl(features, Sentiment.NEUTRAL, neutralClassCertainty);

            // add certainties to list in order to order them in descending order
            certaintyList.add(negativeClassCertainty);
            certaintyList.add(positiveClassCertainty);
            certaintyList.add(neutralClassCertainty);
            Collections.sort(certaintyList);
            Collections.reverse(certaintyList);

            for (Double d : certaintyList) {
                if (Math.abs(d - negativeClass.getClassificationCertainty()) < 0.000000001) {
                    result.add(negativeClass);
                } else if (Math.abs(d - positiveClass.getClassificationCertainty()) < 0.000000001) {
                    result.add(positiveClass);
                } else if (Math.abs(d - neutralClass.getClassificationCertainty()) < 0.000000001) {
                    result.add(neutralClass);
                } else {
                    throw new IllegalArgumentException("probabilityFeatureInCategory calculation is inconsistent");
                }
            }
            return result;
        }
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
        if(features == null){
            throw new IllegalArgumentException("Null parameter passed to classify method");
        } else {
            List<Classification> categoryOrder = categoryOrderOfProbability(features);
            if(categoryOrder == null){
                throw new NullPointerException("category order of probability method is returning a null value for this list of features");
            } else {
                return categoryOrder.get(0);
            }
        }
    }
}
