package twittercorpus;

/**
 * Created by Andrew on 25/07/15.
 */
public class TwitterForecastSystem {

    public static void main(String[] args) {
        TwitterForecastSystem tfs = new TwitterForecastSystem();
        tfs.launchSystem();
    }

    private void launchSystem(){
        String twitterFilename = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/Test Twitter Corpus Sample.txt";
        TwitterCorpus tc = new TwitterCorpusListImpl(twitterFilename);
        tc.extractTweetsFromFile(twitterFilename);
        String priceDataFilename = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/Test Price Data Sample.txt";
        PriceLabelCorpus plCorpus = new PriceLabelCorpusImpl(priceDataFilename);
    }
}
