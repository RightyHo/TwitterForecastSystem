package twittercorpus;

import java.time.ZoneOffset;
import java.util.List;

/**
 * Created by Andrew on 25/07/15.
 */
public class TwitterForecastSystem {

    public static final String TWITTER_CORPUS_FILENAME = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/Test Twitter Corpus Sample.txt";
    public static final String PRICE_DATA_FILENAME = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/Test Price Data Sample.txt";
    public static final String SENTIWORDNET_FILENAME = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/SentiWordNet_3.0.txt";
    public static final String ABBREVIATION_DICTIONARY_FILENAME = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/Twerminology - 100 Twitter Slang Words & Abbreviations.txt";
    public static final String SPELLING_DICTIONARY_FILENAME = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/TwitterForecastSystem/TFS/dictionary.txt";
    public static final String STOP_WORDS_FILENAME = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/TwitterForecastSystem/TFS/English Stop Words.txt";

    public static final int NGRAM_COUNT = 1;                        // set to 1 to use unigrams or 2 to use bigrams as features for the classifier
    public static final int CLASSIFIER_STORAGE_LIMIT = 1000;        // set storage limit to adjust for forgetful learning effect
    public static final ZoneOffset TIME_ZONE = ZoneOffset.of("Z");  // set time zone for date information used in the TFS to GMT/UTC
    public static final int MILLENIUM = 0;                          // needs to be changed to 2000 if the date format of input data is 23/11/15

    private List<Tweet> trainingData = null;
    private List<Tweet> testData = null;
    private Classifier classifier = null;

    public static void main(String[] args) {
        TwitterForecastSystem tfs = new TwitterForecastSystem();
        tfs.launchSystem();
    }

    public void launchSystem() {
        // Build corpus of tweets

        TwitterCorpus tCorpus = new TwitterCorpusListImpl(TWITTER_CORPUS_FILENAME);
        tCorpus.extractTweetsFromFile(TWITTER_CORPUS_FILENAME);
        System.out.println("\n******************************************************************************************");
        System.out.println("\nExtracted Twitter corpus from file.");
        System.out.println("--> Corpus contains a total of " + tCorpus.getCorpus().size() + " tweets.");

        // Tweets cleaning process
        tCorpus.removeRetweets();
        System.out.println("Removed Retweets from the Twitter corpus.");
        System.out.println("--> Number of tweets remaining in corpus: " + tCorpus.getCorpus().size());
        tCorpus.replaceLinks();     // *** CONSIDER DELETING LINKS FROM THE TWEET TEXT INSTEAD OF SUBSTITUTING IT FOR A TOKEN AS WE DO CURRENTLY ***
        tCorpus.replaceUsernames(); // *** CONSIDER DELETING LINKS FROM THE TWEET TEXT INSTEAD OF SUBSTITUTING IT FOR A TOKEN AS WE DO CURRENTLY ***
        tCorpus.translateAbbreviations(new AbbreviationDictionary(ABBREVIATION_DICTIONARY_FILENAME));
        tCorpus.checkSpelling(new SpellingDictionary(SPELLING_DICTIONARY_FILENAME));
        tCorpus.filterOutStopWords(STOP_WORDS_FILENAME);
        tCorpus.extractFeatures(NGRAM_COUNT);         // extract uni-grams as features change to 2 for bi-grams
        tCorpus.removeFilteredTweetsWithNoFeatures(); // if the tweet cleaning and filter process results in a tweet with no features, remove the tweet from the corpus
        System.out.println("Removed mispelled words and 'stop' words.");
        System.out.println("Removed tweets with no features remaining from the Twitter corpus.");
        System.out.println("--> Number of tweets remaining in corpus after filtering process: " + tCorpus.getCorpus().size());

        // Label the entire data set
        PriceLabelCorpus pCorpus = new PriceLabelCorpusImpl(PRICE_DATA_FILENAME,TIME_ZONE,MILLENIUM);
        pCorpus.extractPriceDataFromFile(PRICE_DATA_FILENAME);
        tCorpus.labelCorpus(pCorpus);
        System.out.println("\n******************************************************************************************");
        System.out.println("\nLabelled the Twitter corpus with stock market price data.");

        // Train the TFS classifier on the labelled data set
        splitUpTrainingAndTestData(tCorpus);
        classifier = new NaiveBayesClassifier(CLASSIFIER_STORAGE_LIMIT);     // set storage limit to adjust for forgetful learning effect
        trainTFS();
        System.out.println("\n******************************************************************************************");
        System.out.println("\nTrained the Twitter Forecast System on the labelled training data corpus of tweets.");

        // Classify the test data set
        setTestDataSentimentToUnclassified();
        classifyTestData();
        System.out.println("\n******************************************************************************************");
        System.out.println("\nClassified the test data corpus tweets of tweets using the knoweldge base of the TFS.");

        // Analyse Results
        PredictionStatistics pStats = new PredictionStatisticsImpl();
        pStats.calculateTFSAccuracy(testData);
        pStats.calculateMACDAccuracy(testData);

        SentiWordNet swn = new SentiWordNetImpl(SENTIWORDNET_FILENAME);
        getSentiWordNetPredictions(swn);
        pStats.calculateSentiWordNetAccuracy(testData);
        pStats.printResults();
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
            t.setSentiWordNetClassification(swn.classifySentiment(t.getFeatures()));
        }
    }
}
