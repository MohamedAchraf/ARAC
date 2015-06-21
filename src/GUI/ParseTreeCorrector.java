/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Core.Utils;
import aracplatform.ParseExpression;
import edu.stanford.nlp.ling.StringLabelFactory;
import edu.stanford.nlp.trees.LabeledScoredTreeFactory;
import edu.stanford.nlp.trees.PennTreeReader;
import edu.stanford.nlp.trees.Tree;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JInternalFrame;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED <mohamedachraf@gmail.com>
 */
public class ParseTreeCorrector extends javax.swing.JInternalFrame {

    String formattedExpression;
    int sentenceOrder;
    ArrayList<String> sentences = new ArrayList<>();
    Utils utils = new Utils();
    File file = new File("/data/PCFG/ParsedSentences.txt");
    String XMLFile = "/data/PCFG/ParsedSentencesXML.XML";

    /**
     * Update XML file after a parse correction
     *
     * @param XMLFilePath
     * @param sentenceId
     * @param NewParse
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void updateXMLFILe(String XMLFilePath, Integer sentenceId, String NewParse) throws FileNotFoundException, IOException {

        ArrayList<String> Joumlas = new ArrayList<>();
        ArrayList<String> TotalXMLFile = new ArrayList<>();
        String XMLFileContent = utils.LoadFileToStringByPath(XMLFilePath);
        File FileXML = new File(XMLFilePath);

        // Find sentences by pattern 
        String pattern = "(<Joumla length=)(.*?)(</Joumla>)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(XMLFileContent);

        // Load Sentences into List
        while (m.find()) {
            Joumlas.add(m.group(0));
        }

        // Update list
        while (NewParse.contains("  ")) {
            NewParse = NewParse.replaceAll("  ", " ");
        }

        NewParse = NewParse.replaceAll("\t", "");
        NewParse = NewParse.replaceAll("\n", "");
        NewParse = NewParse.replaceAll(" <", "<");
        NewParse = NewParse.replaceAll("> ", ">");
        Joumlas.set(sentenceId - 3, NewParse);

        // Reconstruct XML file
        String XMLFileHead = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                DocumentAttribute = "<Document>",
                EndDocumentAttribute = "</Document>";

        TotalXMLFile.add(XMLFileHead);
        TotalXMLFile.add(DocumentAttribute);
        for (String joumla : Joumlas) {
            TotalXMLFile.add(joumla);
        }
        TotalXMLFile.add(EndDocumentAttribute);

        // Save into XML file 
        utils.SaveArrayListToFileByLine(TotalXMLFile, FileXML);

    }

    /**
     *
     * @param sentence
     * @return parse form of a sentence, example: ( جملة (خبر (مضاف_ومضاف_إليه
     * (مضاف_إليه الغِذَاءِ) (مضاف أسْعَارَ) ) (فعل_مضارع_معلوم يَرْفَعُ) (تحقيق
     * قَدْ) ) (مبتدأ الجَفَافُ) )
     */
    public static String ParseDisplay(String sentence) {

        String formattedSentence = "", indent = "    ";
        Integer test = 0;
        sentence.replaceAll("\n", "");
        for (int i = 0; i < sentence.length(); i++) {

            if (sentence.charAt(i) == '(') {
                formattedSentence += "\n";
                for (int j = 0; j < test; j++) {
                    formattedSentence += indent;
                }
            }

            formattedSentence += sentence.charAt(i);

            if (sentence.charAt(i) == '(') {
                test++;
            }
            if (sentence.charAt(i) == ')') {
                test--;
            }
        }

        return formattedSentence;
    }

    /**
     *
     * @param parsedSentence
     * @param modifications
     */
    public void TreeParseDisplay(String parsedSentence, Boolean modifications) {
        //======================================================================  
        parseTree.removeAll();
        expression.setText(sentences.get(sentenceOrder - 1));
        sentenceNumber.setText(String.valueOf(sentenceOrder / 2));

        if (modifications == Boolean.FALSE) {
            parse.setText(ParseDisplay(sentences.get(sentenceOrder)));
        }

        String parseString = parsedSentence;

        edu.stanford.nlp.parser.ui.TreeJPanel tjp = new edu.stanford.nlp.parser.ui.TreeJPanel();

        String ptbTreeString = (parseString);

        Tree tree = null;
        try {
            tree = (new PennTreeReader(new StringReader(ptbTreeString), new LabeledScoredTreeFactory(new StringLabelFactory()))).readTree();
        } catch (IOException ex) {
            Logger.getLogger(ParseTreeCorrector.class.getName()).log(Level.SEVERE, null, ex);
        }

        tjp.setTree(tree);
        tjp.setBackground(Color.white);
        tjp.setFont(new Font("Traditional arabic", 10, 10));
        JInternalFrame frame = new JInternalFrame();
        frame.getContentPane().add(tjp, BorderLayout.CENTER);
        frame.setPreferredSize(new Dimension(parseTree.getWidth(), parseTree.getHeight()));
        frame.setMaximizable(true);
        frame.setClosable(true);
        frame.setIconifiable(true);
        frame.setResizable(true);

        frame.pack();
        frame.setVisible(true);
        frame.setVisible(true);
        parseTree.add(frame);
        //====================================================================== 
    }

    /**
     * Creates new form ParseTreeCorrector
     *
     * @throws java.io.FileNotFoundException
     */
    public ParseTreeCorrector() throws FileNotFoundException, IOException {
        initComponents();

        //----------------------------------------------------------------------
        sentenceOrder = 2;
        sentences = utils.LoadFileToArrayListByLine("/data/PCFG/ParsedSentences.txt");

//        parse.setText(ParseDisplay(sentences.get(sentenceOrder)));
//        TreeParseDisplay(sentences.get(sentenceOrder), Boolean.FALSE);
        //----------------------------------------------------------------------
        parse.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        tags.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        //----------------------------------------------------------------------
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        expression = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        sentenceNumber = new javax.swing.JTextField();
        Button_Next = new javax.swing.JButton();
        Button_Load = new javax.swing.JButton();
        Button_Save = new javax.swing.JButton();
        Button_Previous = new javax.swing.JButton();
        parseTree = new javax.swing.JDesktopPane();
        jPanel3 = new javax.swing.JPanel();
        Button_replace = new javax.swing.JButton();
        OldText = new javax.swing.JTextField();
        NewText = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        parse = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        deleteContents = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tags = new javax.swing.JList();
        ReplaceSelectionWith = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(1024, 768));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Parse Tree Corrector"));

        expression.setFont(new java.awt.Font("Arial", 0, 21)); // NOI18N
        expression.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        expression.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expressionActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Parse Tree"));

        sentenceNumber.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        sentenceNumber.setText("1");
        sentenceNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sentenceNumberActionPerformed(evt);
            }
        });

        Button_Next.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/go-next.png"))); // NOI18N
        Button_Next.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_NextActionPerformed(evt);
            }
        });

        Button_Load.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/start.png"))); // NOI18N
        Button_Load.setText("Load");
        Button_Load.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_LoadActionPerformed(evt);
            }
        });

        Button_Save.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save.png"))); // NOI18N
        Button_Save.setText("Save");
        Button_Save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_SaveActionPerformed(evt);
            }
        });

        Button_Previous.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/go-previous.png"))); // NOI18N
        Button_Previous.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_PreviousActionPerformed(evt);
            }
        });

        parseTree.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout parseTreeLayout = new javax.swing.GroupLayout(parseTree);
        parseTree.setLayout(parseTreeLayout);
        parseTreeLayout.setHorizontalGroup(
            parseTreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        parseTreeLayout.setVerticalGroup(
            parseTreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 530, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(parseTree)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(Button_Save)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Button_Previous)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_Next)
                        .addGap(18, 18, 18)
                        .addComponent(sentenceNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_Load)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Button_Save)
                    .addComponent(Button_Load)
                    .addComponent(Button_Previous)
                    .addComponent(Button_Next)
                    .addComponent(sentenceNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(parseTree, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {Button_Load, Button_Next, Button_Previous, Button_Save, sentenceNumber});

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Parse"));

        Button_replace.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/edit-find-replace.png"))); // NOI18N
        Button_replace.setText("Replace All");
        Button_replace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_replaceActionPerformed(evt);
            }
        });

        OldText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        OldText.setToolTipText("");

        NewText.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        parse.setColumns(20);
        parse.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        parse.setRows(5);
        jScrollPane1.setViewportView(parse);

        jLabel2.setText("Find What:");

        jLabel3.setText("Replace With:");

        deleteContents.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete_konsole_content.png"))); // NOI18N
        deleteContents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteContentsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(OldText, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteContents, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(NewText, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Button_replace, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(deleteContents)
                    .addComponent(OldText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NewText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_replace, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 519, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {Button_replace, NewText, OldText, deleteContents});

        tags.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "فعل_ماضي_معلوم", "فعل_مضارع_معلوم", "فعل_مضارع_مجزوم", "فعل_مضارع_منصوب", "فعل_مضارع_مؤكد_ثقيل", "فعل_أمر", "فعل_أمر_مؤكد", "فعل_ماضي_مجهول", "فعل_مضارع_مجهول", "فعل_مضارع_مجهول_مجزوم", "فعل_مضارع_مجهول_منصوب", "فعل_مضارع_مؤكد_ثقيل_مجهول", "استأناف", "استفهام", "استقبال", "اسم_إشارة", "اسم_موصول", "تحقيق", "جار", "حرف_عطف", "شرط", "ضمير_رفع", "نصب", "كان_وأخواتها", "ناسخ", "نداء", "ضمير", "نعت_ومنعوت", "مضاف_ومضاف_إليه", "مجرور", "ظرف_مكان", "حال", "مبتدأ", "خبر", "فاعل", "مفعول_به" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        tags.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(tags);

        ReplaceSelectionWith.setText("Replace Selection With");
        ReplaceSelectionWith.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ReplaceSelectionWithActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(expression))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                    .addComponent(ReplaceSelectionWith, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ReplaceSelectionWith, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(expression))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 591, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void expressionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expressionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_expressionActionPerformed

    private void Button_SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_SaveActionPerformed

        // Update Display
        // Update pasrse tree
        sentences.set(sentenceOrder, parse.getText().replace("\n", "").trim());
        sentences.set(sentenceOrder, sentences.get(sentenceOrder).trim().replaceAll("    ", ""));

        // Update sentence
        sentences.set(sentenceOrder - 1, expression.getText().trim());

        // Update parsed sentences file
        try {
            utils.SaveArrayListToFileByLine(sentences, file);
        } catch (IOException ex) {
            Logger.getLogger(ParseTreeCorrector.class.getName()).log(Level.SEVERE, null, ex);
        }

        TreeParseDisplay(sentences.get(sentenceOrder), Boolean.FALSE);
        parse.setText(ParseDisplay(sentences.get(sentenceOrder)));

        // Convert parse to XML
        String XMLResult = "";
        ParseExpression parseExpression = new ParseExpression();
        try {
            XMLResult = parseExpression.parseToXMLConvert(expression.getText().trim(), parse.getText().trim());

            //System.out.println("expression =\t"+expression.getText().trim()+"\nParse = "+parse.getText().trim());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ParseTreeCorrector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ParseTreeCorrector.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Update XML file
        //System.out.println("sentenceOrder / 2 ="+ sentenceOrder );
        try {
            updateXMLFILe(XMLFile, sentenceOrder / 2, XMLResult);
        } catch (IOException ex) {
            Logger.getLogger(ParseTreeCorrector.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_Button_SaveActionPerformed

    private void Button_PreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_PreviousActionPerformed
        sentenceOrder--;
        sentenceOrder--;

        if (sentenceOrder < 1) {
            sentenceOrder = sentences.size() - 1;
        }

        TreeParseDisplay(sentences.get(sentenceOrder), Boolean.FALSE);

        parse.setText(ParseDisplay(sentences.get(sentenceOrder)));

    }//GEN-LAST:event_Button_PreviousActionPerformed

    private void Button_NextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_NextActionPerformed
        sentenceOrder++;
        sentenceOrder++;

        if (sentenceOrder > sentences.size()) {
            sentenceOrder = 2;
        }

        TreeParseDisplay(sentences.get(sentenceOrder), Boolean.FALSE);

        parse.setText(ParseDisplay(sentences.get(sentenceOrder)));
    }//GEN-LAST:event_Button_NextActionPerformed

    private void Button_LoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_LoadActionPerformed

        sentenceOrder = Integer.parseInt(sentenceNumber.getText()) * 2;
        TreeParseDisplay(sentences.get(sentenceOrder), Boolean.FALSE);

        parse.setText(ParseDisplay(sentences.get(sentenceOrder)));
    }//GEN-LAST:event_Button_LoadActionPerformed

    private void Button_replaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_replaceActionPerformed

        parse.setText(parse.getText().replaceAll(OldText.getText(), NewText.getText()));

        TreeParseDisplay(parse.getText().trim(), Boolean.TRUE);

    }//GEN-LAST:event_Button_replaceActionPerformed

    private void sentenceNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sentenceNumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sentenceNumberActionPerformed

    private void ReplaceSelectionWithActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ReplaceSelectionWithActionPerformed
        int start = parse.getSelectionStart();
        int end = parse.getSelectionEnd();
        StringBuilder strBuilder = new StringBuilder(parse.getText());
        strBuilder.replace(start, end, tags.getSelectedValue().toString());
        parse.setText(strBuilder.toString());
        TreeParseDisplay(parse.getText().trim(), Boolean.TRUE);
    }//GEN-LAST:event_ReplaceSelectionWithActionPerformed

    private void deleteContentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteContentsActionPerformed
        OldText.setText("");
        NewText.setText("");
    }//GEN-LAST:event_deleteContentsActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_Load;
    private javax.swing.JButton Button_Next;
    private javax.swing.JButton Button_Previous;
    private javax.swing.JButton Button_Save;
    private javax.swing.JButton Button_replace;
    private javax.swing.JTextField NewText;
    private javax.swing.JTextField OldText;
    private javax.swing.JButton ReplaceSelectionWith;
    private javax.swing.JButton deleteContents;
    private javax.swing.JTextField expression;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea parse;
    private javax.swing.JDesktopPane parseTree;
    private javax.swing.JTextField sentenceNumber;
    private javax.swing.JList tags;
    // End of variables declaration//GEN-END:variables
}
