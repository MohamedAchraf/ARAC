
package aracplatform;

import Core.SchemesForConversion;
import Core.SchemesNM;
import Core.Utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED<mohamedachraf@wanadoo.fr>
 */
public class SchemesConverter {

    /**
     * Convert a text into scheme (Mashkoul or not)
     * @param DevSet
     * @param Mashkoul
     * @param WithSplit
     * @return
     * @throws FileNotFoundException 
     */
    public ArrayList<String> ShemesConvert(ArrayList<String> DevSet, 
            Boolean Mashkoul,
            Boolean WithSplit) throws FileNotFoundException {

        ArrayList<String> output = new ArrayList<>();
        ArrayList<String> schemes = new ArrayList<>();
        ArrayList<String> sentence = new ArrayList<>();
        ArrayList<Integer> f_ = new ArrayList<>();
        ArrayList<Integer> a_ = new ArrayList<>();
        ArrayList<Integer> l_ = new ArrayList<>();
                
        File schemesList;
        File roots = new File("data/include/tripleRootsList.txt");
        
        if(Mashkoul){
            schemesList = new File("data/include/schemesListSorted_.txt");
        }else{
            schemesList = new File("data/include/schemesListNmSorted__.txt");
        }
        
        Scanner x  = new Scanner(schemesList);
        while(x.hasNextLine()){
            String info = x.nextLine();
            if (info.contains(",")) {
                String[] line = info.split(",");
                schemes.add(line[3]);
                f_.add(Integer.valueOf(line[0]));
                a_.add(Integer.valueOf(line[1]));
                l_.add(Integer.valueOf(line[2]));
            }            
        }
        
        Utils include = new Utils();                
        ArrayList<String> Roots = include.LoadFileToArrayList(roots);
        
        if (Mashkoul) {
            SchemesForConversion s = new SchemesForConversion();                  
            for (String w : DevSet) {                
                String S =  s.schemesMatcher(w.trim(),
                        schemes, 
                        f_, 
                        a_, 
                        l_, 
                        Roots,
                        WithSplit);
                w = w+ "        ";
                String ww = w;
                //--------------------------------------------------------------
                if (S.matches(".*\\s+.*")) {                    
                    ww ="";
                    int j = 0;
                    for (int i = 0; i < S.length(); i++) {
                        if (S.charAt(i) != ' ') {                            
                            ww += w.charAt(j++);                        

                        } else if (S.charAt(i) == ' ') {
                            ww += ' ';

                        }
                    }
                }
                //--------------------------------------------------------------
                sentence.add(ww.toString());
                output.add(S+"|"+ww);  
                /*
                if (S.contains("_مجهول_")) {
                    System.out.print("\n" + ww + "\t [Not recognized] \t" + S);
                } else {
                    System.out.print("\n" + ww + "\t [  Recognized  ] \t" + S);
                }
                
                */
            }
        } else {
            SchemesNM scheme = new SchemesNM();
            for (String w : DevSet) {
                String S = scheme.schemesMatcher(w.trim(), schemes, f_, a_, l_, Roots);
                output.add(S);
            }
        }

        //*************
//        double sc=0.0, acc=0.0;
//        for(String s : output){
//            if(s.equals("_مجهول_")){
//                sc++;
//            }          
//        }
//         System.out.print("\nAccuracy = "+ (100-( sc / (double)output.size())*100) +"\n"); 
        //*************
        
        return output;
    }

public void ShemesConvertDirectory(File directorySource, File directoryDestination, Boolean Mashkoul) throws FileNotFoundException, IOException{
    
    Utils utils = new Utils();
    
    for(File file : directorySource.listFiles()){
        ArrayList<String> text  = utils.LoadFileToArrayList(file);
        File destination = new File(directoryDestination.getPath()+"/"+file.getName());
        utils.SaveArrayListToFile(ShemesConvert(text, Mashkoul, Boolean.FALSE), destination);
    }
}

}
