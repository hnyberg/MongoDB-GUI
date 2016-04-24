
public class MongoMain {

	public static void main(String[] args) {
		
		MongoView view = new MongoView();
		MongoModel model = new MongoModel(view);
		new MongoController(model, view);
		
		view.setVisible(true);
	}
}