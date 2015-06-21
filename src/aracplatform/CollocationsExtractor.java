package aracplatform;

import Core.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED<mohamedachraf@wanadoo.fr>
 */
public class CollocationsExtractor {

    ArrayList<String> Trigrams = new ArrayList<>();
    Map<String, Double> countTrigrams = new HashMap<>();
    String document;

    /**
     *
     * @param DevSetSourceFile
     * @param NumberOfCollocationsToBeExtracted
     * @param UsedMethod 1:Likelihood Ratios, 2:Pointwise Mutual Information, 3:TTest
     * @return collocation.
     * @throws FileNotFoundException
     */
    public Map<String, Double> ExtractTwoWordsSizeCollocationsFromFile(File DevSetSourceFile, int NumberOfCollocationsToBeExtracted, int UsedMethod) throws FileNotFoundException {

        Map<String, Double> collocations = new HashMap<>();
        Map<String, Double> returnedCollocations = new HashMap<>();

        ArrayList<String> corpora = new ArrayList<>();
        ArrayList<String> Bigrams = new ArrayList<>();
        Map<String, Double> countUnigrams;
        countUnigrams = new HashMap<>();
        Map<String, Double> countBigrams = new HashMap<>();

        Corpus DevSet = new Corpus();
        corpora = DevSet.LoadCorpus(DevSetSourceFile, Boolean.TRUE);
        Bigrams = DevSet.LoadBigrams(corpora);
        Trigrams = DevSet.LoadTrigrams(corpora);
        countUnigrams = DevSet.CountUnigrams(corpora);
        countBigrams = DevSet.CountBigrams(Bigrams);
        countTrigrams = DevSet.CountTrigrams(corpora);
        document = DevSet.HoleText;

        int N = corpora.size();
        switch (UsedMethod) {
            case 1: // First method : Likelihood Ratios.
                Core.LikelihoodRatios collocation1 = new LikelihoodRatios();
                for (String bigram : Bigrams) {
                    double logλ = collocation1.LikelihoodRatiosCompute(bigram, N, countUnigrams, countBigrams);
                    if (logλ < Double.MAX_VALUE && DevSet.HoleText.contains(bigram)) {
                        collocations.put(bigram.toString(), (double) logλ);
                    }
                }
                break;

            case 2: // Second method : Pointwise Mutual Information.
                Core.PointwiseMutualInformation collocation2 = new PointwiseMutualInformation();
                for (String bigram : Bigrams) {
                    double logλ = collocation2.PointwiseMutualInformationCompute(bigram, N, countUnigrams, countBigrams);
                    if (logλ < Double.MAX_VALUE && DevSet.HoleText.contains(bigram)) {
                        collocations.put(bigram.toString(), (double) logλ);
                    }
                }
                break;

            case 3: // Third method : T Test
                Core.TTest collocation3 = new TTest();
                for (String bigram : Bigrams) {
                    double logλ = collocation3.TTextCompute(bigram, N, countUnigrams, countBigrams);
                    if (logλ < Double.MAX_VALUE && DevSet.HoleText.contains(bigram)) {
                        collocations.put(bigram.toString(), (double) logλ);
                    }
                }
                break;

        }

        // Sorting Map Collocations by values 
        // Source downloaded from : 
        // http://www.mkyong.com/java/how-to-sort-a-map-in-java/
        // (modified)        
        // Begin----------------------------------------------------------------
        List list = new LinkedList(collocations.entrySet());
        //sort list based on comparator
        Collections.sort(list, new Comparator() {

            @Override
            public int compare(Object o2, Object o1) {
                return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        //put sorted list into map again
        int i = 0;
        Map CollocationsSorted = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            i++;
            if (i <= NumberOfCollocationsToBeExtracted) {
                CollocationsSorted.put(entry.getKey(), entry.getValue());
            } else {
                break;
            }
        }
        // End------------------------------------------------------------------

        return CollocationsSorted;
    }

    /**
     * ----------+ 
     * Algorithm | 
     * ----------+
     * -------------------------- 
     * IF (AB and BC are collocations) and (ABC in corpus) 
     *      THEN ABC is collocation
     * -------------------------------------
     *
     * @param DevSetSourceFile
     * @param NumberOfCollocationsToBeExtracted
     * @param UsedMethod
     * @return Three Words Size Collocations
     * @throws FileNotFoundException
     */
    public Map<String, Double> ExtractThreeWordsSizeCollocationsFromFile(File DevSetSourceFile, int NumberOfCollocationsToBeExtracted, int UsedMethod) throws FileNotFoundException {
        Map<String, Double> collocations3w = new HashMap<>();
        Map<String, Double> collocations2w = ExtractTwoWordsSizeCollocationsFromFile(DevSetSourceFile, NumberOfCollocationsToBeExtracted, UsedMethod);

        for (Map.Entry entrie1 : collocations2w.entrySet()) {
            String Words[] = entrie1.getKey().toString().split("\\s");
            String w1 = Words[0],
                    w2 = Words[1];
            //----------
            for (Map.Entry entrie2 : collocations2w.entrySet()) {
                String Word[] = entrie2.getKey().toString().split("\\s");
                String ww1 = Word[0],
                        ww2 = Word[1];
                if (w2.equals(ww1) && document.contains(w1 + " " + w2 + " " + ww2)) {
                    collocations3w.put(w1 + " " + w2 + " " + ww2, 0.0);
                }

            }
            //----------
        }
        return collocations3w;
    }

    /**
     * ----------+ 
     * Algorithm | 
     * ----------+
     * -------------------------- 
     * IF (AB and BC and CD are collocations) and (ABCD in corpus) 
     *         THEN ABCD is collocation
     * -------------------------------------
     *
     * @param DevSetSourceFile
     * @param NumberOfCollocationsToBeExtracted
     * @param UsedMethod
     * @return
     * @throws FileNotFoundException
     */
    public Map<String, Double> ExtractFoorWordsSizeCollocationsFromFile(File DevSetSourceFile, int NumberOfCollocationsToBeExtracted, int UsedMethod) throws FileNotFoundException {
        Map<String, Double> collocations4w = new HashMap<>();
        Map<String, Double> collocations2w = ExtractTwoWordsSizeCollocationsFromFile(DevSetSourceFile, NumberOfCollocationsToBeExtracted, UsedMethod);

        for (Map.Entry entrie1 : collocations2w.entrySet()) {
            String Words1[] = entrie1.getKey().toString().split("\\s");
            String w1 = Words1[0],
                    w2 = Words1[1];
            //----------
            for (Map.Entry entrie2 : collocations2w.entrySet()) {
                String Words2[] = entrie2.getKey().toString().split("\\s");
                String ww1 = Words2[0],
                        ww2 = Words2[1];
                //-----------------------------
                for (Map.Entry entrie3 : collocations2w.entrySet()) {
                    String Words3[] = entrie3.getKey().toString().split("\\s");
                    String www1 = Words3[0],
                            www2 = Words3[1];
                    if (w2.equals(ww1) &&
                            ww2.equals(www1) &&                            
                            document.contains(w1 + " " + w2 + " " + ww2 + " " + www2)) {
                        collocations4w.put(w1 + " " + w2 + " " + ww2 + " " + www2, 0.0);
                    }
                    //-----------------------------
                }

            }
            //----------
        }
        return collocations4w;
    }
    
     
    /**
     * 
     * ----------+ 
     * Algorithm | 
     * ----------+
     * -------------------------- 
     * IF (AB and BC and CD and DE are collocations) and (ABCDE in corpus) 
     *         THEN ABCDE is collocation
     * -------------------------------------
     * 
     * @param DevSetSourceFile
     * @param NumberOfCollocationsToBeExtracted
     * @param UsedMethod
     * @return
     * @throws FileNotFoundException 
     */
    public Map<String, Double> ExtractFiveWordsSizeCollocationsFromFile(File DevSetSourceFile, int NumberOfCollocationsToBeExtracted, int UsedMethod) throws FileNotFoundException {
        Map<String, Double> collocations5w = new HashMap<>();
        Map<String, Double> collocations2w = ExtractTwoWordsSizeCollocationsFromFile(DevSetSourceFile, NumberOfCollocationsToBeExtracted, UsedMethod);

        for (Map.Entry entrie1 : collocations2w.entrySet()) {
            String Words1[] = entrie1.getKey().toString().split("\\s");
            String w1 = Words1[0],
                    w2 = Words1[1];
            //----------
            for (Map.Entry entrie2 : collocations2w.entrySet()) {
                String Words2[] = entrie2.getKey().toString().split("\\s");
                String ww1 = Words2[0],
                        ww2 = Words2[1];
                //-----------------------------
                for (Map.Entry entrie3 : collocations2w.entrySet()) {
                    String Words3[] = entrie3.getKey().toString().split("\\s");
                    String www1 = Words3[0],
                            www2 = Words3[1];
                    //------------------------------
                    for (Map.Entry entrie4 : collocations2w.entrySet()) {
                        String Words4[] = entrie4.getKey().toString().split("\\s");
                        String wwww1 = Words4[0],
                                wwww2 = Words4[1];
                        if (w2.equals(ww1)
                                && ww2.equals(www1)
                                && www2.equals(wwww1)
                                && document.contains(w1 + " " + w2 + " " + ww2 + " " + www2 + " " + wwww2)) {
                            Double put = collocations5w.put(w1 + " " + w2 + " " + ww2 + " " + www2 + " " + wwww2, 0.0);
                        }
                    }
                    //-----------------------------
                }

            }
            //----------
        }
        return collocations5w;
    }
}
