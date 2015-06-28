package twittercorpus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    private static final String DICTIONARY_FILE = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/TwitterForecastSystem/TFS/dictionary.txt";
    private SpellChecker spellChecker;
    private List<String> misspelledWords;
    private static SpellDictionaryHashMap dictionaryHashMap;

    static{

        File dict = new File(DICTIONARY_FILE);
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

    public SpellingDictionary() {
        misspelledWords = new ArrayList<String>();
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
        String result = input;
        List<String> misspelt = getMisspelledWords(input);
        Iterator<String> poorSpelling = misspelt.iterator();
        while (poorSpelling.hasNext()){
            String focus = poorSpelling.next();
            result = result.replace(focus,"").replaceAll("\\s+", " ");
        }
        return result;
    }
}