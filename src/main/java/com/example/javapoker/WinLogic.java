package com.example.javapoker;

import java.util.*;
import java.util.stream.Stream;

public class WinLogic {
    public enum handTypes {
        highCard,
        highestPair,
        highestTwoPair,
        highestThreeOfAKind,
        highestStraight,
        highestFlush,
        highestFullHouse,
        highestFourOfAKind,
        highestStraightFlush,
        royalFlush,
    }
    static String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
    public static String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};
    static Player winner = null;
    static handTypes bestHand = null;
    public static Player winStart(List<Player> remainingPlayers, List<Cards> cards) {
        if (remainingPlayers.size() == 1) return remainingPlayers.get(0);

        getHandValues(remainingPlayers, cards); //get hand values in ascending order to prioritize higher hands with higher value
        //get highest hand
        return winner;
    }

    private static void getHandValues(List<Player> remainingPlayers, List<Cards> cards) {
        highCard(remainingPlayers, cards);
        highestPair(remainingPlayers, cards);
        highestTwoPair(remainingPlayers, cards);
        highestThreeOfAKind(remainingPlayers, cards);
        highestStraight(remainingPlayers, cards);
        highestFlush(remainingPlayers, cards);
        highestFullHouse(remainingPlayers, cards);
        highestFourOfAKind(remainingPlayers, cards);
        highestStraightFlush(remainingPlayers, cards);
        royalFlush(remainingPlayers, cards);
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
                            bestHand = handTypes.royalFlush;
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
        int highestRankCard = -1;

        for (Player player : remainingPlayers) {
            int tempHighFlushCard = -1;

            for (String suit : suits) {
                for (int i = ranks.length - 1; i >= 4; i--) {
                    boolean hasStraightFlush = true;
                    int currentRank = -1;

                    for (int j = i; j > i - 5; j--) {
                        currentRank = i; // initialize the highest card from descending order
                        if (!playerHasCard(player, cards, suit, ranks[j])) {
                            hasStraightFlush = false;
                            break;
                        }
                    }
                    if (hasStraightFlush) {
                        tempHighFlushCard = currentRank; //if the straight flush is successful, assign the current to highest for that user
                        bestHand = handTypes.royalFlush;
                        break;
                    }

                }
            }
            if(tempHighFlushCard > highestRankCard) {
                winner = player; //highest card of straight flush rank will get the assignment for the winner
            }
        }
    }
    private static void highestFourOfAKind(List<Player> remainingPlayers, List<Cards> cards) {
        int highestRank = -1;
        for (Player player : remainingPlayers) {
            int tempRank = -1;
            for (String rank : ranks) {
                if (countRank(player, cards, rank) >= 4) {
                    tempRank = Arrays.asList(ranks).indexOf(rank);
                }
            }
            if(tempRank > highestRank) {
                winner = player;
                highestRank = tempRank;
            }
        }
    }
    private static void highestFullHouse(List<Player> remainingPlayers, List<Cards> cards) {
        int higherThreePair = -1;
        for (Player player : remainingPlayers) {
            int playerRankThreePair = -1; //if full house is existent, then assign the rank index and whoever maxes out the integer is the person with higher full house
            String highestTripleRank = "";
            String highestPairRank = "";

            for (String rank : ranks) {
                if (countRank(player, cards, rank) >= 3) {
                    highestTripleRank = rank;

                } else if (countRank(player, cards, rank) >= 2) {
                    highestPairRank = rank;
                }

                if (!highestTripleRank.isEmpty() && !highestPairRank.isEmpty()) {
                    playerRankThreePair = rank.indexOf(highestTripleRank);
                }
            }
            if(playerRankThreePair > higherThreePair) {
                winner = player;
                higherThreePair = playerRankThreePair; //if full house is non-existent, the statement will not be called since -1 is not > -1
            }
        }
    }

    private static int countRank(Player player, List<Cards> cards, String rank) {
        long countInHand = player.hand.stream().filter(card -> card.rank().equals(rank)).count();
        long countInCommunity = cards.stream().filter(card -> card.rank().equals(rank)).count();
        return (int) (countInHand + countInCommunity);
    }
    private static void highestFlush(List<Player> remainingPlayers, List<Cards> cards) {
        int highRank = -1;

        for (Player player : remainingPlayers) {
            int highestRank = -1;
            for (String suit : suits) {
                int count = 0;
                for (String rank : ranks) {
                    int currRank = -1;
                    if (playerHasCard(player, cards, suit, rank)) {
                        count++;
                        currRank = Math.max(Arrays.asList(ranks).indexOf(rank), currRank);
                        if (count >= 5) {
                            highestRank = currRank;
                            break;
                        }
                    }
                }
            }
            if (highestRank > highRank) {
                winner = player;
                highRank = highestRank;
            }
        }
    }

    private static void highestStraight(List<Player> remainingPlayers, List<Cards> cards) {
        int highestAscendCard = -1;
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

            int getIndex = Arrays.asList(ranks).indexOf(highestRank);
            if(!highestRank.isEmpty()) {
                if(getIndex > highestAscendCard) {
                    highestAscendCard = getIndex;
                    winner = player;
                }
            }
        }
    }
    //Start from here
    //Start from here
    //Start from here
    //Start from here
    //Start from here
    //Start from here
    //Start from here
    //Start from here
    //Start from here
    //Start from here
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
            }
        }
    }
    private static void highestPair(List<Player> remainingPlayers, List<Cards> cards) {
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
    }
}


