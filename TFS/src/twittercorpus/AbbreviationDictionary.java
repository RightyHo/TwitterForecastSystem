package twittercorpus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andrew on 16/06/15.
 */
public class AbbreviationDictionary implements DictionaryTranslator {

    private static final String ABBREVIATION_FILE = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/Twerminology - 100 Twitter Slang Words & Abbreviations.txt";
    private Map<String,String> abbreviations;

    public AbbreviationDictionary(){
        abbreviations = new HashMap<>();
        initialize();
    }

    private void initialize(){
        try(BufferedReader br = new BufferedReader(new FileReader(ABBREVIATION_FILE))){
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                String[] lineParts = currentLine.split(": ");
                abbreviations.put(lineParts[0],lineParts[1]);
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
    public String processString(String input){
        return null;
    }
}
