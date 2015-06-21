package Core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED<mohamedachraf@wanadoo.fr>
 */
public class Corpus {
    
    public String HoleText; // String containing all the corpus.
    public List<String> HoleCorpus = new ArrayList<>(); 
    public int V;
    /**
     * The class corpus provide some useful tools allowing handling with
     * corpora.
     *
     */
    
    /**
     * The Boolean loadArabicStopListWords indicates if the the script should
     * or not include a stop list words.
     *
     * @param corpusfilesource
     * @param loadArabicStopListWords
     * @return
     * @throws FileNotFoundException
     */
    public ArrayList<String> LoadCorpus(File corpusfilesource, Boolean loadArabicStopListWords) throws FileNotFoundException {

        ArrayList<String> Corpus = new ArrayList<>();
        ArrayList<String> ArabicStopListWords = new ArrayList<>();
        Scanner x;
        if (loadArabicStopListWords) { // Loading ArabicStopListWords     
            File StopList = new File("data/include/arabicStopListWords.txt");
            x = new Scanner(StopList);
            while (x.hasNextLine()) {
                String string = x.nextLine();
                ArabicStopListWords.add(string);
            }
        }

        // Lading Unigrams
        x = new Scanner(corpusfilesource);
        while (x.hasNext()) {
            String string = x.next();
            HoleText += string+" ";
            HoleCorpus.add(string);
            if (!ArabicStopListWords.contains(string) && string.length() > 1) {
                string = string.replaceAll("([^آةاأبتثجحخدذرزسشصضطظعغفقكلمنهـويإئءؤًٌٍَُِّْى])", "");

                Corpus.add(string.trim());
            }
        }
        V = Corpus.size();
        return Corpus;
    }

    /**
     *
     * @param corpora
     * @return vocabulary
     */
    public ArrayList<String> LoadVocabulary(ArrayList<String> corpora) {

        ArrayList<String> Unigrams = new ArrayList<>();
        for (int i = 0; i < corpora.size(); i++) {
            if (!Unigrams.contains(corpora.get(i))) {
                Unigrams.add(corpora.get(i));
            }
        }
        return Unigrams;
    }
    
     public ArrayList<String> LoadUniGrams(ArrayList<String> corpora) {

        ArrayList<String> Unigrams = new ArrayList<>();
        for (int i = 0; i < corpora.size(); i++) {            
                Unigrams.add(corpora.get(i));
            }
        
        return Unigrams;
    }

    /**
     *
     * @param corpora
     * @return Bigrams
     */
    public ArrayList<String> LoadBigrams(ArrayList<String> corpora) {

        ArrayList<String> Bigrams = new ArrayList<>();
        for (int i = 0; i < corpora.size() - 1; i++) {
            if (corpora.get(i).length() > 0 && corpora.get(i + 1).length() > 0) {
                Bigrams.add(corpora.get(i) + " " + corpora.get(i + 1));
            }
        }
        return Bigrams;
    }

    /**
     *
     * @param corpora
     * @return Trigrams
     */
    public ArrayList<String> LoadTrigrams(ArrayList<String> corpora) {

        ArrayList<String> Trigrams = new ArrayList<>();
        for (int i = 0; i < corpora.size() - 2; i++) {
            Trigrams.add(corpora.get(i) + " " + corpora.get(i + 1) + " " + corpora.get(i + 2));
        }
        return Trigrams;
    }

    /**
     *
     * @param corpora
     * @return countUnigrams
     */
    public Map<String, Double> CountUnigrams(ArrayList<String> corpora) {

        Map<String, Double> countUnigrams = new HashMap<>();
        for (int i = 0; i < corpora.size(); i++) {
            countUnigrams.put(corpora.get(i), (double) Collections.frequency(corpora, corpora.get(i)));
        }

        return countUnigrams;
    }

    /**
     *
     * @param corpora
     * @return countBigrams
     */
    public Map<String, Double> CountBigrams(ArrayList<String> corpora) {

        Map<String, Double> countBigrams = new HashMap<>();
        for (int i = 0; i < corpora.size(); i++) {
            countBigrams.put(corpora.get(i), (double) Collections.frequency(corpora, corpora.get(i)));
        }

        return countBigrams;
    }

    /**
     *
     * @param corpora
     * @return countTrigrams
     */
    public Map<String, Double> CountTrigrams(ArrayList<String> corpora) {

        Map<String, Double> countTrigrams = new HashMap<>();
        for (int i = 0; i < corpora.size(); i++) {
            countTrigrams.put(corpora.get(i), (double) Collections.frequency(corpora, corpora.get(i)));
        }

        return countTrigrams;
    }
    
    /**
     * 
     * 
     * @param corpora
     * @return vocabulary
     * @throws FileNotFoundException 
     */
    public ArrayList<String> Vocabulary(ArrayList<String> corpora) throws FileNotFoundException {

        ArrayList<String> ArabicStopListWords = new ArrayList<>();
        ArrayList<String> Vocab = new ArrayList<>();
        File StopList = new File("data/include/arabicStopListWords.txt");

        Scanner x = new Scanner(StopList);
        while (x.hasNextLine()) {
            String string = x.nextLine();
            ArabicStopListWords.add(string);
        }

        for (String w : corpora) {
            if (!Vocab.contains(w) &&
                    !ArabicStopListWords.contains(w)) {
                Vocab.add(w);
            }
        }

        return Vocab;
    }
}
