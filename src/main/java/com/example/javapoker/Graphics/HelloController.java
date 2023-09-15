package com.example.javapoker.Graphics;

import com.example.javapoker.GameLogic.PokerLogic;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.event.ActionEvent;

public class HelloController {

    @FXML
    private Button playButton;
    @FXML
    private TextArea outputArea;

    @FXML
    public static TextField inputField;
    private UserInputHandler userInputHandler;
    @FXML
    private void switchScene(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/mainscene.fxml"));
        Parent secondSceneRoot = loader.load();
        Scene secondScene = new Scene(secondSceneRoot);

        PokerLogic.gameStart();
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

        window.setScene(secondScene);
        window.show();
    }
    public void setUserInputHandler(UserInputHandler handler) {
        this.userInputHandler = handler;
    }
    public void goBack(ActionEvent actionEvent) throws IOException {
        // Load the original scene
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/GUI.fxml"));
        Parent originalSceneRoot = loader.load();
        Scene originalScene = new Scene(originalSceneRoot);

        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        window.setScene(originalScene);
        window.show();
    }
    @FXML
    private void handleInput(ActionEvent event) {
        String input = inputField.getText();
        if(input.isEmpty()) return;

        if (userInputHandler != null) {
            userInputHandler.handleUserInput(input);
        }

        outputArea.appendText("User: " + input + "\n");
        inputField.clear();
    }



}