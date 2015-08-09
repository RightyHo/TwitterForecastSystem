package twittercorpus;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Andrew on 09/08/15.
 */
public class SentiWordNetImplTest {

    SentiWordNet swnTest;

    @Before
    public void setUp() throws Exception {

        String sentiWordNetFilename = "/Users/Andrew/Documents/Programming/MSc Project/Natural Language Processing/Project Data Sets/SentiWordNet_3.0.txt";
        swnTest = new SentiWordNetImpl(sentiWordNetFilename);
        swnTest.buildDictionary();
    }

    @Test
    public void testBuildDictionary() throws Exception {

        Map<String,Double> testDict = swnTest.getSwnDictionary();
        assertFalse(testDict.isEmpty());
        System.out.println("SIZE OF SWN3.0 DICTIONARY IS: " + testDict.size());
        Iterator<String> keyWords = testDict.keySet().iterator();
        int count = 0;
        while(keyWords.hasNext() && count < 3){
            String word = keyWords.next();
            count++;
            System.out.println("FIRST ELEMENT IN SWN3.0 MAP: " + word + ".  WORD SCORE: " + testDict.get(word));
        }
    }

    @Test
    public void testClassifySentiment() throws Exception {

        // Tweet example Fri Jan 16 07:26:00 2015	I made up the text for this tweet.
        LocalDateTime lDateT = LocalDateTime.of(2015,1,16,7,26,0);
        ZonedDateTime tTime = ZonedDateTime.of(lDateT, ZoneOffset.of("Z"));
        Tweet testTw = new TweetImpl(tTime,"obscure masked versatility");
        testTw.extractNGramFeatures(1);

        // calculate expected SentiWordNet3.0 score



        Sentiment actualSenti = swnTest.classifySentiment(testTw.getTweetText());
//        assertEquals(expectedSenti,actualSenti);
    }

    // totalScore = sentiScore1 / 1 + sentiScore2 / 2 + sentiScore3 / 3 + ...
    // totalScore += sentiScore / (double) wordDefinitionRank;
    // sum = 1/1 + 1/2 + 1/3 + ...
    // sum += 1.0 / (double) wordDefinitionRank;
    // double overallScoreForWordAndPos = totalScore / sum;
    @Test
    public void testGetFeatureSentimentScore() throws Exception {

        // calculate expected SentiWordNet3.0 score for versatility
        // List of all definitions for versatility in SentiWordNet3.0:
        //
        // n	05641834	0.375	0	versatility#1	having a wide variety of skills
        double expectedA = ((0.375 - 0) / 1) / 1;
        double actualA = swnTest.getFeatureSentimentScore("versatility");
        assert(Math.abs(expectedA - actualA) < 0.000000001);

        // calculate expected SentiWordNet3.0 score for masked
        // List of all definitions for masked in SentiWordNet3.0:
        //
        // a	01707230	0.25	0.375	masked#1 disguised#1 cloaked#1	having its true character concealed with the intent of misleading; "hidden agenda"; "masked threat"
        // a	01481014	0	0.625	masked#2	having markings suggestive of a mask; "the masked face of a raccoon"
        double expectedB = ((0.25 - 0.375) / 1 + (0 - 0.625) / 2) / (1 + 1/2);
        double actualB = swnTest.getFeatureSentimentScore("masked");
        assert(Math.abs(expectedB - actualB) < 0.000000001);

        // calculate expected SentiWordNet3.0 score for obscure
        // List of all definitions for masked in SentiWordNet3.0:
        //
        // a	00431004	0.25	0.375	vague#1 obscure#1	not clearly understood or expressed; "an obscure turn of phrase"; "an impulse to go off and fight certain obscure battles of his own spirit"-Anatole Broyard; "their descriptions of human behavior become vague, dull, and unclear"- P.A.Sorokin; "vague...forms of speech...have so long passed for mysteries of science"- John Locke
        // a	00534250	0	0.375	obscure#2 dark#8	marked by difficulty of style or expression; "much that was dark is now quite clear to me"; "those who do not appreciate Kafka's work say his style is obscure"
        // a	02089377	0	0.75	obscure#3 hidden#3	difficult to find; "hidden valleys"; "a hidden cave"; "an obscure retreat"
        // a	01122595	0	0.75	unsung#1 unknown#4 obscure#4	not famous or acclaimed; "an obscure family"; "unsung heroes of the war"
        // a	00581637	0	0.375	unnoticeable#3 obscure#5	not drawing attention; "an unnoticeable cigarette burn on the carpet"; "an obscure flaw"
        // a	00567860	0.5	0	obscure#6 isolated#6 apart#1	remote and separate physically or socially; "existed over the centuries as a world apart"; "preserved because they inhabited a place apart"- W.H.Hudson; "tiny isolated villages remote from centers of civilization"; "an obscure village"
        // v	02157731	0	0.125	obscure#1 obnubilate#1 mist#2 haze_over#1 fog#1 cloud#2 befog#1 becloud#1	make less visible or unclear; "The stars are obscured by the clouds"; "the big elm tree obscures our view of the valley"
        // v	00620673	0.25	0.375	obscure#2 obnubilate#2 confuse#5 blur#3	make unclear, indistinct, or blurred; "Her remarks confused the debate"; "Their words obnubilate their intentions"
        // v	00313712	0	0.25	overcloud#2 obscure#3 bedim#2	make obscure or unclear; "The distinction was obscured"
        // v	00587390	0	0	obscure#4	reduce a vowel to a neutral one, such as a schwa
        // v	00313987	0	0.25	veil#2 obscure#5 obliterate#2 hide#4 blot_out#1	make undecipherable or imperceptible by obscuring or concealing; "a hidden message"; "a veiled threat"

        double expectedAdjectiveScore = ((0.25 - 0.375) / 1 + (0 - 0.375) / 2 + (0 - 0.75) / 3 + (0 - 0.75) / 4 + (0 - 0.375) / 5 + (0.5 - 0) / 6) / (1 + 1/2 + 1/3 + 1/4 + 1/5 + 1/6);
        double expectedVerbScore = ((0 - 0.125) / 1 + (0.25 - 0.375) / 2 + (0 - 0.25) / 3 + (0 - 0) / 4 + (0 - 0.25) / 5) / (1 + 1/2 + 1/3 + 1/4 + 1/5);
        double expectedC = (expectedAdjectiveScore + expectedVerbScore) / 2;
        double actualC = swnTest.getFeatureSentimentScore("obscure");
        assert(Math.abs(expectedC - actualC) < 0.000000001);
    }
}