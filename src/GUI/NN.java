/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Core.Utils;
import aracplatform.SchemesConverter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED<mohamedachraf@wanadoo.fr>
 */
public class NN {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        
        ArrayList<String> source = new ArrayList<String>();
        ArrayList<String> result = new ArrayList<String>();
        ArrayList<String> resultTMP = new ArrayList<String>();
        Boolean mashkoul = Boolean.TRUE;
        SchemesConverter converter = new SchemesConverter();
        Utils utils = new Utils();
        File file = new File("D:/java_projects/AracPlatform_V0.1/data/docs/mashkoul1DevSet_.txt");        
        source = utils.LoadFileToArrayList(file);
        System.out.print(source);
         
        result = converter.ShemesConvert(source, mashkoul, Boolean.FALSE);
        System.out.print(result);
        
    }
}
