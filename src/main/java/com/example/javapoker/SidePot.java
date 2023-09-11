package com.example.javapoker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.javapoker.PokerLogic.winner;

public class SidePot {
    static boolean sidePostExist = false;
    static int winnerSidePot = 0;
    static Map<Player, Integer> sidePots = new HashMap<>();

    public static void addToSidePot(Player player) {
        sidePots.computeIfAbsent(player, key -> { return PokerLogic.pot; });
        sidePostExist = true;
    }
    public static void sidePotWinner(List<Player> players) {
        int sidePotCurr = sidePots.get(winner);
        winner.chips += sidePotCurr;
        winnerSidePot = sidePotCurr;
        players.remove(winner);

        winner = WinLogic.winStart(players, PokerLogic.cardsList);
        if(winner == null) {
            SplitLogic.split(players, PokerLogic.pot);
        } else { winner.chips += PokerLogic.pot - sidePotCurr; }
    }
}
