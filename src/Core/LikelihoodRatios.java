package Core;

import java.util.Map;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED<mohamedachraf@wanadoo.fr>
 */
public class LikelihoodRatios {

    /**
     * Extraction collocations using the likelihood ratios method which is an
     * hypothesis testing method : 
     *      -> Hypothesis H1 : P(w2 | w1 ) = p = P(w2 |¬w1 ) 
     *      -> Hypothesis H2 : P(w2 | w1 ) = p1 != p2 = P(w2 | ¬w1 )
     *
     * log λ = log L(H1)/L(H2) 
     *       = log( ( L(c12, c1,p )*L(c2 - c12, n-c1, p )) /
     *              ( L(c12, c1,p1)*L(c2 - c12, n-c1, p2)) ) 
     *       = log L(c12, c1,p ) + log L(c2 - c12, n-c1, p ) - 
     *         log L(c12, c1,p1) - log L(c2 - c12, n-c1, p2)
     *
     * With L(k, n , x) = (x^k)*[(1-x)^(n-k)] , 
     *      p  = c2 /n , 
     *      p1 = c12 / c1, 
     *      p2 = (c1 - c12) / (n -c1).
     */
    
    /**
     *
     * @param k
     * @param n
     * @param x
     * @return L(k, n , x) = (x^k)*[(1-x)^(n-k)].
     */
    public Double L(Double k, Double n, Double x) {
        return Math.pow(x, k) * Math.pow((1 - x), (n - k));
    }

    /**
     *
     * @param bigram
     * @param N
     * @param countUnigrams
     * @param countBigrams
     * @return Likelihood Ratios
     */
    public double LikelihoodRatiosCompute(String bigram,
            int N,
            Map<String, Double> countUnigrams,
            Map<String, Double> countBigrams) {

        Double p = 0.0, p1 = 0.0, p2 = 0.0, c1 = 0.0, c2 = 0.0, c12 = 0.0;
        String[] words = bigram.split("\\s");
        String w1 = words[0],
                w2 = words[1];

        c1 = countUnigrams.get(w1);
        c2 = countUnigrams.get(w2);
        c12 = countBigrams.get(w1 + " " + w2);
        p = c2 / N;
        p1 = c12 / c1;
        p2 = (c2 - c12) / (N - c1);

        Double logλ =
                Math.log(L(c12, c1, p))
                + Math.log(L(c2 - c12, N - c1, p))
                - Math.log(L(c12, c1, p1))
                - Math.log(L(c2 - c12, N - c1, p2));

        return -2 * logλ;
    }
}
