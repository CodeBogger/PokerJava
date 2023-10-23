package com.example.javapoker.Graphics;

import com.example.javapoker.GameLogic.PokerLogic;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        showWelcomeScene();
    }

    public void showWelcomeScene() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/welcomeScene.fxml"));
        Parent root = loader.load();
        WelcomeController controller = loader.getController();
        controller.setMainApp(this);

        primaryStage.setTitle("Welcome");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public void showGameScene() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameScene.fxml"));
        Parent root = loader.load();

        PokerLogic.gameStart();
        primaryStage.setTitle("Game");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}