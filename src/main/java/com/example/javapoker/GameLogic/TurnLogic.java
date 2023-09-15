package com.example.javapoker.GameLogic;

import com.example.javapoker.Graphics.OutputSystem;
import com.example.javapoker.PlayerObject.Player;
import com.example.javapoker.PlayerObject.PlayerBot;
import com.example.javapoker.PlayerObject.PlayerUser;

import java.util.*;
public class TurnLogic {

    public enum CHOICE {
        CHECK,
        RAISE,
        FOLD,
        CALL,
        ALLIN,
    }
    public static void preFlopChoices(List<Player> players, Scanner scan, int startIndex) {
        if(players.size() == 1) return;

        for (int i = 0; i < players.size(); i++) {
            int index = (startIndex + i) % players.size();

            Player player = players.get(index);
            OutputSystem.print("CURRENT PLAYER: "+player.getName());
            TurnLogic.CHOICE choice;

            choice = player instanceof PlayerUser ? PlayerUser.preFlopTurn(scan, player) : PlayerBot.preFlopTurn(player);

            if (choice == CHOICE.RAISE) {
                int raise = player instanceof PlayerUser ? PlayerUser.raiseTo(scan, player) : PlayerBot.raiseTo();
                OutputSystem.print("\n\n" + player.getName() + " DECIDES TO RAISE. AMOUNT TO RAISE: " + raise);

                player.addToBet(raise);
                raiseAround(player, players, raise, scan);
                break;

            } else if (choice == CHOICE.FOLD) {
                player.fold(player);
                OutputSystem.print("PLAYER COUNT: "+players.size());

            } else if (choice == CHOICE.CALL) {
                int callAmount = player.getBlindType() == Player.BlindType.SMALLBLIND ? 15 : 30;
                player.call(0);

                player.addToBet(callAmount);
                OutputSystem.print(player.getName() + " decides to call. " + callAmount + " chips have been deducted from their amount.");
            } else if (choice == CHOICE.ALLIN) {
                int allInCall = player.getChips();
                player.AllIn();
                raiseAround(player, players, allInCall, scan);
                break;
            }
            if(players.size() == 1) break;
        }

        for(Player player : players) { player.outOfPreFlop(); }
    }

    public static void turns(List<Player> players, Scanner scan) {
        if(players.size() == 1) return;

        for (int i = 0; i < players.size(); i++) {
            Player current = players.get(i);

            if(current.isAllIn()) continue;
            boolean isUser = current instanceof PlayerUser;
            CHOICE choice = isUser ? PlayerUser.playerTurn(scan) : PlayerBot.botTurn();

            switch (choice) {
                case RAISE -> {
                    int raise = isUser ? PlayerUser.raiseTo(scan, current) : PlayerBot.raiseTo();
                    PokerLogic.pot += raise;
                    current.addToBet(raise);

                    OutputSystem.print("\n\n" + current.getName() + " DECIDES TO RAISE. AMOUNT TO RAISE: " + raise);
                    raiseAround(current, players, raise, scan);
                    return;
                }
                case CHECK -> OutputSystem.print("\n\n" + current.getName() + " DECIDES TO CHECK");
                case FOLD -> {
                    i--;
                    current.fold(current);
                    OutputSystem.print("Remaining players:");
                    OutputSystem.print("COUNT: "+players.size());
                    for (Player player : players) {
                        OutputSystem.print(player.getName());
                    }
                }
                case ALLIN -> {
                    int allInAmount = current.getChips();
                    current.AllIn();
                    raiseAround(current, players, allInAmount, scan);
                    return;
                }
            }
            if(players.size() == 1) break;
        }
    }
    public static void raiseAround(Player current, List<Player> players, int initialRaise, Scanner scan) {
        if(players.size() == 1) return;
        int totalRaise = initialRaise, numPlayers = players.size(), startIndex = players.indexOf(current);

        for (int i = 0; i < numPlayers; i++) {
            int index = (startIndex + 1 + i) % players.size();

            Player currentPlayer = players.get(index);
            boolean isUser = currentPlayer instanceof PlayerUser;

            if(currentPlayer.isAllIn() || currentPlayer == current) continue;
            TurnLogic.CHOICE playerAction = isUser ? PlayerUser.callFoldRaise(totalRaise, scan, currentPlayer) : PlayerBot.callFoldRaise(totalRaise, currentPlayer);

            switch (playerAction) {
                case CALL -> {
                    currentPlayer.addToBet(totalRaise);
                    currentPlayer.call(totalRaise);
                    OutputSystem.print(currentPlayer.getName()+" decides to call "+totalRaise);
                }
                case ALLIN -> {
                    int allInAmount = currentPlayer.getChips();
                    currentPlayer.AllIn();
                    raiseAround(currentPlayer, players, allInAmount, scan);
                }
                case FOLD -> {
                    players.remove(currentPlayer);
                    currentPlayer.fold(currentPlayer);
                }
                case RAISE -> {
                    int newRaise = isUser ? PlayerUser.raiseTo(scan, currentPlayer) : PlayerBot.raiseTo();
                    totalRaise += newRaise;

                    if(currentPlayer.getBlindCallAmount() != 0) {
                        currentPlayer.call(totalRaise + (30 - currentPlayer.getBlindCallAmount()));
                    }
                    currentPlayer.addToBet(newRaise);
                    OutputSystem.print(currentPlayer.getName() + " decides to re-raise. New raise amount: " + totalRaise);
                    raiseAround(currentPlayer, players, totalRaise, scan);
                    return;
                }
            }
            if(players.size() == 1) break;
        }
    }

}
