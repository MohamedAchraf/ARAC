package Core;

import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED<mohamedachraf@wanadoo.fr>
 */
public class MissingWord {

    /**
     * Try to guess Missing word; return true if success
     *
     * @param sentence
     * @param Vocabulary
     * @param CountingUnigram
     * @param CountingBigrams
     * @param CountingTrigrams
     * @param N
     * @param V
     * @return
     */
    public Integer Guess(ArrayList<String> sentence,
            ArrayList<String> Vocabulary,
            Map<String, Double> CountingUnigram,
            Map<String, Double> CountingBigrams,
            Map<String, Double> CountingTrigrams,
            int N,
            int V,
            Boolean scheme) {

        ArrayList<String> PrunedSentence = new ArrayList<String>();
        for (int i = 0; i < sentence.size() - 1; i++) {
            PrunedSentence.add(sentence.get(i));
        }


        double MaxScore = 0.0;
        String MissingWord = "", MissingWord1 = "", MissingWord2 = "", MissingWord3 = "";
        CustumModelScore custumModelScore = new CustumModelScore();

        for (String word : Vocabulary) {
            if (scheme) {
                if (word.contains("ف") && word.contains("ع") && word.contains("ل")) {
                    ArrayList<String> phrase = new ArrayList<String>();
                    phrase.addAll(PrunedSentence);
                    phrase.add(word);

                    double score = custumModelScore.Score(phrase, CountingUnigram, CountingBigrams, CountingTrigrams, N, V);
                    if (score > MaxScore) {
                        MaxScore = score;
                        MissingWord3 = MissingWord2;
                        MissingWord2 = MissingWord1;
                        MissingWord1 = MissingWord;
                        MissingWord = word;
                    }
                }
            } else {
                ArrayList<String> phrase = new ArrayList<String>();
                phrase.addAll(PrunedSentence);
                phrase.add(word);

                double score = custumModelScore.Score(phrase, CountingUnigram, CountingBigrams, CountingTrigrams, N, V);
                if (score > MaxScore) {
                    MaxScore = score;
                    MissingWord3 = MissingWord2;
                    MissingWord2 = MissingWord1;
                    MissingWord1 = MissingWord;
                    MissingWord = word;
                }
            }
        }
   
//        if ( ( MaxScore / (double)sentence.size() ) <= (2.0 / (double) (N + V)) ) {
//            System.out.println("NOT COUNTED\t[" + MissingWord + "]"
//                    + "[" + MissingWord1 + "]"
//                    + "[" + MissingWord2 + "]"
//                    + "[" + MissingWord3 + "]\t" + sentence + "\t" + MaxScore);
//            return -1;
//        }

        if (MissingWord.equals(sentence.get(sentence.size() - 1))) {
            System.out.println("GUESSED    \t[**" + MissingWord + "**]" 
                + "[**" + MissingWord1 + "**]"
                + "[**" + MissingWord2 + "**]"
                + "[**" + MissingWord3 + "**]\t" + sentence + "\t" + MaxScore);
            return 1;
        }

        System.out.println("NOT GUESSED\t[" + MissingWord + "]"
                + "[" + MissingWord1 + "]"
                + "[" + MissingWord2 + "]"
                + "[" + MissingWord3 + "]\t" + sentence + "\t" + MaxScore);
        return 0;
    }
}
