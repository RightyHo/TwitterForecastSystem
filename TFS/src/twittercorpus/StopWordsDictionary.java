package twittercorpus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Andrew on 09/08/15.
 */
public class StopWordsDictionary implements DictionaryTranslator {
    List<String> stopWords;
    String stopWordsListFile;

    StopWordsDictionary(String stopWordsListFile){
        stopWords = new ArrayList<>();
        this.stopWordsListFile = stopWordsListFile;
        extractStopWords();
    }

    public void extractStopWords(){

        // extract list of stop words from file
        try(BufferedReader br = new BufferedReader(new FileReader(stopWordsListFile))){
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                Scanner s = new Scanner(currentLine);
                s.useDelimiter(",");
                while(s.hasNext()){
                    stopWords.add(s.next());
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * takes a string as input and returns a new string after applying a pre-analysis cleaning process to the input.
     * @param input
     * @return
     */
    public String processString(String input) {
        StringBuilder revisedTweetText = new StringBuilder();
        Scanner textScan = new Scanner(input);
        while (textScan.hasNext()) {
            String word = textScan.next();
            if (!stopWords.contains(word)) {
                revisedTweetText.append(word + " ");
            }
        }
        return revisedTweetText.toString().trim();
    }
}
