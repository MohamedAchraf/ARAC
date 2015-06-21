/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aracplatform;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED <mohamedachraf@gmail.com>
 */
public class ParseExpression {

    ArrayList<String> ExtractRulesResult = new ArrayList<>();

    /**
     * This method takes as argument a parse expression and returns for every
     * word of the sentence, the token , the scheme and the root (if exist).<br>
     * <u>Example</u>:(senetnce : كَانَ المَطَرُ يَهْطِلُ عَلَى الجِبَالِ)<br>
     * Input : Parse
     * <br>( جملة
     * <br>	(خبر
     * <br>	(ظرف_مكان
     * <br>	(مجرور الجِبَالِ)
     * <br>	(جار عَلَى) )
     * <br>	(فعل_مضارع_معلوم يَهْطِلُ) )
     * <br>	(مبتدأ المَطَرُ)
     * <br>	(كان_وأخواتها كَانَ) )
     * <br>
     * <br>Output :[word, token, scheme, root] <br>
     * <br>[الجِبَالِ مجرور الفِعَالِ جبل,
     * <br> عَلَى جار _مجهول_1_0_ 0,
     * <br> يَهْطِلُ فعل_مضارع_معلوم يَفْعِلُ هطل,
     * <br> المَطَرُ مبتدأ الفَعَلُ مطر,
     * <br> كَانَ كان_وأخواتها _أخواتكان_ 0]
     * <br>
     *
     * @param expression
     * @return
     * @throws FileNotFoundException
     */
    public ArrayList<String> ExtractTokensShemesRoots(String expression) throws FileNotFoundException, IOException {

        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> phrase = new ArrayList<>();
        ArrayList<String> tokens = new ArrayList<>();
        ArrayList<String> SchemesConversion = new ArrayList<>();
        ArrayList<String> schemes = new ArrayList<>();
        ArrayList<String> roots = new ArrayList<>();

        //---------------------------------------------
        // Extract tokens from the parse expression
        Pattern pattern;
        pattern = Pattern.compile(
                "([ةئءؤإاآأاىبتثجحخدذرزسشصضطظعغفقكلمنهوي_ًٌٍَُِّْ]+)"
                + "(\\s)"
                + "([ةئءؤإاآأاىبتثجحخدذرزسشصضطظعغفقكلمنهوي_ًٌٍَُِّْ]+)"
        );
        Matcher m = pattern.matcher(expression);

        while (m.find()) {
            tokens.add(m.group(1));
            phrase.add(m.group(3));
        }

        //---------------------------------------------
        //  Find Scheme conversion for sentence words
        SchemesParser schemesParser = new SchemesParser();
        SchemesConversion = schemesParser.SchemesConvert(phrase, Boolean.TRUE, Boolean.FALSE);
        for (String WS : SchemesConversion) {
            schemes.add(WS.trim().split("\\|")[0]);
        }

        //---------------------------------------------
        // Extract words roots 
        for (String s : schemes) {
            String root = "0";
            if (s.contains("ف") && s.contains("ع") && s.contains("ل")) {

                root = "";
                String word = phrase.get(schemes.indexOf(s));
                int pos_Fa = s.indexOf("ف"),
                        pos_3a = s.indexOf("ع", pos_Fa),
                        pos_La = s.indexOf("ل", pos_3a);

                if (!"ًٌٍَُِّْ".contains(String.valueOf(word.charAt(pos_Fa)))) {
                    root += word.charAt(pos_Fa);
                } else {
                    root += word.charAt(pos_Fa + 1);
                }

                if (!"ًٌٍَُِّْ".contains(String.valueOf(word.charAt(pos_3a)))) {
                    root += word.charAt(pos_3a);
                } else {
                    root += word.charAt(pos_3a - 1);
                }

                if (!"ًٌٍَُِّْ".contains(String.valueOf(word.charAt(pos_La)))) {
                    root += word.charAt(pos_La);
                } else {
                    root += word.charAt(pos_La - 1);
                }

            }
            roots.add(root);
        }

        //---------------------------------------------
        // Group and return result
        for (int i = 0; i < phrase.size(); i++) {
            result.add(phrase.get(i) + " "
                    + tokens.get(i) + " "
                    + schemes.get(i) + " "
                    + roots.get(i));
        }

        return result;
    }

    public ArrayList<String> ExtractRules(String expression) {

        if (countCaracters(expression, '(') > 1) {
            expression = expression.substring(expression.indexOf('(') + 1, expression.length());
            expression = expression.substring(expression.indexOf('('), expression.length());
            int index = 0;
            String component = "";

            for (int i = 0; i < expression.length(); i++) {
                if (expression.charAt(i) == '(') {
                    index++;
                    component += expression.charAt(i);
                } else if (expression.charAt(i) == ')') {
                    index--;
                    component += expression.charAt(i);
                    if (index == 0) {
                        ExtractRulesResult.add(component);
                        ExtractRules(component);
                        component = "";
                    }
                } else {
                    component += expression.charAt(i);
                }
            }
        }

        return ExtractRulesResult;
    }

    /**
     * Return the number of occurrences of a character in String
     *
     * @param input
     * @param c
     * @return
     */
    public Integer countCaracters(String input, char c) {

        int charCount = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == c) {
                charCount++;
            }
        }
        return charCount;
    }

    /**
     * Convert a parse expression to XML format
     *
     * @param sentence
     * @param parse
     * @return
     * @throws java.io.FileNotFoundException
     */
    public String parseToXMLConvert(String sentence, String parse) throws FileNotFoundException, IOException {

        ArrayList<String> result2 = new ArrayList<>();
        ArrayList<String> result1 = new ArrayList<>();
        String XMLResult = "";
        String[] words = sentence.split("\\s");

        //-------------------------------------------------<Lemmas>
        result1 = ExtractTokensShemesRoots(parse);
        XMLResult += "<Joumla length='" + result1.size() + "'>\n";
        for (int i = result1.size() - 1; i >= 0; i--) {
            XMLResult += "<Lemma>\n";
            String[] lemma = result1.get(i).split("\\s");
            XMLResult += "\t<String>" + lemma[0] + "</String>\n";
            XMLResult += "\t<Token>" + lemma[1] + "</Token>\n";
            XMLResult += "\t<Scheme>" + lemma[2] + "</Scheme>\n";
            XMLResult += "\t<Root>" + lemma[3] + "</Root>\n";
            XMLResult += "</Lemma>\n";
        }

        //-------------------------------------------------<Components>
        result2 = ExtractRules(parse);
        for (String res : result2) {
            if (countCaracters(res, '(') > 1) {
                //-----------------
                // Extract componenet title
                String title = "";
                Boolean b = false;
                for (int i = 0; i < res.length(); i++) {
                    if (res.charAt(i) == '(' && b == false) {
                        b = true;
                    } else if (res.charAt(i) == '(' && b == true) {
                        b = false;
                        break;
                    } else if (res.charAt(i) != '(' && b == true) {
                        title += res.charAt(i);
                    }
                }

                //-----------------
                String S = "";
                //-----------------
                Pattern pattern;
                pattern = Pattern.compile(
                        "([ةئءؤإاآأاىبتثجحخدذرزسشصضطظعغفقكلمنهوي_ًٌٍَُِّْ]+)"
                        + "(\\s)"
                        + "([ةئءؤإاآأاىبتثجحخدذرزسشصضطظعغفقكلمنهوي_ًٌٍَُِّْ]+)"
                );
                Matcher m = pattern.matcher(res);

                while (m.find()) {
                    S = m.group(3) + " " + S;
                }
                S = S.trim();
                String comp[] = S.split("\\s");
                Boolean test = true;
                //****************                
                for (int i = 0; i < words.length - comp.length + 1; i++) {
                    for (int j = 0; j < comp.length; j++) {
                        test = comp[j].equals(words[i + j]);
                    }
                    if (test == true) {
                        int start = i + 1, end = start + comp.length - 1;
                        XMLResult += "<Component>\n";
                        XMLResult += "\t<Title>" + title + "</Title>\n"
                                + "\t<Start>" + start + "</Start>\n"
                                + "\t<End>" + end + "</End>\n";
                        XMLResult += "</Component>\n";
                        break;
                    } else {
                        test = true;
                    }
                }

                //---------------------
            }

        }
        XMLResult += "<Parse>" + parse + "</Parse>\n";
        XMLResult += "</Joumla>\n";

        return XMLResult;
    }

}
