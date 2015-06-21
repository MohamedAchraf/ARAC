package Core;

import java.util.Map;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED<mohamedachraf@wanadoo.fr>
 */
public class TTest {
    /*
     * The test looks at the difference between the observed and expected means,
     * (x ̅ and μ) scaled by the variance of the data, and tells us how likely
     * one is to get a sample of that mean and variance.
     *
     * t = (x ̅ - μ) / sqrt(S^2 / N )
     * 
     * where :
     *     →  N = corpus size.
     *     →  x ̅ = count(bigram) / N.
     *     →  μ  = P(1-P) since for most bigram P is small═>P(1-P)~P═>μ = P
     *        with P = P(w1,w2) = P(w1)*P(w2).
     *     →  S^2 = n*P(1-P) , since n=1 (number of trial) S^2 = P.     
     *
     */

    /**
     * 
     * @param bigram
     * @param N
     * @param countUnigrams
     * @param countBigrams
     * @return t = (x ̅ - μ) / sqrt(S^2 / N )
     */
    public double TTextCompute(String bigram, int N, Map<String, Double> countUnigrams, Map<String, Double> countBigrams) {
        
        double observedMeanX = 0.0, expectedMeanμ = 0.0, SqrS = 0.0 ;
        String words[] = bigram.split("\\s"),
                w1 = words[0],
                w2 = words[1];
        
        observedMeanX = countBigrams.get(bigram) / N ;
        expectedMeanμ = (countUnigrams.get(w1)* countUnigrams.get(w2)) / Math.pow(N, 2); 
        SqrS = observedMeanX;
        
        return ( observedMeanX - expectedMeanμ ) / Math.sqrt( SqrS / N );       
    }
}
