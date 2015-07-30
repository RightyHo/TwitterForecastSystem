package twittercorpus;

import java.util.List;

/**
 * Created by Andrew on 28/07/15.
 */
public interface SentiWordNet {

    void buildDictionary();

    Sentiment classifySentiment(List<String> features);

    double getFeatureSentimentScore(String word);

}
