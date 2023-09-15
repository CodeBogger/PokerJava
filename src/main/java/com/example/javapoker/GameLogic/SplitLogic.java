package com.example.javapoker.GameLogic;

import com.example.javapoker.CardsLogic.Cards;
import com.example.javapoker.Graphics.OutputSystem;
import com.example.javapoker.PlayerObject.Player;

import java.util.List;

public class SplitLogic {
    public static void split(List<Player> players, int pot) {
        int splitEach = pot/players.size();

        OutputSystem.print("SPLIT!\n");
        for(Cards card : PokerLogic.cardsList) {
            OutputSystem.print(card.rank()+" of "+card.suit());
        }
        for(Player player : players) {
            OutputSystem.print("\nPLAYER: "+player.getName());
            player.addToBet(splitEach);
            player.hand();
            OutputSystem.print("\n");
        }
    }
}
