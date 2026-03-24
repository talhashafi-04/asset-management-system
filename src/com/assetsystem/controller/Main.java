package com.assetsystem.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Path must match your project structure exactly
            // Assuming the file is in src/main/resources/com/assetsystem/view/AssetDisplay.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/assetsystem/controller/Login.fxml"));
            Parent root = loader.load();
            
            primaryStage.setTitle("PEC - Sign in");
            primaryStage.setScene(new Scene(root, 1200, 700));
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Could not load FXML file. Check the path!");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}