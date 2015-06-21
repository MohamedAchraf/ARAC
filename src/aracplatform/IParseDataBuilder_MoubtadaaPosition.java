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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author user
 */
public class IParseDataBuilder_MoubtadaaPosition {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     * @throws org.xml.sax.SAXException
     * @throws javax.xml.parsers.ParserConfigurationException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, SAXException, ParserConfigurationException {

        Utils utils = new Utils();
        ArrayList<String> tokens = new ArrayList<>();
        ArrayList<String> sentences_nominal_simple = new ArrayList<>();
        ArrayList<String> sentences_nominal_component = new ArrayList<>();
        ArrayList<String> moubdataa_position = new ArrayList<>();
        ArrayList<String> sentences_verbal = new ArrayList<>();
        File xmlFile = new File("D:\\Development\\java_projects\\AracPlatform_V0.1\\data\\PCFG\\ParsedSentencesXML___.XML");
        String moubtadaa = "مبتدأ";
        int MaxLength = 0;

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        org.w3c.dom.Document doc = documentBuilder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        NodeList nodes = doc.getElementsByTagName("Joumla");

        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            String result1 = "";
            String result2 = "";

            NodeList word = element.getElementsByTagName("Lemma");
            for (int j = 0; j < word.getLength(); j++) {
                Element line1 = (Element) word.item(j);
                result1 = result1 + line1.getTextContent();
            }

            NodeList Component = element.getElementsByTagName("Component");
            for (int j = 0; j < Component.getLength(); j++) {
                Element line2 = (Element) Component.item(j);
                result2 = result2 + line2.getTextContent();
            }
            result1 = result1.replaceAll("  ", " ").trim();
            result2 = result2.replaceAll("  ", " ").trim();

            if (result1.contains(moubtadaa)) { // Nominal simple
                String[] words = result1.split(" ");
                String sentence = "";
                for (int j = 2; j < words.length;) {
                    //System.out.print(words[j] + " ");
                    sentence = sentence + words[j] + " ";
                    if (!tokens.contains(words[j])) {
                        tokens.add(words[j]);
                    }
                    j = j + 4;
                }
                sentences_nominal_simple.add(sentence.trim());         
                if(sentence.trim().split(" ").length > MaxLength) MaxLength = sentence.trim().split(" ").length;

            } else if (result2.contains(moubtadaa) && !result1.split(" ")[1].contains("فعل_")) { // Nominal component  
                //-------------------------
                String MobtadaaPosition = "-1";
                //System.out.println(result2);
                Pattern pattern = Pattern.compile("(مبتدأ)(\\s)(\\d+)(\\s)(\\d+)");
                Matcher matcher = pattern.matcher(result2);
                if(matcher.find()){
                    //System.out.println("mobtadaa at "+ matcher.group(5));
                    MobtadaaPosition = matcher.group(5);
                }
                //-------------------------
                String[] words = result1.split(" ");
                String sentence = "";
                for (int j = 2; j < words.length;) {
                    //System.out.print(words[j] + " ");
                    sentence = sentence + words[j] + " ";
                    if (!tokens.contains(words[j])) {
                        tokens.add(words[j]);
                    }
                    j = j + 4;
                }
                //System.out.println("\n------------------------------------");
                sentences_nominal_component.add(sentence.trim());
                moubdataa_position.add(MobtadaaPosition);
                if(sentence.trim().split(" ").length > MaxLength) MaxLength = sentence.trim().split(" ").length;
            } 
//            else { // Verbal
//                String[] words = result1.split(" ");
//                String sentence = "";
//                for (int j = 1; j < words.length;) {
//                    //System.out.print(words[j] + " ");
//                    sentence = sentence + words[j] + " ";
//                    if (!tokens.contains(words[j])) {
//                        tokens.add(words[j]);
//                    }
//                    j = j + 4;
//                }
//                //System.out.println("");
//                sentences_verbal.add(sentence.trim());
//                if(sentence.trim().split(" ").length > MaxLength) MaxLength = sentence.trim().split(" ").length ;
//            }

        }
        int totalSentencesNumber = sentences_nominal_simple.size() + sentences_nominal_component.size();
        System.out.println("[INFO]\tTokens size = " + tokens.size());
        System.out.println("[INFO]\tTotal Sentences Number = " + totalSentencesNumber);
        System.out.println("[INFO]\tMax Length = " + MaxLength);
        System.out.println("[INFO]\tVectorization : ");
        
         //--------------------------- Nominal simple
        for (String phrase : sentences_nominal_simple) {
            String[] words = phrase.trim().split(" ");
            for (String word : words) {
                System.out.print(tokens.indexOf(word) + ", ");
            }
            for (int i = 0; i < MaxLength-words.length; i++) {
                System.out.print("0, ");
            }
            System.out.println("1");
        }
        //--------------------------- Nominal component
        int MobtadaaPos = 0;
        for (String phrase : sentences_nominal_component) {
            //System.out.println(phrase);
            String[] words = phrase.trim().split(" ");
            for (String word : words) {
                System.out.print(tokens.indexOf(word) + ", ");
            }
            for (int i = 0; i < MaxLength-words.length; i++) {
                System.out.print("0, ");
            }
            System.out.println(moubdataa_position.get(MobtadaaPos));
            MobtadaaPos++;
        }

        //------------------------------------------
    }
}
