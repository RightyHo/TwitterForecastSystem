package twittercorpus;

import java.time.*;

/**
 * Created by Andrew on 08/06/15.
 */
public class TweetImpl implements Tweet {
    private ZonedDateTime timeStamp;
    private boolean isRetweet;
    private boolean isLabelled;
    private boolean isPublishedOutsideMarketHours;
    private String tweetText;       // split into three variables to separate unabbreviated and spell checked states?
    private PriceSnapshot priceSnapshot;
    private Tweet nextTweet;
    private Sentiment sentiment;    // sentiment object is initially set to unclassified.  Once classified it is set to positive, negative or neutral.

    // constructors

    public TweetImpl(ZonedDateTime timeStamp, boolean isPublishedOutsideMarketHours, String tweetText) {
        this.timeStamp = timeStamp;
        this.isRetweet = false;
        this.isLabelled = false;
        this.isPublishedOutsideMarketHours = isPublishedOutsideMarketHours;
        this.tweetText = tweetText;
        this.priceSnapshot = null;
        this.nextTweet = null;
        this.sentiment = Sentiment.UNCLASSIFIED;
    }

    public TweetImpl(ZonedDateTime timeStamp, boolean isRetweet, boolean isLabelled, boolean isPublishedOutsideMarketHours, String tweetText, PriceSnapshot priceSnapshot, Tweet nextTweet, Sentiment sentiment) {
        this.timeStamp = timeStamp;
        this.isRetweet = isRetweet;
        this.isLabelled = isLabelled;
        this.isPublishedOutsideMarketHours = isPublishedOutsideMarketHours;
        this.tweetText = tweetText;
        this.priceSnapshot = priceSnapshot;
        this.nextTweet = nextTweet;
        this.sentiment = sentiment;
    }

    // getters and setters

    public ZonedDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(ZonedDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isRetweet() {
        return isRetweet;
    }

    public void setIsRetweet(boolean isRetweet) {
        this.isRetweet = isRetweet;
    }

    public boolean isLabelled() {
        return isLabelled;
    }

    public void setIsLabelled(boolean isLabelled) {
        this.isLabelled = isLabelled;
    }

    public boolean isPublishedOutsideMarketHours() {
        return isPublishedOutsideMarketHours;
    }

    public void setIsPublishedOutsideMarketHours(boolean isPublishedOutsideMarketHours) {
        this.isPublishedOutsideMarketHours = isPublishedOutsideMarketHours;
    }

    public String getTweetText() {
        return tweetText;
    }

    public void setTweetText(String tweetText) {
        this.tweetText = tweetText;
    }

    public PriceSnapshot getPriceSnapshot() {
        return priceSnapshot;
    }

    public void setPriceSnapshot(PriceSnapshot priceSnapshot) {
        this.priceSnapshot = priceSnapshot;
    }

    public Tweet getNextTweet() {
        return nextTweet;
    }

    public void setNextTweet(Tweet nextTweet) {
        this.nextTweet = nextTweet;
    }

    public Sentiment getSentiment() {
        return sentiment;
    }

    public void setSentiment(Sentiment sentiment) {
        this.sentiment = sentiment;
    }

    /**
     * labels the tweet by setting the price snapshot that corresponds to the timestamp of the tweet as well as
     * setting the sentiment value of the tweet and marking the isLabelled flag as true
     * @param priceSnapshot
     */
    public void labelTweet(PriceSnapshot priceSnapshot){

    }
}
