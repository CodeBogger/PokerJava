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
        List<Player> remainingPlayers = new ArrayList<>(players);
        int numPlayers = remainingPlayers.size();

        List<Player> foldedPlayers = new ArrayList<>();  // Temporary list for folded players

        for (int i = 0; i < numPlayers; i++) {

            int index = (startIndex + i) % numPlayers;
            if (remainingPlayers.isEmpty()) break;

            Player player = remainingPlayers.get(index);

            if (player.folded) continue;

            TurnLogic.CHOICE choice;

            if (player instanceof PlayerUser) {
                choice = PlayerUser.preFlopTurn(scan, player);
            } else {
                choice = PlayerBot.preFlopTurn(player);
            }

            if (choice == TurnLogic.CHOICE.RAISE) {
                int raise = player instanceof PlayerUser ? PlayerUser.raiseTo(scan) : PlayerBot.raiseTo();
                System.out.println("\n\n" + player.getName() + " DECIDES TO RAISE. AMOUNT TO RAISE: " + raise);
                raiseAround(player, remainingPlayers, raise, scan);
            } else if (choice == TurnLogic.CHOICE.FOLD) {
                System.out.println("\n\n" + player.getName() + " DECIDES TO FOLD");
                foldedPlayers.add(player);  // Add folded players to temporary list
                player.fold();
            } else if (choice == TurnLogic.CHOICE.CALL) {
                int callAmount = player.blindType == Player.BlindType.SMALLBLIND ? 15 : 30;
                System.out.println(player.getName() + " decides to call. " + callAmount + " chips have been deducted from their amount.");
                player.chips -= callAmount;
            }
        }

        players.removeAll(foldedPlayers);

    }

    public static void turns(List<Player> players, Scanner scan) {
        List<Player> remainingPlayers = new ArrayList<>(players);

        for (int i = 0; i < remainingPlayers.size(); i++) {
            Player player = remainingPlayers.get(i);

            if (player.folded) {
                continue;
            }

            boolean isUser = player instanceof PlayerUser;
            TurnLogic.CHOICE choice;

            if (isUser) {
                choice = PlayerUser.playerTurn(scan);
            } else {
                choice = PlayerBot.botTurn();
            }

            switch (choice) {
                case RAISE -> {
                    int raise = isUser ? PlayerUser.raiseTo(scan) : PlayerBot.raiseTo();
                    System.out.println("\n\n" + player.getName() + " DECIDES TO RAISE. AMOUNT TO RAISE: " + raise);
                    raiseAround(player, remainingPlayers, raise, scan);

                }
                case CHECK -> System.out.println("\n\n" + player.getName() + " DECIDES TO CHECK");
                case FOLD -> {

                    System.out.println("\n\n" + player.getName() + " DECIDES TO FOLD");
                    remainingPlayers.remove(player);
                    i--;

                    System.out.println("Remaining players:");
                    for (Player remainingPlayer : remainingPlayers) {
                        System.out.println(remainingPlayer.getName());
                    }
                }
            }
        }
    }
    public static void raiseAround(Player current, List<Player> players, int initialRaise, Scanner scan) {
        List<Player> playersCopy = new ArrayList<>(players);
        int currentIndex = playersCopy.indexOf(current);
        int numPlayers = playersCopy.size();

        int totalRaise = initialRaise;

        List<Player> foldedPlayers = new ArrayList<>();  // Temporary list for folded players

        for (int i = currentIndex + 1; i < currentIndex + numPlayers; i++) {
            int index = i % numPlayers;

            if(foldedPlayers.size() == numPlayers-1) break;
            Player currentPlayer = playersCopy.get(index);

            if (currentPlayer != current) {
                TurnLogic.CHOICE playerAction;

                if(currentPlayer.folded) continue;

                if (currentPlayer instanceof PlayerUser) {
                    playerAction = PlayerUser.callFoldRaise(totalRaise, scan);
                } else {
                    playerAction = PlayerBot.callFoldRaise(totalRaise);
                }

                switch (playerAction) {
                    case CALL -> currentPlayer.chips -= totalRaise;
                    case FOLD -> {
                        foldedPlayers.add(currentPlayer);  // Add folded players to temporary list
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
                        System.out.println(currentPlayer.getName() + " decides to re-raise. New raise amount: " + totalRaise);
                        raiseAround(currentPlayer, players, totalRaise, scan);
                    }
                }
            }
        }
        players.removeAll(foldedPlayers);
    }
}
