package twittercorpus;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Created by Andrew on 16/06/15.
 */
public interface TwitterCorpus {

     String getUsernameEquivalenceToken();

     String getLinkEquivalenceToken();

     Map<ZonedDateTime, Tweet> getCorpus();

     void setCorpus(Map<ZonedDateTime, Tweet> corpus);

     String getFileName();

     void setFileName(String fileName);

     Tweet getFirstTweet();

     void setFirstTweet(Tweet firstTweet);

     void extractTweetsFromFile(String fileName);

    /**
     * takes a string depicting a calendar month and returns the corresponding month number
     * @param mth
     * @return
     */
     int getMonthNum(String mth);

     void labelCorpus(PriceLabelCorpus labels);

     void removeRetweets();

     void replaceLinks(String linkEquivalenceToken);

     void replaceUsernames(String usernameEquivalenceToken);

     void translateAbbreviations(DictionaryTranslator abbreviationDict);

     void checkSpelling(DictionaryTranslator spellingDict);
}
