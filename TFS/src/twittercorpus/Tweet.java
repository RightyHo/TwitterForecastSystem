package twittercorpus;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by Andrew on 08/06/15.
 */
public interface Tweet {

     ZonedDateTime getTimeStamp();

     void setTimeStamp(ZonedDateTime timeStamp);

     boolean isRetweet();

     void setIsRetweet(boolean isRetweet);

     boolean isLabelled();

     void setIsLabelled(boolean isLabelled);

     String getTweetText();

     void setTweetText(String tweetText);

     PriceSnapshot getInitialSnapshot();

     void setInitialSnapshot(PriceSnapshot initialSnapshot);

     PriceSnapshot getPostTweetSnapshot();

     void setPostTweetSnapshot(PriceSnapshot postTweetSnapshot);

     Tweet getNextTweet();

     void setNextTweet(Tweet nextTweet);

     Sentiment getSentiment();

     void setSentiment(Sentiment sentiment);

     void extractNGramFeatures(int numGrams,String text);

     List<String> getFeatures();

}
