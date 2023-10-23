package com.example.javapoker.GameLogic;

import com.example.javapoker.CardsLogic.Cards;
import com.example.javapoker.PlayerObject.Player;

import java.util.List;

public class SplitLogic {
    public static void split(List<Player> players, int pot) {
        int splitEach = pot/players.size();

        // ScreenText.printToScreen("SPLIT!\n");
        for(Cards card : PokerLogic.cardsList) {
            // ScreenText.printToScreen(card.rank()+" of "+card.suit());
        }
        for(Player player : players) {
            player.addToBet(splitEach);
        }
    }
}
