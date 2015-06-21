/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package GUI;

import Core.Utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED <mohamedachraf@gmail.com>
 */
public class UniqSentenceFromTwoFiles {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        Utils utils = new Utils();
        String ParsedSentencesFile = "data/PCFG/ParsedSentences.txt";
        String SimpleSentencesFile = "data/PCFG/sentencesList.txt";
        
        ArrayList<String> Sentences = new ArrayList<>();
        ArrayList<String> NotParsedSentences = new ArrayList<>();
        ArrayList<String> ParsedSentences = utils.LoadFileToArrayListByLine(ParsedSentencesFile);
        ArrayList<String> SimpleSentences = utils.LoadFileToArrayListByLine(SimpleSentencesFile);
        
        for(String S : ParsedSentences){
            if(!S.contains(")")){
                Sentences.add(S);
            }
        }
        
        for(String S : SimpleSentences){
            if(!ParsedSentences.contains(S)){
                NotParsedSentences.add(S);
            }
        }
        
        for (int i = 0; i < NotParsedSentences.size(); i++) {
            System.out.println("\t"+NotParsedSentences.get(i));
        }
        
        
    }
    
}
