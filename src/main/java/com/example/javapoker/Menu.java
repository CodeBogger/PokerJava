package com.example.javapoker;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Menu {
    public static boolean buttonClicked = false;

    public static void setupGUI(Stage primaryStage) {
        primaryStage.setTitle("Poker");

        StackPane root = new StackPane();
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);

        Button button = new Button("Play!");

        button.setStyle("-fx-font-size: 24px; -fx-min-width: 150px; -fx-min-height: 60px;");

        button.setOnAction(event -> pokerScene(primaryStage, root));
        root.getChildren().add(button);
    }

    private static void pokerScene(Stage primaryStage, StackPane root) {
        StackPane nextRoot = new StackPane();
        Scene nextScene = new Scene(nextRoot, 800, 600);

        primaryStage.setScene(nextScene);

        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-font-size: 24px; -fx-min-width: 100px; -fx-min-height: 20px;");
        exitButton.setOnAction(event -> {
            Platform.exit();
            buttonClicked = true;
        });
        nextRoot.getChildren().add(exitButton);

        Thread backgroundThread = new Thread(PokerLogic::gameStart);
        backgroundThread.start();
    }
}