package twittercorpus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.*;
import java.util.*;

/**
 * Created by Andrew on 18/06/15.
 */
public class TwitterCorpusListImpl implements TwitterCorpus {

    private static final String USERNAME_EQUIVALENCE_TOKEN = "USERNAME";
    private static final String LINK_EQUIVALENCE_TOKEN = "LINK";
    private static final LocalTime BMW_XETRA_OPEN = LocalTime.of(8,0,0);	    // London time

    // *** NEED TO CHANGE CODE TO ACCOUNT FOR THE FACT THAT THERE IS NEVER A 16:30 PRICE TIME STAMP
    // INSTEAD THERE IS A 5 MINUTE END OF DAY AUCTION AND THEN A FINAL PRICE PRINT FOR THE DAY AT 16:35 ***

    private static final LocalTime BMW_XETRA_CLOSE = LocalTime.of(16,35,0);	    // London time

    private static final LocalTime BMW_US_OTC_OPEN = LocalTime.of(14,30,0);	    // London time
    private static final LocalTime BMW_US_OTC_CLOSE = LocalTime.of(21, 0, 0);	// London time

    private List<Tweet> corpus;
    private String fileName;

    public TwitterCorpusListImpl(String fileName) {
        this.corpus = new ArrayList<>();
        this.fileName = fileName;
    }
    public TwitterCorpusListImpl(List<Tweet> corpus, String fileName) {
        this.corpus = corpus;
        this.fileName = fileName;
    }

    public String getUsernameEquivalenceToken() {
        return USERNAME_EQUIVALENCE_TOKEN;
    }

    public String getLinkEquivalenceToken() {
        return LINK_EQUIVALENCE_TOKEN;
    }

    public List<Tweet> getCorpus() {
        return corpus;
    }

    public void setCorpus(List<Tweet> corpus) {
        this.corpus = corpus;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void extractTweetsFromFile(String fileName){
        try(BufferedReader br = new BufferedReader(new FileReader(fileName))){
            String currentLine;
            while ((currentLine = br.readLine()) != null) {

                // divide current line into its individual constituents

                Scanner s = new Scanner(currentLine);
                String dayOfTheWeek = s.next().trim();
                int month = getMonthNum(s.next());
                int dayNum = s.nextInt();
                String timeString = s.next().trim();
                int year =  s.nextInt();
                s.useDelimiter("\\z");              // sets scanner delimiter to ignore all spaces
                String tweet =  s.next().trim();    // reads in the rest of the current line
                Scanner splitTime = new Scanner(timeString).useDelimiter(":");
                int hour = splitTime.nextInt();
                int min = splitTime.nextInt();
                int sec = splitTime.nextInt();

                // create new ZonedDateTime object for each row in the file

                LocalDateTime localTS = LocalDateTime.of(year, month, dayNum, hour, min, sec);
                ZonedDateTime ts = ZonedDateTime.of(localTS, ZoneId.of("Europe/London"));

                // check if the tweet was published during BMW stock market trading hours...not 100% sure if this is flag is necessary?

                boolean tsOutOfXetraMarketHours = (ts.toLocalTime().compareTo(BMW_XETRA_OPEN) < 0
                        || ts.toLocalTime().compareTo(BMW_XETRA_CLOSE) > 0);

                boolean tsOutOfUSOTCMarketHours = (ts.toLocalTime().compareTo(BMW_US_OTC_OPEN) < 0
                        || ts.toLocalTime().compareTo(BMW_US_OTC_CLOSE) > 0);

                // create new Tweet for every row in the file

                Tweet inputTweet = new TweetImpl(ts,tsOutOfXetraMarketHours,tweet);
                corpus.add(inputTweet);

            }
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     * takes a string depicting a calendar month and returns the corresponding month number
     * @param mth
     * @return
     */
    public int getMonthNum(String mth) {
        int monthNum = 0;
        switch (mth.toLowerCase()) {
            case "jan":
                monthNum = 1;
                break;
            case "feb":
                monthNum = 2;
                break;
            case "mar":
                monthNum = 3;
                break;
            case "apr":
                monthNum = 4;
                break;
            case "may":
                monthNum = 5;
                break;
            case "jun":
                monthNum = 6;
                break;
            case "jul":
                monthNum = 7;
                break;
            case "aug":
                monthNum = 8;
                break;
            case "sep":
                monthNum = 9;
                break;
            case "oct":
                monthNum = 10;
                break;
            case "nov":
                monthNum = 11;
                break;
            case "dec":
                monthNum = 12;
                break;
            default:
                monthNum = 0;
                break;
        }
        return monthNum;
    }

    /**
     * labels the tweet by setting the price snapshot that corresponds to the timestamp of the tweet as well as
     * setting the sentiment value of the tweet and marking the isLabelled flag as true.
     * @param labels
     */
    public void labelCorpus(PriceLabelCorpus labels){
        Iterator<Tweet> corpusIterator = corpus.iterator();
        while(corpusIterator.hasNext()){
            Tweet focus = corpusIterator.next();
            ZonedDateTime focusTS = focus.getTimeStamp();
            PriceSnapshot openingSnap = null;

            // Amend labelCorpus() method to account for the following:
            // 1.  Missing price data at particular time stamps during normal trading hours.
            // 2.  Tweets that occur during a weekend (including the previous evening and following morning).
            // 3.  Tweets that occur during a market holiday (including the previous evening and following morning):
            //	--> Non Trading Days are 1 Jan, 3 Apr, 6 Apr, 1 May, 25 May, 24 Dec, 25 Dec, 31 Dec

            ZonedDateTime preTweetTime = lastPrintBeforeTweet(focusTS);

            if(labels.getPriceMap().containsKey(preTweetTime)){
                // timestamp key exists in map
                PriceSnapshot openingSnap = labels.getPriceMap().get(preTweetTime);
            } else if(notValidTradeDate(lastPrintBeforeTweet(focusTS))){
                // timestamp key does not exist in map because it falls on a weekend or a market holiday

            } else {
                // exception situation in which timestamp does not appear in priceLabel corpus but should

            }
            if(focusTS.getDayOfWeek().equals(DayOfWeek.SATURDAY) || focusTS.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
                // timestamp key does not exist in map because it falls on a weekend or a market holiday
            }

            ZonedDateTime postTweetTime = twentyMinsAfterTweet(focusTS);
            PriceSnapshot closingSnap = labels.getPriceMap().get(postTweetTime);

            // set the two price snapshots and labelled flag for the tweet

            focus.setInitialSnapshot(openingSnap);
            focus.setPostTweetSnapshot(closingSnap);
            focus.setIsLabelled(true);

            // *** change initial snapshot to the previous minutes closing price (or this minutes open price ***
            // *** need to account for gaps in the price data for example the following gap is currently producing a null pointer when we look up 9:27 ***
            // *** can we check if not found then search for next valid price? ***
            // *** also holidays and weekends to take into account ***
            // 16/01/2015 09:29	91.13	91.18	91.08	91.16	-0.0005	0.0009	-0.0014
            // 16/01/2015 09:26	91.15	91.17	91.14	91.15	0.0008	0.0012	-0.0004

            if(focus.getInitialSnapshot() != null)
                System.out.println(focus.getTimeStamp() +" -->"+"opening share price: "+ focus.getInitialSnapshot().getClosingSharePrice());
            if(focus.getPostTweetSnapshot() != null)
                System.out.println(focus.getTimeStamp() +" -->"+"closing share price: " + focus.getPostTweetSnapshot().getClosingSharePrice());

            // compare the two market price snapshots to discern the implied sentiment of the tweet from the change in price

            if(focus.getInitialSnapshot().getOpeningSharePrice() > focus.getPostTweetSnapshot().getClosingSharePrice()){
                focus.setSentiment(Sentiment.NEGATIVE);
            } else if(focus.getInitialSnapshot().getOpeningSharePrice() < focus.getPostTweetSnapshot().getClosingSharePrice()) {
                focus.setSentiment(Sentiment.POSITIVE);
            } else {
                focus.setSentiment(Sentiment.NEUTRAL);
            }
        }
    }

    /**
     * takes the timestamp of a tweet and returns the time stamp of the last price print before the tweet,
     * taking into consideration whether or not the tweet was published outside of market trading hours.
     * @param tweetTimeStamp
     * @return time stamp of the last price print before the tweet
     */
    public ZonedDateTime lastPrintBeforeTweet(ZonedDateTime tweetTimeStamp){
        if(tweetTimeStamp.toLocalTime().compareTo(BMW_XETRA_OPEN) <= 0){
            // time stamp of the tweet occurs on or before market open --> compare previous day's close to the price 20 mins after the tweet is published
            return ZonedDateTime.of(BMW_XETRA_CLOSE.atDate(tweetTimeStamp.toLocalDate().minusDays(1)), ZoneId.of("Europe/London"));
        } else if(tweetTimeStamp.toLocalTime().compareTo(BMW_XETRA_CLOSE) > 0){
            // time stamp of the tweet occurs after the market close --> compare today's close to the next day's open price
            return ZonedDateTime.of(BMW_XETRA_CLOSE.atDate(tweetTimeStamp.toLocalDate()), ZoneId.of("Europe/London"));
        } else {
            // timestamp of the tweet occurs during normal market hours
            return tweetTimeStamp.minusMinutes(1);
        }
    }

    /**
     * takes the timestamp of a tweet and returns the time stamp of the price print 20 minutes after the tweet is published,
     * taking into consideration whether or not the tweet was published outside of market trading hours.
     * @param tweetTimeStamp
     * @return time stamp of the price print 20 minutes after the tweet is published
     */
    public ZonedDateTime twentyMinsAfterTweet(ZonedDateTime tweetTimeStamp){
        if(tweetTimeStamp.toLocalTime().plusMinutes(20).compareTo(BMW_XETRA_OPEN) < 0){
            // time stamp of the tweet occurs more than 20 minutes prior to the market open --> compare previous day's close to today's open price
            return ZonedDateTime.of(BMW_XETRA_OPEN.atDate(tweetTimeStamp.toLocalDate()), ZoneId.of("Europe/London"));
        } else if(tweetTimeStamp.plusMinutes(20).toLocalTime().compareTo(BMW_XETRA_CLOSE) > 0){
            // time stamp of the tweet occurs at a time later than 20 minutes before the market close --> return the next day's open price
            return ZonedDateTime.of(BMW_XETRA_OPEN.atDate(tweetTimeStamp.toLocalDate().plusDays(1)),ZoneId.of("Europe/London"));
        } else {
            // timestamp of the tweet occurs during normal market hours
            return tweetTimeStamp.plusMinutes(20);
        }
    }

    public void removeRetweets(){
        return;
    }

    public void replaceLinks(String linkEquivalenceToken){
        return;
    }

    public void replaceUsernames(String usernameEquivalenceToken){
        return;
    }

    public void translateAbbreviations(DictionaryTranslator abbreviationDict){
        return;
    }

    public void checkSpelling(DictionaryTranslator spellingDict){
        return;
    }

}
