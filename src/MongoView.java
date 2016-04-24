import java.util.Vector;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentListener;

public class MongoView extends JFrame{
	
	//	TEXT CONSTANTS
	private final String STRING_TITLE = "J E N S E N   S T U D E N T S";
	
	//	DIMENSION & COORDINATE CONTSTANTS
	private final int BIG_FONT_SIZE = 18;
	private final int TITLE_FONT_SIZE = 30;
	private final int WINDOW_WIDTH = 1000;
	private final int WINDOW_HEIGHT = 600;
	private final int FIELD_WIDTH = 16;
	private final int ID_COLUMN_WIDTH = 200;
	
	//	COLOR CONSTANTS
	private final Color BG_COLOR = new Color(0.1f, 0.1f, 0.1f);
	private final Color MAIN_COLOR = new Color(0.8f, 0.6f, 0.2f);
	private final Color WHITE = new Color(0.9f, 0.9f, 0.8f);
	
	//	FONT CONSTANTS
	private final Font standardFont = new Font(Font.MONOSPACED, Font.BOLD, BIG_FONT_SIZE);
	private final Font titleFont = new Font(Font.MONOSPACED, Font.BOLD, TITLE_FONT_SIZE);
	
	//	OTHER CONSTANTS
	protected int COL_XPOS_1 = 0;
	protected int COL_XPOS_2 = 3;
	
	protected GridBagConstraints gbcForm;
	private GridBagConstraints gbcMain;
	
	private JLabel labelTitle;
	
	protected JPanel panelForm;
	private JPanel panelMain;
	private JPanel panelTable;
	
	private JTable table;
	private JScrollPane pane;
	
	protected MongoClient mongoClient;
	protected MongoDatabase db;
	
	//	CONSTRUCTOR
	public MongoView(){
		initView();
	}
	
	private void initView(){
		
		setTitle("MongoDB");
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setFocusable(true);
        
        //	CREATE MAIN PANEL
        panelMain = new JPanel(new GridBagLayout());
        panelMain.setBackground(BG_COLOR);
        add(panelMain);
        
        //	CREATE TITLE
        labelTitle = new JLabel(STRING_TITLE, SwingConstants.CENTER);
        labelTitle.setFont(titleFont);
        labelTitle.setForeground(MAIN_COLOR);
        gbcMain = new GridBagConstraints();
        gbcMain.fill = GridBagConstraints.BOTH;
        gbcMain.weightx = 1;
        gbcMain.weighty = 0.2;
        gbcMain.gridwidth = 3;
        gbcMain.gridheight = 1;
        panelMain.add(labelTitle, gbcMain);
        
        //	CREATE STUDENT LIST PANEL
        panelTable = new JPanel(new GridBagLayout());
        panelTable.setBackground(BG_COLOR);
        gbcMain.weightx = 0.8;
        gbcMain.weighty = 1;
        gbcMain.gridwidth = 1;
        gbcMain.gridy = 1;
        gbcMain.gridheight = 5;
        panelMain.add(panelTable, gbcMain);
        
        //	CREATE FORM PANEL
        panelForm = new JPanel();
        panelForm.setBackground(BG_COLOR);
        gbcMain.weightx = 0.2;
        gbcMain.gridx = 2;
        gbcMain.gridy = 1;
        gbcMain.gridwidth = 2;
        panelMain.add(panelForm, gbcMain);
        
        //	PREPARE FOR FORM PANEL COMPONENTS
        panelForm.setLayout(new GridBagLayout());
        gbcForm = new GridBagConstraints();
        gbcForm.weightx = 1;
        gbcForm.weighty = 0.2;
        gbcForm.gridwidth = 3;
        gbcForm.gridheight = 1;
        
        gbcForm.gridx = COL_XPOS_1;
        gbcForm.gridy = COL_XPOS_2;
        
        //	NOW WAIT FOR CONTROLLER TO CREATE COMPONENTS
	}
	
	protected JTextField addField(String labelString, DocumentListener listener){
		
		gbcForm.gridy++;
		gbcForm.gridx = COL_XPOS_1;
		JLabel tempLabel = new JLabel(labelString);
		tempLabel.setFont(standardFont);
		tempLabel.setForeground(WHITE);
		panelForm.add(tempLabel, gbcForm);
		
		gbcForm.gridx = COL_XPOS_2;
		JTextField tempField = new JTextField();
		tempField.setFont(standardFont);
		tempField.setBackground(WHITE);
		tempField.setForeground(BG_COLOR);
		tempField.setColumns(FIELD_WIDTH);
		tempField.getDocument().addDocumentListener(listener);
		panelForm.add(tempField, gbcForm);
		
		return tempField;
	}

	protected JButton addButton(String buttonString, ActionListener listener){
		
		if (gbcForm.gridx == COL_XPOS_1){
			gbcForm.gridx = COL_XPOS_2;
		}
		else {
			gbcForm.gridx = COL_XPOS_1;
			gbcForm.gridy++;
		}
		JButton tempButton = new JButton(buttonString);
		tempButton.setFont(standardFont);
		tempButton.setBackground(MAIN_COLOR);
		tempButton.setForeground(BG_COLOR);
		tempButton.addActionListener(listener);
		panelForm.add(tempButton, gbcForm);
		
		return tempButton;
	}
	
	protected void showTable(Vector<Vector<String>> rowInput, Vector<String> colInput){
		
		table = new JTable(rowInput, colInput);
		table.setAutoCreateRowSorter(true);
		table.setFillsViewportHeight(true);
		//	MAKE ID-COLUMN WIDER
		table.getColumnModel().getColumn(colInput.size()-1).setPreferredWidth(ID_COLUMN_WIDTH);
		pane = new JScrollPane(table);
		panelTable.removeAll();
		panelTable.add(pane);
		panelTable.validate();
	}

}