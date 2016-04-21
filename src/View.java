import java.util.ArrayList;
import java.util.Vector;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.bson.Document;
import org.bson.types.ObjectId;

import static java.util.Arrays.asList;

public class View extends JFrame implements ActionListener, DocumentListener{
	
	//	DIMENSION & COORDINATE CONTSTANTS
	private final int BIG_FONT_SIZE = 18;
	private final int TITLE_FONT_SIZE = 30;
	private final int WINDOW_WIDTH = 1000;
	private final int WINDOW_HEIGHT = 600;
	private final int FIELD_WIDTH = 16;
	private final int ID_COLUMN_WIDTH = 200;
	
	//	TEXT CONSTANTS
	private final String STRING_TITLE = "J E N S E N   S T U D E N T S";
	protected final String STRING_DATABASE = "mongo_project";
	protected final String STRING_COLLECTION = "students";
	protected final String STRING_KEY_ID = "_id";
	protected final String STRING_KEY_FIRST_NAME = "first_name";
	protected final String STRING_KEY_LAST_NAME = "last_name";
	protected final String STRING_KEY_CLASS = "class";
	protected final String STRING_KEY_AGE = "age";
	protected final String STRING_KEY_SPC = "students in class";
	
	protected final String STRING_BUTTON_UPDATE = "UPDATE";
	protected final String STRING_BUTTON_ADD = "INSERT";
	protected final String STRING_BUTTON_REMOVE = "REMOVE";
	protected final String STRING_BUTTON_GET_DATA = "GET DATA";
	protected final String STRING_BUTTON_SEARCH = "SEARCH";
	protected final String STRING_BUTTON_STUDENTS_PER_CLASS = "STDS/CLASS";
	protected final String STRING_BUTTON_AGE_PER_CLASS = "AGE/CLASS";
	protected final String STRING_BUTTON_REFRESH = "REFRESH";
	
	//	COLOR CONSTANTS
	private final Color BG_COLOR = new Color(0.1f, 0.1f, 0.1f);
	private final Color MAIN_COLOR = new Color(0.8f, 0.6f, 0.2f);
	private final Color WHITE = new Color(0.9f, 0.9f, 0.8f);
	
	//	FONT CONSTANTS
	private final Font standardFont = new Font(Font.MONOSPACED, Font.BOLD, BIG_FONT_SIZE);
	private final Font titleFont = new Font(Font.MONOSPACED, Font.BOLD, TITLE_FONT_SIZE);
	
	//	DECLARATIONS
	private ArrayList<JButton> buttons;
	private ArrayList<JLabel> labels;
	private ArrayList<JTextField> fields;
	private ArrayList<JComponent> col1;
	private ArrayList<JComponent> col2;
	
	private GridBagConstraints gbcForm;
	private GridBagConstraints gbcMain;
	
	protected JButton buttonAdd;
	protected JButton buttonAgePerClass;
	protected JButton buttonGetData;
	protected JButton buttonRemove;
	protected JButton buttonSearch;
	protected JButton buttonStudentsPerClass;
	protected JButton buttonUpdate;
	protected JButton buttonRefresh;
	
	private JLabel labelClass;
	private JLabel labelFirstName;
	private JLabel labelLastName;
	private JLabel labelAge;
	private JLabel labelTitle;
	private JLabel labelID;
	
	private JPanel panelForm;
	private JPanel panelMain;
	private JPanel panelTable;
	
	private JTable table;
	private JScrollPane pane;
	private Vector<Vector<String>> rowData;
	private Vector<String> colData;
	
	private JTextField fieldClass;
	private JTextField fieldFirstName;
	private JTextField fieldID;
	private JTextField fieldLastName;
	private JTextField fieldSearch;
	private JTextField fieldOutput;
	private JTextField fieldAge;
	
	protected MongoClient mongoClient;
	protected MongoDatabase db;
	
	public static void main(String[] args) {
		new View();
	}
	
	//	CONSTRUCTOR
	public View(){
		initView();
	}
	
	private void initView(){
		
		setTitle("Mongo");
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
        panelTable.setBackground(WHITE);
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
        
        //	CREATE FIELD-LABELS
        labelClass = new JLabel("Class:");
        labelFirstName = new JLabel("First name:");
        labelLastName = new JLabel("Last name:");
        labelID = new JLabel("ID:");
        labelAge = new JLabel("Age:");
        
        labels = new ArrayList<JLabel>();
        labels.add(labelClass);
        labels.add(labelFirstName);
        labels.add(labelLastName);
        labels.add(labelID);
        labels.add(labelAge);
        
        for (int i = 0; i < labels.size(); i++){
        	labels.get(i).setFont(standardFont);
        	labels.get(i).setForeground(WHITE);
        }
        
        //	CREATE FIELDS
        fieldClass = new JTextField();
        fieldFirstName = new JTextField();
        fieldLastName = new JTextField();
        fieldID = new JTextField();
        fieldAge = new JTextField();
        fieldSearch = new JTextField();
        fieldOutput = new JTextField();
        
        fields = new ArrayList<JTextField>();
        fields.add(fieldClass);
        fields.add(fieldFirstName);
        fields.add(fieldLastName);
        fields.add(fieldID);
        fields.add(fieldAge);
        fields.add(fieldSearch);
        fields.add(fieldOutput);
        
        for (int i = 0; i < fields.size(); i++){
        	fields.get(i).setFont(standardFont);
        	fields.get(i).setBackground(WHITE);
        	fields.get(i).setForeground(BG_COLOR);
        	fields.get(i).setColumns(FIELD_WIDTH);
        	fields.get(i).getDocument().addDocumentListener(this);
        }
        
        fieldOutput.setEnabled(false);
        fieldOutput.setColumns(FIELD_WIDTH*2);
        
        //	CREATE BUTTONS
        buttonUpdate = new JButton(STRING_BUTTON_UPDATE);
        buttonAdd = new JButton(STRING_BUTTON_ADD);
        buttonRemove = new JButton(STRING_BUTTON_REMOVE);
        buttonGetData = new JButton(STRING_BUTTON_GET_DATA);
        buttonSearch = new JButton(STRING_BUTTON_SEARCH);
        buttonStudentsPerClass = new JButton(STRING_BUTTON_STUDENTS_PER_CLASS);
        buttonAgePerClass = new JButton(STRING_BUTTON_AGE_PER_CLASS);
        buttonRefresh = new JButton(STRING_BUTTON_REFRESH);
        
        buttons = new ArrayList<JButton>();
        buttons.add(buttonAdd);
        buttons.add(buttonGetData);
        buttons.add(buttonRemove);
        buttons.add(buttonUpdate);
        buttons.add(buttonSearch);
        buttons.add(buttonStudentsPerClass);
        buttons.add(buttonAgePerClass);
        buttons.add(buttonRefresh);
        
        for (int i = 0; i < buttons.size(); i++){
        	buttons.get(i).setFont(standardFont);
        	buttons.get(i).setBackground(MAIN_COLOR);
        	buttons.get(i).setForeground(BG_COLOR);
        	buttons.get(i).addActionListener(this);
        }
        
        //	SET ORDER OF FORM PANEL COMPONENTS	
        col1 = new ArrayList<JComponent>();
        col2 = new ArrayList<JComponent>();
        
        col1.add(labelFirstName);
        col1.add(labelLastName);
        col1.add(labelClass);
        col1.add(labelAge);
        col1.add(labelID);
        col1.add(buttonAdd);
        col1.add(buttonUpdate);
        col1.add(buttonSearch);
        col1.add(buttonStudentsPerClass);
        col1.add(buttonRefresh);
        
        col2.add(fieldFirstName);
        col2.add(fieldLastName);
        col2.add(fieldClass);
        col2.add(fieldAge);
        col2.add(fieldID);
        col2.add(buttonGetData);
        col2.add(buttonRemove);
        col2.add(fieldSearch);
        col2.add(buttonAgePerClass);
        
        //	PLACE FORM PANEL COMPONENTS
        panelForm.setLayout(new GridBagLayout());
        gbcForm = new GridBagConstraints();
        gbcForm.weightx = 1;
        gbcForm.weighty = 0.2;
        gbcForm.gridwidth = 3;
        gbcForm.gridheight = 1;
        
        gbcForm.gridx = 0;
        gbcForm.gridy = 1;
        for (JComponent comp : col1) {
        	panelForm.add(comp, gbcForm);
        	gbcForm.gridy++;
		}
        
        gbcForm.gridx = 3;
        gbcForm.gridy = 1;
        for (JComponent comp : col2) {
        	panelForm.add(comp, gbcForm);
        	gbcForm.gridy++;
		}
        
		//	START GUI
        checkFields();
		setVisible(true);
		
		//	CONNECT
		mongoClient = new MongoClient();
		db = mongoClient.getDatabase(STRING_DATABASE);
		System.out.println("Connected to " + STRING_DATABASE);
		
		showCurrentDB();
		
	}
	
	private void showCurrentDB(){
		
		//	GET ALL DB DOCUMENTS
		FindIterable<Document> results = db.getCollection(STRING_COLLECTION).find();
		
		//	PREPARE TABLE DATA VECTORS
		rowData = new Vector<Vector<String>>();
		colData = new Vector<String>();
		colData.add(STRING_KEY_FIRST_NAME);
		colData.add(STRING_KEY_LAST_NAME);
		colData.add(STRING_KEY_CLASS);
		colData.add(STRING_KEY_AGE);
		colData.add(STRING_KEY_ID);
		
		//	GO THROUGH RESULTS AND ADD TO TABLE DATA VECTOR
		results.forEach(new Block<Document>() {
       	    @Override
       	    public void apply(final Document document) {
   	    		Vector rowVector = new Vector<String>(1,1);
   	    		rowVector.add(document.getString(STRING_KEY_FIRST_NAME));
   	    		rowVector.add(document.getString(STRING_KEY_LAST_NAME));
   	    		rowVector.add(document.getString(STRING_KEY_CLASS));
   	    		rowVector.add(document.get(STRING_KEY_AGE).toString());
   	    		rowVector.add(document.getObjectId(STRING_KEY_ID).toString());
   	    		rowData.add(rowVector);
       	    }
        });
		
		//	SHOW RESULT DATA ON TABLE
		showTable(rowData, colData);
	}
	
	private void showTable(Vector<Vector<String>> rowInput, Vector<String> colInput){
		
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
	
	private void addStudent(){
		try{
			db.getCollection(STRING_COLLECTION).insertOne(
				new Document()
				.append(STRING_KEY_FIRST_NAME, fieldFirstName.getText())
				.append(STRING_KEY_LAST_NAME, fieldLastName.getText())
				.append(STRING_KEY_CLASS, fieldClass.getText())
				.append(STRING_KEY_AGE, Float.parseFloat(fieldAge.getText()))
				);
		}catch(Exception e){
	         System.err.println(e.getClass().getName() + ": " + e.getMessage());
	         }
		
		showCurrentDB();
		
	}
	
	private void removeStudent(){
		
		db.getCollection(STRING_COLLECTION).deleteOne(
				new Document(STRING_KEY_ID, new ObjectId(fieldID.getText()))
				);
		
		showCurrentDB();
	}
	
	private void updateStudent(){
		db.getCollection(STRING_COLLECTION).updateOne(
				new Document(STRING_KEY_ID, new ObjectId(fieldID.getText())),
				new Document("$set", (
						new Document()
						.append(STRING_KEY_FIRST_NAME, fieldFirstName.getText())
						.append(STRING_KEY_LAST_NAME, fieldLastName.getText())
						.append(STRING_KEY_CLASS, fieldClass.getText())
						.append(STRING_KEY_AGE, Float.parseFloat(fieldAge.getText()))
						))
				);
		
		showCurrentDB();
	}

	private void getDataFromID(){
		
		FindIterable<Document> results = db.getCollection(STRING_COLLECTION).find(
				new Document(STRING_KEY_ID, new ObjectId(fieldID.getText()))
				);
		
		System.out.println();
		results.forEach(new Block<Document>() {
       	    @Override
       	    public void apply(final Document document) {
       	    	fieldFirstName.setText(document.getString(STRING_KEY_FIRST_NAME));
       	    	fieldLastName.setText(document.getString(STRING_KEY_LAST_NAME));
       	    	fieldClass.setText(document.getString(STRING_KEY_CLASS));
       	    	fieldAge.setText(Double.toString(document.getDouble(STRING_KEY_AGE)));
       	    }
        });
	}
	
	private void search(){
		
		//	PREPARE SEARCH
		String sWord = "^.*" + fieldSearch.getText() + ".*$";
		String options = Character.toString('i');
		
		//	GET RESULTS FROM DB
		FindIterable<Document> resultsFind = db.getCollection(STRING_COLLECTION).find(
				Filters.or(Filters.regex(STRING_KEY_FIRST_NAME, sWord, options),
						Filters.regex(STRING_KEY_LAST_NAME, sWord, options),
						Filters.regex(STRING_KEY_CLASS, sWord, options),
						Filters.regex(STRING_KEY_AGE, sWord, options)));
		
		//	PREPARE TABLE DATA VECTORS
		rowData = new Vector<Vector<String>>();
		colData = new Vector<String>();
		colData.add(STRING_KEY_FIRST_NAME);
		colData.add(STRING_KEY_LAST_NAME);
		colData.add(STRING_KEY_CLASS);
		colData.add(STRING_KEY_AGE);
		colData.add(STRING_KEY_ID);
		
		//	GO THROUGH RESULTS AND ADD TO TABLE DATA VECTOR
		resultsFind.forEach(new Block<Document>() {
       	    @Override
       	    public void apply(final Document document) {
   	    		Vector rowVector = new Vector<String>(1,1);
   	    		rowVector.add(document.getString(STRING_KEY_FIRST_NAME));
   	    		rowVector.add(document.getString(STRING_KEY_LAST_NAME));
   	    		rowVector.add(document.getString(STRING_KEY_CLASS));
   	    		rowVector.add(document.get(STRING_KEY_AGE).toString());
   	    		rowVector.add(document.getObjectId(STRING_KEY_ID).toString());
   	    		rowData.add(rowVector);
       	    }
        });
		
		showTable(rowData, colData);
	}
	
	private void getStudentsPerClass(){
		
		AggregateIterable<Document> resultsAgg = db.getCollection(STRING_COLLECTION).aggregate(
				asList(new Document("$group", new Document()
									.append("_id", "$class")
									.append("students", new Document("$sum", 1)))
				, new Document("$sort", new Document("students", -1))));
		
		rowData = new Vector<Vector<String>>();
		colData = new Vector<String>();
		colData.add(STRING_KEY_CLASS);
		colData.add(STRING_KEY_SPC);
		
		resultsAgg.forEach(new Block<Document>() {
		    @Override
		    public void apply(final Document document) {
		        Vector rowVector = new Vector<String>(1,1);
   	    		rowVector.add(document.getString("_id"));
   	    		rowVector.add(document.get("students").toString());
   	    		rowData.add(rowVector);
		    }
		});
		
		showTable(rowData, colData);
	}
	
	private void getAgePerClass(){
		
		AggregateIterable<Document> resultsAgg = db.getCollection(STRING_COLLECTION).aggregate(
				asList(new Document("$group", new Document()
									.append("_id", "$class")
									.append("avgAge", new Document("$avg", "$age")))
				, new Document("$sort", new Document("avgAge", -1))));
		
		rowData = new Vector<Vector<String>>();
		colData = new Vector<String>();
		colData.add(STRING_KEY_CLASS);
		colData.add(STRING_KEY_AGE);
		
		resultsAgg.forEach(new Block<Document>() {
		    @Override
		    public void apply(final Document document) {
		        Vector rowVector = new Vector<String>(1,1);
   	    		rowVector.add(document.getString("_id"));
   	    		rowVector.add(document.get("avgAge").toString());
   	    		rowData.add(rowVector);
		    }
		});
		
		showTable(rowData, colData);
	}
	
	private void dropData(){
		try{
			db.getCollection(STRING_COLLECTION).drop();
		}catch(Exception e){
	         System.err.println(e.getClass().getName() + ": " + e.getMessage());
	         }
	}

	private void checkFields(){
		
		buttonAdd.setEnabled(false);
		buttonRemove.setEnabled(false);
		buttonUpdate.setEnabled(false);
		buttonGetData.setEnabled(false);
		buttonSearch.setEnabled(false);
		
		//	AGE OK?
		boolean ageOK = false;
		if (!fieldAge.getText().equals("")){
			try{
				Float.parseFloat(fieldAge.getText());
				ageOK = true;
			}
			catch(NumberFormatException e){
				
			}
		}
		
		//	TEXT FIELDS OK?
		boolean firstNameOK = (!fieldFirstName.getText().equals(""));
		boolean lastNameOK = (!fieldLastName.getText().equals(""));
		boolean classOK = (!fieldClass.getText().equals(""));
		boolean searchOK = (!fieldSearch.getText().equals(""));
		boolean idOK = (!fieldID.getText().equals(""));
		
		//	ENABLE/DISABLE BUTTONS
		if (firstNameOK && lastNameOK && classOK && ageOK)
		{
			buttonAdd.setEnabled(true);
			
			if (idOK){
				buttonUpdate.setEnabled(true);
			}
		}
		if (idOK){
			buttonRemove.setEnabled(true);
			buttonGetData.setEnabled(true);
		}
		if (searchOK){
			buttonSearch.setEnabled(true);
		}
	}
	
	@Override
	public void insertUpdate(DocumentEvent e) {
		checkFields();
	}
	@Override
	public void removeUpdate(DocumentEvent e) {
		checkFields();
	}
	@Override
	public void changedUpdate(DocumentEvent e) {
		checkFields();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = ((JButton)(e.getSource())).getText();
		switch (command)
		{
		case (String)STRING_BUTTON_ADD:
			addStudent();
			break;
		case (String)STRING_BUTTON_UPDATE:
			updateStudent();
			break;
		case (String)STRING_BUTTON_REMOVE:
			removeStudent();
			break;
		case (String)STRING_BUTTON_GET_DATA:
			getDataFromID();
			break;
		case (String)STRING_BUTTON_SEARCH:
			search();
			break;
		case (String)STRING_BUTTON_STUDENTS_PER_CLASS:
			getStudentsPerClass();
			break;
		case (String)STRING_BUTTON_AGE_PER_CLASS:
			getAgePerClass();
			break;
		case (String)STRING_BUTTON_REFRESH:
			showCurrentDB();
			break;
		default:
			break;
		}
	}
}