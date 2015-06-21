package aracplatform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;
/**
 *
 * @author Mohamed Achraf BEN MOHAMED<mohamedachraf@wanadoo.fr>
 */
public class AracPlatform {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, XPathExpressionException, SAXException, ParserConfigurationException {
//
//        File Data = new File("F:\\tmp\\321\\fatiha\\Corpus");
//        File Doc1 = new File("F:\\tmp\\321\\fatiha\\3.txt");
//        File Doc2 = new File("F:\\tmp\\321\\fatiha\\4.txt");
//        File PLDictionnary = new File("F:\\tmp\\321\\fatiha\\PLdictionnary.txt");        
//        File PLPostingList = new File("F:\\tmp\\321\\fatiha\\PLPostingList.txt");  
//        File PLStatistics  = new File("F:\\tmp\\321\\fatiha\\PLStatistics.txt");  
//        
//        PostingList postingList = new PostingList();
//
//        postingList.Init  (Data,  PLDictionnary, PLPostingList, PLStatistics);
//        postingList.Update(Doc1 , PLDictionnary, PLPostingList, PLStatistics);
//        postingList.Update(Doc2 , PLDictionnary, PLPostingList, PLStatistics);       
                
        File Doc1 = new File("F:\\tmp\\321\\fatiha\\Corpus\\1.txt");
        File Doc2 = new File("F:\\tmp\\321\\fatiha\\Corpus\\2.txt");
        File Doc3 = new File("F:\\tmp\\321\\fatiha\\Corpus\\3.txt");
        File Doc4 = new File("F:\\tmp\\321\\fatiha\\Corpus\\4.txt");
          
        XMLCorpus xmlcorpus = new XMLCorpus();
        xmlcorpus.AddXMLDocument(Doc1);
        xmlcorpus.AddXMLDocument(Doc2);
        xmlcorpus.AddXMLDocument(Doc3);
        xmlcorpus.AddXMLDocument(Doc4);
        
    }
}