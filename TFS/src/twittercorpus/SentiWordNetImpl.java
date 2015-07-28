package twittercorpus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Andrew on 28/07/15.
 */
public class SentiWordNetImpl implements SentiWordNet {

    private String fileName;
    private Map<String,Double> swnDictionary;

    public SentiWordNetImpl(String fileName){
        this.fileName = fileName;
        swnDictionary = new HashMap<>();
        buildDictionary();
    }

    public void buildDictionary(){
        try(BufferedReader br = new BufferedReader(new FileReader(fileName))){
            String currentLine;
            while ((currentLine = br.readLine()) != null) {

                // divide current line into its individual constituents
                Scanner s = new Scanner(currentLine);
                String word = s.next().trim();
                double sentimentScore = s.nextDouble();

                // add new mapping for every row in the file
                swnDictionary.put(word,sentimentScore);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public Sentiment classifySentiment(List<String> features){
        return null;
    }
}
