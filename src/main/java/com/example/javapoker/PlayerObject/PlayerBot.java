package com.example.javapoker.PlayerObject;

import com.example.javapoker.GameLogic.TurnLogic;
import com.example.javapoker.GameLogic.SidePot;

import java.util.Random;

public class PlayerBot extends Player {
    static Random rand = new Random();
    public PlayerBot(int chipCount, String playerName) {
        super(chipCount, playerName);
    }
    public static TurnLogic.CHOICE botTurn() {
        TurnLogic.CHOICE[] choices = TurnLogic.CHOICE.values();
        return choices[rand.nextInt(choices.length)];
    }
    public static TurnLogic.CHOICE allInFold(int allInAmount, Player bot) {
        TurnLogic.CHOICE[] choices = {TurnLogic.CHOICE.ALLIN, TurnLogic.CHOICE.FOLD};
        TurnLogic.CHOICE res = choices[rand.nextInt(choices.length)];

        if(res == TurnLogic.CHOICE.ALLIN) SidePot.addToSidePot(bot);
        return res;
    }
    public static TurnLogic.CHOICE preFlopTurn(Player player) {
        TurnLogic.CHOICE[] choices;

        if (player.getBlindType() == BlindType.BIGBLIND) {
            choices = new TurnLogic.CHOICE[]{TurnLogic.CHOICE.CHECK, TurnLogic.CHOICE.RAISE, TurnLogic.CHOICE.FOLD, TurnLogic.CHOICE.ALLIN};
            return choices[rand.nextInt(choices.length)];
        }

        choices = new TurnLogic.CHOICE[]{TurnLogic.CHOICE.CALL, TurnLogic.CHOICE.RAISE, TurnLogic.CHOICE.FOLD, TurnLogic.CHOICE.ALLIN};
        return choices[rand.nextInt(choices.length)];
    }
    public static TurnLogic.CHOICE callFoldRaise(int raise, Player player) {
        if (player.chips - raise <= 0) {
            return allInFold(raise, player);
        }
        TurnLogic.CHOICE[] choices = { TurnLogic.CHOICE.CALL, TurnLogic.CHOICE.RAISE, TurnLogic.CHOICE.FOLD, TurnLogic.CHOICE.ALLIN };
        return choices[rand.nextInt(choices.length)];
    }
    public static int raiseTo() {
        return 80;
    }

}