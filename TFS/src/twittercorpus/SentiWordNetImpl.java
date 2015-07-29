package twittercorpus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

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
        HashMap<String,HashMap<Integer,Double>> auxDict = new HashMap<>();
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
                    for (String word : setMemberWords) {
                        String[] wordsWithRank = word.split("#");
                        String wordWithType = wordsWithRank[0] + "#" + typeOfWord;
                        int wordWithTypeRanking = Integer.parseInt(wordsWithRank[1]);
                        if (!auxDict.containsKey(wordWithType)) {
                            auxDict.put(wordWithType,new HashMap<>());
                        }
                        HashMap<Integer,Double> focusMap = auxDict.get(wordWithType);
                        focusMap.put(wordWithTypeRanking,totalScore);
                    }
                }
            }
            // traverse all word/type combinations and calculate their corresponding overall weighted average sentiment score
            Set<String> wordPosSet = auxDict.keySet();
            for(String wordAndPos : wordPosSet){
                double totalScore = 0.0;
                double sum = 0.0;
                HashMap<Integer, Double> wordRankToSentiScoreMap = auxDict.get(wordAndPos);

                // calc weighted average score for the wordAndPos combination
                Set<Integer> wordDefinitionRankingSet = wordRankToSentiScoreMap.keySet();
                for(Integer wordDefinitionRank : wordDefinitionRankingSet){
                    Double sentiScore = wordRankToSentiScoreMap.get(wordDefinitionRank);
                    totalScore += sentiScore / (double) wordDefinitionRank;
                    sum += 1.0 / (double) wordDefinitionRank;
                }
                double overallScoreForWordAndPos = totalScore / sum;
                // calc average of the non-zero scores of the various POS versions of the word (sum of all non-zero
                // sentiment scores divided by the number of non-zero sentiment scores)
                swnDictionary.put();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public Sentiment classifySentiment(List<String> features){
        return null;
    }
}
