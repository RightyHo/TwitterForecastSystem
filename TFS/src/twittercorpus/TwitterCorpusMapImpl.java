package twittercorpus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andrew on 16/06/15.
 */
public class TwitterCorpusMapImpl implements TwitterCorpus {
    private static final String usernameEquivalenceToken = "USERNAME";
    private static final String linkEquivalenceToken = "LINK";
    private Map<ZonedDateTime,Tweet> corpus;
    private String fileName;
    private Tweet firstTweet;

    public TwitterCorpusMapImpl(String fileName) {
        this.corpus = new HashMap<ZonedDateTime, Tweet>();
        this.fileName = fileName;
        this.firstTweet = null;
    }
    public TwitterCorpusMapImpl(Map<ZonedDateTime, Tweet> corpus, String fileName, Tweet firstTweet) {
        this.corpus = corpus;
        this.fileName = fileName;
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
                System.out.println("day of the week is "+ dayOfTheWeek +" : month is "+ month +" : day number is "+ dayNum + " : time of day is "+ timeOfDay + " : year is "+ year +" : string text is --> "+ tweet);

                // create new ZonedDateTime object for each row in the file

                //--ZonedDateTime ts = ZonedDateTime.parse();
                //--ZonedDateTime dateTime = ZonedDateTime.parse("2007-12-03T10:15:30+01:00[Europe/Paris]");

                // check if the tweet was published during BMW stock market trading hours

                //--boolean tsOutOfMarketHours = if()

                // create new Tweet for every row in the file

                //--Tweet inputTweet = new TweetImpl(ts,tsOutOfMarketHours,tweet);
                //--corpus.put(ts,inputTweet);
            }
        } catch (IOException e){
            e.printStackTrace();
        }

    }

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
