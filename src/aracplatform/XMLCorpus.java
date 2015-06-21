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
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;

/**
 *
 * @author user
 */
public class XMLCorpus {

    /**
     * Load text file and :
     * <li> Genetrates XML file with caracteristics of text.
     * <li> Update list of collocations.
     * 
     * @param DocumentSourceFile
     * @throws FileNotFoundException
     * @throws IOException
     * @throws XPathExpressionException
     * @throws ParserConfigurationException
     * @throws SAXException 
     */
    public void AddXMLDocument(File DocumentSourceFile) throws FileNotFoundException, IOException, XPathExpressionException, ParserConfigurationException, SAXException {
        Utils utils = new Utils();
        SchemesConverter converter = new SchemesConverter();
        CollocationsExtractor collocationsExtractor = new CollocationsExtractor();
        File PLStatistics = new File("F:\\tmp\\321\\fatiha\\PLStatistics.txt");
        ArrayList<String> document = utils.LoadFileToArrayList(DocumentSourceFile);
        ArrayList<String> docTiTle = utils.LoadFileToArrayListByLine(DocumentSourceFile.getAbsolutePath());
        String documentTitle = docTiTle.get(0);
        documentTitle = documentTitle.replaceAll("[\\[\\]]", "");
        
        ArrayList<String> Statistics = utils.LoadFileToArrayList(PLStatistics);

        ArrayList<String> XMLDocument = new ArrayList<>();
        ArrayList<String> XMLCollocations = new ArrayList<>();
        XMLDocument.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        XMLDocument.add("<text>\n");
        XMLDocument.add("<head>\n");
        Integer id_text = Statistics.size() + 1;
        long Documentsize = document.size();
        String Subject = "";
        String Mashkoul = "";
        String Source = "";

        XMLDocument.add("<id_text>" + id_text + "</id_text>\n");
        XMLDocument.add("<title>" + documentTitle + "</title>\n");
        XMLDocument.add("<length>" + Documentsize + "</length>\n");
        XMLDocument.add("<subject>" + Subject + "</subject>\n");
        XMLDocument.add("<mashkoul>" + Mashkoul + "</mashkoul>\n");


        XMLDocument.add("</head>\n");
        XMLDocument.add("<body>\n");
        XMLDocument.add("<source>" + Source + "</source>\n");
        XMLDocument.add("<content>\n");
        int wordId = 0;
        for (String word : document) {
            ArrayList<String> TermToConvert = new ArrayList<>();
            word = word.replaceAll("[ًٌٍَُِّْ]", "");
            word = word.replaceAll("[^ةئءؤإاآأابتثجحخدذرزسشصضطظعغفقكلمنهوىي ]", "");
            TermToConvert.add(word);
            if (!word.isEmpty()) {
                wordId++;
                XMLDocument.add("<word ");
                XMLDocument.add("id_word='" + wordId + "'");                             
                XMLDocument.add("scheme='" + converter.ShemesConvert(TermToConvert, Boolean.FALSE, Boolean.FALSE).get(0) + "'");
                XMLDocument.add("occurrence='" + Collections.frequency(document, word) + "'>");                
                XMLDocument.add(word+"</word>\n");
            }
        }
        XMLDocument.add("</content>\n");
        
        Map<String, Double> DoubleCollocationList    = collocationsExtractor.ExtractTwoWordsSizeCollocationsFromFile(DocumentSourceFile, 30, 1);collocationsExtractor.ExtractTwoWordsSizeCollocationsFromFile(DocumentSourceFile, wordId, wordId);
        Map<String, Double> TripleCollocationList    = collocationsExtractor.ExtractThreeWordsSizeCollocationsFromFile(DocumentSourceFile, 30, 1);collocationsExtractor.ExtractTwoWordsSizeCollocationsFromFile(DocumentSourceFile, wordId, wordId);
        Map<String, Double> QuadrupleCollocationList = collocationsExtractor.ExtractFoorWordsSizeCollocationsFromFile(DocumentSourceFile, 30, 1);collocationsExtractor.ExtractTwoWordsSizeCollocationsFromFile(DocumentSourceFile, wordId, wordId);
        Map<String, Double> QuintupleCollocationList = collocationsExtractor.ExtractFiveWordsSizeCollocationsFromFile(DocumentSourceFile, 30, 1);collocationsExtractor.ExtractTwoWordsSizeCollocationsFromFile(DocumentSourceFile, wordId, wordId);
        
        XMLDocument.add("<collocations>\n");          
        for(String w : DoubleCollocationList.keySet()){
            XMLDocument.add("<DoubCol LakelihoodRatio='"+DoubleCollocationList.get(w)+"' id_text='" + id_text+ "'>"+w+"</DoubCol>\n");
            
            XMLCollocations.add(
                      "<DoubCol>\n"
                    + "<col>"+w+"</col>\n"
                    + "<id_text>" + id_text+ "</id_text>\n"                    
                    + "<LakelihoodRatio>"+DoubleCollocationList.get(w)+"</LakelihoodRatio>\n"
                    + "</DoubCol>\n");            
        }
        
        
        for(String w : TripleCollocationList.keySet()){
            XMLDocument.add("<TripCol LakelihoodRatio='"+TripleCollocationList.get(w)+"'  id_text='" + id_text+ "'>"+w+"</TripCol>\n");
            
             XMLCollocations.add(
                      "<DoubCol>\n"
                    + "<col>"+w+"</col>\n"
                    + "<id_text>" + id_text+ "</id_text>\n"                    
                    + "<LakelihoodRatio>"+TripleCollocationList.get(w)+"</LakelihoodRatio>\n"
                    + "</DoubCol>\n"); 

        
        }
        
        for(String w : QuadrupleCollocationList.keySet()){
            XMLDocument.add("<QuadCol LakelihoodRatio='"+QuadrupleCollocationList.get(w)+"'  id_text='" + id_text+ "'>"+w+"</QuadCol>\n");  
            
             XMLCollocations.add(
                      "<DoubCol>\n"
                    + "<col>"+w+"</col>\n"
                    + "<id_text>" + id_text+ "</id_text>\n"                    
                    + "<LakelihoodRatio>"+QuadrupleCollocationList.get(w)+"</LakelihoodRatio>\n"
                    + "</DoubCol>\n"); 

        }
        
        for(String w : QuintupleCollocationList.keySet()){
            XMLDocument.add("<QuintCol LakelihoodRatio='"+QuintupleCollocationList.get(w)+"'  id_text='" + id_text+ "'>"+w+"</QuintCol>\n");  
            
             XMLCollocations.add(
                      "<DoubCol>\n"
                    + "<col>"+w+"</col>\n"
                    + "<id_text>" + id_text+ "</id_text>\n"                    
                    + "<LakelihoodRatio>"+QuintupleCollocationList.get(w)+"</LakelihoodRatio>\n"
                    + "</DoubCol>\n"); 

        }
        XMLDocument.add("</collocations>\n");
        
                
        XMLDocument.add("</body>\n");
        XMLDocument.add("</text>\n");

        File XMLCorpusSourceFile = new File("F:\\tmp\\321\\fatiha\\XMLCorpus\\" + DocumentSourceFile.getName().split("\\.")[0] + ".XML");
        utils.SaveArrayListToFile(XMLDocument, XMLCorpusSourceFile);
        
        File XMLCollocationsSourceFile = new File("F:\\tmp\\321\\fatiha\\collocations.XML");
        ArrayList<String> collocationsList = utils.LoadFileToArrayList(XMLCollocationsSourceFile);
        
        if(collocationsList.get(collocationsList.size()-1).equals("</collocations>"))
            collocationsList.remove(collocationsList.size()-1); // delete balise "</collocation> to append XML file.
        
        collocationsList.addAll(XMLCollocations);
        collocationsList.add("</collocations>");
        utils.SaveArrayListToFile(collocationsList, XMLCollocationsSourceFile);
        
    }
}