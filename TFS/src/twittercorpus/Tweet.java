package twittercorpus;

import java.time.ZonedDateTime;

/**
 * Created by Andrew on 08/06/15.
 */
public interface Tweet {

    public ZonedDateTime getTimeStamp();

    public void setTimeStamp(ZonedDateTime timeStamp);

    public boolean isRetweet();

    public void setIsRetweet(boolean isRetweet);

    public boolean isLabelled();

    public void setIsLabelled(boolean isLabelled);

    public boolean isPublishedOutsideMarketHours();

    public void setIsPublishedOutsideMarketHours(boolean isPublishedOutsideMarketHours);

    public String getTweetText();

    public void setTweetText(String tweetText);

    public PriceSnapshot getPriceSnapshot();

    public void setPriceSnapshot(PriceSnapshot priceSnapshot);

    public Tweet getNextTweet();

    public void setNextTweet(Tweet nextTweet);

    public Sentiment getSentiment();

    public void setSentiment(Sentiment sentiment);

    /**
     * labels the tweet by setting the price snapshot that corresponds to the timestamp of the tweet as well as
     * setting the sentiment value of the tweet and marking the isLabelled flag as true.
     * @param priceSnapshot
     */
    public void labelTweet(PriceSnapshot priceSnapshot);
}
