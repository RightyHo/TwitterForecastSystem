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

    public static final String USERNAME_EQUIVALENCE_TOKEN = "USERNAME";
    public static final String LINK_EQUIVALENCE_TOKEN = "LINK";
    public static final LocalTime BMW_XETRA_OPEN = LocalTime.of(8,0,0);	        // London time
    public static final LocalTime BMW_XETRA_CLOSE = LocalTime.of(16,35,0);	    // London time
    public static final LocalTime BMW_US_OTC_OPEN = LocalTime.of(14,30,0);	    // London time
    public static final LocalTime BMW_US_OTC_CLOSE = LocalTime.of(21, 0, 0);	// London time
    public static final int THIS_YEAR = 2015;
    public static final LocalDate[] SET_VALUES = new LocalDate[]{LocalDate.of(THIS_YEAR,1,1),
                                                                LocalDate.of(THIS_YEAR,4,3),
                                                                LocalDate.of(THIS_YEAR,4,6),
                                                                LocalDate.of(THIS_YEAR,5,1),
                                                                LocalDate.of(THIS_YEAR,5,25),
                                                                LocalDate.of(THIS_YEAR,12,24),
                                                                LocalDate.of(THIS_YEAR,12,25),
                                                                LocalDate.of(THIS_YEAR,12,31)};
    public static final Set<LocalDate> MARKET_HOLIDAY = new HashSet<>(Arrays.asList(SET_VALUES));
    public static final ZonedDateTime EARLIEST_CORPUS_TIME_STAMP = ZonedDateTime.of(THIS_YEAR, 1, 1, 0, 0, 0, 0, ZoneId.of("Europe/London"));

    // *** NEED TO ADJUST LATEST_CORPUS_TIME_STAMP TO THE LAST PRICE TIMESTAMP WE HAVE IN THE CORPUS ***

    public static final ZonedDateTime LATEST_CORPUS_TIME_STAMP = ZonedDateTime.of(THIS_YEAR, 6, 23, 0, 0, 0, 0, ZoneId.of("Europe/London"));

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
            PriceSnapshot openingSnap = getPriorPrices(labels,focusTS);
            PriceSnapshot closingSnap = getPrice20MinsAfterTweet(labels, focusTS);

            // set the two price snapshots and labelled flag for the tweet

            focus.setInitialSnapshot(openingSnap);
            focus.setPostTweetSnapshot(closingSnap);
            focus.setIsLabelled(true);

            // compare the two market price snapshots to discern the implied sentiment of the tweet from the change in price

            // *** DOES IT MAKE MORE SENSE TO USE THE focus.getPostTweetSnapshot().getOpeningSharePrice() FOR THIS COMPARISON? ***
            // *** IT DEFINITELY SEEMS TO BE MORE APPROPRIATE IN THE CASE WHERE THE postTweetSnapshot HAS BEEN ADJUSTED TO BE ***
            // *** THE FIRST TIME STAMP OF THE DAY BECAUSE THE TWEET WAS PUBLISHED OVERNIGHT OR DURING A WEEKEND ***

            if(focus.getInitialSnapshot().getClosingSharePrice() > focus.getPostTweetSnapshot().getClosingSharePrice()){
                focus.setSentiment(Sentiment.NEGATIVE);
            } else if(focus.getInitialSnapshot().getClosingSharePrice() < focus.getPostTweetSnapshot().getClosingSharePrice()) {
                focus.setSentiment(Sentiment.POSITIVE);
            } else {
                focus.setSentiment(Sentiment.NEUTRAL);
            }
        }
    }

    /**
     * recursively searches through the price label corpus to find the last price snapshot occurring prior to the tweet time stamp passed to it as a parameter.
     * takes into account holidays, weekends and missing price data that occurs during normal trading hours.
     * @param labels
     * @param tweetTime
     * @return
     */
    private PriceSnapshot getPriorPrices(PriceLabelCorpus labels,ZonedDateTime tweetTime){

        ZonedDateTime preTweetTime = lastTimePrintBeforeTweet(tweetTime);

        if (preTweetTime.compareTo(EARLIEST_CORPUS_TIME_STAMP) < 0){
            // error situation
            throw new IllegalArgumentException("We have reached the start of the price label corpus without finding any relevant timestamp to match up with this particular tweet!");
        } else if(labels.getPriceMap().containsKey(preTweetTime)){
            // timestamp key exists in map --> return map value
            return labels.getPriceMap().get(preTweetTime);
        } else if(MARKET_HOLIDAY.contains(preTweetTime.toLocalDate())){
            // timestamp key does not exist in map because it falls on a market holiday --> try the previous days closing price
            return getPriorPrices(labels, ZonedDateTime.of(LocalDateTime.of(preTweetTime.toLocalDate().minusDays(1), BMW_XETRA_CLOSE.plusMinutes(30)), ZoneId.of("Europe/London")));
        } else if (preTweetTime.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
            // timestamp key does not exist in map because it falls on a Saturday  --> try the previous days closing price
            return getPriorPrices(labels, ZonedDateTime.of(LocalDateTime.of(preTweetTime.toLocalDate().minusDays(1), BMW_XETRA_CLOSE.plusMinutes(30)), ZoneId.of("Europe/London")));
        } else if (preTweetTime.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
            // timestamp key does not exist in map because it falls on a Sunday  --> try the closing price two days previous
            return getPriorPrices(labels, ZonedDateTime.of(LocalDateTime.of(preTweetTime.toLocalDate().minusDays(2), BMW_XETRA_CLOSE.plusMinutes(30)), ZoneId.of("Europe/London")));
        } else {
            // exception situation in which timestamp does not appear in priceLabel corpus but should be in the corpus --> try the previous minute
            return getPriorPrices(labels, preTweetTime);
        }
    }

    /**
     * recursively searches through the price label corpus to find the first price snapshot occurring 20 minutes after the tweet time stamp passed to it as a parameter.
     * takes into account holidays, weekends and missing price data that occurs during normal trading hours.
     * @param labels
     * @param tweetTime
     * @return
     */
    private PriceSnapshot getPrice20MinsAfterTweet(PriceLabelCorpus labels,ZonedDateTime tweetTime){

        ZonedDateTime postTweetTime = getTimeAfterTweet(tweetTime);

        if (postTweetTime.compareTo(LATEST_CORPUS_TIME_STAMP) > 0){
            // error situation
            throw new IllegalArgumentException("We have reached the end of the price label corpus without finding any relevant timestamp to match up with this particular tweet!");
        } else if(labels.getPriceMap().containsKey(postTweetTime)){
            // timestamp key exists in map --> return map value
            return labels.getPriceMap().get(postTweetTime);
        } else if(MARKET_HOLIDAY.contains(postTweetTime.toLocalDate())){
            // timestamp key does not exist in map because it falls on a market holiday --> try the next days opening price
            return getPrice20MinsAfterTweet(labels, ZonedDateTime.of(LocalDateTime.of(postTweetTime.toLocalDate().plusDays(1), BMW_XETRA_OPEN.minusMinutes(30)), ZoneId.of("Europe/London")));
        } else if (postTweetTime.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
            // timestamp key does not exist in map because it falls on a Saturday  --> try the opening price in two days time
            return getPrice20MinsAfterTweet(labels, ZonedDateTime.of(LocalDateTime.of(postTweetTime.toLocalDate().plusDays(2), BMW_XETRA_OPEN.minusMinutes(30)), ZoneId.of("Europe/London")));
        } else if (postTweetTime.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
            // timestamp key does not exist in map because it falls on a Sunday  --> try the next days opening price
            return getPrice20MinsAfterTweet(labels, ZonedDateTime.of(LocalDateTime.of(postTweetTime.toLocalDate().plusDays(1), BMW_XETRA_OPEN.minusMinutes(30)), ZoneId.of("Europe/London")));
        } else {
            // exception situation in which timestamp does not appear in priceLabel corpus but should be in the corpus --> try the next minute
            return getPriorPrices(labels, postTweetTime.minusMinutes(19));
        }
    }

    /**
     * takes the timestamp of a tweet and returns the time stamp of the last price print before the tweet,
     * taking into consideration whether or not the tweet was published outside of market trading hours.
     * @param tweetTimeStamp
     * @return time stamp of the last price print before the tweet
     */
    private ZonedDateTime lastTimePrintBeforeTweet(ZonedDateTime tweetTimeStamp){
        if(tweetTimeStamp.toLocalTime().compareTo(BMW_XETRA_OPEN) <= 0){
            // time stamp of the tweet occurs on or before market open --> return the previous day's closing price
            return ZonedDateTime.of(BMW_XETRA_CLOSE.atDate(tweetTimeStamp.toLocalDate().minusDays(1)), ZoneId.of("Europe/London"));
        } else if(tweetTimeStamp.toLocalTime().compareTo(BMW_XETRA_CLOSE) > 0){
            // time stamp of the tweet occurs after the market close --> return today's closing price
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
    private ZonedDateTime getTimeAfterTweet(ZonedDateTime tweetTimeStamp){
        if(tweetTimeStamp.toLocalTime().plusMinutes(20).compareTo(BMW_XETRA_OPEN) < 0){
            // time stamp of the tweet occurs more than 20 minutes prior to the market open --> return today's open price
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
        Iterator<Tweet> corpusIterator = corpus.iterator();
        while(corpusIterator.hasNext()){
            Tweet focus = corpusIterator.next();
            String tText = focus.getTweetText();
            if(tText.charAt(0) == 'r' && tText.charAt(1) == 't'){
                corpusIterator.remove();
            }
        }
    }

    public void replaceLinks(){
        Iterator<Tweet> corpusIterator = corpus.iterator();
        while(corpusIterator.hasNext()){
            Tweet focus = corpusIterator.next();
            String tText = focus.getTweetText();
            Scanner scanText = new Scanner(tText);
            String replacementText = "";
            while (scanText.hasNext()){
                String focusWord = scanText.next();
                if(focusWord.startsWith("http://")){
                    replacementText += LINK_EQUIVALENCE_TOKEN + " ";
                } else {
                    replacementText += focusWord + " ";
                }
            }
            focus.setTweetText(replacementText);
        }
    }

    public void replaceUsernames(){
        Iterator<Tweet> corpusIterator = corpus.iterator();
        while(corpusIterator.hasNext()){
            Tweet focus = corpusIterator.next();
            String tText = focus.getTweetText();
            Scanner scanText = new Scanner(tText);
            String replacementText = "";
            while (scanText.hasNext()){
                String focusWord = scanText.next();
                if(focusWord.startsWith("@")){
                    replacementText += USERNAME_EQUIVALENCE_TOKEN + " ";
                } else {
                    replacementText += focusWord + " ";
                }
            }
            focus.setTweetText(replacementText);
        }
    }

    public void translateAbbreviations(DictionaryTranslator abbreviationDict){
        Iterator<Tweet> corpusIterator = corpus.iterator();
        while(corpusIterator.hasNext()){
            Tweet focus = corpusIterator.next();
            String tText = focus.getTweetText();
            Scanner scanText = new Scanner(tText);
            String replacementText = "";
            while (scanText.hasNext()){
                String focusWord = scanText.next();
                if(checkSpelling(focusWord)){
                    replacementText += abbreviationDict.convertAbbrev(focusWord) + " ";
                } else if(abbreviationDict.convertAbbrev(focusWord) != null){
                    replacementText += focusWord + " ";
                }
            }
            focus.setTweetText(replacementText);
        }
    }

    public void checkSpelling(DictionaryTranslator spellingDict){
        return;
    }

}
