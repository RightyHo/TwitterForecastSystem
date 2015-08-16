package twittercorpus;

import java.util.List;

/**
 * Created by Andrew on 27/07/15.
 */
public interface PredictionStatistics {

    void calculateTFSAccuracy(List<Tweet> testData);

//    void calculateMACDAccuracy(List<Tweet> testData);

    void calculateSentiWordNetAccuracy(List<Tweet> testData);

    void printResults();
}
