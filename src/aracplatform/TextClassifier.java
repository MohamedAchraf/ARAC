package aracplatform;

import Core.Corpus;
import Core.CustumModelScore;
import Core.Utils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED<mohamedachraf@wanadoo.fr>
 */
public class TextClassifier {

    /**
     *
     * @param textFile : Document to classify
     * @param direcory : of file used as language model (Training Set)
     * @param Accuracy : number of files to be taken in concideration
     * @return class name
     * @throws IOException
     */
    public String TextClassify(File textFile, File direcory, int Accuracy, int NbrWords) throws IOException {

        // System.out.print(textFile.getName()+" :");
        Double MaxScore = 0.0;
        ArrayList<String> ClassNames = new ArrayList<>();
        int ClassOrder = 0, MaxClassOrder = 0;

        Utils utils = new Utils();
        ArrayList Sentence = utils.LoadFileToArrayList(textFile);

        for (File dir : direcory.listFiles()) {
            int NbrFiles = Accuracy; //(Integer) ((dir.listFiles().length * Accuracy) / 100);

            Corpus corpus = new Corpus();
            CustumModelScore custumModelScore = new CustumModelScore();

            ClassNames.add(dir.getName());
            File totalFile = new File(dir.getPath() + "/_______Total_______.txt");
            if (!totalFile.exists()) {
                totalFile.createNewFile();
            }

            utils.MergeTextFiles(dir, totalFile, NbrFiles, NbrWords);
            ArrayList<String> corpora = corpus.LoadCorpus(totalFile, Boolean.TRUE);
            ArrayList<String> Bigrams = corpus.LoadBigrams(corpora);

            Map<String, Double> countUnigrams = corpus.CountUnigrams(corpora);
            Map<String, Double> countBigrams = corpus.CountBigrams(Bigrams);
            Map<String, Double> countTrigrams = corpus.CountTrigrams(corpora);

            double Score = custumModelScore.Score(Sentence,
                    countUnigrams,
                    countBigrams,
                    countTrigrams,
                    corpus.HoleCorpus.size(),
                    corpus.V);

            if (Score > MaxScore) {
                MaxScore = Score;
                MaxClassOrder = ClassOrder;
            }

            // if(!totalFile.delete()) {}            
            ClassOrder++;
        }


        return ClassNames.get(MaxClassOrder);
    }

    
    /**
     * Guess class for a list of texts in the directory DirectoryOfTexts
     * @param DirectoryOfTexts
     * @param LanguageModelDirecory
     * @param Accuracy
     * @return list of file and correspondent class
     * @throws IOException 
     */
    public Map<String, String> TextsListClassify(File DirectoryOfTexts, File LanguageModelDirecory, int Accuracy, int NbrWords) throws IOException {

        // System.out.print(textFile.getName()+" :");
        ArrayList<Map<String, Double>> countUnigrams = new ArrayList<>();
        ArrayList<Map<String, Double>> countBigrams = new ArrayList<>();
        ArrayList<Map<String, Double>> countTrigrams = new ArrayList<>();
        Map<String, String> result = new HashMap<>();
        ArrayList<String> ClassNames = new ArrayList<>();
        ArrayList<Integer> corpusSize = new ArrayList<>();
        ArrayList<Integer> vocabulary = new ArrayList<>();

        int MaxClassOrder = 0;

        Utils utils = new Utils();
        Corpus corpus = new Corpus();
        CustumModelScore custumModelScore = new CustumModelScore();


        // Loading Model caracteristics (counts)
        for (File dir : LanguageModelDirecory.listFiles()) {
            int NbrFiles = Accuracy; //(Integer) ((dir.listFiles().length * Accuracy) / 100);

            ClassNames.add(dir.getName());
            File totalFile = new File(dir.getPath() + "/ZZZZTotal_______.txt");
            if (!totalFile.exists()) {
                totalFile.createNewFile();
            }

            utils.MergeTextFiles(dir, totalFile, NbrFiles, NbrWords);
            ArrayList<String> corpora = corpus.LoadCorpus(totalFile, Boolean.TRUE);
            ArrayList<String> Bigrams = corpus.LoadBigrams(corpora);

            countUnigrams.add(corpus.CountUnigrams(corpora));
            countBigrams.add(corpus.CountBigrams(Bigrams));
            countTrigrams.add(corpus.CountTrigrams(corpora));
            corpusSize.add(corpus.HoleCorpus.size());
            vocabulary.add(corpus.V);
        }

        // Guess class
        for (File text : DirectoryOfTexts.listFiles()) {
            Double MaxScore = 0.0;
            for (int i = 0; i < countUnigrams.size(); i++) {
                ArrayList<String> Sentence = utils.LoadFileToArrayList(text);

                double Score = custumModelScore.Score(
                        Sentence,
                        countUnigrams.get(i),
                        countBigrams.get(i),
                        countTrigrams.get(i),
                        corpusSize.get(i),
                        vocabulary.get(i));

                if (Score > MaxScore) {
                    MaxScore = Score;
                    MaxClassOrder = i;
                }

            }

            result.put(text.getName(), ClassNames.get(MaxClassOrder));

        }

        return result;

    }
}
