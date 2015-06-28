package experiment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.engine.Word;
import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import com.swabunga.spell.event.SpellChecker;
import com.swabunga.spell.event.StringWordTokenizer;
import com.swabunga.spell.event.TeXWordFinder;

public class Dictionary implements SpellCheckListener {

    private SpellChecker spellChecker;
    private List<String> misspelledWords;

    /**
     * get a list of misspelled words from the text
     * @param text
     */
    public List<String> getMisspelledWords(String text) {
        StringWordTokenizer texTok = new StringWordTokenizer(text, new TeXWordFinder());
        spellChecker.checkSpelling(texTok);
        return misspelledWords;
    }

    private static SpellDictionaryHashMap dictionaryHashMap;

    static{

        File dict = new File("/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/TwitterForecastSystem/TFS/dictionary.txt");
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


    public Dictionary() {

        misspelledWords = new ArrayList<String>();
        initialize();
    }

    @Override
    public void spellingError(SpellCheckEvent event) {
        event.ignoreWord(true);
        misspelledWords.add(event.getInvalidWord());
    }

    public static void main(String[] args) {
        Dictionary spellChecker = new Dictionary();
        String input = "you bettr recognize aav or iss it recognise?";
        System.out.println("Sentance before processing: " + input);
        List<String> misspelt = spellChecker.getMisspelledWords(input);
        Iterator<String> poorSpelling = misspelt.iterator();
        System.out.println("Removing the following words: ");
        while (poorSpelling.hasNext()){
            String focus = poorSpelling.next();
            input = input.replace(focus,"").replaceAll("\\s+", " ");
            System.out.println(focus);
        }
        System.out.println("Remaining text after removing misspelt words: " + input);
    }
}