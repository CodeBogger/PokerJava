package com.example.javapoker.PlayerObject;

import com.example.javapoker.GameLogic.TurnLogic;

import java.util.Random;

public class PlayerUser extends Player {

    public PlayerUser(int chipCount, String playerName) {
        super(chipCount, playerName);
    }

    public static void checkFoldRaiseAllIn() {
        // ScreenText.printToScreen("Enter your choice ('CHECK', 'FOLD', 'RAISE', 'ALLIN'): ");
    }

    public static TurnLogic.CHOICE preFlopTurn(Player player) {
        // Return a random choice instead of asking the user
        return TurnLogic.CHOICE.values()[new Random().nextInt(TurnLogic.CHOICE.values().length)];
    }

    public static TurnLogic.CHOICE allInFold(Player user) {
        // Return a random choice instead of asking the user
        return TurnLogic.CHOICE.values()[new Random().nextInt(2)]; // Only FOLD or ALLIN
    }

    public static TurnLogic.CHOICE playerTurn() {
        // Return a random choice instead of asking the user
        return TurnLogic.CHOICE.values()[new Random().nextInt(TurnLogic.CHOICE.values().length)];
    }

    public static TurnLogic.CHOICE callFoldRaise(int amountToCall, Player player) {
        // Return a random choice instead of asking the user
        return TurnLogic.CHOICE.values()[new Random().nextInt(TurnLogic.CHOICE.values().length)];
    }

    public static int raiseTo(Player player) {
        // Return a random raise amount instead of asking the user
        return player.chips+1;
    }

    public static String userInp() {
        String[] choices = {"CHECK", "FOLD", "RAISE", "ALLIN", "CALL"};
        return choices[new Random().nextInt(choices.length)];
    }
}