
/**
 *
 * @author Eric Jorens
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;


@SuppressWarnings("serial")
public class DnaAnalysis extends JFrame{
    
    public DnaAnalysis(){
        //ctor
        super("DNA Analysis");
        buildGui();
        
    }
    
    //ACTIONS
    private void pButtonAction (ActionEvent e) {
       //process action
        
        
        
        //validate data input
        int invalid = validateData();
        //System.out.println(invalid);
        
        if(invalid > 0 && invalid != 1){
            JOptionPane.showMessageDialog(this, "There are " + invalid + " invalid characters\nin this sequence...\nProcess Aborted!", "DnaAnalysis.java",INFORMATION_MESSAGE);
            formatText.setText("Process Aborted...");
            dataText.setText("There are " + invalid + " invalid characters in the raw sequence:\n");
            dataText.append(errors.toString());
        }else if(invalid == 1){
            JOptionPane.showMessageDialog(this, "There is " + invalid + " invalid character\nin this sequence...\nProcess Aborted!", "DnaAnalysis.java",INFORMATION_MESSAGE);
            formatText.setText("Process Aborted...");
            dataText.setText("There is " + invalid + " invalid character in the raw sequence:\n");
            dataText.append(errors.toString());
        }else if(invalid == -1){
            JOptionPane.showMessageDialog(this, "Try an input sequence...\nProcess Aborted!", "DnaAnalysis.java", INFORMATION_MESSAGE);
        }else if(invalid == 0){
            
            //upper or lower case
            String outTxt = rawText.getText();
            if(upRadio.isSelected()){
                outTxt = outTxt.toUpperCase();
            }else if(lowRadio.isSelected()){
                outTxt = outTxt.toLowerCase();
            }
            
            //remove whitespaces from sequence
            Pattern removeWhite = Pattern.compile("[ACGTacgt]");
            Matcher match = removeWhite.matcher(outTxt);
            StringBuilder tempOut = new StringBuilder();
            while(match.find()){
                tempOut.append(outTxt.charAt(match.start()));
            }
            outTxt = tempOut.toString();

            //produce the analysis data for output
            countData();
            
            //start building the string output
            StringBuilder finalOut = new StringBuilder();
            char c;
            //int count = 1;
            
            //set the line length
            int lineMax = Integer.parseInt(lengths[lineLength.getSelectedIndex()]);
            
            
            //see if line numbers is selected
            boolean numbers = false;
            if(lineNumbers.isSelected()){
                numbers = true;
            }
            //see if grouped output is selected
            boolean group = false;
            if(groupOutput.isSelected()){
                group = true;
            }
            
            //stuff to parse out the number of digits in the sequence length value
            String outDigits = new String(Integer.toString(outTxt.length()));
            String outDigitsCurrent = new String();
            int charI;
            //length of the number of digits of the length of the sequence
            int charLength = outDigits.length();
     
            //main character looop
            for(int i = 0; i<outTxt.length(); i++){

                c = outTxt.charAt(i);
                
                //adjust for line maximum
                if(i%lineMax == 0){
                    finalOut.append("\n");
                    //add numbers if it is selected
                    if(numbers){
                        outDigitsCurrent = Integer.toString(i+1);
                        //length of the number of digits of the length of the sequence
                        charI = outDigitsCurrent.length();
                        //print proceeding zeros
                        for(int x = 0; x <= (charLength - (charI + 1)); x++){
                            finalOut.append("0");
                        };
                        //print line character number
                        finalOut.append(i+1);
                        finalOut.append(" ");
                        
              
                    }
                }     
                //no space before line start when no numbers present
                if(group == true){
                    if(i%10 == 0 && i%lineMax != 0){
                        finalOut.append(" ");
                    }
                } 
                
                finalOut.append(c); //add character to output
            }//end of loop

            //final output
            formatText.setText(finalOut.toString());   
            pButton.setText("Resubmit");
        }  
        //this.resize(550, 500);
        
    }
    
    private void rButtonAction (ActionEvent e) {
        //reset action
        int result = JOptionPane.showConfirmDialog(this, "Reset all fields?");
	if(result == JOptionPane.YES_OPTION){
		rawText.setText("");
                formatText.setText("");
                dataText.setText("");
                lineNumbers.setSelected(false);
                groupOutput.setSelected(false);
                lineLength.setSelectedIndex(2);
                lowRadio.setSelected(true);
                pButton.setText("Process");
	}
    }
    
    private int validateData(){
        String s = rawText.getText();
        errors = new StringBuilder();
        if(s.equals("")){
            return -1;
        }
	Pattern p = Pattern.compile("[^ACGTacgt\\p{Space}]");
	Matcher m = p.matcher(s);
        Highlighter h = rawText.getHighlighter();
        h.removeAllHighlights();
	int countInvalid = 0;
	while(m.find()){
            countInvalid++;
            try{
                h.addHighlight(m.start(), m.end(), new DefaultHighlighter.DefaultHighlightPainter(new Color(255,0,0)));
                errors.append("Error " + countInvalid +": position " + m.start() + "\n");
            }catch(Exception ble){}
	}
        
        return countInvalid;
    }
    
    private void countData(){
        String s = rawText.getText();
        StringBuilder pOut = new StringBuilder();
        int numA = 0, numC = 0, numG = 0, numT = 0, seqSpace = 0;
        
        //remove only valid characters
        Pattern allValid = Pattern.compile("[ACGTacgt]");
        Matcher matchV = allValid.matcher(s);

        while(matchV.find()){
            seqSpace++;
        }
        
        if(seqSpace == 0){
            JOptionPane.showMessageDialog(this, "Try something other than just spaces...\nProcess Aborted!", "DnaAnalysis.java",INFORMATION_MESSAGE);
        }else if(seqSpace != 0){
            pOut.append("Sequence Length: " + seqSpace + "\n");

            //number of A's in sequence    
            Pattern a = Pattern.compile("[Aa]");
            Matcher matchA = a.matcher(s);

            while(matchA.find()){
                numA++;
            }
            double aVal = (double)numA/(double)seqSpace*100;
            pOut.append("Number of A: " + numA + " --> " + aVal + "%\n");

            //number of C's in sequence
            Pattern c = Pattern.compile("[Cc]");
            Matcher matchC = c.matcher(s);

            while(matchC.find()){
                numC++;
            }
            double cVal = (double)numC/(double)seqSpace*100;
            pOut.append("Number of C: " + numC + " --> " + cVal + "%\n");

            //number of G's in sequence
            Pattern g = Pattern.compile("[Gg]");
            Matcher matchG = g.matcher(s);

            while(matchG.find()){
                numG++;
            }
            double gVal = (double)numG/(double)seqSpace*100;
            pOut.append("Number of G: " + numG + " --> " + gVal + "%\n");

            //number of T's in sequence
            Pattern t = Pattern.compile("[Tt]");
            Matcher matchT = t.matcher(s);

            while(matchT.find()){
                numT++;
            }
            double tVal = (double)numT/(double)seqSpace*100;
            pOut.append("Number of T: " + numT + " --> " + tVal+ "%\n");

            dataText.setText(pOut.toString());   
        }
        
        
    }
    
    
    private void buildGui(){
        
        //buttons
        //process
        pButton = new JButton();
        pButton.addActionListener(new ActionListener(){
            
            public void actionPerformed(ActionEvent e) {
		pButtonAction(e);
            }
        });
        pButton.setText("Process");
        
        //reset
        rButton = new JButton();
        rButton.addActionListener(new ActionListener(){
            
            public void actionPerformed(ActionEvent e) {
		rButtonAction(e);
            }
        });
        rButton.setText("Reset");
        
        //textares
        rawText = new JTextArea();
        rawText.setColumns(85);
        rawText.setWrapStyleWord(true);
        rawText.setLineWrap(true);
        rawText.setFont(new Font("Monospaced", Font.PLAIN, 12)); 
      
        formatText = new JTextArea();
        formatText.setEditable(false);
        formatText.setColumns(85);
        formatText.setWrapStyleWord(true);
        formatText.setLineWrap(true);
        formatText.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        dataText = new JTextArea();
        dataText.setEditable(false);
        dataText.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        
        //scrollpanes
        s1 = new JScrollPane(rawText);
        s2 = new JScrollPane(formatText);
        s3 = new JScrollPane(dataText);
        
        //checkboxs
        lineNumbers = new JCheckBox();
        groupOutput = new JCheckBox();
        
        //radio buttons
        upRadio = new JRadioButton("", false);
        lowRadio = new JRadioButton("", true);
        //group the radio buttons
        ButtonGroup rGroup = new ButtonGroup();
        rGroup.add(lowRadio);
        rGroup.add(upRadio);
        
        //combobox
        lineLength = new JComboBox<Object>(lengths);
        lineLength.setSelectedIndex(2);
        
        //labels
        labOld = new JLabel();
        labOld.setText("Raw Sequence");
        labNew = new JLabel();
        labNew.setText("Formatted Sequence");
        labData = new JLabel();
        labData.setText("Sequence Analysis");
        labFormat = new JLabel();
        labFormat.setText("Line Length");
        labUpper = new JLabel();
        labUpper.setText("Upper Case:");
        labLower = new JLabel();
        labLower.setText("Lower Case:");
        labNum = new JLabel();
        labNum.setText("Show line Numbers: ");
        labGroup = new JLabel();
        labGroup.setText("Group Output (10):");
        
      
        //raw sequence panel
        p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
        p1.add(labOld);
        p1.add(s1);
        
        //formatted sequence panel
        p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
        p2.add(labNew);
        p2.add(s2);
        
        //formatted sequence panel
        p3 = new JPanel();
        p3.setLayout(new BoxLayout(p3, BoxLayout.Y_AXIS));
        p3.add(labData);
        p3.add(s3);
       
      
        //BUTTON PANEL
        p4 = new JPanel(new GridLayout(1,5));
        
        //inner panel 1
        p41 = new JPanel(new FlowLayout());
        p41.add(labFormat);
        p41.add(lineLength);
        
        //inner panel 2
        p42 = new JPanel(new GridLayout(2,1));
        JPanel upPanel = new JPanel(new FlowLayout());
        upPanel.add(labUpper);
        upPanel.add(upRadio);
        JPanel lowPanel = new JPanel(new FlowLayout());
        lowPanel.add(labLower);
        lowPanel.add(lowRadio);
        p42.add(upPanel);
        p42.add(lowPanel);
     
        //inner panel 3
        p43 = new JPanel(new GridLayout(2,1));
        JPanel numPanel = new JPanel(new FlowLayout());
        numPanel.add(labNum);
        numPanel.add(lineNumbers);
        JPanel groupPanel = new JPanel(new FlowLayout());
        groupPanel.add(labGroup);
        groupPanel.add(groupOutput);
        p43.add(numPanel);
        p43.add(groupPanel);
        
        //inner panel 4
        p44 = new JPanel(new GridLayout(2,1));
        p44.add(pButton);
        p44.add(rButton);
        
        //add inner panels to button panel
        p4.add(p41);
        p4.add(p42);
        p4.add(p43);
        p4.add(p44);
     
        //panes to container
        Container c = getContentPane();
        c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
        c.add(p1);
        c.add(p2);
        c.add(p3);
        c.add(p4);
        
        
        
        setMinimumSize(new Dimension(550,500));
        setVisible(true);
    }
    
    //declare the internal variables
    private JButton pButton, rButton;
    private JTextArea rawText, formatText, dataText;
    private JCheckBox lineNumbers, groupOutput;
    private JRadioButton upRadio, lowRadio;
    private JComboBox<Object> lineLength;
    private JLabel labOld, labNew, labData, labFormat, labUpper, labLower, labNum, labGroup;
    private JPanel p1,p2,p3,p4,p41,p42,p43,p44;
    private JScrollPane s1,s2,s3;
    private String[] lengths = {"40", "50", "60", "70"};
    private int INFORMATION_MESSAGE;
    private StringBuilder errors;
    
    
    public static void main(String[] args) {
    	DnaAnalysis main = new DnaAnalysis();
    	main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    
    
}
