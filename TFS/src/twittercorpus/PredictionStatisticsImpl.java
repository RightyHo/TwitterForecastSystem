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
    private int correctMatchingTfsAndMacdCount;
    private int incorrectMatchingTfsAndMacdCount;

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
        correctMatchingTfsAndMacdCount = 0;
        incorrectMatchingTfsAndMacdCount = 0;
    }

    public void calculateTFSAccuracy(List<Tweet> testData){
        if(testData == null) throw new IllegalArgumentException("Test data list is null!");
        sizeOfTestDataSet = testData.size();

        // reset counts
        correctTFSCount = 0;
        incorrectTFSCount = 0;
        correctMACDCount = 0;
        incorrectMACDCount = 0;
        correctMatchingTfsAndMacdCount = 0;
        incorrectMatchingTfsAndMacdCount = 0;

        // compare each tweets classification with the actual price move during the twenty minutes after the tweet is published
        for(Tweet t : testData){

            boolean bothAgree = false;
            int tfsSentimentPrediction = 0;      // 0 for neutral, 1 for positive

            if(t == null) throw new NullPointerException("testData is returning null tweets!");

//            if(t.getSentiment() == Sentiment.NEGATIVE){
//                if(t.getPostTweetSnapshot().getClosingSharePrice() < t.getInitialSnapshot().getClosingSharePrice()){
//                    correctTFSCount++;
//                } else {
//                    incorrectTFSCount++;
//                }
//            } else if(t.getSentiment() == Sentiment.POSITIVE){
//                if(t.getPostTweetSnapshot().getClosingSharePrice() > t.getInitialSnapshot().getClosingSharePrice()){
//                    correctTFSCount++;
//                } else {
//                    incorrectTFSCount++;
//                }
//            } else if(t.getSentiment() == Sentiment.NEUTRAL){
//                // If time permits it may be worth experimenting by widening a range of prices to fall under neutral category
//                if(t.getPostTweetSnapshot().getClosingSharePrice() == t.getInitialSnapshot().getClosingSharePrice()){
//                    correctTFSCount++;
//                } else {
//                    incorrectTFSCount++;
//                }
//            } else {
//                throw new IllegalArgumentException("The test data has not been correctly classified");
//            }

            if(t.getPostTweetSnapshot().getClosingSharePrice() > t.getInitialSnapshot().getClosingSharePrice()) {

                // price went up after tweet was published

                if (t.getSentiment() == Sentiment.POSITIVE) {

                    // TFS prediction was correct
                    correctTFSCount++;

                    // A positive MACD signal indicates upward price momentum
                    if (t.getInitialSnapshot().getOpeningMACDDirectionSignal() > 0) {

                        // MACD prediction was correct
                        correctMACDCount++;

                        // both TFS and MACD predictions were correct and matching
                        correctMatchingTfsAndMacdCount++;

                    } else {

                        // MACD prediction was wrong
                        incorrectMACDCount++;

                    }

                } else if (t.getSentiment() == Sentiment.NEGATIVE) {

                    // TFS prediction was wrong
                    incorrectTFSCount++;

                    // A positive MACD signal indicates upward price momentum
                    if (t.getInitialSnapshot().getOpeningMACDDirectionSignal() > 0) {

                        // MACD prediction was correct
                        correctMACDCount++;

                    } else if (t.getInitialSnapshot().getOpeningMACDDirectionSignal() < 0) {
                        //A negative MACD signal indicates downward price momentum

                        // MACD prediction was wrong
                        incorrectMACDCount++;

                        // both TFS and MACD predictions were wrong and matching
                        incorrectMatchingTfsAndMacdCount++;

                    } else {    // MACD signal was NEUTRAL

                        // MACD prediction was wrong
                        incorrectMACDCount++;

                    }

                } else {    // TFS predicted NEUTRAL

                    // TFS prediction was wrong
                    incorrectTFSCount++;

                    // A positive MACD signal indicates upward price momentum
                    if (t.getInitialSnapshot().getOpeningMACDDirectionSignal() > 0) {

                        // MACD prediction was correct
                        correctMACDCount++;


                    } else if (t.getInitialSnapshot().getOpeningMACDDirectionSignal() < 0) {

                        // A negative MACD signal indicates downward price momentum

                        // MACD prediction was wrong
                        incorrectMACDCount++;

                    } else {    // MACD signal was NEUTRAL

                        // MACD prediction was wrong
                        incorrectMACDCount++;

                        // both TFS and MACD predictions were wrong and matching
                        incorrectMatchingTfsAndMacdCount++;

                    }
                }

            } else if(t.getPostTweetSnapshot().getClosingSharePrice() < t.getInitialSnapshot().getClosingSharePrice()){

                // price went down after tweet was published

                if (t.getSentiment() == Sentiment.NEGATIVE) {

                    // TFS prediction was correct
                    correctTFSCount++;

                    // A negative MACD signal indicates downward price momentum
                    if (t.getInitialSnapshot().getOpeningMACDDirectionSignal() < 0) {

                        // MACD prediction was correct
                        correctMACDCount++;

                        // both TFS and MACD predictions were correct and matching
                        correctMatchingTfsAndMacdCount++;

                    } else {

                        // MACD prediction was wrong
                        incorrectMACDCount++;

                    }

                } else if (t.getSentiment() == Sentiment.POSITIVE) {

                    // TFS prediction was wrong
                    incorrectTFSCount++;

                    // A negative MACD signal indicates downward price momentum
                    if (t.getInitialSnapshot().getOpeningMACDDirectionSignal() < 0) {

                        // MACD prediction was correct
                        correctMACDCount++;

                    } else if (t.getInitialSnapshot().getOpeningMACDDirectionSignal() > 0) {
                        //A positive MACD signal indicates upward price momentum

                        // MACD prediction was wrong
                        incorrectMACDCount++;

                        // both TFS and MACD predictions were wrong and matching
                        incorrectMatchingTfsAndMacdCount++;

                    } else {    // MACD signal was NEUTRAL

                        // MACD prediction was wrong
                        incorrectMACDCount++;

                    }

                } else {    // TFS predicted NEUTRAL

                    // TFS prediction was wrong
                    incorrectTFSCount++;

                    // A negative value MACD signal indicates downward price momentum
                    if (t.getInitialSnapshot().getOpeningMACDDirectionSignal() < 0) {

                        // MACD prediction was correct
                        correctMACDCount++;


                    } else if (t.getInitialSnapshot().getOpeningMACDDirectionSignal() > 0) {

                        // A positive MACD signal indicates upward price momentum

                        // MACD prediction was wrong
                        incorrectMACDCount++;

                    } else {    // MACD signal was NEUTRAL

                        // MACD prediction was wrong
                        incorrectMACDCount++;

                        // both TFS and MACD predictions were wrong and matching
                        incorrectMatchingTfsAndMacdCount++;

                    }
                }

            } else {

                // price was unchanged after tweet was published

                if (t.getSentiment() == Sentiment.NEUTRAL) {

                    // TFS prediction was correct
                    correctTFSCount++;

                    // A zero value MACD signal indicates neutral price momentum
                    if (t.getInitialSnapshot().getOpeningMACDDirectionSignal() == 0) {

                        // MACD prediction was correct
                        correctMACDCount++;

                        // both TFS and MACD predictions were correct and matching
                        correctMatchingTfsAndMacdCount++;

                    } else {

                        // MACD prediction was wrong
                        incorrectMACDCount++;

                    }

                } else if (t.getSentiment() == Sentiment.POSITIVE) {

                    // TFS prediction was wrong
                    incorrectTFSCount++;

                    // A zero value MACD signal indicates neutral price momentum
                    if (t.getInitialSnapshot().getOpeningMACDDirectionSignal() == 0) {

                        // MACD prediction was correct
                        correctMACDCount++;

                    } else if (t.getInitialSnapshot().getOpeningMACDDirectionSignal() > 0) {
                        //A positive MACD signal indicates upward price momentum

                        // MACD prediction was wrong
                        incorrectMACDCount++;

                        // both TFS and MACD predictions were wrong and matching
                        incorrectMatchingTfsAndMacdCount++;

                    } else {    //A negative MACD signal indicates downward price momentum

                        // MACD prediction was wrong
                        incorrectMACDCount++;

                    }

                } else {    // TFS predicted NEGATIVE

                    // TFS prediction was wrong
                    incorrectTFSCount++;

                    // A zero value MACD signal indicates neutral price momentum
                    if (t.getInitialSnapshot().getOpeningMACDDirectionSignal() == 0) {

                        // MACD prediction was correct
                        correctMACDCount++;


                    } else if (t.getInitialSnapshot().getOpeningMACDDirectionSignal() > 0) {

                        // A positive MACD signal indicates upward price momentum

                        // MACD prediction was wrong
                        incorrectMACDCount++;

                    } else {    // A negative MACD signal indicates downward price momentum

                        // MACD prediction was wrong
                        incorrectMACDCount++;

                        // both TFS and MACD predictions were wrong and matching
                        incorrectMatchingTfsAndMacdCount++;

                    }
                }
            }
        }
        madeTFSCalcs = true;
        madeMACDCalcs = true;
    }

//    public void calculateMACDAccuracy(List<Tweet> testData){
//        if(testData == null) throw new IllegalArgumentException("Test data list is null!");
//        sizeOfTestDataSet = testData.size();
//
//        // reset counts
//        correctMACDCount = 0;
//        incorrectMACDCount = 0;
//
//        // compare the MACD direction indicator prior to the release of each tweet with the actual price move during the twenty minutes after the tweet is published
//        for(Tweet t : testData){
//            if(t == null) throw new NullPointerException("testData is returning null tweets!");
//            // Signal Line Crossovers:  MACD Level minus Signal Level.  A positive signal indicates upward price momentum and vice versa
//            if(t.getInitialSnapshot().getOpeningMACDDirectionSignal() > 0){
//                if(t.getPostTweetSnapshot().getClosingSharePrice() > t.getInitialSnapshot().getClosingSharePrice()){
//                    correctMACDCount++;
//                } else {
//                    incorrectMACDCount++;
//                }
//            } else if(t.getInitialSnapshot().getOpeningMACDDirectionSignal() < 0){
//                if(t.getPostTweetSnapshot().getClosingSharePrice() < t.getInitialSnapshot().getClosingSharePrice()){
//                    correctMACDCount++;
//                } else {
//                    incorrectMACDCount++;
//                }
//            } else {
//                // Sentiment is NEUTRAL - if time permits it may be worth experimenting by widening a range of prices to fall under neutral category
//                if(t.getPostTweetSnapshot().getClosingSharePrice() == t.getInitialSnapshot().getClosingSharePrice()){
//                    correctMACDCount++;
//                } else {
//                    incorrectMACDCount++;
//                }
//            }
//        }
//        madeMACDCalcs = true;
//    }

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
        System.out.println("\n******************************************************************************************");
        System.out.println("*********************************** EXPERIMENT RESULTS ***********************************");
        System.out.println("******************************************************************************************\n");
        if(madeTFSCalcs) {
            System.out.println("Number of Correct TFS Predictions: " + correctTFSCount);
            System.out.println("Number of Incorrect TFS Predictions: " + incorrectTFSCount);
            double successRate = (double) correctTFSCount / (double) sizeOfTestDataSet;
            System.out.printf("Overall Accuracy of TFS Predictions - Proportion of correct predictions: %.2f\n",successRate);
            System.out.println("\n******************************************************************************************\n");
            if (correctTFSCount + incorrectTFSCount != sizeOfTestDataSet)
                throw new IllegalArgumentException("TFS calculations are inconsistent");
        }
        if(madeMACDCalcs) {
            System.out.println("Number of Correct MACD Predictions: " + correctMACDCount);
            System.out.println("Number of Incorrect MACD Predictions: " + incorrectMACDCount);
            double successRate = (double) correctMACDCount / (double) sizeOfTestDataSet;
            System.out.printf("Overall Accuracy of MACD Predictions - Proportion of correct predictions: %.2f\n",successRate);
            System.out.println("\n******************************************************************************************\n");
            if (correctMACDCount + incorrectMACDCount != sizeOfTestDataSet) throw new IllegalArgumentException("MACD calculations are inconsistent");
        }
        if(madeTFSCalcs && madeMACDCalcs) {
            System.out.println("Number of Correct Predictions when the TFS & MACD Indicators Both Agreed on the Direction : " + correctMatchingTfsAndMacdCount);
            System.out.println("Number of Incorrect Predictions when the TFS & MACD Indicators Both Agreed on the Direction : " + incorrectMatchingTfsAndMacdCount);
            double successRate = (double) correctMatchingTfsAndMacdCount / (double) (correctMatchingTfsAndMacdCount + incorrectMatchingTfsAndMacdCount);
            System.out.printf("Overall Accuracy of Predictions when TFS & MACD Indicators Both Agreed on the Direction: %.2f\n",successRate);
            System.out.println("\n******************************************************************************************\n");
        }
        if(madeSentiWordNetCalcs) {
            System.out.println("Number of Correct SentiWordNet Predictions: " + correctSentiWordNetCount);
            System.out.println("Number of Incorrect SentiWordNet Predictions: " + incorrectSentiWordNetCount);
            double successRate = (double) correctSentiWordNetCount / (double) sizeOfTestDataSet;
            System.out.printf("Overall Accuracy of SentiWordNet Predictions - Proportion of correct predictions: %.2f\n", successRate);
            System.out.println("\n******************************************************************************************\n");
            if (correctSentiWordNetCount + incorrectSentiWordNetCount != sizeOfTestDataSet) throw new IllegalArgumentException("SentiWordNet calculations are inconsistent");
        }
    }
}
