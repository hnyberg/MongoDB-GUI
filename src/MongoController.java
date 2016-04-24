
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class MongoController implements ActionListener, DocumentListener{
	
	//	TEXT CONSTANTS
	protected final String STRING_COLLECTION = "students";
	protected final String STRING_DATABASE = "mongo_project";
	
	protected final String STRING_KEY_AGE = "age";
	protected final String STRING_KEY_CLASS = "class";
	protected final String STRING_KEY_FIRST_NAME = "first_name";
	protected final String STRING_KEY_ID = "_id";
	protected final String STRING_KEY_LAST_NAME = "last_name";
	protected final String STRING_KEY_SPC = "students in class";
	
	protected final String STRING_LABEL_AGE = "Age:";
	protected final String STRING_LABEL_CLASS = "Class:";
	protected final String STRING_LABEL_FIRST_NAME = "First name:";
	protected final String STRING_LABEL_LAST_NAME = "Last name:";
	protected final String STRING_LABEL_SEARCH = "Search:";
	protected final String STRING_LABEL_ID = "ID:";
	
	protected final String STRING_BUTTON_ADD = "ADD";
	protected final String STRING_BUTTON_AGE_PER_CLASS = "AGE/CLASS";
	protected final String STRING_BUTTON_GET_DATA = "GET DATA";
	protected final String STRING_BUTTON_REFRESH = "REFRESH";
	protected final String STRING_BUTTON_REMOVE = "REMOVE";
	protected final String STRING_BUTTON_SEARCH = "SEARCH";
	protected final String STRING_BUTTON_STUDENTS_PER_CLASS = "STDS/CLASS";
	protected final String STRING_BUTTON_UPDATE = "UPDATE";
	
	private JTextField fieldFirstName;
	private JTextField fieldLastName;
	private JTextField fieldClass;
	private JTextField fieldAge;
	private JTextField fieldID;
	private JTextField fieldSearch;
	
	private JButton buttonAdd;
	private JButton buttonRemove;
	private JButton buttonUpdate;
	private JButton buttonSearch;
	private JButton buttonGetData;
	private JButton buttonRefresh;
	private JButton buttonStudentsPerClass;
	private JButton buttonAgePerClass;
	
	protected MongoModel model;
	protected MongoView view;
	
	public MongoController(MongoModel model, MongoView view){
		
		this.model = model;
		this.view = view;
		
		//	CREATE FIELDS
		fieldFirstName = view.addField(STRING_LABEL_FIRST_NAME, this);
		fieldLastName = view.addField(STRING_LABEL_LAST_NAME, this);
		fieldClass = view.addField(STRING_LABEL_CLASS, this);
		fieldAge = view.addField(STRING_LABEL_AGE, this);
		fieldID = view.addField(STRING_LABEL_ID, this);
		fieldSearch = view.addField(STRING_LABEL_SEARCH, this);
		
		//	CREATE BUTTONS
		buttonAdd = view.addButton(STRING_BUTTON_ADD, this);
		buttonRemove = view.addButton(STRING_BUTTON_REMOVE, this);
		buttonUpdate = view.addButton(STRING_BUTTON_UPDATE, this);
		buttonGetData = view.addButton(STRING_BUTTON_GET_DATA, this);
		buttonSearch = view.addButton(STRING_BUTTON_SEARCH, this);
		buttonRefresh = view.addButton(STRING_BUTTON_REFRESH, this);
		buttonAgePerClass = view.addButton(STRING_BUTTON_AGE_PER_CLASS, this);
		buttonStudentsPerClass = view.addButton(STRING_BUTTON_STUDENTS_PER_CLASS, this);
		
		//	START FIELD CHECKER
		checkFields();
		
		//	SHOW CURRENT DATABASE
		model.getCurrentDB();
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

	//	TEXT FIELD LISTENERS
	public void insertUpdate(DocumentEvent e) {checkFields();}
	public void removeUpdate(DocumentEvent e) {checkFields();}
	public void changedUpdate(DocumentEvent e) {checkFields();}

	//	BUTTON LISTENER
	public void actionPerformed(ActionEvent e) {
		
		String command = ((JButton)(e.getSource())).getText();
		switch (command)
		{
		case (String)STRING_BUTTON_ADD:
			model.addStudent(
					fieldFirstName.getText(),
					fieldLastName.getText(),
					fieldClass.getText(),
					Float.parseFloat(fieldAge.getText()));
			break;
		case (String)STRING_BUTTON_UPDATE:
			model.updateStudent(
					fieldID.getText(),
					fieldFirstName.getText(),
					fieldLastName.getText(),
					fieldClass.getText(),
					Float.parseFloat(fieldAge.getText()));
			break;
		case (String)STRING_BUTTON_REMOVE:
			model.removeStudent(fieldID.getText());
			break;
		case (String)STRING_BUTTON_GET_DATA:
			model.getDataFromID(
					fieldID.getText(),
					fieldFirstName,
					fieldLastName,
					fieldClass,
					fieldAge);
			break;
		case (String)STRING_BUTTON_SEARCH:
			model.search(fieldSearch.getText());
			break;
		case (String)STRING_BUTTON_STUDENTS_PER_CLASS:
			model.getStudentsPerClass();
			break;
		case (String)STRING_BUTTON_AGE_PER_CLASS:
			model.getAgePerClass();
			break;
		case (String)STRING_BUTTON_REFRESH:
			model.getCurrentDB();
			break;
		default:
			break;
		}
	}
}
