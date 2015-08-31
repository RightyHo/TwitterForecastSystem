package twittercorpus;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * Created by Andrew on 16/06/15.
 */
public interface TwitterCorpus {

    String getUsernameEquivalenceToken();

    String getLinkEquivalenceToken();

    List<Tweet> getCorpus();

    void setCorpus(List<Tweet> corpus);

    String getFileName();

    void setFileName(String fileName);

    void extractTweetsFromFile(String fileName);

    /**
     * takes a string depicting a calendar month and returns the corresponding month number
     *
     * @param mth
     * @return
     */
    int getMonthNum(String mth);

    void preProcessTwitterCorpus(DictionaryTranslator abbreviationDict, DictionaryTranslator spellingDict, DictionaryTranslator stopWordsDict, int numGrams, PriceLabelCorpus labels);

    void labelCorpus(PriceLabelCorpus labels, Tweet tw);

    void removeLinks(Tweet tw);

    /**
     * helper function to be called by cleanInputTweetData()
     *
     * @param tw
     */
    void removeUsernames(Tweet tw);

    /**
     * helper function to be called by cleanInputTweetData()
     *
     * @param abbreviationDict
     * @param tw
     */
    void translateAbbreviations(DictionaryTranslator abbreviationDict, Tweet tw);

    /**
     * helper function to be called by cleanInputTweetData()
     * filters out words that do not appear in our reference spelling dictionary.
     *
     * @param spellingDict
     * @param tw
     */
    void checkSpelling(DictionaryTranslator spellingDict, Tweet tw);

    /**
     * helper function to be called by cleanInputTweetData()
     * filters out the most common (and least informative) English words from the
     * text of each tweet.  This should help reduce noise when extracting features for classification.
     *
     * @param stopWordsDict
     * @param tw
     */
    void filterOutStopWords(DictionaryTranslator stopWordsDict, Tweet tw);

    /**
     * helper function to be called by cleanInputTweetData()
     * calls the extractNGramFeatures() method to
     * initialize the tweet ready for classification
     *
     * @param numGrams
     * @param tw
     */
    void extractFeatures(int numGrams, Tweet tw);
}