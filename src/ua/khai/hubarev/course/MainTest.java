package ua.khai.hubarev.course;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ua.khai.hubarev.course.db.Connector;


public class MainTest extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception{
		System.out.println(getClass().getResource("Form.fxml"));
		Parent root =FXMLLoader.load(getClass().getResource("Form.fxml"));
		
		Scene scene =new Scene(root,1354,725);
		primaryStage.setTitle("PONConnection");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) throws IOException {
		launch(args);

	}
}