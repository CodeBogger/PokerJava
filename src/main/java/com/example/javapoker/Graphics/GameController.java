package com.example.javapoker.Graphics;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class GameController {

    @FXML
    private static TextArea outputTextArea;

    @FXML
    private TextArea inputTextArea;

    public static void printToOutput(String message) {
        outputTextArea.appendText(message + "\n");
    }

    public String getUserInput() {
        return inputTextArea.getText();
    }
}