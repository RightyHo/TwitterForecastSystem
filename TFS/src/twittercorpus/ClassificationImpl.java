package twittercorpus;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Andrew on 05/07/15.
 */
public class ClassificationImpl implements Classification {

    private List<String> listOfFeatures;
    private Sentiment sentimentCategory;
    private double classificationCertainty;

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
        return sentimentCategory;
    }

    /**
     * getter for the classificationCertainty field
     * @return the probability that the list of features has been classified in the correct category
     */
    public double getClassificationCertainty(){
        return classificationCertainty;
    }

    /**
     * returns a nicely formatted string
     * @return a formatted string depicting the object
     */
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        result.append("[[The Feature List --> [");

        // add feature list elements
        Iterator<String> listIt = listOfFeatures.listIterator();
        // take care of first element special case
        if (listIt.hasNext()) result.append("(" + listIt.next() + ")");
        // take care of the rest of the list
        while (listIt.hasNext()){
            result.append(",(" + listIt.next() + ")");
        }
        result.append("] is classified as falling in the ");

        // add classification
        result.append(sentimentCategory.toString().toLowerCase());

        result.append(" category with a probability of ");

        //add certainty number
        int percent = (int)(classificationCertainty * 100);
        result.append(String.valueOf(percent) + "%]]");

        return result.toString();
    }
}
