package twittercorpus;

import java.util.List;
import java.util.Map;

/**
 * Created by Andrew on 28/07/15.
 */
public interface SentiWordNet {

    void buildDictionary();

    Sentiment classifySentiment(String text);

    double getFeatureSentimentScore(String word);

    Map<String, Double> getSwnDictionary();

}
