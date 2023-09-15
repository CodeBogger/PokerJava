package com.example.javapoker.GameLogic;

import com.example.javapoker.PlayerObject.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SidePot {
    static boolean sidePostExist = false;
    static int winnerSidePot = 0;
    static Map<Player, Integer> sidePots = new HashMap<>();

    public static void addToSidePot(Player player) {
        sidePots.computeIfAbsent(player, key -> { return PokerLogic.pot; });
        sidePostExist = true;
    }
    public static void sidePotWinner(List<Player> players) {
        int sidePotCurr = sidePots.get(PokerLogic.winner);
        PokerLogic.winner.addToChips(sidePotCurr);
        winnerSidePot = sidePotCurr;
        players.remove(PokerLogic.winner);

        PokerLogic.winner = WinLogic.winStart(players, PokerLogic.cardsList);
        if(PokerLogic.winner == null) {
            SplitLogic.split(players, PokerLogic.pot);
        } else { PokerLogic.winner.addToChips(PokerLogic.pot - sidePotCurr); }
    }
}
