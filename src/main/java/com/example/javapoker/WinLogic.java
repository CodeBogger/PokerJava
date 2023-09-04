package com.example.javapoker;

import java.util.*;
import java.util.stream.Stream;

public class WinLogic {
    static String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
    static String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};
    static Player winner = null;
    public static Player winStart(List<Player> remainingPlayers, List<Cards> cards) {
        if (remainingPlayers.size() == 1) return remainingPlayers.get(0);

        highCard(remainingPlayers, cards);
        pair(remainingPlayers, cards);
        highestTwoPair(remainingPlayers, cards);
        highestThreeOfAKind(remainingPlayers, cards);
        highestStraight(remainingPlayers, cards);
        highestFlush(remainingPlayers, cards);
        highestFullHouse(remainingPlayers, cards);
        highestFourOfAKind(remainingPlayers, cards);
        highestStraightFlush(remainingPlayers, cards);
        royalFlush(remainingPlayers, cards);

        return winner;
    }


    private static boolean playerHasCard(Player player, List<Cards> cards, String suit, String rank) {
        return player.hand.stream().anyMatch(card -> card.suit().equals(suit) && card.rank().equals(rank)) ||
                cards.stream().anyMatch(card -> card.suit().equals(suit) && card.rank().equals(rank));
    }
    private static void royalFlush(List<Player> remainingPlayers, List<Cards> cards) {
        for (Player player : remainingPlayers) {
            for (String suit : suits) {
                int count = 0;
                for (String rank : ranks) {
                    if (playerHasCard(player, cards, suit, rank)) {
                        count++;
                        if (count >= 5) {
                            winner = player;
                            System.out.println(player.getName()+" has a royal flush!");
                            return;
                        }
                    } else {
                        count = 0;
                    }
                }
            }
        }
    }
    private static void highestStraightFlush(List<Player> remainingPlayers, List<Cards> cards) {
        for (Player player : remainingPlayers) {
            for (String suit : suits) {
                for (int i = ranks.length - 1; i >= 4; i--) {
                    boolean hasStraightFlush = true;
                    for (int j = i; j > i - 5; j--) {
                        if (!playerHasCard(player, cards, suit, ranks[j])) {
                            hasStraightFlush = false;
                            break;
                        }
                    }
                    if (hasStraightFlush) {
                        winner = player;
                        System.out.println(player.getName()+" has the highest straight flush!");
                        return;
                    }
                }
            }
        }
    }
    private static void highestFourOfAKind(List<Player> remainingPlayers, List<Cards> cards) {
        for (Player player : remainingPlayers) {
            for (String rank : ranks) {
                if (countRank(player, cards, rank) >= 4) {
                    winner = player;
                    System.out.println(player.getName()+" has the highest four of a kind!");

                    return;
                }
            }
        }
    }
    private static void highestFullHouse(List<Player> remainingPlayers, List<Cards> cards) {
        for (Player player : remainingPlayers) {
            String highestTripleRank = "";
            String highestPairRank = "";

            for (String rank : ranks) {
                if (countRank(player, cards, rank) >= 3) {
                    highestTripleRank = rank;
                } else if (countRank(player, cards, rank) >= 2) {
                    highestPairRank = rank;
                }
            }

            if (!highestTripleRank.isEmpty() && !highestPairRank.isEmpty()) {
                winner = player;
                System.out.println(player.getName()+" has the highest full house!");
                return;
            }
        }
    }

    private static int countRank(Player player, List<Cards> cards, String rank) {
        long countInHand = player.hand.stream().filter(card -> card.rank().equals(rank)).count();
        long countInCommunity = cards.stream().filter(card -> card.rank().equals(rank)).count();
        return (int) (countInHand + countInCommunity);
    }
    private static void highestFlush(List<Player> remainingPlayers, List<Cards> cards) {
        for (Player player : remainingPlayers) {
            for (String suit : suits) {
                int count = 0;
                for (String rank : ranks) {
                    if (playerHasCard(player, cards, suit, rank)) {
                        count++;
                        if (count >= 5) {
                            winner = player;
                            System.out.println(player.getName()+" has the highest flush!");
                            return;
                        }
                    }
                }
            }
        }
    }
    private static void highestStraight(List<Player> remainingPlayers, List<Cards> cards) {
        for (Player player : remainingPlayers) {
            String highestRank = "";
            int consecutiveCount = 0;

            for (String rank : ranks) {
                if (playerHasCard(player, cards, "", rank)) {
                    consecutiveCount++;
                    if (consecutiveCount >= 5) {
                        highestRank = rank;
                        break;
                    }
                } else {
                    consecutiveCount = 0;
                }
            }

            if (!highestRank.isEmpty()) {
                winner = player;
                System.out.println(player.getName()+" has the highest straight!");
                return;
            }
        }
    }

    private static void highestThreeOfAKind(List<Player> remainingPlayers, List<Cards> cards) {
        for (Player player : remainingPlayers) {
            String highestRank = "";
            int highestCount = 0;

            for (String rank : ranks) {
                int count = 0;
                for (String suit : suits) {
                    if (playerHasCard(player, cards, suit, rank)) {
                        count++;
                        if (count >= 3) {
                            if (rank.equals("Ace") && highestRank.equals("King")) {
                                highestRank = rank;
                            } else if (count > highestCount) {
                                highestRank = rank;
                                highestCount = count;
                            }
                        }
                    }
                }
            }

            if (!highestRank.isEmpty()) {
                winner = player;
                System.out.println(player.getName()+" has the highest three of a kind!");
                return;
            }
        }
    }
    private static void highestTwoPair(List<Player> remainingPlayers, List<Cards> cards) {
        int highestPairRank1 = -1;
        int highestPairRank2 = -1;

        for (Player player : remainingPlayers) {
            int pairCount = 0;
            int pairRank1 = -1;
            int pairRank2 = -1;

            for (String rank : ranks) {
                int count = 0;
                for (String suit : suits) {
                    if (playerHasCard(player, cards, suit, rank)) {
                        count++;
                        if (count >= 2) {
                            if (pairCount == 0) {
                                pairRank1 = Arrays.asList(ranks).indexOf(rank);
                            } else if (pairCount == 1) {
                                pairRank2 = Arrays.asList(ranks).indexOf(rank);
                            }
                            pairCount++;
                        }
                    }
                }
            }

            if (pairCount >= 2 && (pairRank1 > highestPairRank1 || (pairRank1 == highestPairRank1 && pairRank2 > highestPairRank2))) {
                highestPairRank1 = pairRank1;
                highestPairRank2 = pairRank2;
                winner = player;
                System.out.println(player.getName()+" has the highest two pair!");
            }
        }
    }
    private static void pair(List<Player> remainingPlayers, List<Cards> cards) {
        int highestPairRank = -1;

        for (Player player : remainingPlayers) {
            for (String rank : ranks) {
                int count = 0;
                for (String suit : suits) {
                    if (playerHasCard(player, cards, suit, rank)) {
                        count++;
                        if (count >= 2) {
                            int pairRank = Arrays.asList(ranks).indexOf(rank);
                            if (pairRank > highestPairRank) {
                                highestPairRank = pairRank;
                                winner = player;
                                System.out.println(player.getName()+" has the highest pair!");
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
    private static void highCard(List<Player> remainingPlayers, List<Cards> cards) {
        int highestValue = 0;
        Player highestPlayer = null;

        for (Player player : remainingPlayers) {
            for (String rank : ranks) {
                int rankValue = Arrays.asList(ranks).indexOf(rank) + 2;

                if (playerHasCard(player, cards, suits[0], rank) ||
                        playerHasCard(player, cards, suits[1], rank) ||
                        playerHasCard(player, cards, suits[2], rank) ||
                        playerHasCard(player, cards, suits[3], rank)) {

                    if (rankValue > highestValue) {
                        highestValue = rankValue;
                        highestPlayer = player;
                    }
                }
            }
        }
        winner = highestPlayer;

        assert highestPlayer != null;
        System.out.println(highestPlayer.getName()+" has the highest card!");
    }
}


