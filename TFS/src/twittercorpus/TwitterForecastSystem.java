package twittercorpus;

/**
 * Created by Andrew on 25/07/15.
 */
public class TwitterForecastSystem {

    public static void main(String[] args) {
        TwitterForecastSystem tfs = new TwitterForecastSystem();
        tfs.launchSystem();
    }

    private void launchSystem() {
        // Build corpus of tweets
        String twitterFilename = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/Test Twitter Corpus Sample.txt";
        TwitterCorpus tCorpus = new TwitterCorpusListImpl(twitterFilename);
        tCorpus.extractTweetsFromFile(twitterFilename);

        // Tweets cleaning process
        tCorpus.removeRetweets();
        tCorpus.replaceLinks();
        tCorpus.replaceUsernames();
        tCorpus.translateAbbreviations();
        // Label the training data set
        String priceDataFilename = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/Test Price Data Sample.txt";
        PriceLabelCorpus pCorpus = new PriceLabelCorpusImpl(priceDataFilename);

    }
}