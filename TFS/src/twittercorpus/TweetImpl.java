package twittercorpus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Andrew on 08/06/15.
 */
public class TweetImpl implements Tweet {

    private ZonedDateTime timeStamp;
    private boolean isRetweet;
    private boolean isLabelled;
    private String tweetText;       // split into three variables to separate unabbreviated and spell checked states?
    private PriceSnapshot initialSnapshot;	// last price snapshot before the tweet was published
    private PriceSnapshot postTweetSnapshot;	// price snapshot 20 minutes after the tweet was published
    private Tweet nextTweet;
    private Sentiment sentiment;    // sentiment object is initially set to unclassified.  Once classified it is set to positive, negative or neutral.
    private List<String> features;

    // constructors

    public TweetImpl(ZonedDateTime timeStamp, String tweetText) {
        this.timeStamp = timeStamp;
        isRetweet = false;
        isLabelled = false;
        this.tweetText = tweetText;
        initialSnapshot = null;
        postTweetSnapshot = null;
        nextTweet = null;
        sentiment = Sentiment.UNCLASSIFIED;
        features = new ArrayList<>();
    }

    public TweetImpl(ZonedDateTime timeStamp, boolean isRetweet, boolean isLabelled, String tweetText,PriceSnapshot initialSnapshot, PriceSnapshot postTweetSnapshot, Tweet nextTweet, Sentiment sentiment) {
        this.timeStamp = timeStamp;
        this.isRetweet = isRetweet;
        this.isLabelled = isLabelled;
        this.tweetText = tweetText;
        this.initialSnapshot = initialSnapshot;
        this.postTweetSnapshot = postTweetSnapshot;
        this.nextTweet = nextTweet;
        this.sentiment = sentiment;
        features = new ArrayList<>();
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

    public String getTweetText() {
        return tweetText;
    }

    public void setTweetText(String tweetText) {
        this.tweetText = tweetText;
    }

    public PriceSnapshot getInitialSnapshot() {
        return initialSnapshot;
    }

    public void setInitialSnapshot(PriceSnapshot initialSnapshot) {
        this.initialSnapshot = initialSnapshot;
    }

    public PriceSnapshot getPostTweetSnapshot() {
        return postTweetSnapshot;
    }

    public void setPostTweetSnapshot(PriceSnapshot postTweetSnapshot) {
        this.postTweetSnapshot = postTweetSnapshot;
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

    public void removeStopWords(String fileName){
        List<String> stopWords = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(fileName))){
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
        Scanner textScan = new Scanner(tweetText);
        StringBuilder revisedTweetText = new StringBuilder();
        while (textScan.hasNext()){
            String focus = textScan.next();
            if(!stopWords.contains(focus)){
                revisedTweetText.append(" " + focus);
            }
        }
        tweetText = revisedTweetText.toString().trim();
    }

    /**
     * takes the number of ngrams and draws in a tokenized form of the tweet text as input and returns a list of features separated by comma's
     * on which to train the TFS classifier
     * @param numGrams
     */
    public void extractNGramFeatures(int numGrams){
        features.clear();
        String[] tokens = tokenizeString(tweetText);
        int len = tokens.length;
        int focus = 0;
        while(focus + numGrams <= len) {
            StringBuilder s = new StringBuilder();
            for (int i = focus; i < focus + numGrams; i++) {
                if (i > focus) {
                    s.append("," + tokens[i]);
                } else {
                    s.append(tokens[i]);
                }
            }
            features.add(s.toString());
            focus++;
        }
    }

    /**
     * returns a tokenized form of the input string
     * @param input
     * @return
     */
    private String[] tokenizeString(String input){
        return input.split("\\s");
    }

    public List<String> getFeatures(){
        return this.features;
    }
}
