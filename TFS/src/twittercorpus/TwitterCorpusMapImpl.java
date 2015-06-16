package twittercorpus;

import java.time.ZonedDateTime;
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

    public void extractTweetsFromFile(String fileName){

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
