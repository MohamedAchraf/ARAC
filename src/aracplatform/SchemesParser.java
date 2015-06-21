package aracplatform;

import Core.SchemesForParsing;
import Core.SchemesNM;
import Core.Utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED<mohamedachraf@wanadoo.fr>
 */
public class SchemesParser {

    /**
     * Convert a text into scheme (Mashkoul or not)
     *
     * @param DevSet
     * @param Mashkoul
     * @param WithSplit
     * @return
     * @throws FileNotFoundException
     */
    public ArrayList<String> SchemesConvert(ArrayList<String> DevSet,
            Boolean Mashkoul,
            Boolean WithSplit) throws FileNotFoundException, IOException {

        Utils utils = new Utils();
        ArrayList<String> output = new ArrayList<>();
        ArrayList<String> schemes = new ArrayList<>();
        ArrayList<String> schemesFileContent = new ArrayList<>();
        ArrayList<String> sentence = new ArrayList<>();
        ArrayList<Integer> f_ = new ArrayList<>();
        ArrayList<Integer> a_ = new ArrayList<>();
        ArrayList<Integer> l_ = new ArrayList<>();

        String schemesList;
        String roots = "/data/include/tripleRootsList.txt";
        String countries = "/data/include/countries.txt";

        if (Mashkoul) {
            schemesList = "/data/include/schemesListSorted_.txt";
        } else {
            schemesList = "/data/include/schemesListNmSorted__.txt";
        }

        schemesFileContent = utils.LoadFileToArrayListByLine(schemesList);
        for (String info : schemesFileContent) {
            if (info.contains(",")) {
                String[] line = info.split(",");
                schemes.add(line[3]);
                f_.add(Integer.valueOf(line[0]));
                a_.add(Integer.valueOf(line[1]));
                l_.add(Integer.valueOf(line[2]));
            }
        }

        ArrayList<String> Roots = utils.LoadFileToArrayListByLine(roots);
        ArrayList<String> Countries = utils.LoadFileToArrayListByLine(countries);
        String original_S = "";

        if (Mashkoul) {
            SchemesForParsing s = new SchemesForParsing();
            for (String w : DevSet) {
                String S;
                String wTemp = w;
                if (w.length() == 1) {
                    w = "مجهول";
                }
                //******************************************************************            
                // Start by testing if it's a country name, in this case we delete
                // all word diacritics.
                if (Countries.contains(wTemp.replaceAll("[ًٌٍَُِّْ]", ""))) {
                    w = wTemp.replaceAll("[ًٌٍَُِّْ]", "");
                    System.out.println("\tException #0 \t" + w + "\t" + wTemp);
                }
                //******************************************************************

                S = s.schemesMatcher(w.trim(),
                        schemes,
                        f_,
                        a_,
                        l_,
                        Roots,
                        WithSplit);

                original_S = S;

                //==============================================================
                // if conversion fails we try to do some consecutif
                // modifications and retry converting
                //==============================================================
                // Begin by a simple case : 
                //          if 
                //          Last character = TA with soukoun                 
                //          then Fi3L MADHI
                // 
                if (S.contains("_مجهول_")
                        && w.charAt(w.length() - 2) == 'ت'
                        && w.charAt(w.length() - 1) == 'ْ') {
                    wTemp = "فَعَلَتْ";

                    S = s.schemesMatcher(wTemp.trim(),
                            schemes,
                            f_,
                            a_,
                            l_,
                            Roots,
                            WithSplit);
                    if (!S.contains("_مجهول_")) {
                        System.out.println("\tException #1 \t" + w + "\t" + wTemp + "\t" + S);
                    }
                }

                //==============================================================
                // First possibility: adding raf3a above al alif
                // in order to try to catch verbs type ajwaf:عاد 
                // We dont add a raf3a at the beginnig of the word       
                wTemp = w;

                if (S.contains("_مجهول_") && wTemp.length() > 3 && wTemp.substring(2, 3).equals("ا")) {
                    wTemp = wTemp.replaceFirst("ا", "اَ");

                    S = s.schemesMatcher(wTemp.trim(),
                            schemes,
                            f_,
                            a_,
                            l_,
                            Roots,
                            WithSplit);
                    if (!S.contains("_مجهول_")) {
                        System.out.println("\tException #2 \t" + w + "\t" + wTemp + "\t" + S);
                    }
                }

                //==============================================================
                // Second possibility : dealing with chadda : duplicate char 
                // with chadda and replace 7araka par soukoun. 
                // There are 3 possible cases : 
                //          1) yachOUddou -> yachdOUdou
                //          2) ymAllou    -> yamlAlou
                //          3) yafIrrou   -> yafrIrou
                wTemp = w;

                int ChaddaPosition = wTemp.indexOf("ّ");

                if (S.contains("_مجهول_") && wTemp.contains("ّ") && ChaddaPosition - 2 > 0 && !wTemp.substring(0, 1).equals("ا")) {

                    char _7arf = wTemp.charAt(ChaddaPosition - 1),
                            _7araka = wTemp.charAt(ChaddaPosition - 2);

                    String sub = new StringBuilder().append(_7araka).append(_7arf).toString();
                    wTemp = wTemp.replaceAll("ّ", sub);
                    wTemp = wTemp.substring(0, ChaddaPosition - 2) + "ْ" + wTemp.substring(ChaddaPosition - 1);

                    S = s.schemesMatcher(wTemp.trim(),
                            schemes,
                            f_,
                            a_,
                            l_,
                            Roots,
                            WithSplit);
                    if (!S.contains("_مجهول_")) {
                        System.out.println("\tException #3 \t" + w + "\t" + wTemp + "\t" + S);
                    }
                }

                //==============================================================                
                // Trying to catch al af3al al jawfaa fil moudhara3 like 
                // zara, 9ala, 3ada..
                // azourou, yazourou, nazourou, tazourou
                // will be 
                // azwarou, yazwarou, nazwarou, tazwarou   
                wTemp = w;

                if (wTemp.length() > 5
                        && S.contains("_مجهول_")
                        && (wTemp.substring(0, 2).equals("أَ")
                        || wTemp.substring(0, 2).equals("يَ")
                        || wTemp.substring(0, 2).equals("نَ")
                        || wTemp.substring(0, 2).equals("تَ"))
                        && (wTemp.substring(3, 5).equals("ُو") // َأزور
                        || wTemp.substring(3, 5).equals("ِي") // أسير
                        )) {
                    if (wTemp.substring(3, 4).equals("ُ")) {
                        wTemp = wTemp.replaceFirst("ُ", "ْ"); // azourou --> youzwirou
                        wTemp = wTemp.replaceFirst("و", "وَ");
                    }

                    if (wTemp.substring(3, 4).equals("ِ")) {//                           
                        StringBuilder builder = new StringBuilder(wTemp);
                        builder.setCharAt(3, 'ْ');
                        builder.setCharAt(4, 'و');
                        wTemp = builder.toString();
                        wTemp = wTemp.replaceFirst("و", "وَ");

                    }

                    S = s.schemesMatcher(wTemp.trim(),
                            schemes,
                            f_,
                            a_,
                            l_,
                            Roots,
                            WithSplit);
                    if (!S.contains("_مجهول_")) {
                        System.out.println("\tException #4 \t" + w + "\t" + wTemp + "\t" + S);
                    }

                }

                //==============================================================                
                // Trying again to catch af3al al jawfaa fil moudhara3 like 
                // arada, saara
                // ouridou will be Orwidou
                wTemp = w;

                if (wTemp.length() > 5
                        && S.contains("_مجهول_")
                        && (wTemp.substring(0, 2).equals("أُ")
                        || wTemp.substring(0, 2).equals("يُ")
                        || wTemp.substring(0, 2).equals("نُ")
                        || wTemp.substring(0, 2).equals("تُ"))
                        && (wTemp.substring(3, 5).equals("ِي") // أريد
                        )) {
                    StringBuilder builder = new StringBuilder(wTemp);
                    builder.setCharAt(3, 'ْ');
                    builder.setCharAt(4, 'و');
                    wTemp = builder.toString();
                    wTemp = wTemp.replaceFirst("و", "وِ");

                    S = s.schemesMatcher(wTemp.trim(),
                            schemes,
                            f_,
                            a_,
                            l_,
                            Roots,
                            WithSplit);
                    if (!S.contains("_مجهول_")) {
                        System.out.println("\tException #5 \t" + w + "\t" + wTemp + "\t" + S);
                    }
                }

                //==============================================================
                // Dealing with verb sa7i7 mahmouz : 
                //     First case آكل -->  أأكل
                wTemp = w;

                if (S.contains("_مجهول_")
                        && wTemp.substring(0, 1).equals("آ")) {
                    wTemp = wTemp.replaceFirst("آ", "أَأْ");

                    S = s.schemesMatcher(wTemp.trim(),
                            schemes,
                            f_,
                            a_,
                            l_,
                            Roots,
                            WithSplit);
                    if (!S.contains("_مجهول_")) {
                        System.out.println("\tException #6 \t" + w + "\t" + wTemp + "\t" + S);
                    }
                }

                //==============================================================
                // Dealing with verb sa7i7 mahmouz : 
                //    Second case بدآ -->  بدأا
                wTemp = w;

                if (S.contains("_مجهول_")
                        && wTemp.contains("آ")) {
                    wTemp = wTemp.replaceFirst("آ", "أَا");

                    S = s.schemesMatcher(wTemp.trim(),
                            schemes,
                            f_,
                            a_,
                            l_,
                            Roots,
                            WithSplit);
                    if (!S.contains("_مجهول_")) {
                        System.out.println("\tException #7 \t" + w + "\t" + wTemp + "\t" + S);
                    }
                }

                //==============================================================
                // Try to catch verb da3a by adding dhamma at the end of the
                // verb yad3ou 
                wTemp = w;

                if (S.contains("_مجهول_") && wTemp.charAt(wTemp.length() - 1) == 'و') {
                    wTemp = wTemp + "ُ";

                    S = s.schemesMatcher(wTemp.trim(),
                            schemes,
                            f_,
                            a_,
                            l_,
                            Roots,
                            WithSplit);
                    if (!S.contains("_مجهول_")) {
                        System.out.println("\tException #8 \t" + w + "\t" + wTemp + "\t" + S);
                    }
                }

                //==============================================================
                // Try to catch verb da3a fil jam3 by replacing at the end of 
                // the verb yad3ouna by yad3ououna
                wTemp = w;

                if (wTemp.length() > 4
                        && S.contains("_مجهول_")
                        && wTemp.charAt(wTemp.length() - 1) == 'َ'
                        && wTemp.charAt(wTemp.length() - 2) == 'ن'
                        && wTemp.charAt(wTemp.length() - 3) == 'و'
                        && wTemp.charAt(wTemp.length() - 4) == 'ُ') {
                    wTemp = wTemp.replace("ُونَ", "ُوُونَ");

                    S = s.schemesMatcher(wTemp.trim(),
                            schemes,
                            f_,
                            a_,
                            l_,
                            Roots,
                            WithSplit);
                    if (!S.contains("_مجهول_")) {
                        System.out.println("\tException #9 \t" + w + "\t" + wTemp + "\t" + S);
                    }
                }

                //==============================================================
                //==============================================================
                // if ALL attenpts are negative; we swich to the original result
                if (S.contains("_مجهول_")) {
                    S = original_S;
                }
                //--------------------------------------------------------------
                w = w + "        ";
                String ww = w;
                //--------------------------------------------------------------
                if (S.matches(".*\\s+.*")) {
                    ww = "";
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
                sentence.add(ww);
                output.add(S + "|" + ww);
            }
        } else {
            SchemesNM scheme = new SchemesNM();
            for (String w : DevSet) {
                String S = scheme.schemesMatcher(w.trim(), schemes, f_, a_, l_, Roots);
                output.add(S);
            }
        }

        //*************
        return output;
    }

    //==========================================================================
    /**
     * Convert an Array Of Sentences into Schemes
     *
     * @param DevSet
     * @param Mashkoul
     * @param WithSplit
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public ArrayList<String> SchemesConvertArrayOfSentences(ArrayList<String> DevSet,
            Boolean Mashkoul,
            Boolean WithSplit) throws FileNotFoundException, IOException {

        Utils utils = new Utils();
        ArrayList<String> FinalOutput = new ArrayList<>();
        ArrayList<String> schemes = new ArrayList<>();
        ArrayList<String> schemesFileContent = new ArrayList<>();
        ArrayList<String> sentence = new ArrayList<>();
        ArrayList<Integer> f_ = new ArrayList<>();
        ArrayList<Integer> a_ = new ArrayList<>();
        ArrayList<Integer> l_ = new ArrayList<>();

        String schemesList;
        String roots = "/data/include/tripleRootsList.txt";
        String countries = "/data/include/countries.txt";

        if (Mashkoul) {
            schemesList = "/data/include/schemesListSorted_.txt";
        } else {
            schemesList = "/data/include/schemesListNmSorted__.txt";
        }

        schemesFileContent = utils.LoadFileToArrayListByLine(schemesList);
        for (String info : schemesFileContent) {
            if (info.contains(",")) {
                String[] line = info.split(",");
                schemes.add(line[3]);
                f_.add(Integer.valueOf(line[0]));
                a_.add(Integer.valueOf(line[1]));
                l_.add(Integer.valueOf(line[2]));
            }
        }

        ArrayList<String> Roots = utils.LoadFileToArrayListByLine(roots);
        ArrayList<String> Countries = utils.LoadFileToArrayListByLine(countries);
        String original_S = "";

        int cpt = 0;
        SchemesForParsing s = new SchemesForParsing();
        for (String phrase_s : DevSet) {
            cpt++;
            phrase_s = phrase_s.replaceAll("  ", " ").trim();
            System.out.println("Phrase [" + cpt + "/" + DevSet.size() + "] :" + phrase_s);
            List<String> phrase = new ArrayList<>(Arrays.asList(phrase_s.split(" ")));
            ArrayList<String> output = new ArrayList<>();
            for (String w : phrase) {
                String S;
                String wTemp = w;
                if (w.length() == 1) {
                    w = "مجهول";
                }
                //******************************************************************            
                // Start by testing if it's a country name, in this case we delete
                // all word diacritics.
                if (Countries.contains(wTemp.replaceAll("[ًٌٍَُِّْ]", ""))) {
                    w = wTemp.replaceAll("[ًٌٍَُِّْ]", "");
                    System.out.println("\tException #0 \t" + w + "\t" + wTemp);
                }
                //******************************************************************

                S = s.schemesMatcher(w.trim(),
                        schemes,
                        f_,
                        a_,
                        l_,
                        Roots,
                        WithSplit);

                original_S = S;

                //==============================================================
                // if conversion fails we try to do some consecutif
                // modifications and retry converting
                //==============================================================
                // Begin by a simple case : 
                //          if 
                //          Last character = TA with soukoun                 
                //          then Fi3L MADHI
                // 
                if (S.contains("_مجهول_")
                        && w.charAt(w.length() - 2) == 'ت'
                        && w.charAt(w.length() - 1) == 'ْ') {
                    wTemp = "فَعَلَتْ";

                    S = s.schemesMatcher(wTemp.trim(),
                            schemes,
                            f_,
                            a_,
                            l_,
                            Roots,
                            WithSplit);
                    if (!S.contains("_مجهول_")) {
                        System.out.println("\tException #1 \t" + w + "\t" + wTemp + "\t" + S);
                    }
                }

                //==============================================================
                // First possibility: adding raf3a above al alif
                // in order to try to catch verbs type ajwaf:عاد 
                // We dont add a raf3a at the beginnig of the word       
                wTemp = w;

                if (S.contains("_مجهول_") && wTemp.length() > 3 && wTemp.substring(2, 3).equals("ا")) {
                    wTemp = wTemp.replaceFirst("ا", "اَ");

                    S = s.schemesMatcher(wTemp.trim(),
                            schemes,
                            f_,
                            a_,
                            l_,
                            Roots,
                            WithSplit);
                    if (!S.contains("_مجهول_")) {
                        System.out.println("\tException #2 \t" + w + "\t" + wTemp + "\t" + S);
                    }
                }

                //==============================================================
                // Second possibility : dealing with chadda : duplicate char 
                // with chadda and replace 7araka par soukoun. 
                // There are 3 possible cases : 
                //          1) yachOUddou -> yachdOUdou
                //          2) ymAllou    -> yamlAlou
                //          3) yafIrrou   -> yafrIrou
                wTemp = w;

                int ChaddaPosition = wTemp.indexOf("ّ");

                if (S.contains("_مجهول_") && wTemp.contains("ّ") && ChaddaPosition - 2 > 0 && !wTemp.substring(0, 1).equals("ا")) {

                    char _7arf = wTemp.charAt(ChaddaPosition - 1),
                            _7araka = wTemp.charAt(ChaddaPosition - 2);

                    String sub = new StringBuilder().append(_7araka).append(_7arf).toString();
                    wTemp = wTemp.replaceAll("ّ", sub);
                    wTemp = wTemp.substring(0, ChaddaPosition - 2) + "ْ" + wTemp.substring(ChaddaPosition - 1);

                    S = s.schemesMatcher(wTemp.trim(),
                            schemes,
                            f_,
                            a_,
                            l_,
                            Roots,
                            WithSplit);
                    if (!S.contains("_مجهول_")) {
                        System.out.println("\tException #3 \t" + w + "\t" + wTemp + "\t" + S);
                    }
                }

                //==============================================================                
                // Trying to catch al af3al al jawfaa fil moudhara3 like 
                // zara, 9ala, 3ada..
                // azourou, yazourou, nazourou, tazourou
                // will be 
                // azwarou, yazwarou, nazwarou, tazwarou   
                wTemp = w;

                if (wTemp.length() > 5
                        && S.contains("_مجهول_")
                        && (wTemp.substring(0, 2).equals("أَ")
                        || wTemp.substring(0, 2).equals("يَ")
                        || wTemp.substring(0, 2).equals("نَ")
                        || wTemp.substring(0, 2).equals("تَ"))
                        && (wTemp.substring(3, 5).equals("ُو") // َأزور
                        || wTemp.substring(3, 5).equals("ِي") // أسير
                        )) {
                    if (wTemp.substring(3, 4).equals("ُ")) {
                        wTemp = wTemp.replaceFirst("ُ", "ْ"); // azourou --> youzwirou
                        wTemp = wTemp.replaceFirst("و", "وَ");
                    }

                    if (wTemp.substring(3, 4).equals("ِ")) {//                           
                        StringBuilder builder = new StringBuilder(wTemp);
                        builder.setCharAt(3, 'ْ');
                        builder.setCharAt(4, 'و');
                        wTemp = builder.toString();
                        wTemp = wTemp.replaceFirst("و", "وَ");

                    }

                    S = s.schemesMatcher(wTemp.trim(),
                            schemes,
                            f_,
                            a_,
                            l_,
                            Roots,
                            WithSplit);
                    if (!S.contains("_مجهول_")) {
                        System.out.println("\tException #4 \t" + w + "\t" + wTemp + "\t" + S);
                    }

                }

                //==============================================================                
                // Trying again to catch af3al al jawfaa fil moudhara3 like 
                // arada, saara
                // ouridou will be Orwidou
                wTemp = w;

                if (wTemp.length() > 5
                        && S.contains("_مجهول_")
                        && (wTemp.substring(0, 2).equals("أُ")
                        || wTemp.substring(0, 2).equals("يُ")
                        || wTemp.substring(0, 2).equals("نُ")
                        || wTemp.substring(0, 2).equals("تُ"))
                        && (wTemp.substring(3, 5).equals("ِي") // أريد
                        )) {
                    StringBuilder builder = new StringBuilder(wTemp);
                    builder.setCharAt(3, 'ْ');
                    builder.setCharAt(4, 'و');
                    wTemp = builder.toString();
                    wTemp = wTemp.replaceFirst("و", "وِ");

                    S = s.schemesMatcher(wTemp.trim(),
                            schemes,
                            f_,
                            a_,
                            l_,
                            Roots,
                            WithSplit);
                    if (!S.contains("_مجهول_")) {
                        System.out.println("\tException #5 \t" + w + "\t" + wTemp + "\t" + S);
                    }
                }

                //==============================================================
                // Dealing with verb sa7i7 mahmouz : 
                //     First case آكل -->  أأكل
                wTemp = w;

                if (S.contains("_مجهول_")
                        && wTemp.substring(0, 1).equals("آ")) {
                    wTemp = wTemp.replaceFirst("آ", "أَأْ");

                    S = s.schemesMatcher(wTemp.trim(),
                            schemes,
                            f_,
                            a_,
                            l_,
                            Roots,
                            WithSplit);
                    if (!S.contains("_مجهول_")) {
                        System.out.println("\tException #6 \t" + w + "\t" + wTemp + "\t" + S);
                    }
                }

                //==============================================================
                // Dealing with verb sa7i7 mahmouz : 
                //    Second case بدآ -->  بدأا
                wTemp = w;

                if (S.contains("_مجهول_")
                        && wTemp.contains("آ")) {
                    wTemp = wTemp.replaceFirst("آ", "أَا");

                    S = s.schemesMatcher(wTemp.trim(),
                            schemes,
                            f_,
                            a_,
                            l_,
                            Roots,
                            WithSplit);
                    if (!S.contains("_مجهول_")) {
                        System.out.println("\tException #7 \t" + w + "\t" + wTemp + "\t" + S);
                    }
                }

                //==============================================================
                // Try to catch verb da3a by adding dhamma at the end of the
                // verb yad3ou 
                wTemp = w;

                if (S.contains("_مجهول_") && wTemp.charAt(wTemp.length() - 1) == 'و') {
                    wTemp = wTemp + "ُ";

                    S = s.schemesMatcher(wTemp.trim(),
                            schemes,
                            f_,
                            a_,
                            l_,
                            Roots,
                            WithSplit);
                    if (!S.contains("_مجهول_")) {
                        System.out.println("\tException #8 \t" + w + "\t" + wTemp + "\t" + S);
                    }
                }

                //==============================================================
                // Try to catch verb da3a fil jam3 by replacing at the end of 
                // the verb yad3ouna by yad3ououna
                wTemp = w;

                if (wTemp.length() > 4
                        && S.contains("_مجهول_")
                        && wTemp.charAt(wTemp.length() - 1) == 'َ'
                        && wTemp.charAt(wTemp.length() - 2) == 'ن'
                        && wTemp.charAt(wTemp.length() - 3) == 'و'
                        && wTemp.charAt(wTemp.length() - 4) == 'ُ') {
                    wTemp = wTemp.replace("ُونَ", "ُوُونَ");

                    S = s.schemesMatcher(wTemp.trim(),
                            schemes,
                            f_,
                            a_,
                            l_,
                            Roots,
                            WithSplit);
                    if (!S.contains("_مجهول_")) {
                        System.out.println("\tException #9 \t" + w + "\t" + wTemp + "\t" + S);
                    }
                }

                //==============================================================
                //==============================================================
                // if ALL attenpts are negative; we swich to the original result
                if (S.contains("_مجهول_")) {
                    S = original_S;
                }
                //--------------------------------------------------------------
                w = w + "        ";
                String ww = w;
                //--------------------------------------------------------------
                if (S.matches(".*\\s+.*")) {
                    ww = "";
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
                sentence.add(ww);
                output.add(S + "|" + ww);
            }
            String listString = "";
            for (String SS : output) {
                listString += SS + " ";
            }
            FinalOutput.add(listString);
        }

        //*************
        return FinalOutput;
    }
    //==========================================================================

    public void ShemesConvertDirectory(File directorySource, File directoryDestination, Boolean Mashkoul) throws FileNotFoundException, IOException {

        Utils utils = new Utils();

        for (File file : directorySource.listFiles()) {
            ArrayList<String> text = utils.LoadFileToArrayList(file);
            File destination = new File(directoryDestination.getPath() + "/" + file.getName());
            utils.SaveArrayListToFile(SchemesConvert(text, Mashkoul, Boolean.FALSE), destination);
        }
    }

}
