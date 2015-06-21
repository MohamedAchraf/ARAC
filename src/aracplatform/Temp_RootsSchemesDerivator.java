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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/**
 *
 * @author Mohamed Achraf BEN MOHAMED <mohamedachraf@gmail.com>
 */
public class Temp_RootsSchemesDerivator {
    
    
    static int MaxWordLength = 18;
    /**
     * Build a word from a given root and scheme.
     * @param root
     * @param schemeLine
     * @return 
     */
    static String Derivate(String root, String schemeLine){
        
        int c1,c2,c3;
        c1 = Integer.valueOf(schemeLine.split(",")[0])-2;
        c2 = Integer.valueOf(schemeLine.split(",")[1])-2;
        c3 = Integer.valueOf(schemeLine.split(",")[2])-2;
        String scheme = schemeLine.split(",")[3];
        
        StringBuilder word = new StringBuilder(scheme);
        word.setCharAt(c1, root.charAt(0));
        word.setCharAt(c2, root.charAt(1));
        word.setCharAt(c3, root.charAt(2));
                
        return word.toString();
    }
    
    static String Vectorize(String word, String schemeLine, int schemeIndex){
        String line = "";        
        String[] ArabicAlpha = "ةئءؤإاآأابتثجحخدذرزسشصضطظىعغفقكلمنهويًٌٍَُِّْ".split("");
        List<String> ArabicChar = Arrays.asList(ArabicAlpha);
        int c1,c2,c3;
        c1 = Integer.valueOf(schemeLine.split(",")[0]);
        c2 = Integer.valueOf(schemeLine.split(",")[1]);
        c3 = Integer.valueOf(schemeLine.split(",")[2]);
        
        for (int i = 0; i < word.length(); i++) {            
            line = line + ArabicChar.indexOf(String.valueOf(word.charAt(i)))+", ";
        }
        
        for (int i = word.length(); i < MaxWordLength; i++) {
            line = line + "0, ";
        }
        
        // line = line + c1 + ", " + c2 + ", " + c3 + ", " + schemeIndex;
        line = line + schemeIndex;
        return line;        
    }

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    @SuppressWarnings("empty-statement")
    public static void main(String[] args) throws FileNotFoundException, IOException {
        
        Utils utils = new Utils();        
        ArrayList<String> roots = new ArrayList<>();
        ArrayList<String> schemes = new ArrayList<>();
        ArrayList<String> vector = new ArrayList<>();
        
        File rootsData = new File("D:\\ML\\iparse\\iParseSchemeConverter\\Roots_3280.txt");
        File schemesData = new File("D:\\ML\\iparse\\iParseSchemeConverter\\Schemes_02_196_verbs.txt");        
        File vectorResult = new File("D:\\ML\\iparse\\iParseSchemeConverter\\Vector_02_196_verbs.txt");
        
        roots = utils.LoadFileToArrayList(rootsData);
        schemes  = utils.LoadFileToArrayList(schemesData);        
        // total roots size  = 3280
        //total schemes size = 1900
                    
                
        for (int i = 0; i < 300; i++) { // roots           
            for (int j = 0; j < schemes.size(); j++) { // schemes
                String word = Derivate(roots.get(i),schemes.get(j));                 
                vector.add(Vectorize(word,schemes.get(j), j+1));                   
            }
        }        
        
        //Collections.shuffle(vector);
        utils.SaveArrayListToFileByLine(vector, vectorResult);  
        
        
    }    
}