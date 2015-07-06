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
     * Returns the probability that the given feature belongs in the given category
     * @param feature
     * @param sentimentCategory
     * @return
     */
    double calcFeatureProbability(String feature,Sentiment sentimentCategory);

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
