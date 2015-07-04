package twittercorpus;

import java.util.List;

/**
 * Created by Andrew on 04/07/15.
 */
public interface Classification {

    List<String> getListOfFeatures();

    Sentiment getSentimentCategory();

    double getClassificationCertainty();

    String toString();
}
