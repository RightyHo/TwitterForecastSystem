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
                System.out.println(currentLine);
            }
        } catch (IOException e){
            e.printStackTrace();
        }

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
