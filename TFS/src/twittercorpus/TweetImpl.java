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
    private String rawTweetText;                        // consolidate into one over-written variable?
    private String unabbreviatedTweetText;              // consolidate into one over-written variable?
    private String spellCheckedUnabbreviatedTweetText;  // consolidate into one over-written variable?
    private PriceSnapshot priceSnapshot;
    private Tweet nextTweet;
    private Sentiment sentiment;    // sentiment object is initially set to unclassified.  Once classified it is set to positive, negative or neutral.

    public void labelTweet(PriceSnapshot priceSnapshot){

    }
}
