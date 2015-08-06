package twittercorpus;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by Andrew on 08/06/15.
 */
public interface Tweet {

     ZonedDateTime getTimeStamp();

     boolean isLabelled();

     void setIsLabelled(boolean isLabelled);

     String getTweetText();

     void setTweetText(String tweetText);

     PriceSnapshot getInitialSnapshot();

     void setInitialSnapshot(PriceSnapshot initialSnapshot);

     PriceSnapshot getPostTweetSnapshot();

     void setPostTweetSnapshot(PriceSnapshot postTweetSnapshot);

     Sentiment getSentiment();

     void setSentiment(Sentiment sentiment);

     Sentiment getSentiWordNetClassification();

     void setSentiWordNetClassification(Sentiment sentiWordNetClassification);

     void removeStopWords(String fileName);

     void extractNGramFeatures(int numGrams);

     List<String> getFeatures();

}
