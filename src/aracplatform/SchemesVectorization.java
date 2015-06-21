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
public class SchemesVectorization {

    /**
     * Convert File Content into Scheme.<br>
     * Result form is : <b>WORD|SCHEME</b>.
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    public ArrayList<String> ConvertFileContentToScheme(File file) throws FileNotFoundException {
        ArrayList<String> XY = new ArrayList<>();
        Utils utils = new Utils();
        ArrayList<String> WordScheme = utils.LoadFileToArrayList(file);
        for (String ws : WordScheme) {
            if (ws.contains("|")) {
                ws = ws.replaceAll("\\s", " ");
                ws = ws.replaceAll("  ", " ");
                XY.add(ws);
            }
        }

        return XY;
    }

    /**
     * Convert a text into decimal uni-code value.<br>
     * Exp : <br>
     * <ul>
     * <li> أب -->1571, 1576, </li>
     * <li> أبصر -->1571, 1576, 1589, 1585,  </li>
     * <li> ... </li>
     * </ul>
     *
     * @param text
     * @return
     */
    public static String UnicodeTodecimal(String text) {
        String res = "";
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int value = c;
            res = res + String.valueOf(value) + ", ";
        }

        return res;
    }

    public static ArrayList<String> VectorizeFileContent(File file) throws FileNotFoundException {

        Utils utils = new Utils();
        ArrayList<String> vector = new ArrayList<>();
        ArrayList<String> X = new ArrayList<>();
        ArrayList<String> Y = new ArrayList<>();
        ArrayList<String> Y_Uniq = new ArrayList<>();
        ArrayList<String> fileContent = utils.LoadFileToArrayList(file);
        int MaxLength = 0;

        // Collecting data
        for (String ws : fileContent) {
            if (ws.contains("|")) {
                String[] w_s = ws.split("\\|");
                X.add(w_s[0]);
                Y.add(w_s[0]);

                if (w_s[0].length() > MaxLength) {
                    MaxLength = w_s[0].length();
                }
            }
        }

        // Gatering Schemes Vocabulary
        for (String s : Y) {
            if (!Y_Uniq.contains(s)) {
                Y_Uniq.add(s);
            }
        }

        for (int i = 0; i < X.size(); i++) {
            String line = "";
            line = UnicodeTodecimal(X.get(i));
            for (int j = X.get(i).length(); j < MaxLength; j++) {
                line = line + "0, ";
            }
            line = line + String.valueOf(Y_Uniq.indexOf(Y.get(i)) + 1);
            vector.add(line);
        }

        return vector;
    }

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        Utils utils = new Utils();
        File WS_file = new File("D:\\ML\\iparse\\data\\TEMP_RESULT_FORMATTED.txt");
        File Vector_file = new File("D:\\ML\\iparse\\data\\TEMP_RESULT_VECTORIZED.txt");
        
        utils.SaveArrayListToFileByLine(VectorizeFileContent(WS_file), Vector_file);
    }

}
