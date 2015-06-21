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
public class parsedSentencesCleaner {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        Utils utils = new Utils();
        ArrayList<String> content = new ArrayList<>();
        ArrayList<String> parses = new ArrayList<>();
        ArrayList<String> sentences = new ArrayList<>();
         ArrayList<String> parsesUniq = new ArrayList<>();
        ArrayList<String> sentencesUniq = new ArrayList<>();

        File file = new File("D:\\Development\\java_projects\\AracPlatform_V0.1\\data\\PCFG\\ParsedSentences.txt");
        content = utils.LoadFileToArrayListByLine(file.getAbsolutePath());

        for (String S : content) {
            if (S.contains("(")) {
                parses.add(S);
            } else {
                sentences.add(S);
            }
        }
        
        for (int i = 1; i < sentences.size(); i++) {
            if(!sentencesUniq.contains(sentences.get(i))){
                sentencesUniq.add(sentences.get(i));
                parsesUniq.add(parses.get(i-1));
            }else{
                System.err.println("\n[DUPLICATE]\t"+i+"\t"+ sentences.get(i)+"\n");
            }
        }
        int ind=1;
        for (int i = 0; i < sentencesUniq.size(); i++) {            
            System.out.println(sentencesUniq.get(i));
            ind++;
        }

    }

}
