package dataextraction;

import java.time.*;

/**
 * Created by Andrew on 08/06/15.
 */
public class TweetImpl implements Tweet {
    private DayOfWeek timeStampDayOfWeek;
    private Month timeStampMonth;
    private MonthDay timeStampMonthDay;
    private Clock timeStampClock;
    private Year timeStampYear;
    private boolean isRetweet;
    private boolean publishedOutsideMarketHours;
    private String authorUsername;
    private String taggedUsername;
    private String usernameEquivalenceToken = "USERNAME";
    private String link;
    private String linkEquivalenceToken = "LINK";
    private String rawTweetText;
    private String unabbreviatedTweetText;
    private String spellCheckedUnabbreviatedTweetText;
    private boolean isClassified;
    private Sentiment sentiment;    // sentiment object is initially set to unclassified.  Once classified it is set to positive, negative or neutral.
}
