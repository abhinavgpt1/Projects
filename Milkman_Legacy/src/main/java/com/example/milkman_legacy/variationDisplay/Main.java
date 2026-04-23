package com.example.milkman_legacy.variationDisplay;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

import static com.example.milkman_legacy.Constants.VARIATION_DISPLAY_VIEW;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource(VARIATION_DISPLAY_VIEW));
			Scene scene = new Scene(root, 550, 550);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
