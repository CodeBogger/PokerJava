package com.example.javapoker;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Menu.setupGUI(primaryStage);
        primaryStage.show();
    }
}