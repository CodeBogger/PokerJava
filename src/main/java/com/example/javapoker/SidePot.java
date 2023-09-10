package com.example.javapoker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.javapoker.PokerLogic.winner;

public class SidePot {
    static int sidePot = 0;
    static boolean sidePostExist = false;
    static Map<Player, Integer> sidePots = new HashMap<>();

    public static void addToSidePot(Player player, int sidePot) {
        sidePots.computeIfAbsent(player, key -> { return sidePot; });
        sidePostExist = true;
    }
    public static void startSidePot(List<Player> players) {
        winner.chips += sidePots.get(winner);
        players.remove(winner);

        if(sidePots.size() > 1) {
            for (Map.Entry<Player, Integer> entry : sidePots.entrySet()) {
                if (entry.getKey() == winner) continue;
                players.remove(entry.getKey());
            }
        }
        winner = WinLogic.winStart(players, PokerLogic.cardsList);
        if(winner == null) SplitLogic.split(players, PokerLogic.pot);
    }
}
