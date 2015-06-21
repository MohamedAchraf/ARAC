
package Core;

import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED<mohamedachraf@wanadoo.fr>
 */
public class CustumModelScore {
    
    /**
     * The Algorithm used is a hybrid algorithm combining linear interpolation 
     * and Back Off algorithms with n=3.
     *  
     */
    

    /**
     * 
     * @param sentence
     * @param CountingUnigram
     * @param CountingBigrams
     * @param CountingTrigrams
     * @param N
     * @param V
     * @return Sentence score
     */
    public double Score(ArrayList<String> sentence,
            Map<String, Double> CountingUnigram,
            Map<String, Double> CountingBigrams,
            Map<String, Double> CountingTrigrams,
            int N,
            int V) {

        double score = 0.0;
        for (int k = 0; k < sentence.size() - 2; k++) {
            String word1 = sentence.get(k);
            String word2 = sentence.get(k + 1);
            String word3 = sentence.get(k + 2);

            if (CountingTrigrams.get(word1 + " " + word2 + " " + word3) != null) {
                double lambda1 = 7 / 100, lambda2 = 14 / 100, lambda3 = (1 - (lambda1 + lambda2));
                score += (double) (lambda3 * ((double) CountingTrigrams.get(word1 + " " + word2 + " " + word3)) / ((double) CountingBigrams.get(word1 + " " + word2)))
                        + (double) (lambda2 * ((double) CountingBigrams.get(word2 + " " + word3)) / (CountingUnigram.get(word2)))
                        + (double) (lambda1 * ((double) CountingUnigram.get(word3) + 1) / (N + V));


            } else if (CountingBigrams.get(word2 + " " + word3) != null) {
                double lambda1 = 14 / 100, lambda2 = (1 - lambda1);
                score += (double) (lambda2 * ((double) CountingBigrams.get(word2 + " " + word3)) / (CountingUnigram.get(word2)))
                        + (double) (lambda1 * ((double) CountingUnigram.get(word3) + 1) / (N + V));


            } else if (CountingUnigram.get(word3) != null) {
                score += (double) (((double) CountingUnigram.get(word3) + 1) / (N + V));
            }

        }
        return score;
    }
}
