/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Core;

import java.util.Map;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED<mohamedachraf@wanadoo.fr>
 */
public class PointwiseMutualInformation {
    
    /**
     * Compute log in base 2
     * 
     * @param x
     * @return log[2]X = ln X / ln 2
     */
    public double LogBase2(double x) {

        return Math.log(x) / Math.log(2);
    }

    
    /**
     * PMI measures information shared by w1 and w2 how much knowing one word 
     * reduces uncertainty about the other.
     * 
     * @param bigram
     * @param N
     * @param countUnigrams
     * @param countBigrams
     * @return PMI(W1, w2) = log ( P(w1,w2) / ( p(w1) P(w2) ) )
     */
    public double PointwiseMutualInformationCompute(String bigram, int N, Map<String, Double> countUnigrams, Map<String, Double> countBigrams) {
        
        double p1 = 0.0, p2 = 0.0, p12 = 0.0;
        
        String[] words = bigram.split("\\s");
        String w1 = words[0],
                w2 = words[1];
        
        p1  = countUnigrams.get(w1)/N; 
        p2  = countUnigrams.get(w2)/N;
        p12 = countBigrams.get(w1+" "+w2)/N;
                
        
        return LogBase2(p12/(p1*p2));
    }
    
}
