package twittercorpus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Andrew on 16/06/15.
 */
public class AbbreviationDictionary implements DictionaryTranslator {

    private String dictionaryFilename;
    private Map<String,String> abbreviations;

    public AbbreviationDictionary(String dictionaryFilename){
        abbreviations = new HashMap<>();
        this.dictionaryFilename = dictionaryFilename;
        initialize();
    }

    private void initialize(){
        try(BufferedReader br = new BufferedReader(new FileReader(dictionaryFilename))){
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                String[] lineParts = currentLine.split(": ");
                if(lineParts.length > 1) {
                    abbreviations.put(lineParts[0].toLowerCase(), lineParts[1]);
                } else if(lineParts.length == 1){
                    System.out.println(lineParts[0]);
                } else {
                    System.out.println("Current line split failure - " + currentLine);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    /**
     * takes a string as input and returns a new string after applying a pre-analysis cleaning process to the input.
     * returns the input string with any abbreviations found in the abbreviation dictionary converted into their full English definitions
     * @param input
     * @return
     */
    public String processString(String input){
        String result = "";
        Scanner sc = new Scanner(input);
        while (sc.hasNext()){
            String focus = sc.next();
            if(abbreviations.containsKey(focus.toLowerCase())){
                result = result + " " + abbreviations.get(focus.toLowerCase());
            } else {
                result = result + " " + focus;
            }
        }
        return result.replaceAll("\\s+", " ").trim();
    }
}
