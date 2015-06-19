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

     boolean isPublishedOutsideMarketHours();

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

    /**
     * labels the tweet by setting the price snapshot that corresponds to the timestamp of the tweet as well as
     * setting the sentiment value of the tweet and marking the isLabelled flag as true.
     * @param priceSnapshot
     */
     void labelTweet(PriceSnapshot priceSnapshot);
}
