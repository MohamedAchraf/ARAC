package aracplatform;

import Core.Corpus;
import Core.MissingWord;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED<mohamedachraf@wanadoo.fr>
 */
public class GuessAMissingWord {

   public double Guess(File DevSet, ArrayList<ArrayList<String>> sentences, Boolean scheme) throws FileNotFoundException{
                     
       Corpus corpus = new Corpus();
       ArrayList<String> corpora = corpus.LoadCorpus(DevSet, Boolean.TRUE);
       ArrayList<String> Vocab = corpus.Vocabulary(corpora); 
              
       Map<String, Double> CountUnigram = corpus.CountUnigrams(corpus.LoadUniGrams(corpora));
       Map<String, Double> CountBigram  = corpus.CountBigrams (corpus.LoadBigrams (corpora));
       Map<String, Double> CountTrigram = corpus.CountTrigrams(corpus.LoadTrigrams(corpora));
       
       int N = corpus.HoleCorpus.size(),
               V = corpus.V;
       
       MissingWord missingWord = new MissingWord();
    
       double guessed = 0.0, notCounted = 0.0;
       for (ArrayList<String> sentence : sentences) {
           int result = missingWord.Guess(sentence, Vocab, CountUnigram, CountBigram, CountTrigram, N, V, scheme); 
           if (result == 1) {
               guessed++;
           }else if (result == -1) {
               notCounted++;
           }
       }
       
       //--------
       System.out.print("\n GUESSED     :"+ guessed);
       double v  = sentences.size()- notCounted - guessed;
       System.out.print("\n NOT GUESSED :" + v);
       System.out.print("\n NOT COUNTED :" + notCounted);
       
      // System.out.print("\n" +CountTrigram+"\n" +CountBigram+"\n" +CountUnigram);
       
   
       
       //--------
       return ((guessed / (sentences.size()- notCounted))*100);
   }
}
