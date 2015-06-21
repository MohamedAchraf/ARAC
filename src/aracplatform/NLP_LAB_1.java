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
import java.util.Collections;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED <mohamedachraf@gmail.com>
 */
public class NLP_LAB_1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        Utils utils = new Utils();
        File textFile = new File("D:\\Development\\java_projects\\NLP_LAB\\data\\8k_VOY_TX.txt");
        File SchemeFile = new File("D:\\Development\\java_projects\\NLP_LAB\\data\\8k_VOY_SC.txt");
        
        SchemesParser parser = new SchemesParser();
        
        ArrayList<String> text = utils.LoadFileToArrayList(textFile);
        ArrayList<String> schemes = utils.LoadFileToArrayList(SchemeFile);
        ArrayList<String> schemesClean = utils.LoadFileToArrayList(SchemeFile);
        ArrayList<String> schemesUniq = new ArrayList<>();
        ArrayList<Integer> schemesCount = new ArrayList<>();
        
        
        for (int i = 0; i < schemes.size(); i++) {
            //schemesClean.add(schemes.get(i).split("\\|")[0]);
            
        }
        
                
        for (int i = 0; i < schemesClean.size(); i++) {
            String S = schemesClean.get(i);
            if(!schemesUniq.contains(S)){
                schemesUniq.add(S);
                schemesCount.add(Collections.frequency(schemes, S));
            }
        }
        
        for (int i = 0; i < schemesUniq.size(); i++) {
            System.out.println(schemesUniq.get(i)+"\t"+schemesCount.get(i));
        }
        
        
    }
    
    
}
