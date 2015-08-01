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
     * @param mth
     * @return
     */
     int getMonthNum(String mth);

     void labelCorpus(PriceLabelCorpus labels);

     void removeRetweets();

     void replaceLinks();

     void replaceUsernames();

     void translateAbbreviations(DictionaryTranslator abbreviationDict);

     void checkSpelling(DictionaryTranslator spellingDict);

     void filterOutStopWords();

     void extractFeatures(int numGrams);

     /**
     *  if the tweet cleaning and filter process results in a tweet with no features, remove the tweet from the corpus
     */
     void removeFilteredTweetsWithNoFeatures();
}
