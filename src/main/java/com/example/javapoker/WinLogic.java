package com.example.javapoker;

import java.net.JarURLConnection;
import java.util.*;
import java.util.stream.Stream;

public class WinLogic {
    public enum handTypes {
        highCard, highestPair, highestTwoPair, highestThreeOfAKind, highestStraight, highestFlush, highestFullHouse, highestFourOfAKind, highestStraightFlush, royalFlush,
    }

    static String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
    public static String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};
    static Player winner = null;
    static handTypes bestHand = null;

    public static Player winStart(List<Player> remainingPlayers, List<Cards> cards) {
        if (remainingPlayers.size() == 1) return remainingPlayers.get(0);

        getHandValues(remainingPlayers, cards); //get hand values in ascending order to prioritize higher hands with higher value
        //get highest hand
        if (bestHand != null) System.out.println("WINNERS HAND: " + bestHand.toString().toUpperCase());
        return winner;
    }

    static List<Player> sameHand = null;

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

    private static int countRank(Player player, List<Cards> cards, String rank) {
        long countInHand = player.hand.stream().filter(card -> card.rank().equals(rank)).count();
        long countInCommunity = cards.stream().filter(card -> card.rank().equals(rank)).count();
        return (int) (countInHand + countInCommunity);
    }
    private static boolean playerHasCard(Player player, List<Cards> cards, String suit, String rank) {
        return player.hand.stream().anyMatch(card -> card.suit().equals(suit) && card.rank().equals(rank)) ||
                cards.stream().anyMatch(card -> card.suit().equals(suit) && card.rank().equals(rank));
    }

    private static boolean hasNumber(Player player, List<Cards> cards, String rank) {
        return player.hand.stream().anyMatch(card -> card.rank().equals(rank)) || cards.stream().anyMatch(card -> card.rank().equals(rank));
    }

    private static boolean rFInDraw(List<Cards> cards) {
        String[] rFCards = new String[]{"10", "Jack", "Queen", "King", "Ace"};
        for (String suit : suits) {
            int count = 0;
            for (String rFCard : rFCards)
                if(cards.stream().anyMatch(card -> card.rank().equals(rFCard) && card.suit().equals(suit))) {
                    count++;
                    if(count == 5) return true;
                }
        }
        return false;
    }

    private static void royalFlush(List<Player> remainingPlayers, List<Cards> cards) {
        if (rFInDraw(cards)) {
            sameHand = remainingPlayers;
            winner = null;
            return;
        }
        for (Player player : remainingPlayers) {
            for (String suit : suits) {
                int count = 0;
                for (String rank : ranks) {
                    if (playerHasCard(player, cards, suit, rank)) {
                        count++;
                        if (count >= 5) {
                            winner = player;
                            bestHand = handTypes.royalFlush;
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
        HashMap<Integer, List<Player>> handValues = new HashMap<>();

        for (Player player : remainingPlayers) {
            int tempHighFlushCard = -2;

            for (String suit : suits) {
                for (int i = ranks.length - 1; i >= 4; i--) {
                    boolean hasStraightFlush = true;
                    int currentRank = 0;

                    for (int j = i; j > i - 5; j--) {
                        currentRank = i; // initialize the highest card from descending order
                        if (!playerHasCard(player, cards, suit, ranks[j])) {
                            hasStraightFlush = false;
                            break;
                        }
                    }
                    if (hasStraightFlush) {
                        tempHighFlushCard = currentRank; //if the straight flush is successful, assign the current to highest for that user
                        break;
                    }

                }
            }
            if (tempHighFlushCard > highestRankCard) {
                handValues.clear();  // clear the map since we have a new highest card
                handValues.computeIfAbsent(tempHighFlushCard, k -> new ArrayList<>()).add(player);
                winner = player;
                bestHand = handTypes.highestStraightFlush;
                highestRankCard = tempHighFlushCard;

            } else if (tempHighFlushCard == highestRankCard) {
                handValues.computeIfAbsent(tempHighFlushCard, k -> new ArrayList<>()).add(player); //if 2 or more players have the same hand, add to same key
            }
        }
        List<Player> playersWithSameHand = handValues.get(highestRankCard);
        if (playersWithSameHand != null && playersWithSameHand.size() > 1) {
            winner = null;
            sameHand = null;
            sameHand = handValues.get(highestRankCard);
        } else if (highestRankCard > -1) sameHand = null;
    }

    private static void highestFourOfAKind(List<Player> remainingPlayers, List<Cards> cards) {
        int highestRank = -1;
        HashMap<Integer, List<Player>> handValues = new HashMap<>();

        for (Player player : remainingPlayers) {
            int tempRank = -2;
            for (String rank : ranks) {
                if (countRank(player, cards, rank) >= 4) {
                    tempRank = Arrays.asList(ranks).indexOf(rank);
                }
            }
            if (tempRank > highestRank) {
                handValues.clear();  // clear the map since we have a new highest card
                handValues.computeIfAbsent(tempRank, k -> new ArrayList<>()).add(player);
                winner = player;
                bestHand = handTypes.highestFourOfAKind;
                highestRank = tempRank;

            } else if (tempRank == highestRank) {
                handValues.computeIfAbsent(highestRank, k -> new ArrayList<>()).add(player); //if 2 or more players have the same hand, add to same key
            }
        }
        List<Player> playersWithSameHand = handValues.get(highestRank);
        if (playersWithSameHand != null && playersWithSameHand.size() > 1) {
            winner = null;
            sameHand = null;
            sameHand = handValues.get(highestRank);
        } else if (highestRank > -1) sameHand = null;
    }

    private static void highestFullHouse(List<Player> remainingPlayers, List<Cards> cards) {
        int higherThreePair = -1;
        HashMap<Integer, List<Player>> handValues = new HashMap<>();

        for (Player player : remainingPlayers) {
            int playerRankThreePair = -1; //if full house is existent, then assign the rank index and whoever maxes out the integer is the person with higher full house
            String highestTripleRank = "";
            String highestPairRank = "";
            boolean hasFullHouse = false;

            for (String rank : ranks) {
                if (countRank(player, cards, rank) >= 2) {
                    highestPairRank = rank;

                } else if (countRank(player, cards, rank) >= 3) {
                    highestTripleRank = rank;
                }

                if (!highestTripleRank.isEmpty() && !highestPairRank.isEmpty()) {
                    playerRankThreePair = rank.indexOf(highestTripleRank);
                    hasFullHouse = true;
                }
            }
            if (playerRankThreePair > higherThreePair) {
                handValues.clear();  // clear the map since we have a new highest card
                handValues.computeIfAbsent(playerRankThreePair, k -> new ArrayList<>()).add(player);
                winner = player;
                bestHand = handTypes.highestFullHouse;
                higherThreePair = playerRankThreePair; //if full house is non-existent, the statement will not be called since -1 is not > -1

            } else if (playerRankThreePair == higherThreePair && hasFullHouse) {
                handValues.computeIfAbsent(playerRankThreePair, k -> new ArrayList<>()).add(player); //if 2 or more players have the same hand, add to same key
            }
        }
        List<Player> playersWithSameHand = handValues.get(higherThreePair);
        if (playersWithSameHand != null && playersWithSameHand.size() > 1) {
            winner = null;
            sameHand = null;
            sameHand = handValues.get(higherThreePair);
        } else if (higherThreePair > -1) sameHand = null;
    }

    private static void highestFlush(List<Player> remainingPlayers, List<Cards> cards) {
        int highestRank = -1;
        HashMap<Integer, List<Player>> handValues = new HashMap<>();

        for (Player player : remainingPlayers) {
            int highFlushCard = -1;
            boolean hasFlush = false;

            for(String suit : suits) {
                int suitCount = 0;

                for(String rank : ranks) {
                    if(playerHasCard(player, cards, suit, rank)) {
                        suitCount++;

                        if(suitCount >= 5) {
                            highFlushCard = Arrays.toString(ranks).indexOf(rank);
                            hasFlush = true;
                        }
                    }
                }
            }
            if(hasFlush && highFlushCard == highestRank) {
                handValues.computeIfAbsent(highFlushCard, k -> new ArrayList<>()).add(player); //if 2 or more players have the same hand, add to same key

            } else if (highFlushCard > highestRank) {
                handValues.clear();  // clear the map since we have a new highest card
                handValues.computeIfAbsent(highFlushCard, k -> new ArrayList<>()).add(player);
                winner = player;
                bestHand = handTypes.highestFlush;
                highestRank = highFlushCard;
            }
        }
        List<Player> playersWithSameHand = handValues.get(highestRank);
        boolean notNull = playersWithSameHand != null;

        if (notNull && playersWithSameHand.size() > 1) {
            winner = null;
            sameHand = null;
            sameHand = handValues.get(highestRank);
        } else if (notNull && playersWithSameHand.size() == 1) sameHand = null;
    }

    private static void highestStraight(List<Player> remainingPlayers, List<Cards> cards) {
        int highestAscendCard = -1;
        HashMap<Integer, List<Player>> handValues = new HashMap<>();

        for (Player player : remainingPlayers) {
            int highestRank = -1;
            boolean hasStraight = false;
            List<Integer> straightEnd = new ArrayList<>();

            // check for A-2-3-4-5 (ace-case)
            if (playerHasCard(player, cards, "", "A") &&
                    playerHasCard(player, cards, "", "2") &&
                    playerHasCard(player, cards, "", "3") &&
                    playerHasCard(player, cards, "", "4") &&
                    playerHasCard(player, cards, "", "5")) {
                straightEnd.add(3);  // 5 is at index 3
            }

            int consecCard = 0;
            for (String rank : ranks) {
                if (hasNumber(player, cards, rank)) {
                    consecCard++;
                    if (consecCard >= 5) {
                        hasStraight = true;
                        straightEnd.add(Arrays.toString(ranks).indexOf(rank));
                    }
                } else {
                    consecCard = 0;
                }
            }
            Collections.sort(straightEnd);
            if (hasStraight) highestRank = straightEnd.get(straightEnd.size() - 1);

            if (hasStraight && highestRank == highestAscendCard) {
                handValues.computeIfAbsent(highestRank, k -> new ArrayList<>()).add(player);

            } else if (highestRank > highestAscendCard) {
                handValues.clear();
                handValues.computeIfAbsent(highestRank, k -> new ArrayList<>()).add(player);
                winner = player;
                bestHand = handTypes.highestStraight;
                highestAscendCard = highestRank;
            }
        }

        List<Player> playersWithSameHand = handValues.get(highestAscendCard);
        boolean notNull = playersWithSameHand != null;

        if (notNull && playersWithSameHand.size() > 1) {
            winner = null;
            sameHand = playersWithSameHand;
        } else if (notNull && playersWithSameHand.size() == 1) sameHand = null;
    }

    private static void highestThreeOfAKind(List<Player> remainingPlayers, List<Cards> cards) {
        int highestIndex = -1;
        HashMap<Integer, List<Player>> handValues = new HashMap<>();

        for (Player player : remainingPlayers) {
            int currHighestRank = -1;
            boolean hasThree = false;

            for (String rank : ranks) {
                if (countRank(player, cards, rank) >= 3) {
                    hasThree = true;
                    currHighestRank = Arrays.toString(ranks).indexOf(rank);
                }
            }
            if (hasThree && highestIndex == currHighestRank) {
                handValues.computeIfAbsent(currHighestRank, k -> new ArrayList<>()).add(player); //if 2 or more players have the same hand, add to same key

            } else if (currHighestRank > highestIndex) {
                handValues.clear();  // clear the map since we have a new highest card
                handValues.computeIfAbsent(currHighestRank, k -> new ArrayList<>()).add(player);
                winner = player;
                bestHand = handTypes.highestThreeOfAKind;
                highestIndex = currHighestRank;
            }
        }

        List<Player> playersWithSameHand = handValues.get(highestIndex);
        boolean notNull = playersWithSameHand != null;

        if (notNull && playersWithSameHand.size() > 1) {
            winner = null;
            sameHand = null;
            sameHand = handValues.get(highestIndex);
        } else if (notNull && playersWithSameHand.size() == 1) sameHand = null;
    }

    private static void highestTwoPair(List<Player> remainingPlayers, List<Cards> cards) {
        int highestPairRank1 = -1;
        HashMap<Integer, List<Player>> handValues = new HashMap<>();

        for (Player player : remainingPlayers) {
            List<Integer> pairs = new ArrayList<>();
            boolean hasTwoPair = false;
            int highPairRank = -1;

            for (String rank : ranks) {
                if (countRank(player, cards, rank) == 2) pairs.add(Arrays.toString(ranks).indexOf(rank));

                if (pairs.size() >= 2) {
                    Collections.sort(pairs);
                    highPairRank = pairs.get(pairs.size() - 1);
                    hasTwoPair = true;
                }

                if (hasTwoPair && highPairRank == highestPairRank1) {
                    handValues.computeIfAbsent(highPairRank, k -> new ArrayList<>()).add(player);

                } else if (highPairRank > highestPairRank1) {
                    handValues.clear();  // clear the map since we have a new highest card
                    handValues.computeIfAbsent(highPairRank, k -> new ArrayList<>()).add(player);
                    winner = player;
                    bestHand = handTypes.highestTwoPair;
                    highestPairRank1 = highPairRank;
                }

            }
        }
        List<Player> playersWithSameHand = handValues.get(highestPairRank1);
        boolean notNull = playersWithSameHand != null;

        if (notNull && playersWithSameHand.size() > 1) {
            winner = null;
            sameHand = null;
            sameHand = handValues.get(highestPairRank1);
            System.out.println("LENGTH OF SAMEHAND: "+sameHand.size());
        } else if (notNull && playersWithSameHand.size() == 1) sameHand = null;
    }

    private static void highestPair(List<Player> remainingPlayers, List<Cards> cards) {
        int highestPairRank = -1;
        HashMap<Integer, List<Player>> handValues = new HashMap<>();

        for (Player player : remainingPlayers) {
            int pairRank = -1;
            boolean hasPair = false;

            for (String rank : ranks) {
                int count = 0;
                for (String suit : suits) {
                    if (playerHasCard(player, cards, suit, rank)) {
                        count++;
                        if (count >= 2) {
                            pairRank = Arrays.asList(ranks).indexOf(rank);
                            hasPair = true;
                        }
                    }
                }
            }
            if (pairRank == highestPairRank && hasPair) {
                handValues.computeIfAbsent(pairRank, k -> new ArrayList<>()).add(player);

            } else if (pairRank > highestPairRank) {
                handValues.clear();  // clear the map since we have a new highest card
                handValues.computeIfAbsent(pairRank, k -> new ArrayList<>()).add(player);
                winner = player;
                bestHand = handTypes.highestPair;
                highestPairRank = pairRank;
            }

        }
        List<Player> playersWithSameHand = handValues.get(highestPairRank);
        boolean notNull = playersWithSameHand != null;

        if (notNull && playersWithSameHand.size() > 1) {
            winner = null;
            sameHand = null;
            sameHand = handValues.get(highestPairRank);
        } else if (notNull && playersWithSameHand.size() == 1) sameHand = null;
    }

    private static void highCard(List<Player> remainingPlayers, List<Cards> cards) {
        int highestValue = -1;
        HashMap<Integer, List<Player>> handValues = new HashMap<>();

        for (Player player : remainingPlayers) {
            int tempHighest = -1;

            for (Cards card : PokerLogic.cardsList) {
                int curr = Arrays.toString(ranks).indexOf(card.rank());
                tempHighest = Math.max(curr, tempHighest);
            }

            for (Cards card : player.hand) {
                int curr = Arrays.toString(ranks).indexOf(card.rank());
                tempHighest = Math.max(curr, tempHighest);
            }

            if (tempHighest == highestValue && tempHighest != -1) {
                handValues.computeIfAbsent(tempHighest, k -> new ArrayList<>()).add(player);

            } else if (tempHighest > highestValue) {
                handValues.clear();  // clear the map since we have a new highest card
                handValues.computeIfAbsent(tempHighest, k -> new ArrayList<>()).add(player);
                winner = player;
                bestHand = handTypes.highCard;
                highestValue = tempHighest;
            }

            List<Player> playersWithSameHand = handValues.get(highestValue);
            boolean notNull = playersWithSameHand != null;

            if (notNull && playersWithSameHand.size() > 1) {
                winner = null;
                sameHand = null;
                sameHand = handValues.get(highestValue);
            } else if (notNull && playersWithSameHand.size() == 1) sameHand = null;

        }
    }
}