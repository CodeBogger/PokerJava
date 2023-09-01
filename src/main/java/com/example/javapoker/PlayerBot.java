package com.example.javapoker;

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
    public void allIn() { this.chips = 0; }
    public static TurnLogic.CHOICE allInFold(int allInAmount, Player bot) {
        if(bot.chips >= allInAmount) {
            TurnLogic.CHOICE[] choices = { TurnLogic.CHOICE.CALL, TurnLogic.CHOICE.ALLIN, TurnLogic.CHOICE.FOLD };
            return choices[rand.nextInt(choices.length)];
        }
        TurnLogic.CHOICE[] choices = {TurnLogic.CHOICE.ALLIN, TurnLogic.CHOICE.FOLD};
        return choices[rand.nextInt(choices.length)];
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
        if(player.chips - raise < 0) {
            TurnLogic.CHOICE[] choices = { TurnLogic.CHOICE.ALLIN, TurnLogic.CHOICE.FOLD };
            return choices[rand.nextInt(choices.length)];
        }
        TurnLogic.CHOICE[] choices = { TurnLogic.CHOICE.CALL, TurnLogic.CHOICE.RAISE, TurnLogic.CHOICE.FOLD, TurnLogic.CHOICE.ALLIN };
        return choices[rand.nextInt(choices.length)];
    }
    public static int raiseTo() {
        return 80;
    }

}
