package twittercorpus;

/**
 * Created by Andrew on 16/06/15.
 */
public interface TwitterCorpus {

    /*
     *  Extract raw tweets from twitter corpus file and store them ready for processing
     */
    void extractTweetsFromFile(String fileName);

    void labelCorpus(PriceLabelCorpus labels);

    void removeRetweets();

    void replaceLinks(String linkEquivalenceToken);

    void replaceUsernames(String usernameEquivalenceToken);

    void translateAbbreviations(DictionaryTranslator abbreviationDict);

    void checkSpelling(DictionaryTranslator spellingDict);
}
