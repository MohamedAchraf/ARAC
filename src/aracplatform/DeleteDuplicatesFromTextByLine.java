/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aracplatform;

import Core.Utils;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED <mohamedachraf@gmail.com>
 */
public class DeleteDuplicatesFromTextByLine {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        Utils utils = new Utils();
        ArrayList<String> lines = new ArrayList<>();
        ArrayList<String> linesUniq = new ArrayList<>();
        File file;
        file = new File("D:/ML/iparse/data/sentencesList_Uniq.TXT");

        lines = utils.LoadFileToArrayListByLine_old(file);
        for (String line : lines) {
            line = line.replaceAll("  ", " ").trim();            
            if ( line.contains(" ")  && !linesUniq.contains(line) ) {
                linesUniq.add(line);
                System.out.println(line);
            }
        }
    }

}
