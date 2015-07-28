package twittercorpus;

import java.util.List;

/**
 * Created by Andrew on 28/07/15.
 */
public interface SentiWordNet {

    Sentiment classifySentiment(List<String> features);
}
