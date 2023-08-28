package com.example.javapoker;

import java.util.Scanner;
import java.util.Random;

public class PlayerBot extends Player {
    static Scanner scan = new Scanner(System.in);
    public PlayerBot(int chipCount, String playerName) {
        super(chipCount, playerName);
    }
    public static TurnLogic.CHOICE botTurn() {
        Random rand = new Random();
        TurnLogic.CHOICE[] choices = TurnLogic.CHOICE.values();
        return choices[rand.nextInt(choices.length)];
    }
    public static TurnLogic.CHOICE preFlopTurn(Player player) {
        TurnLogic.CHOICE choice = null;
        Random rand = new Random();
        TurnLogic.CHOICE[] choices;

        if(player.getBlindType() == BlindType.BIGBLIND) {
            choices = new TurnLogic.CHOICE[] { TurnLogic.CHOICE.CHECK, TurnLogic.CHOICE.RAISE, TurnLogic.CHOICE.FOLD };
            return choices[rand.nextInt(choices.length)];
        }

        choices = TurnLogic.CHOICE.values();
        return choices[rand.nextInt(choices.length)];
    }
    public static TurnLogic.CHOICE callFoldRaise(int raise) {
        Random rand = new Random();
        TurnLogic.CHOICE[] choices = TurnLogic.CHOICE.values();

        TurnLogic.CHOICE randomChoice = choices[rand.nextInt(choices.length)];

        System.out.println();
        return TurnLogic.CHOICE.CALL;
    }
    public static int raiseTo() {
        return 40;
    }

}
