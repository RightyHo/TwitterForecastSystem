package twittercorpus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Created by Andrew on 18/06/15.
 */
public class TwitterCorpusListImpl implements TwitterCorpus {

    private static final String usernameEquivalenceToken = "USERNAME";
    private static final String linkEquivalenceToken = "LINK";
    private LocalTime bmwXetraOpen = LocalTime.of(8,0,0);		// London time
    private LocalTime bmwXetraClose = LocalTime.of(16,35,0);	// London time
    private LocalTime bmwUSOTCOpen = LocalTime.of(14,30,0);		// London time
    private LocalTime bmwUSOTCClose = LocalTime.of(21, 0, 0);	// London time

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
        return usernameEquivalenceToken;
    }

    public String getLinkEquivalenceToken() {
        return linkEquivalenceToken;
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
                Scanner s = new Scanner(currentLine);
                String dayOfTheWeek = s.next().trim();
                int month = getMonthNum(s.next());
                int dayNum = s.nextInt();
                String timeString = s.next().trim();
                int year =  s.nextInt();
                s.useDelimiter("\\z");
                String tweet =  s.next().trim();
                Scanner splitTime = new Scanner(timeString).useDelimiter(":");
                int hour = splitTime.nextInt();
                int min = splitTime.nextInt();
                int sec = splitTime.nextInt();

                // create new ZonedDateTime object for each row in the file

                LocalDateTime localTS = LocalDateTime.of(year, month, dayNum, hour, min, sec);
                ZonedDateTime ts = ZonedDateTime.of(localTS, ZoneId.of("Europe/London"));

                // check if the tweet was published during BMW stock market trading hours.
                // use OffsetTime class if subtraction doesn't work using LocalTime objects.

                boolean tsOutOfXetraMarketHours = (ts.toLocalTime().compareTo(bmwXetraOpen) < 0
                        || ts.toLocalTime().compareTo(bmwXetraClose) > 0);

                boolean tsOutOfUSOTCMarketHours = (ts.toLocalTime().compareTo(bmwUSOTCOpen) < 0
                        || ts.toLocalTime().compareTo(bmwUSOTCClose) > 0);

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
            ZonedDateTime lastPrintBeforeTweet = null;
            ZonedDateTime twentyMinsAfterTweet = null;
            if(focusTS.minusMinutes(1).toLocalTime().compareTo(bmwXetraOpen) < 0){
                // time stamp of the tweet occurs before or on market open
                if(focusTS.toLocalTime().plusMinutes(20).compareTo(bmwXetraOpen) >= 0){
                    // time stamp of the tweet occurs within the 20 minutes prior to the market open --> compare previous day's close to the price 20 mins after the tweet is published
                    lastPrintBeforeTweet = ZonedDateTime.of(bmwXetraClose.atDate(focusTS.toLocalDate().minusDays(1)),ZoneId.of("Europe/London"));
                    twentyMinsAfterTweet = focusTS.plusMinutes(20);
                } else {
                    // time stamp of the tweet occurs more than 20 minutes prior to the market open --> compare previous day's close to the next day's open price
                    lastPrintBeforeTweet = ZonedDateTime.of(bmwXetraClose.atDate(focusTS.toLocalDate().minusDays(1)), ZoneId.of("Europe/London"));
                    twentyMinsAfterTweet = ZonedDateTime.of(bmwXetraOpen.atDate(focusTS.toLocalDate()), ZoneId.of("Europe/London"));
                }
            } else if(focusTS.plusMinutes(20).toLocalTime().compareTo(bmwXetraClose) > 0){
                // time stamp of the tweet occurs at a time later than 20 minutes before the market close
                if(focusTS.toLocalTime().compareTo(bmwXetraClose) > 0){
                    // time stamp of the tweet occurs after the market close --> compare  today's close to the next day's open price
                    lastPrintBeforeTweet = ZonedDateTime.of(bmwXetraClose.atDate(focusTS.toLocalDate()),ZoneId.of("Europe/London"));
                    twentyMinsAfterTweet = ZonedDateTime.of(bmwXetraOpen.atDate(focusTS.toLocalDate().plusDays(1)),ZoneId.of("Europe/London"));
                } else {
                    // time stamp of the tweet occurs within 20 minutes of the market close --> compare the price at time of the tweets release to the next day's open price
                    lastPrintBeforeTweet = focusTS.minusMinutes(1);
                    twentyMinsAfterTweet = ZonedDateTime.of(bmwXetraOpen.atDate(focusTS.toLocalDate().plusDays(1)),ZoneId.of("Europe/London"));
                }
            } else {
                // timestamp of the tweet occurs during normal market hours
                lastPrintBeforeTweet = focusTS.minusMinutes(1);
                twentyMinsAfterTweet = focusTS.plusMinutes(20);
            }
            PriceSnapshot openingSnap = labels.getPriceMap().get(lastPrintBeforeTweet);
            PriceSnapshot closingSnap = labels.getPriceMap().get(twentyMinsAfterTweet);
            focus.setInitialSnapshot(openingSnap);
            focus.setPostTweetSnapshot(closingSnap);
            focus.setIsLabelled(true);

            // compare the two market snapshots to discern the implied market sentiment of the tweet
            if(focus.getInitialSnapshot().getOpeningSharePrice() > focus.getPostTweetSnapshot().getClosingSharePrice()){
                focus.setSentiment(Sentiment.NEGATIVE);
            } else if(focus.getInitialSnapshot().getOpeningSharePrice() < focus.getPostTweetSnapshot().getClosingSharePrice()) {
                focus.setSentiment(Sentiment.POSITIVE);
            } else {
                focus.setSentiment(Sentiment.NEUTRAL);
            }
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
