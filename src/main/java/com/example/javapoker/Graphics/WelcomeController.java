package com.example.javapoker.Graphics;

public class WelcomeController {

    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void startGame() {
        try {
            mainApp.showGameScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}