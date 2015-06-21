/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aracplatform;

import Core.Utils;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Collections;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED <mohamedachraf@gmail.com>
 */
public class SentencesVectorization {

    public static ArrayList<String> getVocabulary(File file) throws FileNotFoundException {
        Utils utils = new Utils();
        ArrayList<String> fileContent = utils.LoadFileToArrayList(file);
        ArrayList<String> vocabulary = new ArrayList<>();

        for (String token : fileContent) {
            if (!vocabulary.contains(token)) {
                vocabulary.add(token);
            }
        }

        return vocabulary;
    }

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Utils utils = new Utils();
        ArrayList<String> sentences_Parsed = new ArrayList<>();
        ArrayList<String> sentences_Mashkoul = new ArrayList<>();
        ArrayList<String> sentences_Schemes = new ArrayList<>();
        ArrayList<String> sentences_Vectors = new ArrayList<>();
        ArrayList<String> Y = new ArrayList<>();
        File file_sentences_Mashkoul = new File("D:/ML/iparse/data/2000_sentences_Mashkoul.txt");
        File file_sentences_parsed =   new File("D:/ML/iparse/data/2000_sentences_Mashkoul_parsed.txt");
        File file_sentences_Schemes =  new File("D:/ML/iparse/data/2000_sentences_Mashkoul_Schemes.txt");
        ArrayList<String> tokens = getVocabulary(file_sentences_Schemes);

        sentences_Parsed = utils.LoadFileToArrayListByLine_old(file_sentences_parsed);
        sentences_Mashkoul = utils.LoadFileToArrayListByLine_old(file_sentences_Mashkoul);
        sentences_Schemes = utils.LoadFileToArrayListByLine_old(file_sentences_Schemes);
        int count = 0;

        for (String sentence : sentences_Parsed) {
            if (sentence.startsWith(" NP (")) {
                count++;
                String NP = "";
                String temp = sentence;
                //temp = temp.replace("(ROOT (S (NP ", " NP ");
                int cpt = 1;
                int i = 0;
                while (cpt != 0) {
                    if (temp.charAt(i) == '(') {
                        cpt++;
                        i++;
                        NP = NP + temp.charAt(i);
                    } else if (temp.charAt(i) == ')') {
                        cpt--;
                        i++;
                        NP = NP + temp.charAt(i);
                    } else {
                        i++;
                        NP = NP + temp.charAt(i);
                    }
                }

                NP = NP.replaceAll("[^ةئءؤىإاآأابتثجحخدذرزسشصضطظعغفقكلمنهوىيًٌٍَُِّْ]", " ");
                NP = NP.replaceAll("\\s+", " ");
                //System.out.println(NP + "\t" + NP.trim().split("\\s+").length);
                Y.add(String.valueOf(NP.trim().split("\\s+").length));
            } else {
                //System.out.println("-1");
                count++;
                Y.add("-1");
            }
        }

        System.out.println("=========================Vectorization");
        int maxLength = 0;
        for (String S : sentences_Mashkoul) {
            if (S.split("\\s.").length > maxLength) {
                maxLength = S.split("\\s.").length;
                //System.out.println(S);
            }
        }
        System.out.println("Token Size = " + tokens.size());

        System.out.println("MaxLength = " + maxLength);
        int cpt = 0;
        String vector = "";
        for (String S : sentences_Schemes) {
            String[] words = S.split("\\s+");
            for (int i = 0; i < words.length; i++) {
                vector = vector + tokens.indexOf(words[i]) + ", ";
            }
            for (int i = 0; i < maxLength - words.length - 1; i++) {
                vector = vector + "0, ";
            }
            vector = vector + Y.get(cpt++);
            sentences_Vectors.add(vector);
            vector = "";
        }

        ArrayList<String> sentences_Vectors_Uniq = new ArrayList<>();
        for (String V : sentences_Vectors) {
            if (!sentences_Vectors_Uniq.contains(V)) {
                sentences_Vectors_Uniq.add(V);
            }
        }
        System.out.println("vector size ="+sentences_Vectors.size()+"\n"+"vector size Unic="+sentences_Vectors_Uniq.size());

        cpt = 0;
        for (String uv : sentences_Vectors) {
            cpt++;
            System.out.println(cpt + "\t" + uv );
        }

    }

}
