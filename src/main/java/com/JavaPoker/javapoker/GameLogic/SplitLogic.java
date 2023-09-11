package com.JavaPoker.javapoker.GameLogic;

import com.JavaPoker.javapoker.CardsLogic.Cards;
import com.JavaPoker.javapoker.PlayerObject.Player;

import java.util.List;

public class SplitLogic {
    public static void split(List<Player> players, int pot) {
        int splitEach = pot/players.size();

        System.out.println("SPLIT!\n");
        for(Cards card : PokerLogic.cardsList) {
            System.out.println(card.rank()+" of "+card.suit());
        }
        for(Player player : players) {
            System.out.println("\nPLAYER: "+player.getName());
            player.addToBet(splitEach);
            player.hand();
            System.out.println("\n");
        }
    }
}
