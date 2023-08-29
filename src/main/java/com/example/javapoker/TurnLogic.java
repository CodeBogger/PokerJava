package com.example.javapoker;

import java.util.*;


public class TurnLogic {

    public static enum CHOICE {
        CHECK,
        RAISE,
        FOLD,
        CALL,

    }
    public static void preFlopChoices(List<Player> players, Scanner scan, int startIndex) {
        if(players.size() == 1) return;

        for (int i = 0; i < players.size(); i++) {
            int index = (startIndex + i) % players.size();

            Player player = players.get(index);
            TurnLogic.CHOICE choice;

            choice = player instanceof PlayerUser ? PlayerUser.preFlopTurn(scan, player) : PlayerBot.preFlopTurn(player);

            if (choice == TurnLogic.CHOICE.RAISE) {
                int raise = player instanceof PlayerUser ? PlayerUser.raiseTo(scan) : PlayerBot.raiseTo();
                System.out.println("\n\n" + player.getName() + " DECIDES TO RAISE. AMOUNT TO RAISE: " + raise);
                raiseAround(player, players, raise, scan);
                break;

            } else if (choice == TurnLogic.CHOICE.FOLD) {
                System.out.println("\n\n" + player.getName() + " DECIDES TO FOLD");
                players.remove(player);
                player.fold();

                System.out.println("PLAYER COUNT: "+players.size());

            } else if (choice == TurnLogic.CHOICE.CALL) {
                int callAmount = player.blindType == Player.BlindType.SMALLBLIND ? 15 : 30;
                System.out.println(player.getName() + " decides to call. " + callAmount + " chips have been deducted from their amount.");
                player.chips -= callAmount;
                player.blindCallAmount -= callAmount;
            }
            if(players.size() == 1) break;
        }

       for(Player player : players) player.blindCallAmount = 0;
    }

    public static void turns(List<Player> players, Scanner scan) {
        if(players.size() == 1) return;

        for (int i = 0; i < players.size(); i++) {
            Player current = players.get(i);


            boolean isUser = current instanceof PlayerUser;
            TurnLogic.CHOICE choice;

            if (isUser) {
                choice = PlayerUser.playerTurn(scan);
            } else {
                choice = PlayerBot.botTurn();
            }

            switch (choice) {
                case RAISE -> {
                    int raise = isUser ? PlayerUser.raiseTo(scan) : PlayerBot.raiseTo();
                    System.out.println("\n\n" + current.getName() + " DECIDES TO RAISE. AMOUNT TO RAISE: " + raise);
                    raiseAround(current, players, raise, scan);
                    break;
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
            }
            if(players.size() == 1) break;
        }
    }
    public static void raiseAround(Player current, List<Player> players, int initialRaise, Scanner scan) {
        if(players.size() == 1) return;
        int totalRaise = initialRaise, numPlayers = players.size(), startIndex = players.indexOf(current);

        System.out.println("\n\nPLAYERS (IN RAISE)");
        for(Player player : players) {
            System.out.println(player.getName());
        }
        System.out.println("\n");
        for (int i = 0; i < numPlayers - 1; i++) {
            int index = (startIndex + 1 + i) % players.size();
            System.out.println(index+" <- CURRENT INDEX, START INDEX: "+players.indexOf(current));

            Player currentPlayer = players.get(index);

            if (currentPlayer != current) {
                TurnLogic.CHOICE playerAction;

                if (currentPlayer instanceof PlayerUser) {
                    playerAction = PlayerUser.callFoldRaise(totalRaise, scan, currentPlayer);
                } else {
                    playerAction = PlayerBot.callFoldRaise(totalRaise);
                }

                switch (playerAction) {
                    case CALL -> {
                        if(currentPlayer.blindCallAmount != 0) {
                            currentPlayer.chips -= totalRaise + (30 - current.blindCallAmount);
                            System.out.println(currentPlayer.getName()+" DECIDES TO CALL "+totalRaise);
                            continue;
                        }
                        currentPlayer.chips -= totalRaise;
                        System.out.println(currentPlayer.getName()+" DECIDES TO CALL "+totalRaise);
                    }
                    case FOLD -> {
                        players.remove(currentPlayer);
                        currentPlayer.fold();
                    }
                    case RAISE -> {
                        int newRaise;
                        if (currentPlayer instanceof PlayerUser) {
                            newRaise = PlayerUser.raiseTo(scan);
                        } else {
                            newRaise = PlayerBot.raiseTo();
                        }
                        totalRaise += newRaise;
                        if(currentPlayer.blindCallAmount != 0) {
                            currentPlayer.chips -= totalRaise + (30 - currentPlayer.blindCallAmount);
                        }
                        System.out.println(currentPlayer.getName() + " decides to re-raise. New raise amount: " + totalRaise);
                        raiseAround(currentPlayer, players, totalRaise, scan);
                    }
                }
            }
            if(players.size() == 1) break;
        }
    }
}
