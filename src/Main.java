
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import network.NetworkManager;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("view/MainView.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			//NetworkManager.initNetworkListener();
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	@Override
	public void stop() {
		NetworkManager.closeNetwork();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
