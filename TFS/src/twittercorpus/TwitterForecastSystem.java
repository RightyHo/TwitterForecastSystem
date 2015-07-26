package twittercorpus;

import java.util.List;

/**
 * Created by Andrew on 25/07/15.
 */
public class TwitterForecastSystem {
    private List<Tweet> trainingData = null;
    private List<Tweet> testData = null;

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
        tCorpus.translateAbbreviations(new AbbreviationDictionary());
        tCorpus.checkSpelling(new SpellingDictionary());
        tCorpus.filterOutStopWords();
        tCorpus.extractFeatures(1);         // extract uni-grams as features change to 2 for bi-grams

        // Label the training data set
        String priceDataFilename = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/Test Price Data Sample.txt";
        PriceLabelCorpus pCorpus = new PriceLabelCorpusImpl(priceDataFilename);
        pCorpus.extractPriceDataFromFile(priceDataFilename);
        tCorpus.labelCorpus(pCorpus);

        // Train the TFS classifier on the labelled data set
        splitUpTrainingAndTestData(tCorpus);
    }

    private void splitUpTrainingAndTestData(TwitterCorpus tc){
        int totalNumTweets = tc.getCorpus().size();
        int sizeOfTrainingSet = (int) (totalNumTweets * 0.8);
        trainingData = tc.getCorpus().subList(0,sizeOfTrainingSet);
        testData = tc.getCorpus().subList(sizeOfTrainingSet + 1,totalNumTweets);
    }
}