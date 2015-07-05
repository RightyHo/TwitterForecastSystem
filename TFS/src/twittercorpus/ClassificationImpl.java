package twittercorpus;

import java.util.List;

/**
 * Created by Andrew on 05/07/15.
 */
public class ClassificationImpl implements Classification {

    private List<String> listOfFeatures;
    private Sentiment sentimentCategory;
    double classificationCertainty;

    /**
     * Constructor with all parameters passed to it.  Might be worthwhile adding a constructor
     * with a classificationCertainty default set to 1.0?
     * @param listOfFeatures
     * @param sentimentCategory
     * @param classificationCertainty
     */
    public ClassificationImpl(List<String> listOfFeatures, Sentiment sentimentCategory, double classificationCertainty) {
        this.listOfFeatures = listOfFeatures;
        this.sentimentCategory = sentimentCategory;
        this.classificationCertainty = classificationCertainty;
    }

    /**
     * getter for listOfFeatures field
     * @return the list of features held in the classification object
     */
    public List<String> getListOfFeatures(){
        return listOfFeatures;
    }

    /**
     * getter for sentimentCategory field
     * @return the sentiment category this list of features was classified under
     *
     */
    public Sentiment getSentimentCategory(){
        return null;
    }

    /**
     * getter for the classificationCertainty field
     * @return the probability that the list of features has been classified in the correct category
     */
    public double getClassificationCertainty(){
        return 0.0;
    }

    /**
     * returns a nicely formatted string
     * @return a formatted string depicting the object
     */
    @Override
    public String toString(){
        return null;
    }
}
