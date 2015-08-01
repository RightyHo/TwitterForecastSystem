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

    static{

        File dict = new File(spellingDictionary);
        try {
            dictionaryHashMap = new SpellDictionaryHashMap(dict);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initialize(){
        spellChecker = new SpellChecker(dictionaryHashMap);
        spellChecker.addSpellCheckListener(this);
    }

    public SpellingDictionary(String spellingDictionary) {
        misspelledWords = new ArrayList<String>();
        this.spellingDictionary = spellingDictionary;
        initialize();
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
        String result = "";
        if(input.contains("#")){
            String[] strParts = input.split("#");
            // process words before the hashtag normally
            result = getCorrectlySpeltWords(strParts[0]);
            // keep the hashtag message even though it is likely spelt incorrectly
            for(int i=1;i < strParts.length;i++){
                Scanner sc = new Scanner(strParts[i]);
                if(sc.hasNext()){
                    // retain the hashtag message including the #
                    result = result + " #" + sc.next();
                }
                // process the rest of the message (if any) after the hashtag and before the next hashtag (if any)
                String restOfLine = "";
                while(sc.hasNext()){
                    restOfLine = restOfLine + " " + sc.next();
                }
                result = result + " " + getCorrectlySpeltWords(restOfLine);
            }
        } else {
            result = getCorrectlySpeltWords(input);
        }
        return result.replaceAll("\\s+", " ").trim();
    }

    private String getCorrectlySpeltWords(String str){
        String result = str;
        List<String> misspelt = getMisspelledWords(str);
        Iterator<String> poorSpelling = misspelt.iterator();
        while (poorSpelling.hasNext()){
            String focus = poorSpelling.next();
            result = result.replace(focus, "").replaceAll("\\s+", " ");
        }
        return result;
    }
}