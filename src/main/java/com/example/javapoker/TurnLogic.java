package com.example.javapoker;

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
            System.out.println("CURRENT PLAYER: "+player.getName());
            TurnLogic.CHOICE choice;

            choice = player instanceof PlayerUser ? PlayerUser.preFlopTurn(scan, player) : PlayerBot.preFlopTurn(player);

            if (choice == CHOICE.RAISE) {
                int raise = player instanceof PlayerUser ? PlayerUser.raiseTo(scan) : PlayerBot.raiseTo();
                System.out.println("\n\n" + player.getName() + " DECIDES TO RAISE. AMOUNT TO RAISE: " + raise);
                raiseAround(player, players, raise, scan);
                break;

            } else if (choice == CHOICE.FOLD) {
                player.fold(player);
                System.out.println("PLAYER COUNT: "+players.size());

            } else if (choice == CHOICE.CALL) {
                player.call(0);
                int callAmount = player.blindType == Player.BlindType.SMALLBLIND ? 15 : 30;

                System.out.println(player.getName() + " decides to call. " + callAmount + " chips have been deducted from their amount.");
            } else if (choice == CHOICE.ALLIN) {
                player.AllIn();
                int allInCall = player.chips;

                System.out.println("\n"+player.getName()+" decides to go all in! Amount to call: "+allInCall);
                raiseAround(player, players, allInCall, scan);
                break;
            }
            if(players.size() == 1) break;
        }

        for(Player player : players) { player.blindCallAmount = 0; player.inPreFlop = false; }
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
                    int raise = isUser ? PlayerUser.raiseTo(scan) : PlayerBot.raiseTo();
                    PokerLogic.pot += raise;

                    System.out.println("\n\n" + current.getName() + " DECIDES TO RAISE. AMOUNT TO RAISE: " + raise);
                    raiseAround(current, players, raise, scan);
                    return;
                }
                case CHECK -> System.out.println("\n\n" + current.getName() + " DECIDES TO CHECK");
                case FOLD -> {

                    System.out.println("\n\n" + current.getName() + " DECIDES TO FOLD");
                    players.remove(current);
                    i--;

                    System.out.println("Remaining players:");
                    System.out.println("COUNT: "+players.size());
                    for (Player player : players) {
                        System.out.println(player.getName());
                    }
                }
                case ALLIN -> {
                    int allInAmount = current.chips;
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
                    currentPlayer.call(totalRaise);
                    System.out.println(currentPlayer.getName()+" decides to call "+totalRaise);
                }
                case ALLIN -> {
                    int allInAmount = currentPlayer.chips;
                    currentPlayer.AllIn();
                    raiseAround(currentPlayer, players, allInAmount, scan);
                }
                case FOLD -> {
                    System.out.println(currentPlayer.getName()+" decides to fold!");
                    players.remove(currentPlayer);
                    currentPlayer.fold(currentPlayer);
                }
                case RAISE -> {
                    int newRaise = isUser ? PlayerUser.raiseTo(scan) : PlayerBot.raiseTo();
                    totalRaise += newRaise;

                    if(currentPlayer.blindCallAmount != 0) {
                        currentPlayer.chips -= totalRaise + (30 - currentPlayer.blindCallAmount);
                    }
                    System.out.println(currentPlayer.getName() + " decides to re-raise. New raise amount: " + totalRaise);
                    raiseAround(currentPlayer, players, totalRaise, scan);
                    return;
                }
            }
            if(players.size() == 1) break;
        }
    }

}
