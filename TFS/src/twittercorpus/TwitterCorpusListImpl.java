package twittercorpus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Andrew on 18/06/15.
 */
public class TwitterCorpusListImpl implements TwitterCorpus {

    private static final String usernameEquivalenceToken = "USERNAME";
    private static final String linkEquivalenceToken = "LINK";
    private List<Tweet> corpus;
    private String fileName;
    private Tweet firstTweet;

    public TwitterCorpusListImpl(String fileName) {
        this.corpus = new ArrayList<>();
        this.fileName = fileName;
        this.firstTweet = null;
    }
    public TwitterCorpusListImpl(List<Tweet> corpus, String fileName, Tweet firstTweet) {
        this.corpus = corpus;
        this.fileName = fileName;
        this.firstTweet = firstTweet;
    }

    public String getUsernameEquivalenceToken() {
        return usernameEquivalenceToken;
    }

    public String getLinkEquivalenceToken() {
        return linkEquivalenceToken;
    }

    public Map<ZonedDateTime, Tweet> getCorpus() {
        return corpus;
    }

    public void setCorpus(Map<ZonedDateTime, Tweet> corpus) {
        this.corpus = corpus;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Tweet getFirstTweet() {
        return firstTweet;
    }

    public void setFirstTweet(Tweet firstTweet) {
        this.firstTweet = firstTweet;
    }

    public void extractTweetsFromFile(String fileName){
        try(BufferedReader br = new BufferedReader(new FileReader(fileName))){
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                // System.out.println(currentLine);
                String dayOfTheWeek = currentLine.substring(0, 3);
                String month = currentLine.substring(4, 7);
                int dayNum = Integer.parseInt(currentLine.substring(8, 10));
                int hour = Integer.parseInt(currentLine.substring(11, 13));
                int min = Integer.parseInt(currentLine.substring(14, 16));
                int sec = Integer.parseInt(currentLine.substring(17, 19));
                int year =  Integer.parseInt(currentLine.substring(20, 25));
                String tweet =  currentLine.substring(25);
                System.out.println("day of the week is "+ dayOfTheWeek +" : month is "+ month +" : day number is "+ dayNum + " : time of day is "+ hour +":" + min +":" + sec + " : year is "+ year +" : string text is --> "+ tweet);

                // create new ZonedDateTime object for each row in the file

                LocalDateTime localTS = LocalDateTime.of(year, dayNum, hour, min, sec);
                ZonedDateTime ts = ZonedDateTime.of(localTS, ZoneId.of("Europe/London"));

                // check if the tweet was published during BMW stock market trading hours.
                // use OffsetTime class if subtraction doesn't work using LocalTime objects.

                LocalTime bmwXetraOpen = LocalTime.of(8,0,0);		// London time
                LocalTime bmwXetraClose = LocalTime.of(16,35,0);	// London time
                LocalTime bmwUSOTCOpen = LocalTime.of(14,30,0);		// London time
                LocalTime bmwUSOTCClose = LocalTime.of(21, 0, 0);		// London time

                boolean tsOutOfXetraMarketHours = (ts.toLocalTime().compareTo(bmwXetraOpen) < 0
                        || ts.toLocalTime().compareTo(bmwXetraClose) > 0)

                boolean tsOutOfUSOTCMarketHours = (ts.toLocalTime().compareTo(bmwUSOTCOpen) < 0
                        || ts.toLocalTime().compareTo(bmwUSOTCClose) > 0)

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

    public void labelCorpus(PriceLabelCorpus labels){

    }

    public void removeRetweets(){

    }

    public void replaceLinks(String linkEquivalenceToken){

    }

    public void replaceUsernames(String usernameEquivalenceToken){

    }

    public void translateAbbreviations(DictionaryTranslator abbreviationDict){

    }

    public void checkSpelling(DictionaryTranslator spellingDict){

    }

}
