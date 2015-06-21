/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aracplatform;

import Core.Utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED <mohamedachraf@gmail.com>
 */
public class NLP_LAB_2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        Utils utils = new Utils();
        File file = new File("D:\\Development\\java_projects\\NLP_LAB\\data\\SC_VPast.txt");
        
        ArrayList<String> verbs = utils.LoadFileToArrayList(file);
        ArrayList<String> verbsUniq = new ArrayList<>();
        int i =1;
        for(String v : verbs){
            if(!verbsUniq.contains(v.split("_فعل_1_")[1].substring(1).trim())){
                verbsUniq.add(v.split("_فعل_1_")[1].substring(1).trim());
                System.out.println(i+"\t"+v.split("_فعل_1_")[1].substring(1));
                i++;
            }
        }
        
    }
    
}
