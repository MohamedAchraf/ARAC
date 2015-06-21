/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aracplatform;

import static aracplatform.CKYParser.UTF8_BOM;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED<mohamedachraf@wanadoo.fr>
 */
public class CKYParser_OLD {

    public static final String UTF8_BOM = "\uFEFF";
    ArrayList<String> Unary = new ArrayList<String>();
    ArrayList<String> tluser = new ArrayList<String>();
    ArrayList<String> result = new ArrayList<String>();

    /**
     * This method explore file containing rules and extract terminal or non 
     * terminals in an array list string.
     * 
     * @param Terminal Source File or Non Terminal Source File
     * @return List of terminals or non terminals
     * @throws FileNotFoundException
     */
    public ArrayList<String> LoadParses(File SourceFile) throws FileNotFoundException {

        ArrayList<String> NonTerminals = new ArrayList<String>();
        Scanner x = new Scanner(SourceFile);        
        while (x.hasNextLine()) {
            String[] line = x.nextLine().split("#");              
            if (line[1].contains(" ")) { // if rule is in the form A -> B C                
                String[] words = line[1].split("\\s");
                if (!NonTerminals.contains(words[0])) {
                    NonTerminals.add(words[0]);
                }
                if (!NonTerminals.contains(words[1])) {
                    NonTerminals.add(words[1]);
                }
            } else if (!NonTerminals.contains(line[1])) { // rule is in the form A -> B
                NonTerminals.add(line[1]);

            }
            if (!NonTerminals.contains(line[0])) {
                NonTerminals.add(line[0]);
            }
        }
        return NonTerminals;
    }

    /**
     * This method extract rules fom text source file in an array list string. 
     * 
     * @param NonTerminalSourceFile
     * @return List of rules [Exp: مبتدأ->اسم]
     * @throws FileNotFoundException
     */
    public ArrayList<String> LoadRules(File RulesSourceFile) throws FileNotFoundException {

        ArrayList<String> Rules = new ArrayList<String>();
        Scanner x = new Scanner(RulesSourceFile);
        while (x.hasNextLine()) {            
            String input = x.nextLine();                       
            //System.out.println(input);
            if (!input.contains("@")) {
                String[] line = input.split("#");
                String Rule = line[1] + "#" + line[2];
                if (!Rules.contains(Rule)) {
                    Rules.add(Rule);
                }
            }
        }
        return Rules;
    }

    /**
     * This Method extract rules with their probability into a Map object. 
     * @param RulesSourceFile
     * @return Weighted Rules
     * @throws FileNotFoundException
     */
    public Map<String, Double> LoadWRules(File RulesSourceFile) throws FileNotFoundException {

        Map<String, Double> WeightedRules = new HashMap<String, Double>();
        Scanner x = new Scanner(RulesSourceFile);

        while (x.hasNextLine()) {
            String input = x.nextLine();
            if (!input.contains("@")) { // comments begin with '@'
                String[] line = input.split("#");
                String Rule = line[1] + "#" + line[2];
                if (!WeightedRules.containsKey(Rule)) {
                    if (line[0].contains(UTF8_BOM)) {
                        line[0] = line[0].substring(1);
                    }
                    WeightedRules.put(Rule, Double.valueOf(line[0]));
                }
            }
        }
        return WeightedRules;
    }

    /**
     *
     * This method reorder rules using recursive Depth-first search (DFS).originlly
     * rules are not ordered : <br>
     * --------------------------------------<br>
     * Non Ordered Rules <br>
     * --------------------------------------<br>
     * _مبتدأ->_اسمإشارة  _اسم_<br>
     * _جملة_->مبتدأ خبر	<br>
     * خبر->_اسمفاعل_ _جرمجرور_	<br>
     * _جرمجرور_->_جر_ _مجرور_	<br>
     * ---------------------------------------<br>
     *
     * ---------------------------------------<br>
     * Ordered Rules (from bottom)<br>
     * ---------------------------------------<br>
     * جرمجرور_->_جر_ _مجرور_<br>
     * خبر->_اسمفاعل_ _جرمجرور_<br>
     * مبتدأ->_اسمإشارة_ _اسم_<br>
     * جملة_->مبتدأ خبر_<br>
     * ---------------------------------------<br>
     *
     * @param rules
     * @param NonTerminal
     * @return 
     */
    public ArrayList ReorderRules(ArrayList<String> rules, String NonTerminal) {

        for (String rule : rules) {
            String[] parts = rule.split("\\t");
            String[] A = parts[0].split("->");
            for (int i = 0; i < A.length; i++) {
                if (A[i].trim().equals(NonTerminal.trim())) {
                    if (i < A.length) {
                        String[] B = A[i + 1].split("\\s");
                        if (B.length > 1) {
                            ReorderRules(rules, B[1]);
                            ReorderRules(rules, B[0]);
                        }
                        tluser.add(A[i] + "->" + B[0] + " " + B[1] + "#" + parts[1]);
                    }
                }
            }
        }


        return tluser;
    }

    /**
     * This methos Draw parse tree 
     *
     * @param List of most liekly rules
     */
    public void DrawTree(ArrayList<String> rules) {
        for (String rule : rules) {
            String[] parts = rule.split("\t");
            String[] A = parts[0].split("->");
            String[] Span = parts[1].split("\\|");

            for (int i = 0; i < rules.size(); i++) {
                if (i == Integer.valueOf(Span[1]) - 1) {
                    System.out.print(A[1].split("\\s")[0]);
                    for (int j = 0; j < Integer.valueOf(Span[1]) - Integer.valueOf(Span[0]); j++) {
                        System.out.print("              ");
                    }

                    System.out.print(A[1].split("\\s")[1]
                            + "               ");
                } else {
                    System.out.print("                  ");
                }
            }

            System.out.println();

            for (int i = 0; i < rules.size() + 1; i++) {
                if (rules.size() - i == Integer.valueOf(Span[0]) + Integer.valueOf(Span[1]) - Integer.valueOf(Span[0]) - 1) {
                    System.out.print("|");
                    for (int j = 0; j < Integer.valueOf(Span[1]) - Integer.valueOf(Span[0]); j++) {
                        System.out.print("                ");
                    }
                    System.out.print("|");
                } else {
                    System.out.print("                  ");
                }
            }

            System.out.println();

            for (int i = 0; i < rules.size() + 1; i++) {
                if (rules.size() - i == Integer.valueOf(Span[0]) + Integer.valueOf(Span[1]) - Integer.valueOf(Span[0]) - 1) {
                    for (int j = 0; j < Integer.valueOf(Span[1]) - Integer.valueOf(Span[0]); j++) {
                        System.out.print("+----------------");
                    }
                    System.out.print("+");

                } else {
                    System.out.print("                  ");
                }
            }
            System.out.println();

            for (int i = 0; i < rules.size(); i++) {
                if (rules.size() - i == Integer.valueOf(Span[0])) {
                    System.out.print("                 |");
                } else {
                    System.out.print("                  ");
                }
            }
            System.out.println();

        }
    }

    /**
     * CKY Algorithm.(the Cocke–Younger–Kasami algorithm) it's a parsing algorithm 
     * for context-free grammars.It employs bottom-up parsing and dynamic 
     * programming.(Wiki).it returns all posiible rules.
     *
     * @param words
     * @param TestMode indicate which rule file we should use : the little or the complete one.
     * @return All Possible rules
     * @throws FileNotFoundException
     */
    public ArrayList<String> CKYParse(ArrayList<String> words, Boolean TestMode) throws FileNotFoundException {

        ArrayList<String> parseTree = new ArrayList<String>();
        ArrayList<String> NonTerminal= new ArrayList<String>();
        ArrayList<String> Rules= new ArrayList<String>();
        Map<String, Double> P = new HashMap<String, Double>();
        //--------------------------------------
        // Loading terminals, non terminalsn and rules (+weighted rules)
        System.out.println(
                 "\n-------------------------------------\n"
                + "\tTest Mode: "+TestMode
                +"\n-------------------------------------\n");
        if (TestMode) {            
            NonTerminal = LoadParses(new File("D:/Development/java_projects/AracPlatform_V0.1/data/PCFG/NonTerminalrulesLittle.txt"));
            Rules = LoadRules(new File("D:/Development/java_projects/AracPlatform_V0.1/data/PCFG/rulesLittle.txt"));
            P = LoadWRules(new File("D:/Development/java_projects/AracPlatform_V0.1/data/PCFG/rulesLittle.txt"));
        } else {
            NonTerminal = LoadParses(new File("D:/Development/java_projects/AracPlatform_V0.1/data/PCFG/NonTerminalrules.txt"));
            Rules = LoadRules(new File("D:/Development/java_projects/AracPlatform_V0.1/data/PCFG/Total_rules.txt"));
            P = LoadWRules(new File("D:/Development/java_projects/AracPlatform_V0.1/data/PCFG/Total_rules.txt"));
        }
        //--------------------------------------
        Double[][][] score = new Double[words.size() + 1][words.size() + 1][NonTerminal.size()];
        String[][][] back = new String[words.size() + 1][words.size() + 1][NonTerminal.size()];

        for (int i = 0; i < words.size() + 1; i++) {
            for (int j = 0; j < words.size() + 1; j++) {
                for (int k = 0; k < NonTerminal.size(); k++) {
                    score[i][j][k] = 0.0;
                    back[i][j][k] = "";
                }
            }
        }
        //System.out.println("\n\n----------------------------------------<LEXICONS>");
        String Majhool = "_مجهول_";        
        
        for (int i = 0; i < words.size(); i++) {
            Boolean NoRuleFounded = true;
            for (String A : NonTerminal) {
                String rule = A + "#" + words.get(i).toString().trim();
                if (Rules.contains(rule)) {
                    score[i][i + 1][NonTerminal.indexOf(A)] = P.get(rule);
                    Unary.add(A);
                    NoRuleFounded = false;
                    System.out.println(rule + "\t" + P.get(rule));
                }
            }            
            //------------------------------------------------------------------
            // If no rule was found, we add one : word -> Majhoul_X_ ,
            // where X indicates the diacritics.
            if (NoRuleFounded) {
                // Indicate the diacritic :
                //----------------------------------------------------------------------
                char LastDiacritic = words.get(i).charAt(words.get(i).length() - 1);
                String Diacritic = "0_";
                String AlifLem = "0_";

                switch (LastDiacritic) {
                    case 'َ':
                        Diacritic = "1_";
                        break;
                    case 'ُ':
                        Diacritic = "2_";
                        break;
                    case 'ِ':
                        Diacritic = "3_";
                        break;
                    case 'ً':
                        Diacritic = "4_";
                        break;
                    case 'ٌ':
                        Diacritic = "5_";
                        break;
                    case 'ٍ':
                        Diacritic = "6_";
                        break;
                    default:
                         if (words.get(i).charAt(words.get(i).length() - 1) == 'ا') {
                    if (words.get(i).charAt(words.get(i).length() - 2) == 'َ') {
                        Diacritic = "1_";
                    }else if (words.get(i).charAt(words.get(i).length() - 2) == 'ً') {
                        Diacritic = "4_";
                    }else{
                        Diacritic = "0_";
                    }
                }
                        break;
                }
                
                //---
                String Ta3rif = words.get(i).substring(0, 2);
                if (Ta3rif.equals("ال")) {
                    AlifLem = "1_";
                }
                //---
                Majhool = Majhool + Diacritic + AlifLem;
                //------------ Adding rule :               
                String rule = Majhool + "#" + words.get(i).toString().trim();
                Rules.add(rule);
                P.put(rule, 0.5);                
                score[i][i + 1][NonTerminal.indexOf(Majhool)] = P.get(rule);
                System.out.println(rule + "\t" + P.get(rule));
            }
            NoRuleFounded = true;
            Majhool = "_مجهول_";
            //-------------------------------------------------------------------
            Boolean added = Boolean.TRUE;
            while (added == Boolean.TRUE) {
                added = Boolean.FALSE;
                for (String A : NonTerminal) {
                    for (String B : NonTerminal) {
                        if (score[i][i + 1][NonTerminal.indexOf(B)] != null
                                && score[i][i + 1][NonTerminal.indexOf(B)] > 0
                                && Rules.contains(A + "#" + B)) {
                            Double prob = P.get(A + "#" + B) * score[i][i + 1][NonTerminal.indexOf(B)];
                            if (score[i][i + 1][NonTerminal.indexOf(A)] != null
                                    && prob > score[i][i + 1][NonTerminal.indexOf(A)]) {
                                score[i][i + 1][NonTerminal.indexOf(A)] = prob;
                                back[i][i + 1][NonTerminal.indexOf(A)] = A + "->" + B;
                                added = Boolean.TRUE;
                            }
                        }
                    }
                }
            }
        }
    //**************************** OPTIMIZATION START
        for (int span = 2; span < words.size() + 1; span++) {
            for (int begin = 0; begin < words.size() - span + 1; begin++) {
                int end = begin + span;
                for (int split = begin + 1; split < end; split++) {
                    for (String A : NonTerminal) {
                        for (String B : NonTerminal) {
                            for (String C : NonTerminal) {
                                if (score[begin][split][NonTerminal.indexOf(B)] != null
                                        && score[split][end][NonTerminal.indexOf(C)] != null
                                        && P.get(A + "#" + B + " " + C) != null) {
                                     //System.out.println(A + "#" + B + " " + C);
                                    Double prob = score[begin][split][NonTerminal.indexOf(B)]
                                            * score[split][end][NonTerminal.indexOf(C)]
                                            * P.get(A + "#" + B + " " + C);

                                    if (score[begin][end][NonTerminal.indexOf(A)] != null
                                            && prob > score[begin][end][NonTerminal.indexOf(A)]) {
                                        score[begin][end][NonTerminal.indexOf(A)] = prob;
                                        back[begin][end][NonTerminal.indexOf(A)] = ((Integer.toString(begin) + "|" + Integer.toString(split) + "|" + Integer.toString(end) + " #" + A + "->" + B + " " + C + "#" + prob));
                                        //System.out.println(A + "@" + B + " " + C);
                                    }
                                }
                            }
                        }
                    }

                }
                //handle unaries  
                Boolean added = Boolean.TRUE;
                while (added == Boolean.TRUE) {
                    added = Boolean.FALSE;
                    for (String A : NonTerminal) {
                        for (String B : NonTerminal) {
                            if (P.get(A + "#" + B) != null
                                    && score[begin][end][NonTerminal.indexOf(B)] != null) {
                                Double prob = P.get(A + "#" + B)
                                        * score[begin][end][NonTerminal.indexOf(B)];
                                if (prob > score[begin][end][NonTerminal.indexOf(A)]) {
                                    score[begin][end][NonTerminal.indexOf(A)] = prob;
                                    back[begin][end][NonTerminal.indexOf(A)] = A + "->" + B;
                                    added = Boolean.TRUE;                                    
                                }
                            }
                        }
                    }
                }
            }

        }

        //==========        
        int count = 0;
        for (int i = 0; i < words.size() + 1; i++) {
            for (int j = 0; j < words.size() + 1; j++) {
                for (int k = 0; k < NonTerminal.size(); k++) {
                    if (score[i][j][k] != 0.0) {
                        count++;
                        //System.out.println(count+"\t"+score[i][j][k]);
                    }
                }
            }
        }
        //---------                             
        for (int i = 0; i < words.size() + 1; i++) {
            for (int j = 0; j < words.size() + 1; j++) {
                ArrayList<String> category = new ArrayList<String>();
                for (int k = 0; k < NonTerminal.size(); k++) {                        
                    if (back[i][j][k] != ""
                           && back[i][j][k].contains("|")
                           ) {
                        parseTree.add(back[i][j][k]);
                    }
                }
            }
        }
        
//        System.out.println("\n-------------------------------ParseTree :");
//        for(String w : parseTree){
//            System.out.println(w);
//        }
//        System.out.println("\n-------------------------------\n");
        
        //=========
//        System.out.println("\n-------------------------------Rules\n"+Rules);
//        System.out.println("\n-------------------------------Non Terminals\n"+NonTerminal);
//        System.out.println("\n-------------------------------Non P\n"+P);
        //======================================================================
        // Rules are sorted by split
        ArrayList<String> parseTreeSortedBySplit = new ArrayList<String>();       
        
        for (int i = 0; i <= words.size(); i++) {            
            for (String w : parseTree) {                
                int split = Integer.valueOf(w.split("#")[0].split("\\|")[1]);                
                if (split == i) {
                    parseTreeSortedBySplit.add(w);
                }
            }

        }
        //======================================================================

        return parseTreeSortedBySplit;
    }
    ArrayList<String> MostLikelyRules = new ArrayList<String>();

    /**
     * This method extract from all possibles rules the most likely ones;
     * Sarting with a main nonterminal (جملة) it explore recursevely the most
     * likely rules allowing to parse all the sentence.
     * @param NonTerminal
     * @param begin
     * @param end
     * @param PossibleRules 
     */
    public void FindMostLikelyRules(String NonTerminal, int begin, int end, ArrayList<String> PossibleRules) {

        ArrayList<String> RulesTemp = new ArrayList<String>();

        if (end - begin > 1) {

            for (String rule : PossibleRules) { // Finding Rules beginning with NonTerminal and are between begin and end

                if (NonTerminal.equals(rule.split("#")[1].split("->")[0])
                        && Integer.valueOf(rule.split("#")[0].split("\\|")[0].trim()) == begin
                        && Integer.valueOf(rule.split("#")[0].split("\\|")[2].trim()) == end) {
                    RulesTemp.add(rule);
                }
            }
            double PMax = 0.0;
            String RuleMax = "";
            for (String rule : RulesTemp) { // Finding rule with highest prob.                
                if (Double.valueOf(rule.split("#")[2].trim()) > PMax) {
                    PMax = Double.valueOf(rule.split("#")[2].trim());
                    RuleMax = rule;
                }
            }
            if(!RuleMax.equals("")){
                MostLikelyRules.add(RuleMax);
                // Recursive appel : 
                // A -> B C
                // FindMostLikelyRules(B, Begin, Split, possiblesRules)
                // FindMostLikelyRules(C, Split, End  , possiblesRules)

                FindMostLikelyRules(RuleMax.split("#")[1].split("->")[1].split("\\s")[0]
                        , begin
                        , Integer.valueOf(RuleMax.split("#")[0].split("\\|")[1].trim())
                        , PossibleRules);
                FindMostLikelyRules(RuleMax.split("#")[1].split("->")[1].split("\\s")[1]
                        , Integer.valueOf(RuleMax.split("#")[0].split("\\|")[1].trim())
                        , end
                        , PossibleRules);
            }
        }
    }

    /**
     *  This is the main method, it explore the result given by the CKY algorithm,
     * return an array list of the most likely rules and Display result in the console.
     *
     * @param senetnce to parse.
     * @throws FileNotFoundException
     */
    public ArrayList<String> BuildTree(ArrayList<String> words, Boolean LittleRulesFile) throws FileNotFoundException {

        int Classe = 1;

        ArrayList<ArrayList<Double>> probabilities = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<String>> rules = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<Integer>> Begins = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> Splits = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> Ends = new ArrayList<ArrayList<Integer>>();

        ArrayList<Double> probabilitie = new ArrayList<Double>();
        ArrayList<String> rule = new ArrayList<String>();
        ArrayList<Integer> Begin = new ArrayList<Integer>();
        ArrayList<Integer> Split = new ArrayList<Integer>();
        ArrayList<Integer> End = new ArrayList<Integer>();
                

        ArrayList<String> ParseTree = CKYParse(words, LittleRulesFile);

        //======================================================================
        System.out.println("\n---------------------------------<Possible Rules>");     
        //======================================================================
        for (String parse : ParseTree) {
            System.out.println(parse);
        }

        //--FINDING MOST LIKELY RULES--
        String NT = "_جملة_";
        FindMostLikelyRules(NT, 0, words.size(), ParseTree);
        ArrayList<String> SelectedRules = new ArrayList<String>();
        MostLikelyRules.addAll(ReorderRules(MostLikelyRules, NT));
        System.out.println("\n----------------------<Most Likely Rules Ordered>");
        for (String w : MostLikelyRules) {
            System.out.println(w);
            String SelectedRule = w.split("#")[1]
                    + "\t"
                    +       w.split("#")[0].split("\\|")[0]
                    + "|" + w.split("#")[0].split("\\|")[1]
                    + "|" + w.split("#")[0].split("\\|")[2] ;
            SelectedRules.add(SelectedRule);
        }
        
        //======================================================================    
        System.out.println("\n-------------------------------------<Parse Tree>");
        String tree = "";
        int span = 0;
        System.out.print("+");
        tree += "+";
        for (String word : words) {
            System.out.print("------------------+");
            tree += "------------------+";
        }
        System.out.print("\n" + span);
        tree += "\n" + Integer.toString(span);
        span++;
        for (String word : words) {
            String wordWithoutDiacritics = word.replaceAll("[ًٌٍَُِّْ]", "");
            System.out.print("    " + word);
            tree += "    " + word;
            for (int i = 0; i < 14 - wordWithoutDiacritics.length(); i++) {
                System.out.print(" ");
                tree += " ";
            }
            System.out.print(span);
            tree += Integer.toString(span);
            span++;
        }
        //----------------------------------------------------------------------
        System.out.print("\n+");
        tree += "\n+";
        for (String word : words) {
            System.out.print("------------------+");
            tree += "------------------+";
        }
        //----------------------------------------------------------------------
        System.out.println();

        ArrayList<String> finalRules = (SelectedRules);
        ArrayList<String> finalRulesGUI = new ArrayList<String>();
        finalRulesGUI.addAll(SelectedRules);
        DrawTree(finalRules);
        tluser.clear();

        return finalRulesGUI;
    }
}
