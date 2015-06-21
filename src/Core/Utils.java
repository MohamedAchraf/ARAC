/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Core;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED<mohamedachraf@wanadoo.fr>
 */
public class Utils {

    public static final String UTF8_BOM = "\uFEFF";

    //==========================================================================
    /**
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public ArrayList<String> LoadFileToArrayListByLine(String filePath) throws IOException {

        ArrayList<String> fileContent = new ArrayList<>();
        try (
                //uses the class loader search mechanism:
                InputStream input = this.getClass().getResourceAsStream(filePath);
                InputStreamReader isr = new InputStreamReader(input, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(isr);) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.contains(UTF8_BOM)) {
                    line = line.substring(1);
                }
                fileContent.add(line);
            }
            reader.close();
        }

        return fileContent;
    }

    //==========================================================================
    /**
     * Save String to file in UTF-8 encoding, older content will be deleted.
     *
     * @param data
     * @param file
     * @throws IOException
     */
    public void SaveStringToUTF8File(String data, File file) throws IOException {

        try (Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), "UTF-8"))) {
            out.write(data);
        }
    }

    //==========================================================================
    public String LoadStringFromUTF8File(String filePath) throws IOException {

        String data = "";
        InputStream inputStream = new FileInputStream(filePath);
        try (Reader reader = new InputStreamReader(inputStream, "UTF-8")) {
            int info = reader.read();
            while (info != -1) {
                char theChar = (char) info;
                data = data + theChar;
                info = reader.read();
            }
        }

        return data;

    }

    //==========================================================================
    public String ConvertArrayListToString(ArrayList<String> list) {

        return list.toString().replaceAll("\\[|\\]", "").replaceAll(", ", " ");

    }

    //==========================================================================
    /**
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public ArrayList<String> LoadFileToYasmine(String file) throws FileNotFoundException, IOException {

        ArrayList<String> yasmine = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                yasmine.add(line);
            }
        }

        return yasmine;

    }
    //==========================================================================

    /**
     *
     * @param Lines
     * @param FileName
     * @throws IOException
     */
    public void writeLargerTextFile(String Lines, String FileName) throws IOException {

        Path path = Paths.get(FileName);
        System.out.println("\n---------\n" + System.getProperty("user.dir") + "\n---------\n");

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) { 
            writer.write(Lines);
        }
        
    }

    //==========================================================================
    /**
     *
     * @param data
     * @param file
     * @throws IOException
     */
    public void SaveToUTF8File(String data, File file) throws IOException {

        try (Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), "UTF-8"))) {
            out.write(data);
        }
    }

    //==========================================================================
    /**
     *
     * @param sourceFile
     * @return ArrayList<String>
     * @throws FileNotFoundException
     */
    public ArrayList<String> LoadFileToArrayList(File sourceFile) throws FileNotFoundException {

        ArrayList<String> WordsList = new ArrayList<String>();
        Scanner x = new Scanner(sourceFile);
        while (x.hasNext()) {
            String word = x.next();
            if (word.contains(UTF8_BOM)) {
                word = word.substring(1);
            }
            WordsList.add(word);
        }
        x.close();
        return WordsList;
    }

    /**
     *
     * @param sourceFile
     * @return ArrayList String
     * @throws FileNotFoundException
     */
    public ArrayList<String> LoadFileToArrayListByLine_old(File sourceFile) throws FileNotFoundException {

        ArrayList<String> WordsList = new ArrayList<>();
        try (Scanner x = new Scanner(sourceFile)) {
            while (x.hasNextLine()) {
                String word = x.nextLine();
                if (word.contains(UTF8_BOM)) {
                    word = word.substring(1);
                }
                WordsList.add(word);
                //if( loadedWords++ > 200 ) break; // Used in case we want to specify the number of words the system should load.
            }
        }
        return WordsList;
    }

    /**
     * Used in case we want to specify the number of words the system should
     * load.
     *
     * @param sourceFile
     * @param limit
     * @return
     * @throws FileNotFoundException
     */
    public ArrayList<String> LoadLimitedFileToArrayList(File sourceFile, int limit) throws FileNotFoundException {

        if (limit == -1) {
            limit = Integer.MAX_VALUE;
        }
        int loadedWords = 0;

        ArrayList<String> WordsList = new ArrayList<>();
        Scanner x = new Scanner(sourceFile);
        while (x.hasNext()) {
            String word = x.next();
            if (word.contains(UTF8_BOM)) {
                word = word.substring(1);
            }
            WordsList.add(word);
            if (loadedWords++ > limit) {
                break;
            }
        }

        x.close();
        return WordsList;
    }

    /**
     * Example of use : String content = LoadFileToStringByPath("test.txt");
     *
     * @param path
     * @return
     * @throws IOException
     */
    public String LoadFileToStringByPath(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, Charset.defaultCharset());
    }

    /**
     * Saving an arrayList to a file
     *
     *      * @param text
     * @param file
     * @throws IOException
     */
    public void SaveArrayListToFile(ArrayList<String> text, File file) throws IOException {

        FileWriter output = new FileWriter(file);
        for (String word : text) {
            output.append(word + " ");
        }
        output.close();
    }

    public void SaveArrayListToFileByLine(ArrayList<String> text, File file) throws IOException {

        FileWriter output = new FileWriter(file);
        for (String word : text) {
            output.append(word + "\n");
        }
        output.close();
    }

    /**
     * Saving an arrayList to a file
     *
     *      * @param text
     * @param file
     * @throws IOException
     */
    public void SaveStringToFile(String text, File file) throws IOException {
        try (FileWriter output = new FileWriter(file)) {
            output.append(text);
            output.close();
        }
    }

    /**
     *
     * @param sourceFile
     * @return ArrayList<ArrayList<String>> @t hrows FileNotFoundException
     */
    public ArrayList<ArrayList<String>> LoadFileToArrayArrayList(File sourceFile) throws FileNotFoundException {

        ArrayList<ArrayList<String>> tripleRootsList = new ArrayList<ArrayList<String>>();
        Scanner x = new Scanner(sourceFile);
        while (x.hasNextLine()) {
            String[] line = x.nextLine().split("\\s");
            ArrayList<String> phrase = new ArrayList<String>();
            for (int i = 0; i < line.length; i++) {
                phrase.add(line[i]);
            }

            tripleRootsList.add(phrase);
        }
        x.close();
        return tripleRootsList;
    }

    /**
     * Save ArrayList<ArrayList<String>> to fiel
     *
     * @param text
     * @param file
     * @throws IOException
     */
    public void SaveArrayArrayListToFile(ArrayList<ArrayList<String>> text, File file) throws IOException {

        FileWriter output = new FileWriter(file);
        for (ArrayList<String> sentences : text) {
            String phrase = null;
            for (String word : sentences) {
                phrase += word + " ";
            }
            phrase += "\n";
            output.append(phrase);
        }
        output.close();
    }

    /**
     * Merge the content of text files in the directory in a unique text file
     *
     * @param directory
     * @param file
     */
    public void MergeTextFiles(File directory, File destination, int limitFiles, int limitWords) throws IOException {

        ArrayList<String> TotalText = new ArrayList<>();
        int n = 0;

        for (File source : directory.listFiles()) {
            n++;
            if (n > limitFiles) {
                break;
            }
            ArrayList<String> Text = LoadLimitedFileToArrayList(source, limitWords);
            TotalText.addAll(Text);
        }
        SaveArrayListToFile(TotalText, destination);
    }

    /**
     * Append Content To File source :
     * http://www.mkyong.com/java/how-to-append-content-to-file-in-java/
     *
     * @param data
     * @param fileName
     * @throws IOException
     */
    public void AppendToFile(String data, String fileName) throws IOException {

        //**************************************************
        File file = new File(fileName);
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            out.println(data);
            out.close();
        } catch (IOException e) {
            System.out.println("NOT SAVED: " + e);
        }
        //**************************************************
//        OutputStream os = null;
//        try {
//            //below true flag tells OutputStream to append
//            os = new FileOutputStream(new File(fileName), true);
//            os.write(data.getBytes(), 0, data.length());
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                os.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

    }

    public void SaveMapStringIntegerToFileByLine(Map<String, Integer> Dictionnary, File file) throws IOException {

        FileWriter output = new FileWriter(file);

        for (String word : Dictionnary.keySet()) {
            output.append(word + " " + Dictionnary.get(word) + "\n");
        }
        output.close();
    }

    public void SaveMapStringArrayListToFileByLine(Map<String, ArrayList<Integer>> PostingList, File file) throws IOException {

        FileWriter output = new FileWriter(file);

        for (String word : PostingList.keySet()) {
            String ListDocsID = "";
            for (Integer i : PostingList.get(word)) {
                ListDocsID += i + ",";
            }

            output.append(word + " " + ListDocsID + "\n");
        }
        output.close();
    }

    /**
     *
     * @param file
     * @return Dictionnary
     * @throws FileNotFoundException
     */
    public Map<String, Integer> LoadFileToMapStringIntegerList(File file) throws FileNotFoundException, IOException {

        Map<String, Integer> Dictionnay = new HashMap<>();
        ArrayList<String> fileContent = LoadFileToArrayListByLine(file.getAbsolutePath());
        for (String line : fileContent) {
            String[] words = line.split("\\s");
            Dictionnay.put(words[0], Integer.parseInt(words[1]));
        }

        return Dictionnay;
    }

    public Map<String, ArrayList<Integer>> LoadFileToMapStringArralListIntegerList(File file) throws FileNotFoundException, IOException {

        Map<String, ArrayList<Integer>> PostingList = new HashMap<>();
        ArrayList<String> fileContent = LoadFileToArrayListByLine(file.getAbsolutePath());
        for (String line : fileContent) {
            String[] words = line.split("\\s");
            ArrayList<Integer> Postings = new ArrayList<>();
            for (String w : words[1].split(",")) {
                if (!w.isEmpty()) {
                    Postings.add(Integer.parseInt(w));
                }
            }
            PostingList.put(words[0], Postings);
        }

        return PostingList;
    }

}
