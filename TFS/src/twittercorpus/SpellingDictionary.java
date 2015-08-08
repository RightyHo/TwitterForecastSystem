package twittercorpus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import com.swabunga.spell.event.SpellChecker;
import com.swabunga.spell.event.StringWordTokenizer;
import com.swabunga.spell.event.TeXWordFinder;

/**
 * Created by Andrew on 16/06/15.
 */
public class SpellingDictionary implements DictionaryTranslator, SpellCheckListener {

    private static String spellingDictionary;
    private SpellChecker spellChecker;
    private List<String> misspelledWords;
    private static SpellDictionaryHashMap dictionaryHashMap;
    public static final String[] SPECIAL_PREFIX = new String[]{"#","@","-"};
    private static final Set<String> PREFIX_SET = new HashSet<>(Arrays.asList(SPECIAL_PREFIX));

    public SpellingDictionary(String spellingDictionary) {
        misspelledWords = new ArrayList<String>();
        this.spellingDictionary = spellingDictionary;
        initialize();
    }

    private void initialize(){
        File dict = new File(spellingDictionary);
        try {
            dictionaryHashMap = new SpellDictionaryHashMap(dict);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        spellChecker = new SpellChecker(dictionaryHashMap);
        spellChecker.addSpellCheckListener(this);
    }

    /**
     * Propagates the spelling errors to listeners.
     * @param event The event to handle
     */
    @Override
    public void spellingError(SpellCheckEvent event) {
        event.ignoreWord(true);
        misspelledWords.add(event.getInvalidWord());
    }

    /**
     * get a list of misspelled words from the text
     * @param text
     */
    private List<String> getMisspelledWords(String text) {
        StringWordTokenizer texTok = new StringWordTokenizer(text, new TeXWordFinder());
        spellChecker.checkSpelling(texTok);
        return misspelledWords;
    }

    /**
     * takes a string as input and returns a new string after applying a pre-analysis cleaning process to the input.
     * returns only words that appear in our reference spelling dictionary.
     * @param input
     * @return
     */
    public String processString(String input){
//        String result = "";
//        if(input.contains("#")){
//            String[] strParts = input.split("#");
//            // process words before the hashtag normally after removing non letters/spaces from the string
//            result = getCorrectlySpeltWords(retainLettersOnly(strParts[0]));
//            // keep the hashtag message even though it is likely spelt incorrectly
//            for(int i=1;i < strParts.length;i++){
//                Scanner sc = new Scanner(strParts[i]);
//                if(sc.hasNext()){
//                    // retain the hashtag message including the #
//                    result = result + " #" + sc.next();
//                }
//                // process the rest of the message (if any) after the hashtag and before the next hashtag (if any)
//                String restOfPassage = "";
//                while(sc.hasNext()){
//                    restOfPassage = restOfPassage + " " + sc.next();
//                }
//                result = result + " " + getCorrectlySpeltWords(retainLettersOnly(restOfPassage));
//            }
//        } else {
//            // run spell check after removing non letters/spaces from the string
//            result = getCorrectlySpeltWords(retainLettersOnly(input));
//        }
        // run spell check after removing non letters/spaces from the string
        String result = getCorrectlySpeltWords(retainLettersOnly(input));
        return result.replaceAll("\\s+", " ").trim();
    }

    private String retainLettersOnly(String str){
        String result = "";
        for(int i=0;i<str.length();i++){
            if(Character.isLetter(str.charAt(i)) || Character.isSpaceChar(str.charAt(i))){
                result += str.charAt(i);
            }
        }
        return result;
    }

    private String getCorrectlySpeltWords(String str){

        // *** OPTIONAL TRACE FOR DEBUGGING ***
//        System.out.println("PRE-MISSPELLEDWORDS: "+result);

        String replacementText = "";
        List<String> misspelled = getMisspelledWords(str);

        String[] words = str.split(" ");
        for(String w : words) {
            if(!misspelled.contains(w)) {
                replacementText += w + " ";
            }
        }

            // *** OPTIONAL TRACE FOR DEBUGGING ***
//            System.out.println("MISSPELT: "+ focus);

        return replacementText.trim();
        
        // *** OPTIONAL TRACE FOR DEBUGGING ***
//        System.out.println("POST-MISSPELLEDWORDS: "+result);

    }
}