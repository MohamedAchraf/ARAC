/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aracplatform;

import Core.Utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED <mohamedachraf@gmail.com>
 */
public class ConvertArrayOfSentencesToSchemes {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        SchemesParser converter = new SchemesParser();
        Utils utils = new Utils();
        ArrayList<String> sentences = new ArrayList<>();
        ArrayList<String> sentencesConverted = new ArrayList<>();
        File file = new File("D:/ML/iparse/data/2000_sentences_Mashkoul.txt");

        sentences = utils.LoadFileToArrayListByLine_old(file);
        int cpt =0;
        sentencesConverted = converter.SchemesConvertArrayOfSentences(sentences, Boolean.TRUE, Boolean.FALSE);

        for (String sentence : sentencesConverted) {
            String[] words = sentence.replaceAll("\\s+", " ").trim().split(" ");
            String Sc = "";
            for (int i = 0; i < words.length; i++) {
                if (words[i].contains("|") && words[i].length() > 1) {
                    //System.out.println("words[i] = "+words[i]);
                    String[] ScWo = words[i].split("\\|");
                    Sc = Sc + ScWo[0]+" ";
                }                
                
            }            
            System.out.println("[Sc]\t"+Sc);
            //System.out.println("[Sc]\t"+Sc+"\n[Ph]\t"+sentences.get(cpt++));
        }

    }

}
