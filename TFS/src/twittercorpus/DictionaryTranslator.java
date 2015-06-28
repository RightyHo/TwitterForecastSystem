package twittercorpus;

/**
 * Created by Andrew on 16/06/15.
 */
public interface DictionaryTranslator {

    /**
     * takes a string as input and returns a new string after applying a pre-analysis cleaning process to the input.
     * @param input
     * @return
     */
    String processString(String input);
}
