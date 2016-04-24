import static java.util.Arrays.asList;

import java.util.Vector;

import javax.swing.JTextField;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class MongoModel {
	
	//	TEXT CONSTANTS
	private final String STRING_DATABASE = "mongo_project";
	private final String STRING_COLLECTION = "students";
	private final String STRING_KEY_ID = "_id";
	private final String STRING_KEY_FIRST_NAME = "first_name";
	private final String STRING_KEY_LAST_NAME = "last_name";
	private final String STRING_KEY_CLASS = "class";
	private final String STRING_KEY_AGE = "age";
	private final String STRING_KEY_SPC = "students in class";

	//	DECLARATIONS
	protected MongoClient mongoClient;
	protected MongoDatabase db;
	
	private Vector<Vector<String>> rowData;
	private Vector<String> colData;
	
	private MongoView view;
	
	public MongoModel(MongoView view){
		
		this.view = view;
		mongoClient = new MongoClient();
		db = mongoClient.getDatabase(STRING_DATABASE);
		System.out.println("Connected to " + STRING_DATABASE);
	}
	
	protected void getCurrentDB(){
		
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
   	    		Vector<String> rowVector = new Vector<String>(1,1);
   	    		rowVector.add(document.getString(STRING_KEY_FIRST_NAME));
   	    		rowVector.add(document.getString(STRING_KEY_LAST_NAME));
   	    		rowVector.add(document.getString(STRING_KEY_CLASS));
   	    		rowVector.add(document.get(STRING_KEY_AGE).toString());
   	    		rowVector.add(document.getObjectId(STRING_KEY_ID).toString());
   	    		rowData.add(rowVector);
       	    }
        });
		
		//	SEND DATA TO VIEW
		view.showTable(rowData, colData);
	}
	
	protected void addStudent(
			String stringFieldFirstName,
			String stringFieldLastName,
			String stringFieldClass,
			float floatFieldAge){
		
		try{
			db.getCollection(STRING_COLLECTION).insertOne(
				new Document()
				.append(STRING_KEY_FIRST_NAME, stringFieldFirstName)
				.append(STRING_KEY_LAST_NAME, stringFieldLastName)
				.append(STRING_KEY_CLASS, stringFieldClass)
				.append(STRING_KEY_AGE, floatFieldAge)
				);
		}catch(Exception e){
	         System.err.println(e.getClass().getName() + ": " + e.getMessage());
	         }
		
		getCurrentDB();
	}
	
	protected void removeStudent(String stringFieldID){
		
		db.getCollection(STRING_COLLECTION).deleteOne(
			new Document(STRING_KEY_ID, new ObjectId(stringFieldID))
			);
		
		getCurrentDB();
	}
	
	protected void updateStudent(
			String stringFieldID, 
			String stringFieldFirstName, 
			String stringFieldLastName,
			String stringFieldClass,
			float floatFieldAge){
		
		db.getCollection(STRING_COLLECTION).updateOne(
				new Document(STRING_KEY_ID, new ObjectId(stringFieldID)),
				new Document("$set", (
						new Document()
						.append(STRING_KEY_FIRST_NAME, stringFieldFirstName)
						.append(STRING_KEY_LAST_NAME, stringFieldLastName)
						.append(STRING_KEY_CLASS, stringFieldClass)
						.append(STRING_KEY_AGE, floatFieldAge)
						))
				);
		
		getCurrentDB();
	}

	protected void getDataFromID(
			String stringFieldID,
			JTextField fieldFirstName,
			JTextField fieldLastName,
			JTextField fieldClass,
			JTextField fieldAge){
		
		FindIterable<Document> results = db.getCollection(STRING_COLLECTION).find(
				new Document(STRING_KEY_ID, new ObjectId(stringFieldID))
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
	
	protected void search(String stringSearch){
		
		//	PREPARE SEARCH
		String sWord = "^.*" + stringSearch + ".*$";
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
   	    		Vector<String> rowVector = new Vector<String>(1,1);
   	    		rowVector.add(document.getString(STRING_KEY_FIRST_NAME));
   	    		rowVector.add(document.getString(STRING_KEY_LAST_NAME));
   	    		rowVector.add(document.getString(STRING_KEY_CLASS));
   	    		rowVector.add(document.get(STRING_KEY_AGE).toString());
   	    		rowVector.add(document.getObjectId(STRING_KEY_ID).toString());
   	    		rowData.add(rowVector);
       	    }
        });
		
		//	SEND DATA TO VIEW
		view.showTable(rowData, colData);
	}

	protected void getStudentsPerClass(){
		
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
		        Vector<String> rowVector = new Vector<String>(1,1);
   	    		rowVector.add(document.getString("_id"));
   	    		rowVector.add(document.get("students").toString());
   	    		rowData.add(rowVector);
		    }
		});
		
		view.showTable(rowData, colData);
	}
	
	protected void getAgePerClass(){
		
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
		        Vector<String> rowVector = new Vector<String>(1,1);
   	    		rowVector.add(document.getString("_id"));
   	    		rowVector.add(document.get("avgAge").toString());
   	    		rowData.add(rowVector);
		    }
		});
		
		view.showTable(rowData, colData);
	}
}
