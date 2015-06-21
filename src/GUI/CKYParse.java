/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Core.Utils;
import aracplatform.CKYParser;
import aracplatform.ParseExpression;
import aracplatform.SchemesParser;
import edu.stanford.nlp.ling.StringLabelFactory;
import edu.stanford.nlp.trees.LabeledScoredTreeFactory;
import edu.stanford.nlp.trees.PennTreeReader;
import edu.stanford.nlp.trees.Tree;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED<mohamedachraf@wanadoo.fr>
 */
public class CKYParse extends javax.swing.JInternalFrame {

    //-*-*-*--*-*-*--*-*-*--*-*-*--*-*-*--*-*-*--*-*-*--*-*-*--*-*-*--*-*-*--*-*    
    //The following code redirect System.out to the jTextArea "konsoleContent"
    // Source : http://billwaa.wordpress.com/2011/11/14/java-gui-console-output/
    private void updateTextArea(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                konsoleContent.append(text);
            }
        });
    }

//Followings are The Methods that do the Redirect, you can simply Ignore them. 
    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                updateTextArea(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                updateTextArea(new String(b, off, len));
            }

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
        };

        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }
    //-*-*-*--*-*-*--*-*-*--*-*-*--*-*-*--*-*-*--*-*-*--*-*-*--*-*-*--*-*-*--*-*
    public String FormattedParseRules = "( جملة",
            FormattedParseRulesDup = "";

    int ruleOrder = 0;
    int ruleOrder_ = 0;
    int parentheseToAddA = 0;
    int parentheseToAddB = 0;
    String expression = "( جملة";

    ArrayList<String> NT_SYMB = new ArrayList<>();
    ArrayList<String> NT_TAGS = new ArrayList<>();

    long duration;

    //==========================================================================
    /**
     * This recursive method allows to display rules WITH virtual terminals in
     * the format accepted by Stanford parser : (S(NP(NNP parser))(VP(VBZ
     * works))(PP(IN for)(PRP * me))(.!)) Rules are in the form : Start -> A B
     * Begin|Split|end the 'break' command at the end of the 'if' loop aims to
     * avoid that two rules with the same header be interpreted twice.
     *
     * @param Rules
     * @param Start
     * @param Sentence
     * @param NumRule
     */
    public void FormattedRulesWithVirtualNonTerminals(ArrayList<String> Rules, String Start, ArrayList<String> Sentence, int NumRule) {

        for (int i = 0; i < Rules.size(); i++) {
            Integer begin = Integer.valueOf(Rules.get(i).split("\\t")[1].split("\\|")[0].trim());
            Integer split = Integer.valueOf(Rules.get(i).split("\\t")[1].split("\\|")[1].trim());
            Integer end = Integer.valueOf(Rules.get(i).split("\\t")[1].split("\\|")[2].trim());

            if (Rules.get(i).split("\\t")[0].split("->")[0].split("\\s")[0].equals(Start)
                    && i != NumRule) {
                String A = null, B = null;
                //--------------------------------------------------------------                  
                A = Rules.get(i).split("\\t")[0].split("->")[1].split("\\s")[1];
                if (end - split == 1 && ruleOrder < Sentence.size()) {
                    FormattedParseRules += "(" + A + " " + Sentence.get(ruleOrder++);
                } else {
                    FormattedParseRules += "(" + A;
                }
                FormattedRulesWithVirtualNonTerminals(Rules, A, Sentence, i);
                FormattedParseRules += ")";
                //-------------------------------------------------------------- 
                B = Rules.get(i).split("\\t")[0].split("->")[1].split("\\s")[0];
                if (split - begin == 1 && ruleOrder < Sentence.size()) {
                    FormattedParseRules += "(" + B + " " + Sentence.get(ruleOrder++);
                } else {
                    FormattedParseRules += "(" + B;
                }
                FormattedRulesWithVirtualNonTerminals(Rules, B, Sentence, i);
                FormattedParseRules += ") ";
                //--------------------------------------------------------------
                break;
            }
        }

    }

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
     * @throws java.io.FileNotFoundException
     */
    public void FormattedRulesWithoutVirtualNonTerminals(ArrayList<String> Rules, String Start, ArrayList<String> Sentence, int NumRule) throws FileNotFoundException, IOException {

        Utils utils = new Utils();
        String virtuals = "/data/PCFG/VirtualSymbols.txt";
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
    public void drawV(Graphics g, String A, String B, int x, int y, int size, int level, int terminal) {

        //g.setFont(new Font("Arial", Font.PLAIN, 17));
        int decx = 20, decy = 20, decyy = 25;
        int delta = (DesktopPane.getWidth() / 100);
        super.paintComponent(DesktopPane.getGraphics());
        if (terminal == 0) { // Non terminal
            g.drawLine(x, y, x + size * level + delta, y + level - delta);
            g.drawString(A, x + size * level + delta - decx, y + level - delta + decyy);

            g.drawLine(x, y, x - size * level - delta, y + level - delta);
            g.drawString(B, x - size * level - delta - decx, y + level - delta + decyy);
        } else if (terminal == 1) { // terminal droite
            g.drawLine(x + size * level + delta, y + level + 30 - delta, x + size * level + delta, y + level + 70 - delta);
            g.drawString(A, x + size * level - decx + delta, y + level + 80 + decy - delta);
        } else if (terminal == 2) { // terminal gauche
            g.drawLine(x - size * level - delta, y + level + 30 - delta, x - size * level - delta, y + level + 70 - delta);
            g.drawString(A, x - size * level - decx - delta, y + level + 80 + decy - delta);
        }
    }

    //--------------------------------------------------------------------------
    public void drawruleParse(String A, String B, int position, int terminal) {
        // 16 24 28 30 31 29 26 27 25 20 22 23 21 18 19 17 8 12 14 15 13 10 11 9 4 6 7 5 2 3 1
        int LineSize = DesktopPane.getWidth() / 28;
        int VerticalStep = DesktopPane.getHeight() / 7;
        int HorizontalStep = DesktopPane.getWidth() / 31;
        int initialHeight = 30;

//       A += " ["+Integer.toString(position)+"]";
//       B += " ["+ Integer.toString(position)+"]";       
        if (position == 1) {
            String Title = "جملة";
            DesktopPane.getGraphics().drawString(Title, HorizontalStep * 16, initialHeight - 10);
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 16, initialHeight, 4, LineSize, terminal);
        }
        if (position == 2) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 24, initialHeight + VerticalStep, 3, LineSize, terminal);
        }
        if (position == 3) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 28, initialHeight + 2 * VerticalStep, 2, LineSize, terminal);
        }
        if (position == 4) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 30, initialHeight + 3 * VerticalStep, 1, LineSize, terminal);
        }
        if (position == 5) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 31, initialHeight + 4 * VerticalStep, 0, LineSize, terminal);
        }
        if (position == 6) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 29, initialHeight + 4 * VerticalStep, 0, LineSize, terminal);
        }
        if (position == 7) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 26, initialHeight + 3 * VerticalStep, 1, LineSize, terminal);
        }
        if (position == 8) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 27, initialHeight + 4 * VerticalStep, 0, LineSize, terminal);
        }
        if (position == 9) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 25, initialHeight + 4 * VerticalStep, 0, LineSize, terminal);
        }
        if (position == 10) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 20, initialHeight + 2 * VerticalStep, 2, LineSize, terminal);
        }
        if (position == 11) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 22, initialHeight + 3 * VerticalStep, 1, LineSize, terminal);
        }
        if (position == 12) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 23, initialHeight + 4 * VerticalStep, 0, LineSize, terminal);
        }
        if (position == 13) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 21, initialHeight + 4 * VerticalStep, 0, LineSize, terminal);
        }
        if (position == 14) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 18, initialHeight + 3 * VerticalStep, 1, LineSize, terminal);
        }
        if (position == 15) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 19, initialHeight + 4 * VerticalStep, 0, LineSize, terminal);
        }
        if (position == 16) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 17, initialHeight + 4 * VerticalStep, 0, LineSize, terminal);
        }
        if (position == 17) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 8, initialHeight + VerticalStep, 3, LineSize, terminal);
        }
        if (position == 18) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 12, initialHeight + 2 * VerticalStep, 2, LineSize, terminal);
        }
        if (position == 19) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 14, initialHeight + 3 * VerticalStep, 1, LineSize, terminal);
        }
        if (position == 20) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 15, initialHeight + 4 * VerticalStep, 0, LineSize, terminal);
        }
        if (position == 21) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 13, initialHeight + 4 * VerticalStep, 0, LineSize, terminal);
        }
        if (position == 22) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 10, initialHeight + 3 * VerticalStep, 1, LineSize, terminal);
        }
        if (position == 23) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 11, initialHeight + 4 * VerticalStep, 0, LineSize, terminal);
        }
        if (position == 24) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 9, initialHeight + 4 * VerticalStep, 0, LineSize, terminal);
        }
        if (position == 25) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 4, initialHeight + 2 * VerticalStep, 2, LineSize, terminal);
        }
        if (position == 26) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 6, initialHeight + 3 * VerticalStep, 1, LineSize, terminal);
        }
        if (position == 27) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 7, initialHeight + 4 * VerticalStep, 0, LineSize, terminal);
        }
        if (position == 28) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 5, initialHeight + 4 * VerticalStep, 0, LineSize, terminal);
        }
        if (position == 29) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 2, initialHeight + 3 * VerticalStep, 1, LineSize, terminal);
        }
        if (position == 30) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 3, initialHeight + 4 * VerticalStep, 0, LineSize, terminal);
        }
        if (position == 31) {
            drawV(DesktopPane.getGraphics(), A, B, HorizontalStep * 1, initialHeight + 4 * VerticalStep, 0, LineSize, terminal);
        }

    }
    //==========================================================================

    /**
     * This method allow to check if parse tree is regular, that's mean it's not
     * empty and it covers all the sentence; no span was rejected : 0 1 2 3 ...
     *
     * @param RulesList
     * @return Boolean
     */
    public boolean ParseTreeIsRegular(ArrayList<String> RulesList) {

        if (RulesList.isEmpty()) {
            return false;
        }

        ArrayList<Long> spans = new ArrayList<>();
        for (int i = 0; i < RulesList.size(); i++) {
            long begin = Integer.valueOf(RulesList.get(i).split("\\t")[1].split("\\|")[0].trim());
            long split = Integer.valueOf(RulesList.get(i).split("\\t")[1].split("\\|")[1].trim());
            long end = Integer.valueOf(RulesList.get(i).split("\\t")[1].split("\\|")[2].trim());

            if (!spans.contains(begin)) {
                spans.add(begin);
            }
            if (!spans.contains(split)) {
                spans.add(split);
            }
            if (!spans.contains(end)) {
                spans.add(end);
            }
        }

        Collections.sort(spans);
        System.out.println(spans);

        for (int i = 0; i < spans.size() - 1; i++) {
            if (spans.get(i + 1) - spans.get(i) > 1) {
                return false;
            }
        }

        return true;
    }

    /**
     * Creates new form CKYParse
     */
    public CKYParse() {
        initComponents();
        //-------------- Set image icon for tabs
        ImageIcon icon1 = new ImageIcon(getClass().getResource("/images/konsole_111_icon.png"));
        ImageIcon imgTab1 = new ImageIcon(icon1.getImage().getScaledInstance(21, 21, 1));
        konsole.setIconAt(0, imgTab1);

        ImageIcon icon2 = new ImageIcon(getClass().getResource("/images/konsole_2_icon.png"));
        ImageIcon imgTab2 = new ImageIcon(icon2.getImage().getScaledInstance(21, 21, 1));
        konsole.setIconAt(1, imgTab2);

        ImageIcon icon3 = new ImageIcon(getClass().getResource("/images/konsole_33_icon.png"));
        ImageIcon imgTab3 = new ImageIcon(icon3.getImage().getScaledInstance(21, 21, 1));
        konsole.setIconAt(2, imgTab3);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        TreeDisplayOptions = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        phrase = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        ButtonLoadSentence = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        ButtonSaveParsedSentence = new javax.swing.JButton();
        ButtonParse = new javax.swing.JButton();
        ButtonPastefromClipboard = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        LinguisticTags = new javax.swing.JRadioButton();
        DisplayMode = new javax.swing.JComboBox();
        OriginalTags = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        Fontface = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        ButtonDisplayMode = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        StatusText = new javax.swing.JLabel();
        konsole = new javax.swing.JTabbedPane();
        ParseTreePanel = new javax.swing.JPanel();
        DesktopPane = new javax.swing.JDesktopPane();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        RulesList = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        konsoleContent = new javax.swing.JTextArea();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        setBorder(null);
        setTitle("CKY Parser");
        setMaximumSize(null);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Proposed Parse Tree"));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Sentence"));

        phrase.setFont(new java.awt.Font("Times New Roman", 0, 21)); // NOI18N
        phrase.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        phrase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                phraseActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Parsing Tools"));

        ButtonLoadSentence.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Documents-icon.png"))); // NOI18N
        ButtonLoadSentence.setToolTipText("Load Sentence");
        ButtonLoadSentence.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonLoadSentenceActionPerformed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete_2.gif"))); // NOI18N
        jButton1.setToolTipText("Delete");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        ButtonSaveParsedSentence.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save.png"))); // NOI18N
        ButtonSaveParsedSentence.setToolTipText("Save");
        ButtonSaveParsedSentence.setEnabled(false);
        ButtonSaveParsedSentence.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonSaveParsedSentenceActionPerformed(evt);
            }
        });

        ButtonParse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/start.png"))); // NOI18N
        ButtonParse.setText("   Parse");
        ButtonParse.setToolTipText("Parse");
        ButtonParse.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                ButtonParseMousePressed(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ButtonParseMouseClicked(evt);
            }
        });
        ButtonParse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonParseActionPerformed(evt);
            }
        });

        ButtonPastefromClipboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/paste.gif"))); // NOI18N
        ButtonPastefromClipboard.setToolTipText("Paste");
        ButtonPastefromClipboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonPastefromClipboardActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ButtonParse)
                .addGap(14, 14, 14)
                .addComponent(ButtonPastefromClipboard, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ButtonLoadSentence, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ButtonSaveParsedSentence, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {ButtonLoadSentence, ButtonPastefromClipboard, ButtonSaveParsedSentence, jButton1});

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ButtonParse, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ButtonSaveParsedSentence, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ButtonLoadSentence, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ButtonPastefromClipboard, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {ButtonLoadSentence, ButtonPastefromClipboard, ButtonSaveParsedSentence, jButton1});

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Display Settings"));

        TreeDisplayOptions.add(LinguisticTags);
        LinguisticTags.setSelected(true);
        LinguisticTags.setText("Linguistic");

        DisplayMode.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Standard", "Virtual Non Terminals", "Classical" }));

        TreeDisplayOptions.add(OriginalTags);
        OriginalTags.setText("Original");

        jLabel3.setText("Tags:");

        Fontface.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Traditional Arabic", "Andalus", "Arabic Typesetting", "Arial Unicode MS", "Arial", "Courier New", "Microsoft Uighur", "Sakkal Majalla", "Sakkal Majalla Bold", "Simplified Arabic Bold", "Simplified Arabic", "Simplified Arabic Fixed", "Times New Roman", "Traditional Arabic Bold" }));

        jLabel1.setText("Font:");

        jLabel2.setText("Display Mode:");

        ButtonDisplayMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/update.png"))); // NOI18N
        ButtonDisplayMode.setMaximumSize(new java.awt.Dimension(65, 41));
        ButtonDisplayMode.setMinimumSize(new java.awt.Dimension(65, 41));
        ButtonDisplayMode.setPreferredSize(new java.awt.Dimension(65, 41));
        ButtonDisplayMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonDisplayModeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(DisplayMode, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Fontface, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LinguisticTags)
                    .addComponent(OriginalTags, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ButtonDisplayMode, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(Fontface, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(LinguisticTags, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(DisplayMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(OriginalTags, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ButtonDisplayMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Status"));

        StatusText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        StatusText.setText("Ready");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(StatusText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(StatusText)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(phrase, javax.swing.GroupLayout.PREFERRED_SIZE, 950, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(phrase, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );

        konsole.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);

        ParseTreePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        DesktopPane.setBackground(new java.awt.Color(255, 255, 255));
        DesktopPane.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        DesktopPane.setToolTipText("");
        DesktopPane.setAutoscrolls(true);
        DesktopPane.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        javax.swing.GroupLayout ParseTreePanelLayout = new javax.swing.GroupLayout(ParseTreePanel);
        ParseTreePanel.setLayout(ParseTreePanelLayout);
        ParseTreePanelLayout.setHorizontalGroup(
            ParseTreePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ParseTreePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(DesktopPane)
                .addContainerGap())
        );
        ParseTreePanelLayout.setVerticalGroup(
            ParseTreePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ParseTreePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(DesktopPane, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        konsole.addTab("Parse Tree", ParseTreePanel);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        RulesList.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jScrollPane1.setViewportView(RulesList);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        konsole.addTab("Rules List", jPanel5);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        konsoleContent.setColumns(20);
        konsoleContent.setFont(konsoleContent.getFont());
        konsoleContent.setRows(5);
        jScrollPane2.setViewportView(konsoleContent);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete_konsole_content.png"))); // NOI18N
        jButton2.setToolTipText("Delete Konsole Content");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/increase_font.png"))); // NOI18N
        jButton3.setToolTipText("Increase the font size");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/decrease_font.png"))); // NOI18N
        jButton4.setToolTipText("Decrease the font size");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton2, jButton3, jButton4});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButton2, jButton3, jButton4});

        konsole.addTab("Parsing Details", jPanel1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(konsole)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(konsole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(228, 228, 228))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        int fontSize = konsoleContent.getFont().getSize();
        konsoleContent.setFont(new Font(konsoleContent.getFont().getFontName(), konsoleContent.getFont().getStyle(), --fontSize));
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        int fontSize = konsoleContent.getFont().getSize();
        konsoleContent.setFont(new Font(konsoleContent.getFont().getFontName(), konsoleContent.getFont().getStyle(), ++fontSize));
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        konsoleContent.setText("");
    }//GEN-LAST:event_jButton2ActionPerformed

    private void ButtonDisplayModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonDisplayModeActionPerformed

        String parseString = null;
        DesktopPane.removeAll();
        String expression_tmp = expression;
        String FormattedParseRulesDup_tmp = FormattedParseRulesDup;
        //----------------------------------------------------------------------
        if (LinguisticTags.isSelected()) {
            for (String SYMB : NT_SYMB) {
                if (DisplayMode.getSelectedIndex() == 0 && expression_tmp.contains(SYMB)) { // Display mode = Standard
                    expression_tmp = expression_tmp.replace(SYMB, NT_TAGS.get(NT_SYMB.indexOf(SYMB)));
                }

                if (DisplayMode.getSelectedIndex() == 1 && FormattedParseRulesDup_tmp.contains(SYMB)) { // Display mode = VNT
                    FormattedParseRulesDup_tmp = FormattedParseRulesDup_tmp.replace(SYMB, NT_TAGS.get(NT_SYMB.indexOf(SYMB)));
                }
            }
        }
        //----------------------------------------------------------------------

        if (DisplayMode.getSelectedIndex() == 0) { // Display mode "Standard" is selected
            parseString = expression_tmp;
        } else if (DisplayMode.getSelectedIndex() == 1) { // Display mode "Virtual Non Terminals" is selected
            parseString = FormattedParseRulesDup_tmp;
        }

        //------------------------------------------------------------------
        // Stanford tree display tool :
        //-----------------
        edu.stanford.nlp.parser.ui.TreeJPanel tjp = new edu.stanford.nlp.parser.ui.TreeJPanel();

        String ptbTreeString = (parseString);

        Tree tree = null;
        try {
            tree = (new PennTreeReader(new StringReader(ptbTreeString), new LabeledScoredTreeFactory(new StringLabelFactory()))).readTree();
        } catch (IOException ex) {
            Logger.getLogger(CKYParse.class.getName()).log(Level.SEVERE, null, ex);
        }
        tjp.setTree(tree);
        tjp.setBackground(Color.white);
        tjp.setFont(new Font(Fontface.getSelectedItem().toString(), 10, 10));
        JInternalFrame frame = new JInternalFrame();
        frame.getContentPane().add(tjp, BorderLayout.CENTER);
        frame.setPreferredSize(new Dimension(DesktopPane.getWidth(), DesktopPane.getHeight()));
        frame.setMaximizable(true);
        frame.setClosable(true);
        frame.setIconifiable(true);
        frame.setResizable(true);

        frame.pack();
        frame.setVisible(true);
        frame.setVisible(true);
        DesktopPane.add(frame);
    }//GEN-LAST:event_ButtonDisplayModeActionPerformed

    private void ButtonPastefromClipboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonPastefromClipboardActionPerformed
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        try {
            phrase.setText((String) contents.getTransferData(DataFlavor.stringFlavor));
        } catch (UnsupportedFlavorException | IOException ex) {
            Logger.getLogger(CKYParse.class.getName()).log(Level.SEVERE, null, ex);
        }

        //------------------ Disable save sentence button
        ButtonSaveParsedSentence.setEnabled(false);
    }//GEN-LAST:event_ButtonPastefromClipboardActionPerformed

    private void ButtonParseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonParseActionPerformed

        // Eliminate extra whitespaces
        phrase.setText(phrase.getText().trim().replaceAll("\\s+", " "));
        //----------------------------------------------------------------------
        // Load tags to convert non Terminals symbols to plain tags :
        //                  _مضافمضافإليه_1_ --> مضاف و مضاف إليه
        Utils utils = new Utils();
        try {
            NT_SYMB = utils.LoadFileToArrayListByLine("/data/PCFG/NT_TAG_1.txt");
            NT_TAGS = utils.LoadFileToArrayListByLine("/data/PCFG/NT_TAG_2.txt");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CKYParse.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CKYParse.class.getName()).log(Level.SEVERE, null, ex);
        }

        //-----------
        long start_Time = Calendar.getInstance().getTime().getTime();
        //-----------
        Boolean ParseTreeFound = true;

        if (phrase.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Empty sentense field!\nPlease choose a File or type a sentence.");

        } else {
            ArrayList<String> Phrase = new ArrayList<>();
            ArrayList<String> scheme = new ArrayList<>();
            ArrayList<String> schemeTMP1 = new ArrayList<>();
            ArrayList<String> schemeTMP = new ArrayList<>();
            ArrayList<String> PhraseTMP = new ArrayList<>();
            ArrayList<String> Rules = new ArrayList<>();

            //--------------------- Init/Reinit variables
            FormattedParseRules = "( جملة";
            ruleOrder = 0;
            ruleOrder_ = 0;
            parentheseToAddA = 0;
            parentheseToAddB = 0;
            expression = "( جملة";
            //---------------------

            Boolean mashkoul = Boolean.TRUE;
            Boolean RulesListSize = Boolean.FALSE;

            SchemesParser schemesParser = new SchemesParser();
            CKYParser parser = new CKYParser();

            try {
                String words[] = phrase.getText().trim().split("\\s");
                for (int i = 0; i < words.length; i++) {
                    if (!words[i].equals(" ")) {
                        Phrase.add(words[i]);
                    }
                }
                //Empty konsoleContent
                konsoleContent.setText("");

                //Redirect System.out to konsoleContent
                redirectSystemStreams();
                System.out.println("[SCHEMES CONVERSION]");
                //==============================================================
                //==============================================================
                schemeTMP1 = schemesParser.SchemesConvert(Phrase, mashkoul, Boolean.TRUE);
                //==============================================================
                //==============================================================
                //*******************  Formatting result
                for (String w : schemeTMP1) {
                    String[] s = w.split("\\|");
                    schemeTMP.add(s[0]);
                    PhraseTMP.add(s[1]);
                }
                //**************************************************************
                Phrase.clear();
                for (String S : PhraseTMP) {
                    String[] SArr = S.trim().split("\\s");
                    for (int i = 0; i < SArr.length; i++) {
                        Phrase.add(SArr[i]);
                    }
                }
                //**************************************************************
                for (String S : schemeTMP) {
                    String[] SArr = S.trim().split("\\s");
                    for (int i = 0; i < SArr.length; i++) {
                        scheme.add(SArr[i]);
                    }
                }

                System.out.println("\t" + Phrase + "\n\t" + scheme + "\n----------------------------------------------");
                //phrase.setText(scheme.toString());

                //==============================================================
                //==============================================================
                Rules = parser.BuildTree(scheme, RulesListSize);
                //==============================================================
                //==============================================================
                int i = 0;
                DefaultListModel model = new DefaultListModel();
                RulesList.setModel(model);
                for (i = 0; i < Rules.size(); i++) {
                    model.addElement(Rules.get(i));
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(CKYParse.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CKYParse.class.getName()).log(Level.SEVERE, null, ex);
            }
            String sentence = "";
            for (int i = 0; i < Phrase.size(); i++) {
                sentence += Phrase.get(i) + " ";
            }

            //phrase.setText(sentence.trim());
            //=============================<Display "jidd bou" tree >====================================
            if (ParseTreeIsRegular(Rules)) {
                DesktopPane.removeAll();
                Boolean FirstTimeInSideII = true, SplitDesc = true;
                int position = 0;
                int splitGlobal = Integer.valueOf(Rules.get(0).split("\\t")[1].split("\\|")[1]);
                int Previoussplit = Integer.valueOf(Rules.get(0).split("\\t")[1].split("\\|")[1]);
                int end = Integer.valueOf(Rules.get(0).split("\\t")[1].split("\\|")[2].trim());

                for (int i = 0; i < Rules.size(); i++) {
                    int splitLocal = Integer.valueOf(Rules.get(i).split("\\t")[1].split("\\|")[1]);

                    if (splitLocal <= splitGlobal) {
                        int begin = Integer.valueOf(Rules.get(i).split("\\t")[1].split("\\|")[0].trim());

                        if (Previoussplit >= splitLocal) {
                            position++;
                        } else {
                            if (begin == Previoussplit) {
                                if (position == 3) {
                                    position = 10;
                                } else if (position == 7) {
                                    position = 10;
                                } else if (position == 4) {
                                    position = 7;
                                } else if (position == 11) {
                                    position = 14;
                                } else if (position == 2) {
                                    position = 10;
                                } else if (position == 10) {
                                    position = 14;
                                } else if (position == 14) {
                                    position = 16;
                                }

                            } else {
                                if (position == 3) {
                                    position = 7;
                                } else if (position == 7) {
                                    position = 9;
                                } else if (position == 4) {
                                    position = 6;
                                } else if (position == 11) {
                                    position = 13;
                                } else if (position == 14) {
                                    position = 16;
                                } else if (position == 2) {
                                    position = 10;
                                } else if (position == 10) {
                                    position = 14;
                                } else if (position == 14) {
                                    position = 16;
                                }

                            }

                        }

                        String[] T = Rules.get(i).split("\\t")[0].split("->")[1].split("\\s");
                        drawruleParse(T[0], T[1], position, 0);
                        //------------------ Terminals

                        if (Integer.valueOf(Rules.get(i).split("\\t")[1].split("\\|")[1].trim()) - Integer.valueOf(Rules.get(i).split("\\t")[1].split("\\|")[0].trim()) == 1) {
                            int ind = Integer.valueOf(Rules.get(i).split("\\t")[1].split("\\|")[0]);
                            drawruleParse(Phrase.get(ind), Phrase.get(ind), position, 1);
                        }

                        if (Integer.valueOf(Rules.get(i).split("\\t")[1].split("\\|")[2].trim()) - Integer.valueOf(Rules.get(i).split("\\t")[1].split("\\|")[1].trim()) == 1) {
                            int ind = Integer.valueOf(Rules.get(i).split("\\t")[1].split("\\|")[1]);
                            drawruleParse(Phrase.get(ind), Phrase.get(ind), position, 2);
                        }
                        //-------------------

                    } else if (splitLocal > splitGlobal) {
                        if (FirstTimeInSideII) {// Go to the other side but only one time.
                            position = 16;
                            Previoussplit = Integer.valueOf(Rules.get(i).split("\\t")[1].split("\\|")[1]);
                            FirstTimeInSideII = false;
                        }

                        int begin = Integer.valueOf(Rules.get(i).split("\\t")[1].split("\\|")[0].trim());

                        if (Previoussplit < splitLocal) {
                            if (begin == end) {
                                if (position == 17) {
                                    position = 25;
                                } else if (position == 18) {
                                    position = 25;
                                } else if (position == 25) {
                                    position = 29;
                                } else if (position == 19) {
                                    position = 22;
                                } else if (position == 22) {
                                    position = 25;
                                } else if (position == 26) {
                                    position = 28;
                                } else if (position == 29) {
                                    position = 31;
                                }
                            } else {
                                if (position == 17) {
                                    position = 25;
                                } else if (position == 18) {
                                    position = 22;
                                } else if (position == 25) {
                                    position = 29;
                                } else if (position == 19) {
                                    position = 21;
                                } else if (position == 22) {
                                    position = 24;
                                } else if (position == 26) {
                                    position = 28;
                                } else if (position == 29) {
                                    position = 31;
                                }
                            }

                        } else {
                            position++;
                        }

                        String[] T = Rules.get(i).split("\\t")[0].split("->")[1].split("\\s");
                        drawruleParse(T[0], T[1], position, 0);
                        //------------------Terminals
                        if (Integer.valueOf(Rules.get(i).split("\\t")[1].split("\\|")[1].trim()) - Integer.valueOf(Rules.get(i).split("\\t")[1].split("\\|")[0].trim()) == 1) {
                            int ind = Integer.valueOf(Rules.get(i).split("\\t")[1].split("\\|")[0]);
                            drawruleParse(Phrase.get(ind), Phrase.get(ind), position, 1);
                        }

                        if (Integer.valueOf(Rules.get(i).split("\\t")[1].split("\\|")[2].trim()) - Integer.valueOf(Rules.get(i).split("\\t")[1].split("\\|")[1].trim()) == 1) {
                            int ind = Integer.valueOf(Rules.get(i).split("\\t")[1].split("\\|")[1]);
                            drawruleParse(Phrase.get(ind), Phrase.get(ind), position, 2);
                        }
                        //-------------------
                    }
                    //System.out.println("splitGlobal = " + splitGlobal + "\tPrevioussplit = " + Previoussplit + "\tsplitLocal = " + splitLocal + "\tposition = " + position);
                    Previoussplit = splitLocal;
                    end = Integer.valueOf(Rules.get(i).split("\\t")[1].split("\\|")[2].trim());
                }
                String Joumla1 = "_جملة_";
                String Joumla2 = "_جملة_";
                Collections.reverse(Phrase);
                FormattedRulesWithVirtualNonTerminals(Rules, Joumla1, Phrase, -1);
                FormattedParseRules += ")";
                try {
                    FormattedRulesWithoutVirtualNonTerminals(Rules, Joumla2, Phrase, -1);
                    expression += ")";
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(CKYParse.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CKYParse.class.getName()).log(Level.SEVERE, null, ex);
                }

                // If "Linguistic Tags" scheckbox is selected we convert original
                // tags into linguistic tags, exp :
                //              _مضافمضافإليه_1_ --> مضاف و مضاف إليه
                //--------------------------------------------------------------
                if (LinguisticTags.isSelected()) {
                    for (String SYMB : NT_SYMB) {
                        if (DisplayMode.getSelectedIndex() == 0 && expression.contains(SYMB)) { // Display mode = Standard
                            expression = expression.replace(SYMB, NT_TAGS.get(NT_SYMB.indexOf(SYMB)));
                        }

                        if (DisplayMode.getSelectedIndex() == 1 && FormattedParseRules.contains(SYMB)) { // Display mode = VNT
                            FormattedParseRules = FormattedParseRules.replace(SYMB, NT_TAGS.get(NT_SYMB.indexOf(SYMB)));
                        }
                    }
                }

                System.out.println("\n[EXPRESSION]\t" + expression);
                System.out.print("\n[NON FORMATTED]\t" + FormattedParseRules);
                //==================================================================
                String parseString = null;
                if (DisplayMode.getSelectedIndex() == 0) { // Display mode "Standard" is selected
                    parseString = expression;
                } else if (DisplayMode.getSelectedIndex() == 1) { // Display mode "Virtual Non Terminals" is selected
                    parseString = FormattedParseRules;
                }
                //------------------------------------------------------------------
                if (DisplayMode.getSelectedIndex() == 0 || DisplayMode.getSelectedIndex() == 1) { //standard is selected or VNT is selected
                    // Stanford tree display tool :
                    //-----------------
                    edu.stanford.nlp.parser.ui.TreeJPanel tjp = new edu.stanford.nlp.parser.ui.TreeJPanel();

                    String ptbTreeString = (parseString);

                    Tree tree = null;
                    try {
                        tree = (new PennTreeReader(new StringReader(ptbTreeString), new LabeledScoredTreeFactory(new StringLabelFactory()))).readTree();
                    } catch (IOException ex) {
                        Logger.getLogger(CKYParse.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    tjp.setTree(tree);
                    tjp.setBackground(Color.white);
                    tjp.setFont(new Font(Fontface.getSelectedItem().toString(), end, end));
                    JInternalFrame frame = new JInternalFrame();
                    frame.getContentPane().add(tjp, BorderLayout.CENTER);
                    frame.setPreferredSize(new Dimension(DesktopPane.getWidth(), DesktopPane.getHeight()));
                    frame.setMaximizable(true);
                    frame.setClosable(true);
                    frame.setIconifiable(true);
                    frame.setResizable(true);

                    frame.pack();
                    frame.setVisible(true);
                    frame.setVisible(true);
                    DesktopPane.add(frame);
                    duration = Calendar.getInstance().getTime().getTime() - start_Time;
                    StatusText.setText("Parse Tree was found.");
                    //-----------------
                    FormattedParseRulesDup = FormattedParseRules;
                    FormattedParseRules = "( جملة";
                    ruleOrder = 0;
                    parentheseToAddA = 0;
                    parentheseToAddB = 0;

                    //------------------ Enable save sentence button
                    ButtonSaveParsedSentence.setEnabled(true);
                }
                //==================================================================
            } else { // No parse Tree was found
                System.out.println(" No possible parse Tree was found.");
                JInternalFrame frame = new JInternalFrame();
                JTextField textField = new JTextField(" No possible parse Tree was found.");
                textField.setFont(new Font("SansSerif", Font.PLAIN, 12));
                textField.setForeground(Color.red);
                frame.getContentPane().add(textField, BorderLayout.NORTH);
                frame.setPreferredSize(new Dimension(DesktopPane.getWidth(), DesktopPane.getHeight()));
                frame.setMaximizable(true);
                frame.setClosable(true);
                frame.setIconifiable(true);
                frame.setResizable(true);
                frame.pack();
                frame.setVisible(true);
                frame.setVisible(true);
                DesktopPane.add(frame);
                setVisible(true);
                StatusText.setText("No possible parse Tree was found.");
            }
        }

        //-----------
        duration = Calendar.getInstance().getTime().getTime() - start_Time;
        System.out.println("\n[DURATION]\t" + duration / 1000 + " sec" + "\n----------------------------------------------");
        //-----------
    }//GEN-LAST:event_ButtonParseActionPerformed

    private void ButtonParseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ButtonParseMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_ButtonParseMouseClicked

    private void ButtonParseMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ButtonParseMousePressed
        StatusText.setText("Parsing...");
        DesktopPane.removeAll();
    }//GEN-LAST:event_ButtonParseMousePressed

    private void ButtonSaveParsedSentenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonSaveParsedSentenceActionPerformed
        Utils utils = new Utils();
        String filePath;
        filePath = "c:/ParsedSentences.txt";
        File file = new File(filePath);
        String parsedSentences ="";
        
        try {
            System.out.print("[INFO]\tLoading parsed Sentences file...");
            parsedSentences = utils.LoadStringFromUTF8File(filePath);
            System.out.println("Done!");
        } catch (IOException ex) {
            Logger.getLogger(CKYParse.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        parsedSentences = parsedSentences + "\n"+ phrase.getText();
        parsedSentences = parsedSentences + "\n"+ expression;
        
        try {
            System.out.print("[INFO]\tSaving parsed Sentences file...");
            utils.SaveToUTF8File(parsedSentences, file);
            System.out.println("Done!");            
        } catch (IOException ex) {
            Logger.getLogger(CKYParse.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String XMLFilePath;
        XMLFilePath = "c:/ParsedSentencesXML.XML";
        File XMLFile = new File(XMLFilePath);

        String XMLResult = "";
        String XMLLines = "";
        ParseExpression parseExpression = new ParseExpression();
        try {
            XMLResult = parseExpression.parseToXMLConvert(phrase.getText().trim(), expression);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CKYParse.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CKYParse.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            System.out.print("[INFO]\tLoading XML file...");
            XMLLines = utils.LoadStringFromUTF8File(XMLFilePath);
            System.out.println("Done!");
        } catch (IOException ex) {
            Logger.getLogger(CKYParse.class.getName()).log(Level.SEVERE, null, ex);
        }

        String replacement = XMLResult + "</Document>";               
        XMLLines = XMLLines.replaceAll("</Document>", replacement);       
        
       try {
            System.out.print("[INFO]\tSaving XML file...");
            utils.SaveToUTF8File(XMLLines, XMLFile);
            System.out.println("Done!");
        } catch (IOException ex) {
            Logger.getLogger(CKYParse.class.getName()).log(Level.SEVERE, null, ex);
        }

        //--------------------------Disable save button
        ButtonSaveParsedSentence.setEnabled(false);
    }//GEN-LAST:event_ButtonSaveParsedSentenceActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        phrase.setText("");
        DesktopPane.removeAll();
        //------------------ Disable save sentence button
        ButtonSaveParsedSentence.setEnabled(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void ButtonLoadSentenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonLoadSentenceActionPerformed
        Utils utils = new Utils();
        //--------------
        JFileChooser DevSetChooser = new JFileChooser("data/PCFG");
        DevSetChooser.showOpenDialog(null);
        File f = DevSetChooser.getSelectedFile();
        String sourcefile = f.getAbsolutePath();
        //--------------
        File phrrasePath = new File(sourcefile);
        try {
            ArrayList<String> Sentence = new ArrayList<>();
            Sentence = utils.LoadFileToArrayList(phrrasePath);
            String sentence = "";
            for (int i = 0; i < Sentence.size(); i++) {
                sentence += Sentence.get(i) + " ";
            }
            phrase.setText(sentence.toString().trim());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CKYParse.class.getName()).log(Level.SEVERE, null, ex);
        }

        //------------------ Disable save sentence button
        ButtonSaveParsedSentence.setEnabled(false);
    }//GEN-LAST:event_ButtonLoadSentenceActionPerformed

    private void phraseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_phraseActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_phraseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ButtonDisplayMode;
    private javax.swing.JButton ButtonLoadSentence;
    private javax.swing.JButton ButtonParse;
    private javax.swing.JButton ButtonPastefromClipboard;
    private javax.swing.JButton ButtonSaveParsedSentence;
    private javax.swing.JDesktopPane DesktopPane;
    private javax.swing.JComboBox DisplayMode;
    private javax.swing.JComboBox Fontface;
    private javax.swing.JRadioButton LinguisticTags;
    private javax.swing.JRadioButton OriginalTags;
    private javax.swing.JPanel ParseTreePanel;
    private javax.swing.JList RulesList;
    private javax.swing.JLabel StatusText;
    private javax.swing.ButtonGroup TreeDisplayOptions;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane konsole;
    private javax.swing.JTextArea konsoleContent;
    private javax.swing.JTextField phrase;
    // End of variables declaration//GEN-END:variables
}
