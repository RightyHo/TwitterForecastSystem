package twittercorpus;

import java.util.List;

/**
 * Created by Andrew on 04/07/15.
 */

/**
 * Wrapper class to hold a list of features and the sentiment classification for this list.
 */
public interface Classification {

    /**
     * getter for listOfFeatures field
     * @return the list of features held in the classification object
     */
    List<String> getListOfFeatures();

    /**
     * getter for sentimentCategory field
     * @return the sentiment category this list of features was classified under
     *
     */
    Sentiment getSentimentCategory();

    /**
     * getter for the classificationCertainty field
     * @return the probability that the list of features has been classified in the correct category
     */
    double getClassificationCertainty();

    /**
     * returns a nicely formatted string
     * @return a formatted string depicting the object
     */
    String toString();
}
