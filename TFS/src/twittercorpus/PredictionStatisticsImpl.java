package twittercorpus;

import java.util.List;

/**
 * Created by Andrew on 27/07/15.
 */
public class PredictionStatisticsImpl implements PredictionStatistics {
    private int sizeOfTestDataSet;
    private boolean madeTFSCalcs;
    private boolean madeMACDCalcs;
    private boolean madeSentiWordNetCalcs;
    private int correctTFSCount;
    private int incorrectTFSCount;
    private int correctMACDCount;
    private int incorrectMACDCount;
    private int correctSentiWordNetCount;
    private int incorrectSentiWordNetCount;

    public PredictionStatisticsImpl(){
        sizeOfTestDataSet = 0;
        madeTFSCalcs = false;
        madeMACDCalcs = false;
        madeSentiWordNetCalcs = false;
        correctTFSCount = 0;
        incorrectTFSCount = 0;
        correctMACDCount = 0;
        incorrectMACDCount = 0;
        correctSentiWordNetCount = 0;
        incorrectSentiWordNetCount = 0;
    }

    public void calculateTFSAccuracy(List<Tweet> testData){
        if(testData == null) throw new IllegalArgumentException("Test data list is null!");
        sizeOfTestDataSet = testData.size();

        // reset counts
        correctTFSCount = 0;
        incorrectTFSCount = 0;

        // compare each tweets classification with the actual price move during the twenty minutes after the tweet is published
        for(Tweet t : testData){
            if(t == null) throw new NullPointerException("testData is returning null tweets!");
            if(t.getSentiment() == Sentiment.NEGATIVE){
                if(t.getPostTweetSnapshot().getClosingSharePrice() < t.getInitialSnapshot().getClosingSharePrice()){
                    correctTFSCount++;
                } else {
                    incorrectTFSCount++;
                }
            } else if(t.getSentiment() == Sentiment.POSITIVE){
                if(t.getPostTweetSnapshot().getClosingSharePrice() > t.getInitialSnapshot().getClosingSharePrice()){
                    correctTFSCount++;
                } else {
                    incorrectTFSCount++;
                }
            } else if(t.getSentiment() == Sentiment.NEUTRAL){
                // If time permits it may be worth experimenting by widening a range of prices to fall under neutral category
                if(t.getPostTweetSnapshot().getClosingSharePrice() == t.getInitialSnapshot().getClosingSharePrice()){
                    correctTFSCount++;
                } else {
                    incorrectTFSCount++;
                }
            } else {
                throw new IllegalArgumentException("The test data has not been correctly classified");
            }
        }
        madeTFSCalcs = true;
    }

    public void calculateMACDAccuracy(List<Tweet> testData){
        if(testData == null) throw new IllegalArgumentException("Test data list is null!");
        sizeOfTestDataSet = testData.size();

        // reset counts
        correctMACDCount = 0;
        incorrectMACDCount = 0;

        // compare the MACD direction indicator prior to the release of each tweet with the actual price move during the twenty minutes after the tweet is published
        for(Tweet t : testData){
            if(t == null) throw new NullPointerException("testData is returning null tweets!");
            // Signal Line Crossovers:  MACD Level minus Signal Level.  A positive signal indicates upward price momentum and vice versa
            if(t.getInitialSnapshot().getOpeningMACDDirectionSignal() > 0){
                if(t.getPostTweetSnapshot().getClosingSharePrice() > t.getInitialSnapshot().getClosingSharePrice()){
                    correctMACDCount++;
                } else {
                    incorrectMACDCount++;
                }
            } else if(t.getInitialSnapshot().getOpeningMACDDirectionSignal() < 0){
                if(t.getPostTweetSnapshot().getClosingSharePrice() < t.getInitialSnapshot().getClosingSharePrice()){
                    correctMACDCount++;
                } else {
                    incorrectMACDCount++;
                }
            } else {
                // Sentiment is NEUTRAL - if time permits it may be worth experimenting by widening a range of prices to fall under neutral category
                if(t.getPostTweetSnapshot().getClosingSharePrice() == t.getInitialSnapshot().getClosingSharePrice()){
                    correctMACDCount++;
                } else {
                    incorrectMACDCount++;
                }
            }
        }
        madeMACDCalcs = true;
    }

    public void calculateSentiWordNetAccuracy(List<Tweet> testData){
        if(testData == null) throw new IllegalArgumentException("Test data list is null!");
        sizeOfTestDataSet = testData.size();

        // reset counts
        correctSentiWordNetCount = 0;
        incorrectSentiWordNetCount = 0;

        // compare the SentiWordNet3.0 direction indicator prior to the release of each tweet with the actual price move during the twenty minutes after the tweet is published
        for(Tweet t : testData){
            if(t == null) throw new NullPointerException("testData is returning null tweets!");
            // Get the sentiment classification attributed to the Tweet's list of features by SentiWordNet3.0
            if(t.getSentiWordNetClassification() == Sentiment.POSITIVE){
                if(t.getPostTweetSnapshot().getClosingSharePrice() > t.getInitialSnapshot().getClosingSharePrice()){
                    correctSentiWordNetCount++;
                } else {
                    incorrectSentiWordNetCount++;
                }
            } else if(t.getSentiWordNetClassification() == Sentiment.NEGATIVE){
                if(t.getPostTweetSnapshot().getClosingSharePrice() < t.getInitialSnapshot().getClosingSharePrice()){
                    correctSentiWordNetCount++;
                } else {
                    incorrectSentiWordNetCount++;
                }
            } else if(t.getSentiWordNetClassification() == Sentiment.NEUTRAL){
                // If time permits it may be worth experimenting by widening a range of prices to fall under neutral category
                if(t.getPostTweetSnapshot().getClosingSharePrice() == t.getInitialSnapshot().getClosingSharePrice()){
                    correctSentiWordNetCount++;
                } else {
                    incorrectSentiWordNetCount++;
                }
            } else {
                throw new IllegalArgumentException("The test data has not been correctly classified");
            }
        }
        madeSentiWordNetCalcs = true;
    }

    public void printResults(){
        System.out.println("Size of Test Data Set:  " + sizeOfTestDataSet);
        if(madeTFSCalcs) {
            System.out.println("Number of Correct TFS Predictions: " + correctTFSCount);
            System.out.println("Number of Incorrect TFS Predictions: " + incorrectTFSCount);
            System.out.println("Overall Accuracy of TFS Predictions: " + String.valueOf((double) correctTFSCount / (double) sizeOfTestDataSet));
            if (correctTFSCount + incorrectTFSCount != sizeOfTestDataSet)
                throw new IllegalArgumentException("TFS calculations are inconsistent");
        }
        if(madeMACDCalcs) {
            System.out.println("Number of Correct MACD Predictions: " + correctMACDCount);
            System.out.println("Number of Incorrect MACD Predictions: " + incorrectMACDCount);
            System.out.println("Overall Accuracy of MACD Predictions: " + String.valueOf((double) correctMACDCount / (double) sizeOfTestDataSet));
            if (correctMACDCount + incorrectMACDCount != sizeOfTestDataSet) throw new IllegalArgumentException("MACD calculations are inconsistent");
        }
        if(madeSentiWordNetCalcs) {
            System.out.println("Number of Correct SentiWordNet Predictions: " + correctSentiWordNetCount);
            System.out.println("Number of Incorrect SentiWordNet Predictions: " + incorrectSentiWordNetCount);
            System.out.println("Overall Accuracy of SentiWordNet Predictions: " + String.valueOf((double) correctSentiWordNetCount / (double) sizeOfTestDataSet));
            if (correctSentiWordNetCount + incorrectSentiWordNetCount != sizeOfTestDataSet) throw new IllegalArgumentException("SentiWordNet calculations are inconsistent");
        }
    }
}
