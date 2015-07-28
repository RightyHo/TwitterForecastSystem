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

    /**
     * Example Lines of Input:
     *
     * POS	ID	        PosScore	NegScore	SynsetTerms	                Gloss
     * a	00005473	0.75	    0	        direct#10	                lacking compromising or mitigating elements; exact; "the direct opposite"
     * a	00005599	0.5	        0.5	        unquestioning#2 implicit#2	being without doubt or reserve; "implicit trust"
     * a	00005718	0.125	    0	        infinite#4	                total and all-embracing; "God's infinite wisdom"
     *
     * The number notation comes form Wordnet. It represents the rank in which the given word is commonly used.
     * So rank#5 refers to the context in which rank is used 5th most commonly. Similarly rank#1 refers to the meaning
     * of rank most commonly used. The following are the POS notations:
     *
     *  n - NOUN
     *  v - VERB
     *  a - ADJECTIVE
     *  s - ADJECTIVE SATELLITE
     *  r - ADVERB
     */
    public void buildDictionary(){
        try(BufferedReader br = new BufferedReader(new FileReader(fileName))){
            String currentLine;
            while ((currentLine = br.readLine()) != null) {

                // divide current line into its individual constituents
                Scanner s = new Scanner(currentLine);
                s.useDelimiter("\t");
                String typeOfWord = s.next().trim();

                // ignore comments in the file that are preceeded by a '#'
                if(typeOfWord.charAt(0) != '#') {
                    int synsetID = s.nextInt();
                    double posScore = s.nextDouble();
                    double negScore = s.nextDouble();
                    String synsetTerms = s.next();
                    // ignore gloss definition string
                    double totalScore = posScore - negScore;
                    // split up synset member words
                    String[] setMemberWords = synsetTerms.split(" ");
                    for(String word : setMemberWords){
                        String[] wordWithRank = word.split("#");

                    }

                    double sentimentScore = s.nextDouble();

                    // add new mapping for every row in the file
                    swnDictionary.put(word, sentimentScore);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public Sentiment classifySentiment(List<String> features){
        return null;
    }
}
