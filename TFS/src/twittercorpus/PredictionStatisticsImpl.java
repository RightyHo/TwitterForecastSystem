package twittercorpus;

import java.util.List;

/**
 * Created by Andrew on 27/07/15.
 */
public class PredictionStatisticsImpl implements PredictionStatistics {
    private int sizeOfTestDataSet = 0;
    private boolean madeTFSCalcs = false;
    private boolean madeMACDCalcs = false;
    private boolean madeSentiWordNetCalcs = false;
    private int correctTFSCount = 0;
    private int incorrectTFSCount = 0;
    private int correctMACDCount = 0;
    private int incorrectMACDCount = 0;
    private int correctSentiWordNetCount = 0;
    private int incorrectSentiWordNetCount = 0;

    public void calculateTFSAccuracy(List<Tweet> testData){
        sizeOfTestDataSet = testData.size();

        // reset counts
        correctTFSCount = 0;
        incorrectTFSCount = 0;

        // compare each tweets classification with the actual price move during the twenty minutes after the tweet is published
        for(Tweet t : testData){
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
                if(t.getPostTweetSnapshot().getClosingSharePrice() == t.getInitialSnapshot().getClosingSharePrice()){
                    correctTFSCount++;
                } else {
                    incorrectTFSCount++;
                }
            } else {
                throw new IllegalArgumentException("The test data has not been correctly classified");
            }
        }
    }

    public void calculateMACDAccuracy(List<Tweet> testData){

    }

    public void calculateSentiWordNetAccuracy(List<Tweet> testData){

    }

    public void printResults(){
        System.out.println("Size of Test Data Set:  " + sizeOfTestDataSet);
        if(madeTFSCalcs){
            System.out.println("Number of Correct TFS Predictions: " + correctTFSCount);
            System.out.println("Number of Incorrect TFS Predictions: " + incorrectTFSCount);
            System.out.println("Overall Accuracy of TFS Predictions: " + String.valueOf((double) correctTFSCount / (double) sizeOfTestDataSet));
            if(correctTFSCount + incorrectTFSCount != sizeOfTestDataSet) throw new IllegalArgumentException("TFS calculations are inconsistent");
        } else if(madeMACDCalcs) {
            System.out.println("Number of Correct MACD Predictions: " + correctMACDCount);
            System.out.println("Number of Incorrect MACD Predictions: " + incorrectMACDCount);
            System.out.println("Overall Accuracy of MACD Predictions: " + String.valueOf((double) correctMACDCount / (double) sizeOfTestDataSet));
            if (correctMACDCount + incorrectMACDCount != sizeOfTestDataSet) throw new IllegalArgumentException("MACD calculations are inconsistent");
        } else if(madeSentiWordNetCalcs) {
            System.out.println("Number of Correct SentiWordNet Predictions: " + correctSentiWordNetCount);
            System.out.println("Number of Incorrect SentiWordNet Predictions: " + incorrectSentiWordNetCount);
            System.out.println("Overall Accuracy of SentiWordNet Predictions: " + String.valueOf((double) correctSentiWordNetCount / (double) sizeOfTestDataSet));
            if (correctSentiWordNetCount + incorrectSentiWordNetCount != sizeOfTestDataSet) throw new IllegalArgumentException("SentiWordNet calculations are inconsistent");
        }


    }
}
