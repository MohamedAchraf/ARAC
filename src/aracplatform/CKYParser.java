/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aracplatform;

import Core.Utils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED<mohamedachraf@wanadoo.fr>
 */
public class CKYParser {

    public static final String UTF8_BOM = "\uFEFF";
    ArrayList<String> Unary = new ArrayList<>();
    ArrayList<String> tluser = new ArrayList<>();
    ArrayList<String> result = new ArrayList<>();

    /**
     * This method explore file containing rules and extract terminal or non
     * terminals in an array list string.
     *
     * @param SourceFilepath
     * @return List of terminals or non terminals
     * @throws FileNotFoundException
     */
    public ArrayList<String> LoadParses(String SourceFilepath) throws FileNotFoundException, IOException {

        Utils utils = new Utils();
        ArrayList<String> NonTerminals = new ArrayList<>();
        ArrayList<String> rulesFileContent = new ArrayList<>();

        rulesFileContent = utils.LoadFileToArrayListByLine(SourceFilepath);
        for (String rule : rulesFileContent) {
            String[] line = rule.split("#");
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
     * This method extract rules from text source file in an array list string.
     *
     * @param RulesSourceFile
     * @return List of rules [Exp: مبتدأ->اسم]
     * @throws FileNotFoundException
     */
    public ArrayList<String> LoadRules(String RulesSourceFile) throws FileNotFoundException, IOException {

        Utils utils = new Utils();

        ArrayList<String> Rules = new ArrayList<>();
        ArrayList<String> RulesFileContent = new ArrayList<>();

        RulesFileContent = utils.LoadFileToArrayListByLine(RulesSourceFile);

        for (String input : RulesFileContent) {
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
     *
     * @param RulesSourceFile
     * @return Weighted Rules
     * @throws FileNotFoundException
     */
    public Map<String, Double> LoadWRules(String RulesSourceFile) throws FileNotFoundException, IOException {

        Utils utils = new Utils();
        
        Map<String, Double> WeightedRules = new HashMap<>();
        ArrayList<String> rules = new ArrayList<>();
        
        rules = utils.LoadFileToArrayListByLine(RulesSourceFile);

        for (String input : rules) {
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
     * This method reorder rules using recursive Depth-first search
     * (DFS).originlly rules are not ordered : <br>
     * --------------------------------------<br>
     * Non Ordered Rules <br>
     * --------------------------------------<br>
     * _مبتدأ->_اسمإشارة _اسم_<br>
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
     * This methods Draw parse tree
     *
     * @param rules
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
     * CKY Algorithm.(the Cocke–Younger–Kasami algorithm) it's a parsing
     * algorithm for context-free grammars.It employs bottom-up parsing and
     * dynamic programming.(Wiki).it returns all posiible rules.
     *
     * @param words
     * @param TestMode indicate which rule file we should use : the little or
     * the complete one.
     * @return All Possible rules
     * @throws FileNotFoundException
     */
    public ArrayList<String> CKYParse(ArrayList<String> words, Boolean TestMode) throws FileNotFoundException, IOException {

        ArrayList<String> parseTree = new ArrayList<>();
        ArrayList<String> NonTerminal = new ArrayList<>();
        ArrayList<String> Rules = new ArrayList<>();
        Map<String, Double> P = new HashMap<>();

        //--------------------------------------
        // Loading terminals, non terminalsn and rules (+weighted rules)        
        if (TestMode) {
            NonTerminal = LoadParses("/data/PCFG/NonTerminalRulesLittle.txt");
            Rules = LoadRules("/data/PCFG/rulesLittle.txt");
            P = LoadWRules("/data/PCFG/rulesLittle.txt");

        } else {
            NonTerminal = LoadParses("/data/PCFG/nonTerminalRules.txt");
            Rules = LoadRules("/data/PCFG/Total_rules.txt");
            P = LoadWRules("/data/PCFG/Total_rules.txt");

        }
        //--------------------------------------
        //Display infos
        System.out.println("\n[CKY Parsing Algorithm]\n"
                + "\t[INFO]\t"
                + "\n\t\tRULES LIST SIZE::\t" + Rules.size()
                + "\n\t\tNON TERMINALS SIZE::\t" + NonTerminal.size());
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
        System.out.println("\n\t[LEXICONS]");
        String Majhool = "_مجهول_";

        for (int i = 0; i < words.size(); i++) {
            Boolean NoRuleFounded = true;
            for (String A : NonTerminal) {
                String rule = A + "#" + words.get(i).trim();
                if (Rules.contains(rule)) {
                    score[i][i + 1][NonTerminal.indexOf(A)] = P.get(rule);
                    Unary.add(A);
                    NoRuleFounded = false;
                    System.out.println("\t\t" + rule + "\t" + P.get(rule));
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
                            } else if (words.get(i).charAt(words.get(i).length() - 2) == 'ً') {
                                Diacritic = "4_";
                            } else {
                                Diacritic = "0_";
                            }
                        }
                        break;
                }
                //--------------------------------------------------------------
                //----------------------------------------------------------------------
                // تَكونُ عَلَامَةُ جَرِّهِ الْياءُ فِي ثَلاثَةِ أَنْواعٍ مِن الْأَسْماءِ هِيَ:
                // 1.	الِاسْمُ الْمُثَنَّى مِثْلَ: بُرُورُ الْوَالِدَيْـنِ وَاجِبٌ عَلَى الْأَوْلَادِ..
                // 2.	جَمْعُ الْمُذَكَّرِ السَّالِمُ مِثْلَ: تَعْتَمِدُ الْأُمَمُ عَلَى الْمُفَكِّرِيـنَ الْمُخْلِصِـيـنَ : عَلَامَةُ جَرِّهِ الْياءُ.
                // 3.	الْأَسْمَاءُ الْخَمْسَةُ : "أَبِيكَ"، "وَ"أَخِيكَ"، وَ"حَمِيكَ"، وَ"فِيكَ"، وَ"ذِي"
                if (words.get(i).length() > 3
                        && words.get(i).charAt(words.get(i).length() - 1) == 'َ'
                        && words.get(i).charAt(words.get(i).length() - 2) == 'ن'
                        && words.get(i).charAt(words.get(i).length() - 3) == 'ي'
                        && words.get(i).charAt(words.get(i).length() - 4) == 'ِ') {
                    Diacritic = "3_";
                }
                //--------------------------------------------------------------
                //---
                String Ta3rif = words.get(i).substring(0, 2);
                if (Ta3rif.equals("ال")) {
                    AlifLem = "1_";
                }
                //---
                Majhool = Majhool + Diacritic + AlifLem;
                //------------ Adding rule :               
                String rule = Majhool + "#" + words.get(i).trim();
                Rules.add(rule);
                P.put(rule, 0.5);
                score[i][i + 1][NonTerminal.indexOf(Majhool)] = P.get(rule);
                System.out.println("\t\t" + rule + "\t" + P.get(rule));
            }
            NoRuleFounded = true;
            Majhool = "_مجهول_";
            //-------------------------------------------------------------------
            Boolean added = Boolean.TRUE;
            while (Objects.equals(added, Boolean.TRUE)) {
                added = Boolean.FALSE;
                for (String A : NonTerminal) {
                    for (String B : NonTerminal) {
                        if (score[i][i + 1][NonTerminal.indexOf(B)] > 0
                                && Rules.contains(A + "#" + B)) {
                            Double prob = P.get(A + "#" + B) * score[i][i + 1][NonTerminal.indexOf(B)];
                            if (prob > score[i][i + 1][NonTerminal.indexOf(A)]) {
                                score[i][i + 1][NonTerminal.indexOf(A)] = prob;
                                back[i][i + 1][NonTerminal.indexOf(A)] = A + "->" + B;
                                added = Boolean.TRUE;
                                System.out.println("\t\t[NEW]\t" + A + "->" + B);
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
                                if (P.get(A + "#" + B + " " + C) != null) {
                                    //System.out.println("[NEW]\t"+A + "#" + B + " " + C);
                                    Double prob = score[begin][split][NonTerminal.indexOf(B)]
                                            * score[split][end][NonTerminal.indexOf(C)]
                                            * P.get(A + "#" + B + " " + C);

                                    if (prob > score[begin][end][NonTerminal.indexOf(A)]) {
                                        score[begin][end][NonTerminal.indexOf(A)] = prob;
                                        back[begin][end][NonTerminal.indexOf(A)] = ((Integer.toString(begin) + "|" + Integer.toString(split) + "|" + Integer.toString(end) + " #" + A + "->" + B + " " + C + "#" + prob));
                                        // System.out.println("[NEW]\t"+A + "@" + B + " " + C);
                                    }
                                }
                            }
                        }
                    }

                }
                //handle unaries  
                Boolean added = Boolean.TRUE;
                while (Objects.equals(added, Boolean.TRUE)) {
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
                ArrayList<String> category = new ArrayList<>();
                for (int k = 0; k < NonTerminal.size(); k++) {
                    if (!"".equals(back[i][j][k])
                            && back[i][j][k].contains("|")) {
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
        ArrayList<String> parseTreeSortedBySplit = new ArrayList<>();

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
    ArrayList<String> MostLikelyRules = new ArrayList<>();

    /**
     * This method extract from all possibles rules the most likely ones;
     * Sarting with a main nonterminal (جملة) it explore recursevely the most
     * likely rules allowing to parse all the sentence.
     *
     * @param NonTerminal
     * @param begin
     * @param end
     * @param PossibleRules
     */
    public void FindMostLikelyRules(String NonTerminal, int begin, int end, ArrayList<String> PossibleRules) {

        ArrayList<String> RulesTemp = new ArrayList<>();

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
            if (!RuleMax.equals("")) {
                MostLikelyRules.add(RuleMax);
                // Recursive appel : 
                // A -> B C
                // FindMostLikelyRules(B, Begin, Split, possiblesRules)
                // FindMostLikelyRules(C, Split, End  , possiblesRules)

                FindMostLikelyRules(RuleMax.split("#")[1].split("->")[1].split("\\s")[0], begin, Integer.valueOf(RuleMax.split("#")[0].split("\\|")[1].trim()), PossibleRules);
                FindMostLikelyRules(RuleMax.split("#")[1].split("->")[1].split("\\s")[1], Integer.valueOf(RuleMax.split("#")[0].split("\\|")[1].trim()), end, PossibleRules);
            }
        }
    }

    /**
     * This is the main method, it explore the result given by the CKY
     * algorithm, return an array list of the most likely rules and Display
     * result in the console.
     *
     * @param words
     * @param LittleRulesFile
     * @return 
     * @throws FileNotFoundException
     */
    public ArrayList<String> BuildTree(ArrayList<String> words, Boolean LittleRulesFile) throws FileNotFoundException, IOException {
      

        ArrayList<String> ParseTree = CKYParse(words, LittleRulesFile);

        //======================================================================
        System.out.println("\n\t[POSSIBLE RULES]");
        //======================================================================
        // Display possible rules
        for (String parse : ParseTree) {
            String rulesList[] = parse.split("[#]");
            System.out.print("\t\t" + rulesList[0] + "\t");
            String rulesDetails[] = rulesList[1].split("(->)");
            System.out.print(rulesDetails[1].split("\\s+")[1]
                    + "\t" + rulesDetails[1].split("\\s+")[0]
                    + " \t : \t " + rulesDetails[0]);
            System.out.println("\t\t" + rulesList[2]);
        }

        //--FINDING MOST LIKELY RULES--
        System.out.println("\n\t[MOST LIKELY RULES ORDERED]");
        String NT = "_جملة_";
        FindMostLikelyRules(NT, 0, words.size(), ParseTree);
        ArrayList<String> SelectedRules = new ArrayList<>();
        MostLikelyRules.addAll(ReorderRules(MostLikelyRules, NT));
        for (String w : MostLikelyRules) {
            //--------------------------------
            // Display Most Likely Rules
            String rulesList[];
            rulesList = w.split("[#]");
            System.out.print("\t\t" + rulesList[0] + "\t");
            String rulesDetails[] = rulesList[1].split("(->)");
            System.out.print(rulesDetails[1].split("\\s+")[1]
                    + "\t" + rulesDetails[1].split("\\s+")[0]
                    + " \t : \t " + rulesDetails[0]);
            System.out.println("\t\t" + rulesList[2]);
            //--------------------------------
            String SelectedRule = w.split("#")[1]
                    + "\t"
                    + w.split("#")[0].split("\\|")[0]
                    + "|" + w.split("#")[0].split("\\|")[1]
                    + "|" + w.split("#")[0].split("\\|")[2];
            SelectedRules.add(SelectedRule);
        }
        System.out.println("\n----------------------------------------------");
        //======================================================================    
        System.out.println("[PARSE TREE]");
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
        ArrayList<String> finalRulesGUI = new ArrayList<>();
        finalRulesGUI.addAll(SelectedRules);
        DrawTree(finalRules);
        tluser.clear();

        return finalRulesGUI;
    }
}
