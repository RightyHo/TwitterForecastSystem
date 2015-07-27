package twittercorpus;

import java.util.List;

/**
 * Created by Andrew on 25/07/15.
 */
public class TwitterForecastSystem {
    private List<Tweet> trainingData = null;
    private List<Tweet> testData = null;
    private Classifier classifier = null;

    public static void main(String[] args) {
        TwitterForecastSystem tfs = new TwitterForecastSystem();
        tfs.launchSystem();
    }

    public void launchSystem() {
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

        // Label the entire data set
        String priceDataFilename = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/Test Price Data Sample.txt";
        PriceLabelCorpus pCorpus = new PriceLabelCorpusImpl(priceDataFilename);
        pCorpus.extractPriceDataFromFile(priceDataFilename);
        tCorpus.labelCorpus(pCorpus);

        // Train the TFS classifier on the labelled data set
        splitUpTrainingAndTestData(tCorpus);
        classifier = new NaiveBayesClassifier(1000);     // set storage limit to adjust for forgetful learning effect
        trainTFS();

        // Classify the test data set
        setTestDataSentimentToUnclassified();
        classifyTestData();

        // Analyse Results
        PredictionStatistics pStats = new PredictionStatisticsImpl();
        pStats.calculateTFSAccuracy(testData);
        pStats.calculateMACDAccuracy(testData);
        /*
        String swnFileName = "";
        SentiWordNet swn = new SentiWordNet();
        getSentiWordNetPredictions(swn);
         */
    }

    /**
     * splits the total data set into a training set containing 80% of the overall tweets and a test set containing the remaining 20%
     * The tweets will be split in chronological order with the first 80% of tweets being used at the training set
     * @param tc
     */
    public void splitUpTrainingAndTestData(TwitterCorpus tc){
        int totalNumTweets = tc.getCorpus().size();
        int sizeOfTrainingSet = (int) (totalNumTweets * 0.8);       // allocate 80% of the data set to the training set
        System.out.println("Total number of Tweets = "+ totalNumTweets);

        trainingData = tc.getCorpus().subList(0,sizeOfTrainingSet);
        testData = tc.getCorpus().subList(sizeOfTrainingSet,totalNumTweets);
        System.out.println("Size of training data list = " +trainingData.size());
        System.out.println("Size of test data list = " + testData.size());
    }

    public void trainTFS(){
        for(Tweet t : trainingData){
            if(!t.isLabelled()){
                throw new IllegalArgumentException("Cannot train the TFS on a tweet that has not been labelled yet!");
            } else if(t.getSentiment() == Sentiment.NEGATIVE){
                classifier.learn(new ClassificationImpl(t.getFeatures(),Sentiment.NEGATIVE,1.0));   // setting classification certainty to 1.0 because we know that the price went down after the tweet was published
            } else if(t.getSentiment() == Sentiment.POSITIVE){
                classifier.learn(new ClassificationImpl(t.getFeatures(),Sentiment.POSITIVE,1.0));   // setting classification certainty to 1.0 because we know that the price went up after the tweet was published
            } else if(t.getSentiment() == Sentiment.NEUTRAL){
                classifier.learn(new ClassificationImpl(t.getFeatures(),Sentiment.NEUTRAL,1.0));   // setting classification certainty to 1.0 because we know that the price was unchanged after the tweet was published
            } else {
                throw new IllegalArgumentException("Cannot train the TFS on a tweet whose sentiment is unclassified!");
            }
        }
    }

    public void setTestDataSentimentToUnclassified(){
        for(Tweet t : testData){
            t.setSentiment(Sentiment.UNCLASSIFIED);
        }
    }

    public void classifyTestData(){
        for(Tweet t : testData) {
            Classification freshClassification = classifier.classify(t.getFeatures());
            t.setSentiment(freshClassification.getSentimentCategory());
        }
    }

    public void getSentiWordNetPredictions(SentiWordNet swn){
        for(Tweet t : testData){
            t.setSentiWordNetClassification(swn.classify(t.getFeatures()));
        }


    }
}
