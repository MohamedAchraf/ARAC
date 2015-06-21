/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aracplatform;

import Core.Utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author user
 */
public class PostingList {


    /**
     * ***********************************************************************
     * Itemization of file [Term, DocID]
     *
     * @return list of [Term, DocID]
     */
    public void Init(File directory, File PLDictionnary, File PLPostingList, File PLStatistics) throws FileNotFoundException, IOException {

        Utils util = new Utils();
        
        Map<String, ArrayList<Integer>> PostingList = new HashMap<>();

        ArrayList<String> StopWords = util.LoadFileToArrayList(new File("F:\\tmp\\321\\arabicStopListWords.txt"));
        Map<String, Integer> Dictionnary = new HashMap<>();
        TreeMap<String, Integer> SortedDictionnary = new TreeMap<>();

        for (File doc : directory.listFiles()) {            
            ArrayList<String> words = util.LoadFileToArrayList(doc);
            
            String docStatistics = doc.getName().split("\\.")[0]+","+words.size();
            util.AppendToFile(docStatistics,PLStatistics.getAbsolutePath());
            
            for (String w : words) {
                w = w.replaceAll("[^ةئءؤإاآأابتثجحخدذرزسشصضطظعغفقكلمنهوىي ]", "");
                if (w.length() > 1 /*&& !StopWords.contains(w)*/) {
                    Integer count = Dictionnary.get(w);
                    if (count == null) {
                        Dictionnary.put(w, 1);
                        ArrayList<Integer> temp = new ArrayList<>();
                        temp.add(Integer.parseInt(doc.getName().split("\\.")[0]));
                        PostingList.put(w, temp);

                    } else if (!PostingList.get(w).contains(Integer.parseInt(doc.getName().split("\\.")[0]))) {
                        Dictionnary.put(w, count + 1);
                        PostingList.get(w).add(Integer.parseInt(doc.getName().split("\\.")[0]));
                    }
                }
            }
        }

        // Sort Posting lists
        for (String w : Dictionnary.keySet()) {
            Collections.sort(PostingList.get(w));
        }

        // Sort Dictionnary
        SortedDictionnary.putAll(Dictionnary);


        util.SaveMapStringIntegerToFileByLine(SortedDictionnary, PLDictionnary);
        util.SaveMapStringArrayListToFileByLine(PostingList, PLPostingList);
    }

    /**
     *
     * @param document
     * @param PLDictionnary
     * @param PLPostingList
     */
    public void Update(File document, File PLDictionnary, File PLPostingList, File PLStatistics) throws FileNotFoundException, IOException {

        Utils util = new Utils();
        Map<String, Integer> Dictionnary = util.LoadFileToMapStringIntegerList(PLDictionnary);
        Map<String, ArrayList<Integer>> Postings = util.LoadFileToMapStringArralListIntegerList(PLPostingList);
        TreeMap<String, Integer> SortedDictionnary = new TreeMap<>();
        ArrayList<String> words = util.LoadFileToArrayList(document);
        
        String docStatistics = document.getName().split("\\.")[0]+","+words.size();
        util.AppendToFile(docStatistics,PLStatistics.getAbsolutePath());
        
        for (String w : words) {
            w = w.replaceAll("[^ةئءؤإاآأابتثجحخدذرزسشصضطظعغفقكلمنهوي ]", "");
            if (w.length() > 1 /*&& !StopWords.contains(w)*/) {
                //System.out.println(w);
                Integer DocID = Integer.parseInt(document.getName().split("\\.")[0]);
                if (!Dictionnary.containsKey(w)) {
                    ArrayList<Integer> posting = new ArrayList<>();
                    posting.add(DocID);
                    Dictionnary.put(w, 1);
                    SortedDictionnary.putAll(Dictionnary);
                    Postings.put(w, posting);
                } else if (Dictionnary.containsKey(w) && !Postings.get(w).contains(DocID)) {
                    Postings.get(w).add(DocID);
                    Collections.sort(Postings.get(w));
                    Dictionnary.put(w, Dictionnary.get(w) + 1);
                }
            }
        }

        util.SaveMapStringIntegerToFileByLine(SortedDictionnary, PLDictionnary);
        util.SaveMapStringArrayListToFileByLine(Postings, PLPostingList);

    }
}
