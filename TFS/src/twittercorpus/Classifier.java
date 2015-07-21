package twittercorpus;

import java.util.List;
import java.util.Set;

/**
 * Created by Andrew on 06/07/15.
 * Interface offering methods with which to calculate feature probabilities,
 * category probabilities, creating and storing classification objects.
 */
public interface Classifier {

    /**
     * Getter
     * @return
     */
    int getClassificationStorageLimit();

    /**
     * Returns the set of all features the classifier contains
     * @return
     */
    Set<String> getFeatures();

    /**
     * returns the set of all categories contained in the classifier memory
     * @return
     */
    Set<Sentiment> getCategories();

    /**
     * returns the total number distinct categories contained in the classifier memory
     * @return
     */
    int getTotalNumCategories();

    /**
     * Setter
     * @param numClassificationsRetained
     */
    void setClassificationStorageLimit(int numClassificationsRetained);

    /**
     * Increases the count of the given feature in the given category by one
     * @param feature
     * @param sentimentCategory
     */
    void incrementFeature(String feature,Sentiment sentimentCategory);

    /**
     * Decreases the count of the given feature in the given category by one
     * @param feature
     * @param sentimentCategory
     */
    void decrementFeature(String feature,Sentiment sentimentCategory);

    /**
     * Increases the count of the given category by one
     * @param sentimentCategory
     */
    void incrementCategory(Sentiment sentimentCategory);

    /**
     * Decreases the count of the given category by one
     * @param sentimentCategory
     */
    void decrementCategory(Sentiment sentimentCategory);

    /**
     * Returns the number of times the given feature appears in the given category
     * @param feature
     * @param sentimentCategory
     * @return
     */
    int fCountInCategory(String feature,Sentiment sentimentCategory);

    /**
     * Returns the total number of features in the given category
     * @param sentimentCategory
     * @return
     */
    int getCategoryCount(Sentiment sentimentCategory);

    /**
     * Returns the likelihood P(f|c) of a feature f occurring given that we know the occurrence is in category c
     * Posterior Probability P(A|B) - the probability of event A (feature f occurring) given that event B has occurred (category c)
     * Formula:  Feature Count in Category / Total number of features in the given category
     * @param feature
     * @param sentimentCategory
     * @return
     */
    double calcFeatureLikelihood(String feature,Sentiment sentimentCategory);

    /**
     * Returns the weighted average probability that the given feature belongs to the given category
     * @param feature
     * @param sentimentCategory
     * @param weight - default to 1.0
     * @param assumedProbability - default to 0.5
     * @return
     */
    double calcFeatureWeightedAverage(String feature,Sentiment sentimentCategory,double weight,double assumedProbability);

    /**
     * Returns the product of all weighted average feature probabilities under the given category
     * Formula: [PRODUCT OF (PROBABILITY(feature_i|category)]
     * @param features
     * @param sentimentCategory
     * @return
     */
    double calcProductOfFeatureProbs(List<String> features,Sentiment sentimentCategory);

    /**
     * Calculates the probability that the features belong in the given category
     * @param features
     * @param sentimentCategory
     * @return
     */
    double probabilityFeatureInCategory(List<String> features,Sentiment sentimentCategory);

    /**
     * Returns a sorted list of classifications ordered by the probability that the given set of features belongs to each category
     * @param features
     * @return
     */
    List<Classification> categoryOrderOfProbability(List<String> features);

    /**
     * Trains the classifier by displaying that the given features resulted in the given
     * category classification.
     * @param classification
     */
    void learn(Classification classification);

    /**
     * Returns the most likely category for the given features based upon the knowledge
     * learnt from training on historic classifications.
     * @param features
     * @return
     */
    Classification classify(List<String> features);
}
