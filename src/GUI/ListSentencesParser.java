package GUI;

/*
 * This script allows to parse a list of sentence located in the file :
 * "data/PCFG/sentencesList.txt". 
 * Sentences that the system succeed to parse will be autoatically stored
 * to the file "data/PCFG/ParsedSentences.txt"
 * 
 */
import Core.Utils;
import aracplatform.CKYParser;
import aracplatform.SchemesParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED <mohamedachraf@gmail.com>
 */
public class ListSentencesParser {

    static String expression = "( جملة";
    static int ruleOrder_ = 0;

    //*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/
    //==========================================================================
    /**
     * This recursive method allows to display rules WITHOUT virtual terminals
     * in the format accepted by Stanford parser : (S(NP(NNP parser))(VP(VBZ
     * works))(PP(IN for)(PRP * me))(.!)) Rules are in the form : Start -> A B
     * Begin|Split|end the 'break' command at the end of the 'if' loop aims to
     * avoid that two rules with the same header be interpreted twice.
     *
     * @param Rules
     * @param Start
     * @param Sentence
     * @param NumRule
     */
    static void FormattedRulesWithoutVirtualNonTerminals(ArrayList<String> Rules, String Start, ArrayList<String> Sentence, int NumRule) throws FileNotFoundException, IOException {

        Utils utils = new Utils();
        String virtuals = "data/PCFG/VirtualSymbols.txt";
        ArrayList<String> VirtualSymbols = utils.LoadFileToArrayListByLine(virtuals);

        for (int i = 0; i < Rules.size(); i++) {
            Integer begin = Integer.valueOf(Rules.get(i).split("\\t")[1].split("\\|")[0].trim());
            Integer split = Integer.valueOf(Rules.get(i).split("\\t")[1].split("\\|")[1].trim());
            Integer end = Integer.valueOf(Rules.get(i).split("\\t")[1].split("\\|")[2].trim());

            if (Rules.get(i).split("\\t")[0].split("->")[0].split("\\s")[0].equals(Start)
                    && i != NumRule) {
                String A = null, B = null;
                //--------------------------------------------------------------                  
                A = Rules.get(i).split("\\t")[0].split("->")[1].split("\\s")[1];
                if (end - split == 1 && ruleOrder_ < Sentence.size()) {
                    expression += "(" + A + " " + Sentence.get(ruleOrder_++);
                } else if (!VirtualSymbols.contains(A)) {
                    expression += "(" + A;
                }
                FormattedRulesWithoutVirtualNonTerminals(Rules, A, Sentence, i);
                if (!VirtualSymbols.contains(A)) {
                    expression += ")";
                }
                //-------------------------------------------------------------- 
                B = Rules.get(i).split("\\t")[0].split("->")[1].split("\\s")[0];
                if (split - begin == 1 && ruleOrder_ < Sentence.size()) {
                    expression += "(" + B + " " + Sentence.get(ruleOrder_++);
                } else if (!VirtualSymbols.contains(B)) {
                    expression += "(" + B;
                }
                FormattedRulesWithoutVirtualNonTerminals(Rules, B, Sentence, i);
                if (!VirtualSymbols.contains(B)) {
                    expression += ") ";
                }
                //--------------------------------------------------------------
                break;
            }
        }

    }

    //==========================================================================
    //*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/
    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        int ind = 0, pos = 0, neg = 0;
        String ppos, pneg;
        Utils utils = new Utils();

        String filePath = "data/PCFG/sentencesList.txt";
        File parsedSentences = new File("data/PCFG/ParsedSentences.txt");
        
        ArrayList<String> NT_SYMB = utils.LoadFileToArrayListByLine("data/PCFG/NT_TAG_1.txt");
        ArrayList<String> NT_TAGS = utils.LoadFileToArrayListByLine("data/PCFG/NT_TAG_2.txt");

        ArrayList<String> sentences = utils.LoadFileToArrayListByLine(filePath);

        for (String S : sentences) {
            if (!S.isEmpty()) {                
                DecimalFormat df = new DecimalFormat("#.00");
                ind++;
                System.out.println("\n--------------------------------------\n"+ind+")\t"+S+"\n--------------------------------------");
                S = S.trim();
                SchemesParser converter = new SchemesParser();
                CKYParser parser = new CKYParser();
                CKYParse cKYParse = new CKYParse();

                expression = "( جملة";
                ruleOrder_ = 0;
                ArrayList<String> result = new ArrayList<>();
                ArrayList<String> schemes = new ArrayList<>();

                ArrayList<String> sentence = new ArrayList(Arrays.asList(S.split("\\s")));

                result = converter.SchemesConvert(sentence, Boolean.TRUE, Boolean.FALSE);

                for (String w : result) {
                    schemes.add(w.split("\\|")[0]);
                }

                ArrayList<String> rules = parser.BuildTree(schemes, Boolean.FALSE);
                String Joumla = "_جملة_";
                Collections.reverse(sentence);
                FormattedRulesWithoutVirtualNonTerminals(rules, Joumla, sentence, -1);

                expression += ")";
                if (cKYParse.ParseTreeIsRegular(rules)) {
                    //--------------------
                    for (String SYMB : NT_SYMB) {
                        if (expression.contains(SYMB)) 
                            expression = expression.replace(SYMB, NT_TAGS.get(NT_SYMB.indexOf(SYMB)));
                        }
                    //--------------------
                    pos++;
                    ppos = df.format(100*pos / (pos + neg));                    
                    System.out.println("\n--------------------------------------[" + ind + "/" + sentences.size() + "\t" + ppos + "%]\n"
                            + S + "\n" + expression + "\n----------------------------------------\n");
                    utils.AppendToFile(S+"\n"+expression, parsedSentences.getAbsolutePath());
                } else {
                    neg++;
                    ppos = df.format(100*pos / (pos + neg));
                    System.out.println("\n--------------------------------------[" + ind + "/" + sentences.size() + "\t" + ppos + "%]\n"
                            + S + "\nNo parse tree was found \n-----------------------------------------\n");
                }
                expression = "";
                rules.clear();
                schemes.clear();
                result.clear();
            }

        }
    }

}
