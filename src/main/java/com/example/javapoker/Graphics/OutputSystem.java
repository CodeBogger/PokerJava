package com.example.javapoker.Graphics;

import javafx.scene.control.TextArea;

public class OutputSystem {
    private static TextArea outputArea;

    public static void setOutputArea(TextArea area) {
        outputArea = area;
    }

    public static void print(String message) {
        if (outputArea != null) {
            outputArea.appendText(message + "\n");
        } else {
            System.out.println(message);
        }
    }

}