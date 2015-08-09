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

    private List<Tweet> corpus;
    private String fileName;
    private ZoneOffset timeZone;
    private Set<LocalDate> marketHoliday;
    private ZonedDateTime earliestCorpusTimeStamp;
    private ZonedDateTime latestCorpusTimeStamp;
    private LocalTime stockMarketOpenTime;
    private LocalTime stockMarketCloseTime;

    public static final String USERNAME_EQUIVALENCE_TOKEN = "USERNAME";
    public static final String LINK_EQUIVALENCE_TOKEN = "LINK";

    // constructor

    public TwitterCorpusListImpl(String fileName,
                                 ZoneOffset timeZone,
                                 Set<LocalDate> marketHoliday,
                                 ZonedDateTime earliestCorpusTimeStamp,
                                 ZonedDateTime latestCorpusTimeStamp,
                                 LocalTime stockMarketOpenTime,
                                 LocalTime stockMarketCloseTime){
        this.corpus = new ArrayList<>();
        this.fileName = fileName;
        this.timeZone = timeZone;
        this.marketHoliday = marketHoliday;
        this.earliestCorpusTimeStamp = earliestCorpusTimeStamp;
        this.latestCorpusTimeStamp = latestCorpusTimeStamp;
        this.stockMarketOpenTime = stockMarketOpenTime;
        this.stockMarketCloseTime = stockMarketCloseTime;
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

                // read date and time strings
                String dateString = s.next().trim();
                String timeString = s.next().trim();

                // read in the rest of the current line
                String tweet = "";
                while(s.hasNext()) {
                    tweet += s.next();
                    if(s.hasNext()){
                        tweet += " ";
                    }
                }

                Scanner splitDate = new Scanner(dateString).useDelimiter("/");
                int dayNum = splitDate.nextInt();
                int month = splitDate.nextInt();
                int year = splitDate.nextInt();

                Scanner splitTime = new Scanner(timeString).useDelimiter(":");
                int hour =  splitTime.nextInt();
                int min =  splitTime.nextInt();
                int sec = splitTime.nextInt();
                sec = 0;    // *** DO NOT ADD SECONDS TO THE TWITTER CORPUS TIME IN ORDER TO MAP CORRECTLY WITH MINUTE PRICE BARS ***

                // create new ZonedDateTime object for each row in the file
                LocalDateTime localTS = LocalDateTime.of(year, month, dayNum, hour, min, sec);
                ZonedDateTime ts = ZonedDateTime.of(localTS,timeZone);

                // check if the tweet was published during BMW stock market trading hours...*** not 100% sure if this is flag is necessary? ***
                boolean tsOutOfXetraMarketHours = (ts.toLocalTime().compareTo(stockMarketOpenTime) < 0
                        || ts.toLocalTime().compareTo(stockMarketCloseTime) > 0);

                // create new Tweet for every row in the file
                Tweet inputTweet = new TweetImpl(ts,tweet);
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

            if(focus.getInitialSnapshot().getOpeningSharePrice() > focus.getPostTweetSnapshot().getOpeningSharePrice()){
                focus.setSentiment(Sentiment.NEGATIVE);
            } else if(focus.getInitialSnapshot().getOpeningSharePrice() < focus.getPostTweetSnapshot().getOpeningSharePrice()) {
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

        // *** OPTIONAL TRACE FOR DEBUGGING ***
//        Set<ZonedDateTime> keyS= labels.getPriceMap().keySet();
//        for(ZonedDateTime k : keyS) {
//            System.out.println("PriceLabelCorpus Key: " + k);
//        }

        if (preTweetTime.compareTo(earliestCorpusTimeStamp) < 0){

            // *** OPTIONAL TRACE FOR DEBUGGING ***
//            System.out.println("LAST TIME CHECKED: " + preTweetTime.toString());

            // error situation
            throw new IllegalArgumentException("We have reached the start of the price label corpus without finding any relevant timestamp to match up with this particular tweet!");

        } else if(labels.getPriceMap().containsKey(preTweetTime)){

            // timestamp key exists in map --> return map value
            return labels.getPriceMap().get(preTweetTime);

        } else if(marketHoliday.contains(preTweetTime.toLocalDate())){

            // *** OPTIONAL TRACE FOR DEBUGGING ***
//            System.out.println("Market Holiday on: "+ preTweetTime.toString());

            // timestamp key does not exist in map because it falls on a market holiday --> try the previous days closing price
            return getPriorPrices(labels, ZonedDateTime.of(LocalDateTime.of(preTweetTime.toLocalDate().minusDays(1), stockMarketCloseTime.plusMinutes(1)), timeZone));

        } else if (preTweetTime.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {

            // *** OPTIONAL TRACE FOR DEBUGGING ***
//            System.out.println("Saturday: " + preTweetTime.toString());

            // timestamp key does not exist in map because it falls on a Saturday  --> try the previous days closing price
            return getPriorPrices(labels, ZonedDateTime.of(LocalDateTime.of(preTweetTime.toLocalDate().minusDays(1), stockMarketCloseTime.plusMinutes(1)), timeZone));

        } else if (preTweetTime.getDayOfWeek().equals(DayOfWeek.SUNDAY)){

            // *** OPTIONAL TRACE FOR DEBUGGING ***
//            System.out.println("Sunday: "+ preTweetTime);

            // timestamp key does not exist in map because it falls on a Sunday  --> try the closing price two days previous
            return getPriorPrices(labels, ZonedDateTime.of(LocalDateTime.of(preTweetTime.toLocalDate().minusDays(2), stockMarketCloseTime.plusMinutes(1)), timeZone));

        } else {

            // *** OPTIONAL TRACE FOR DEBUGGING ***
//            System.out.println("exception situation - timestamp does not appear in priceLabel corpus but should be in the corpus --> try the previous minute: "+preTweetTime.toString());

            // exception situation in which timestamp does not appear in priceLabel corpus but should be in the corpus --> try the previous minute
            return getPriorPrices(labels, preTweetTime.minusMinutes(1));

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

        if (postTweetTime.compareTo(latestCorpusTimeStamp) > 0){

            // *** OPTIONAL TRACE FOR DEBUGGING ***
//            System.out.println("LAST TIME CHECKED: " + postTweetTime.toString());

            // error situation
            throw new IllegalArgumentException("We have reached the end of the price label corpus without finding any relevant timestamp to match up with this particular tweet!");

        } else if(labels.getPriceMap().containsKey(postTweetTime)){

            // timestamp key exists in map --> return map value
            return labels.getPriceMap().get(postTweetTime);

        } else if(marketHoliday.contains(postTweetTime.toLocalDate())){

            // timestamp key does not exist in map because it falls on a market holiday --> try the next days opening price
            return getPrice20MinsAfterTweet(labels, ZonedDateTime.of(LocalDateTime.of(postTweetTime.toLocalDate().plusDays(1), stockMarketOpenTime.minusMinutes(21)), timeZone));

        } else if (postTweetTime.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {

            // timestamp key does not exist in map because it falls on a Saturday  --> try the opening price in two days time
            return getPrice20MinsAfterTweet(labels, ZonedDateTime.of(LocalDateTime.of(postTweetTime.toLocalDate().plusDays(2), stockMarketOpenTime.minusMinutes(21)), timeZone));

        } else if (postTweetTime.getDayOfWeek().equals(DayOfWeek.SUNDAY)){

            // timestamp key does not exist in map because it falls on a Sunday  --> try the next days opening price
            return getPrice20MinsAfterTweet(labels, ZonedDateTime.of(LocalDateTime.of(postTweetTime.toLocalDate().plusDays(1), stockMarketOpenTime.minusMinutes(21)), timeZone));

        } else {

            // exception situation in which timestamp does not appear in priceLabel corpus but should be in the corpus --> try the next minute
            return getPrice20MinsAfterTweet(labels, postTweetTime.minusMinutes(19));

        }
    }

    /**
     * takes the timestamp of a tweet and returns the time stamp of the last price print before the tweet,
     * taking into consideration whether or not the tweet was published outside of market trading hours.
     * @param tweetTimeStamp
     * @return time stamp of the last price print before the tweet
     */
    private ZonedDateTime lastTimePrintBeforeTweet(ZonedDateTime tweetTimeStamp){
        if(tweetTimeStamp.toLocalTime().compareTo(stockMarketOpenTime) < 0){

            // time stamp of the tweet occurs before market open --> return the previous day's closing price
            return ZonedDateTime.of(stockMarketCloseTime.atDate(tweetTimeStamp.toLocalDate().minusDays(1)),timeZone);

        } else if(tweetTimeStamp.toLocalTime().compareTo(stockMarketCloseTime) > 0){

            // time stamp of the tweet occurs after the market close --> return today's closing price
            return ZonedDateTime.of(stockMarketCloseTime.atDate(tweetTimeStamp.toLocalDate()), timeZone);

        } else {

            // timestamp of the tweet occurs during normal market hours
            return tweetTimeStamp;

        }
    }

    /**
     * takes the timestamp of a tweet and returns the time stamp of the price print 20 minutes after the tweet is published,
     * taking into consideration whether or not the tweet was published outside of market trading hours.
     * @param tweetTimeStamp
     * @return time stamp of the price print 20 minutes after the tweet is published
     */
    private ZonedDateTime getTimeAfterTweet(ZonedDateTime tweetTimeStamp){
        if(tweetTimeStamp.toLocalTime().plusMinutes(20).compareTo(stockMarketOpenTime) < 0){

            // time stamp of the tweet occurs more than 20 minutes prior to the market open --> return today's open price
            return ZonedDateTime.of(stockMarketOpenTime.atDate(tweetTimeStamp.toLocalDate()),timeZone);

        } else if(tweetTimeStamp.plusMinutes(20).toLocalTime().compareTo(stockMarketCloseTime) > 0){

            // time stamp of the tweet occurs at a time later than 20 minutes before the market close --> return the next day's open price
            return ZonedDateTime.of(stockMarketOpenTime.atDate(tweetTimeStamp.toLocalDate().plusDays(1)), timeZone);

        } else {

            // timestamp of the tweet occurs during normal market hours
            return tweetTimeStamp.plusMinutes(20);
            
        }
    }

    /**
     * iterate through the entire twitter corpus and run the various pre-processing cleaning steps on each tweet
     */
    public void cleanInputTweetData(DictionaryTranslator abbreviationDict, DictionaryTranslator spellingDict){
        Iterator<Tweet> corpusIterator = corpus.iterator();
        while(corpusIterator.hasNext()) {
            Tweet focus = corpusIterator.next();
            String tText = focus.getTweetText();
            if (!tText.isEmpty()) {
                removeLinks(focus);
                removeUsernames(focus);
                translateAbbreviations(abbreviationDict,focus);
                checkSpelling(spellingDict,focus);
            }
        }
    }

    /**
     * helper function to be called by cleanInputTweetData()
     * @param spellingDict
     * @param tw
     */
    public void checkSpelling(DictionaryTranslator spellingDict,Tweet tw){
        String replacementText = spellingDict.processString(tw.getTweetText());
        tw.setTweetText(replacementText.trim());
    }

    /**
     * helper function to be called by cleanInputTweetData()
     * @param abbreviationDict
     * @param tw
     */
    public void translateAbbreviations(DictionaryTranslator abbreviationDict,Tweet tw){
        String replacementText = abbreviationDict.processString(tw.getTweetText());
        tw.setTweetText(replacementText.trim());
    }

    /**
     * helper function to be called by cleanInputTweetData()
     * @param tw
     */
    public void removeUsernames(Tweet tw){
        Scanner scanText = new Scanner(tw.getTweetText());
        String replacementText = "";
        while (scanText.hasNext()) {
            String focusWord = scanText.next();
            if (!focusWord.startsWith("@")) {
                replacementText += focusWord + " ";
            }
        }
        tw.setTweetText(replacementText.trim());
    }

    /**
     * helper function to be called by cleanInputTweetData()
     * @param tw
     */
    public void removeLinks(Tweet tw){
        Scanner scanText = new Scanner(tw.getTweetText());
        String replacementText = "";
        while (scanText.hasNext()) {
            String focusWord = scanText.next();
            if (!focusWord.startsWith("http://") && !focusWord.startsWith("https://")) {
                replacementText += focusWord + " ";
            }
        }
        tw.setTweetText(replacementText.trim());
    }

    public void removeRetweets(){
        Iterator<Tweet> corpusIterator = corpus.iterator();
        while(corpusIterator.hasNext()){
            Tweet focus = corpusIterator.next();
            String tText = focus.getTweetText();
            if(!tText.isEmpty()) {
                if (tText.toLowerCase().charAt(0) == 'r' && tText.toLowerCase().charAt(1) == 't') {
                    corpusIterator.remove();
                }
            }
        }
    }

    public void removeLinks(){
        Iterator<Tweet> corpusIterator = corpus.iterator();
        while(corpusIterator.hasNext()){
            Tweet focus = corpusIterator.next();
            String tText = focus.getTweetText();
            if(!tText.isEmpty()) {
                Scanner scanText = new Scanner(tText);
                String replacementText = "";
                while (scanText.hasNext()) {
                    String focusWord = scanText.next();
                    if (!focusWord.startsWith("http://") && !focusWord.startsWith("https://")) {
                        replacementText += focusWord + " ";
                    }
                }
                focus.setTweetText(replacementText.trim());
            }
        }
    }

    public void replaceLinks(){
        Iterator<Tweet> corpusIterator = corpus.iterator();
        while(corpusIterator.hasNext()){
            Tweet focus = corpusIterator.next();
            String tText = focus.getTweetText();
            if(!tText.isEmpty()) {
                Scanner scanText = new Scanner(tText);
                String replacementText = "";
                while (scanText.hasNext()) {
                    String focusWord = scanText.next();
                    if (focusWord.startsWith("http://")) {
                        replacementText += LINK_EQUIVALENCE_TOKEN + " ";
                    } else {
                        replacementText += focusWord + " ";
                    }
                }
                focus.setTweetText(replacementText.trim());
            }
        }
    }

    public void removeUsernames(){
        Iterator<Tweet> corpusIterator = corpus.iterator();
        while(corpusIterator.hasNext()){
            Tweet focus = corpusIterator.next();
            String tText = focus.getTweetText();
            if(!tText.isEmpty()) {
                Scanner scanText = new Scanner(tText);
                String replacementText = "";
                while (scanText.hasNext()) {
                    String focusWord = scanText.next();
                    if (!focusWord.startsWith("@")) {
                        replacementText += focusWord + " ";
                    }
                }
                focus.setTweetText(replacementText.trim());
            }
        }
    }

    public void replaceUsernames(){
        Iterator<Tweet> corpusIterator = corpus.iterator();
        while(corpusIterator.hasNext()){
            Tweet focus = corpusIterator.next();
            String tText = focus.getTweetText();
            if(!tText.isEmpty()) {
                Scanner scanText = new Scanner(tText);
                String replacementText = "";
                while (scanText.hasNext()) {
                    String focusWord = scanText.next();
                    if (focusWord.startsWith("@")) {
                        replacementText += USERNAME_EQUIVALENCE_TOKEN + " ";
                    } else {
                        replacementText += focusWord + " ";
                    }
                }
                focus.setTweetText(replacementText.trim());
            }
        }
    }

    public void translateAbbreviations(DictionaryTranslator abbreviationDict){
        Iterator<Tweet> corpusIterator = corpus.iterator();
        while(corpusIterator.hasNext()){
            Tweet focus = corpusIterator.next();
            String tText = focus.getTweetText();
            if(!tText.isEmpty()) {
                String replacementText = abbreviationDict.processString(tText);
                focus.setTweetText(replacementText.trim());
            }
        }
    }

    /**
     * iterates through the text in the twitter corpus and filters out words that do not appear in our reference spelling dictionary.
     * @param spellingDict
     */
    public void checkSpelling(DictionaryTranslator spellingDict){
        Iterator<Tweet> corpusIterator = corpus.iterator();
        while(corpusIterator.hasNext()){
            Tweet focus = corpusIterator.next();
            String tText = focus.getTweetText();

            // *** OPTIONAL TRACE FOR DEBUGGING ***
//            System.out.println("PRE-PROCESS: "+tText);

            if(!tText.isEmpty()) {
                String replacementText = spellingDict.processString(tText);

                // *** OPTIONAL TRACE FOR DEBUGGING ***
//                System.out.println("POST-PROCESS:"+ replacementText);

                focus.setTweetText(replacementText.trim());
            }
        }
    }

    /**
     * iterates through the corpus of tweets calling the removeStopWords() method to
     * filter out the most common (and least informative) English words from the
     * text of each tweet.  This should help reduce noise when extracting features for classification.
     */
    public void filterOutStopWords(String stopWordsList){
        List<String> stopWords = new ArrayList<>();

        // extract list of stop words from file
        try(BufferedReader br = new BufferedReader(new FileReader(stopWordsList))){
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                Scanner s = new Scanner(currentLine);
                s.useDelimiter(",");
                while(s.hasNext()){
                    stopWords.add(s.next());
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        // iterate through all tweets and filter out words that appear in the stop words list
        Iterator<Tweet> corpusIterator = corpus.iterator();
        while(corpusIterator.hasNext()) {
            Tweet focus = corpusIterator.next();
            String tText = focus.getTweetText();
            StringBuilder revisedTweetText = new StringBuilder();

            Scanner textScan = new Scanner(tText);
            while (textScan.hasNext()) {
                String word = textScan.next();
                if (!stopWords.contains(word)) {
                    revisedTweetText.append(" " + word);
                }
            }
            focus.setTweetText(revisedTweetText.toString().trim());
        }
    }

    /**
     * iterates through the corpus of tweets calling the extractNGramFeatures() method to
     * initialize the tweet ready for classification
     * @param numGrams
     */
    public void extractFeatures(int numGrams){
        Iterator<Tweet> corpusIterator = corpus.iterator();
        while(corpusIterator.hasNext()){
            Tweet focus = corpusIterator.next();
            focus.extractNGramFeatures(numGrams);
        }
    }

    /**
     *  if the tweet cleaning and filter process results in a tweet with no features, remove the tweet from the corpus
     */
    public void removeFilteredTweetsWithNoFeatures(){
        Iterator<Tweet> corpusIterator = corpus.iterator();
        while(corpusIterator.hasNext()){
            Tweet focus = corpusIterator.next();
            if(focus.getFeatures().isEmpty()){
                corpusIterator.remove();
            }
        }
    }
}
