package controller;

import display.DisplayAdapter;
import display.DisplayManager;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import network.NetworkManager;
import protocol.ConfigManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class MainViewController {
	@FXML private TextField port_field;
	@FXML private Text port_feedback;

	@FXML private TextField config_port_field;
	@FXML private TextField name_field;
	@FXML private ListView<String> aliasList;
	@FXML private TextField exe_path;
	private static Dialog dialog = null;

	public void initialize() {

		DisplayManager.addDisplayAdapter(new DisplayAdapter() {

			@Override
			public void portFeedback(String feedback) {
				port_feedback.setText(feedback);
				port_feedback.setVisible(true);
			}

			@Override
			public void mediaPlayback(File file) {

			}

		});
	}
	
	@FXML
	public void start() {
		try {
			NetworkManager.initNetworkListener(Integer.parseInt(port_field.getText()));
		} catch (NumberFormatException nfe) {
			port_feedback.setText(nfe.getMessage());
			port_feedback.setVisible(true);
		}
	}
	
	@FXML
	public void autoStart() {
		NetworkManager.initNetworkListener();
	}


	//MenuBar handlers
	@FXML
	public void reset() {
        NetworkManager.closeNetwork();
        port_feedback.setVisible(false);
	}

	@FXML
	public void close() {
        Platform.exit();
	}

	@FXML
	public void config() {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("../view/ConfigView.fxml"));
			GridPane configpane = (GridPane) root.lookup("#configpane");
			//config_port_field.setText(String.valueOf(ConfigManager.getDefaultPort()));
			dialog = new Dialog();
			dialog.setTitle("Configuration");
			dialog.getDialogPane().setContent(configpane);
            Window window = dialog.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					dialog.setResult("Close");
					dialog.close();
				}
			});
			dialog.show();
			HashMap<String, String> map = ConfigManager.getExecutablePaths();
			Set<String> ks = map.keySet();
			ListView<String> alist = (ListView) window.getScene().lookup("#aliasList");
			ObservableList<String> aliaslist = alist.getItems();
			for (String key : ks) {
				aliaslist.add(key + ": " + map.get(key));
			}
			alist.setItems(aliaslist);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	public void okPressed() {
		//TODO Save configurations
		try {
			ConfigManager.setDefaultPort(Integer.parseInt(config_port_field.getText()));
		} catch (NumberFormatException nfe) {
			System.err.println(nfe.getMessage());
		}
        dialog.setResult("OK");
        dialog.close();
	}

	@FXML
	public void cancelPressed() {
        dialog.setResult("Cancel");
		dialog.close();
	}

	@FXML
	public void about() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("NetDiction");
        alert.setContentText("The computer side component of NetDiction.\n\n" +
				"The client will wait for incoming connections from the NetDiction app " +
				"and responds to the commands received. ");
        alert.show();
	}

	@FXML
	public void browse(ActionEvent ae) {
		Node node = (Node) ae.getSource();
		File file = new FileChooser().showOpenDialog(node.getScene().getWindow());
		exe_path.setText(file.getAbsolutePath());
	}
	@FXML
	public void addAlias() {
		ObservableList<String> aliases = aliasList.getItems();
		String name = name_field.getText();
		String path = exe_path.getText();
		aliases.add(name + ": " + path);
		ConfigManager.addExecutablePath(name, path);
	}
	@FXML
	public void removeAlias() {
		String temp = aliasList.getSelectionModel().getSelectedItem();
		String[] sel = temp.split(":");
		ConfigManager.removeExecutablePath(sel[0]);
		ObservableList<String> aliases = aliasList.getItems();
		for (int i=0; i<aliases.size(); i++) {
			if (aliases.get(i).equals(temp))
				aliases.remove(i);
		}
		aliasList.setItems(aliases);
	}
}
