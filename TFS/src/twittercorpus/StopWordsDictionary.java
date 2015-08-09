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
    String stopWordsList;

    StopWordsDictionary(String stopWordsList){
        List<String> stopWords = new ArrayList<>();
        this.stopWordsList = stopWordsList;
        extractStopWords();
    }

    public void extractStopWords(){

        // extract list of stop words from file
        try(BufferedReader br = new BufferedReader(new FileReader(stopWordsList))){
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
    String processString(String input){

}
