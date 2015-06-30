package twittercorpus;

import java.time.ZonedDateTime;

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

     // not 100% sure if this is flag is necessary?
     boolean isPublishedOutsideMarketHours();

     // not 100% sure if this is flag is necessary?
     void setIsPublishedOutsideMarketHours(boolean isPublishedOutsideMarketHours);

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

     void extractNGramFeatures(int numGrams,String[] tokens);

}
