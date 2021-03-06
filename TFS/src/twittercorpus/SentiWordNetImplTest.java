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
        System.out.println("SIZE OF SWN3.0 DICTIONARY IS: " + testDict.size() + ".  NUMBER OF LINES IN SWN FILE: 117,659");
        Iterator<String> keyWords = testDict.keySet().iterator();
        int count = 0;
        while(keyWords.hasNext() && count < 3){
            String word = keyWords.next();
            count++;
            System.out.println("FIRST THREE ELEMENTS IN SWN3.0 MAP: " + word + ".  WORD SCORE: " + testDict.get(word));
        }
    }

    @Test
    public void testClassifySentiment() throws Exception {

        // Tweet example Fri Jan 16 07:26:00 2015	I made up the text for this tweet.
        LocalDateTime lDateT = LocalDateTime.of(2015,1,16,7,26,0);
        ZonedDateTime tTime = ZonedDateTime.of(lDateT, ZoneOffset.of("Z"));
        Tweet testTw = new TweetImpl(tTime,"versatility masked obscure bondable");
        testTw.extractNGramFeatures(1);

        // calculate expected SentiWordNet3.0 score
        double expectedA = ((0.375 - 0.0) / 1.0) / 1.0;

        double totalScoreB = ((0.25 - 0.375) / 1.0)  + ((0.0 - 0.625) / 2.0);
        double sumB = 1.0 / 1.0 + 1.0 /2.0;
        double expectedB = totalScoreB / sumB;

        double adjectiveTotalScoreC = ((0.25 - 0.375) / 1.0)  + ((0.0 -	0.375) / 2.0) + ((0.0 -	0.75) / 3.0)
                + ((0.0 - 0.75) / 4.0) + ((0.0 - 0.375) / 5.0)  + ((0.5 - 0.0) / 6.0);
        double adjectiveSumC = 1.0 / 1.0 + 1.0 /2.0 + 1.0 /3.0 + 1.0 /4.0 + 1.0 /5.0 + 1.0 /6.0;
        double adjectiveExpectedC = adjectiveTotalScoreC / adjectiveSumC;

        double verbTotalScoreC = ((0.0 - 0.125) / 1.0)  + ((0.25 - 0.375) / 2.0) + ((0.0 - 0.25) / 3.0)
                + ((0.0 - 0.0) / 4.0) + ((0.0 - 0.25) / 5.0);
        double verbSumC = 1.0 / 1.0 + 1.0 /2.0 + 1.0 /3.0 + 1.0 /4.0 + 1.0 /5.0;
        double verbExpectedC = verbTotalScoreC / verbSumC;

        double expectedC = (adjectiveExpectedC + verbExpectedC) / 2.0;

        double totalScoreD = ((0.5 - 0.0) / 1.0)  + ((0.25 - 0) / 2.0);
        double sumD = 1.0 / 1.0 + 1.0 / 2.0;
        double expectedD = totalScoreD / sumD;

        double expectedTotal = expectedA + expectedB + expectedC + expectedD;
        Sentiment actualSenti = swnTest.classifySentiment(testTw.getTweetText());
        System.out.println("Expected Tweet Sentiment Score: " + expectedTotal);

        Sentiment expectedSenti;
        if(expectedTotal > 0){
            expectedSenti =  Sentiment.POSITIVE;
        } else if(expectedTotal < 0){
            expectedSenti = Sentiment.NEGATIVE;
        } else {
            expectedSenti = Sentiment.NEUTRAL;
        }

        assertEquals(expectedSenti,actualSenti);
    }

    // totalScore = sentiScore1 / 1 + sentiScore2 / 2 + sentiScore3 / 3 + ...
    // totalScore += sentiScore / (double) wordDefinitionRank;
    // sum = 1/1 + 1/2 + 1/3 + ...
    // sum += 1.0 / (double) wordDefinitionRank;
    // double overallScoreForWordAndPos = totalScore / sum;
    @Test
    public void testGetFeatureSentimentScore() throws Exception {

        // test that a word with one SWN record generates the expected sentiment score
        // calculate expected SentiWordNet3.0 score for versatility
        // List of all definitions for versatility in SentiWordNet3.0:
        //
        // n	05641834	0.375	0	versatility#1	having a wide variety of skills
        double expectedA = ((0.375 - 0.0) / 1.0) / 1.0;
        double actualA = swnTest.getFeatureSentimentScore("versatility");
        System.out.println("expectedA value is: " + expectedA);
        System.out.println("actualA value is: " + actualA);
        assert(Math.abs(expectedA - actualA) < 0.000000001);

        // test that a word with two SWN records generates the expected sentiment score
        // calculate expected SentiWordNet3.0 score for masked
        // List of all definitions for masked in SentiWordNet3.0:
        //
        // a	01707230	0.25	0.375	masked#1 disguised#1 cloaked#1	having its true character concealed with the intent of misleading; "hidden agenda"; "masked threat"
        // a	01481014	0	0.625	masked#2	having markings suggestive of a mask; "the masked face of a raccoon"
        double totalScoreB = ((0.25 - 0.375) / 1.0)  + ((0.0 - 0.625) / 2.0);
        double sumB = 1.0 / 1.0 + 1.0 /2.0;
        double expectedB = totalScoreB / sumB;
        double actualB = swnTest.getFeatureSentimentScore("masked");

        System.out.println("expectedB value is: " + expectedB);
        System.out.println("actualB value is: " + actualB);
        assert(Math.abs(expectedB - actualB) < 0.000000001);

        // test that a word with eleven SWN records generates the expected sentiment score
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

        double adjectiveTotalScoreC = ((0.25 - 0.375) / 1.0)  + ((0.0 -	0.375) / 2.0) + ((0.0 -	0.75) / 3.0)
                                    + ((0.0 - 0.75) / 4.0) + ((0.0 - 0.375) / 5.0)  + ((0.5 - 0.0) / 6.0);
        double adjectiveSumC = 1.0 / 1.0 + 1.0 /2.0 + 1.0 /3.0 + 1.0 /4.0 + 1.0 /5.0 + 1.0 /6.0;
        double adjectiveExpectedC = adjectiveTotalScoreC / adjectiveSumC;

        double verbTotalScoreC = ((0.0 - 0.125) / 1.0)  + ((0.25 - 0.375) / 2.0) + ((0.0 - 0.25) / 3.0)
                                + ((0.0 - 0.0) / 4.0) + ((0.0 - 0.25) / 5.0);
        double verbSumC = 1.0 / 1.0 + 1.0 /2.0 + 1.0 /3.0 + 1.0 /4.0 + 1.0 /5.0;
        double verbExpectedC = verbTotalScoreC / verbSumC;

        double expectedC = (adjectiveExpectedC + verbExpectedC) / 2.0;
        double actualC = swnTest.getFeatureSentimentScore("obscure");

        System.out.println("expectedC value is: " + expectedC);
        System.out.println("actualC value is: " + actualC);
        assert(Math.abs(expectedC - actualC) < 0.000000001);


        // test that a word with two SWN records generates the expected sentiment score
        // calculate expected SentiWordNet3.0 score for bondable
        // List of all definitions for bondable in SentiWordNet3.0:
        //
        // a	00161684	0.5	0	bondable#1 bindable#1	capable of being fastened or secured with a rope or bond
        // a	00053248	0.25	0	bondable#2	capable of holding together or cohering; as particles in a mass
        double totalScoreD = ((0.5 - 0.0) / 1.0)  + ((0.25 - 0) / 2.0);
        double sumD = 1.0 / 1.0 + 1.0 / 2.0;
        double expectedD = totalScoreD / sumD;
        double actualD = swnTest.getFeatureSentimentScore("bondable");
        System.out.println("expectedD value is: " + expectedD);
        System.out.println("actualD value is: " + actualD);
        assert(Math.abs(expectedD - actualD) < 0.000000001);
    }
}