package com.JavaPoker.javapoker.Graphics;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/GUI.fxml"));
            primaryStage.setScene(new Scene(loader.load(), 400, 400));
            primaryStage.setTitle("Poker");
            primaryStage.show();
        }
    }


