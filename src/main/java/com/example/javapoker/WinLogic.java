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
        if(bestHand != null) System.out.println("WINNERS HAND: "+bestHand.toString().toUpperCase());
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
        String[] rFCards = new String[] { "10", "Jack", "Queen", "King", "Ace" };
        for(String suit : suits) {
            boolean rFDraw = true;
            for (String rFCard : rFCards) rFDraw = cards.stream().anyMatch(card -> card.rank().equals(rFCard) && card.suit().equals(suit));
            if(rFDraw) return true;
        }
        return false;
    }
    private static void royalFlush(List<Player> remainingPlayers, List<Cards> cards) {
        if(rFInDraw(cards)) {
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
                        bestHand = handTypes.highestStraightFlush;
                        break;
                    }

                }
            }
            if(tempHighFlushCard > highestRankCard) {
                handValues.clear();  // clear the map since we have a new highest card
                handValues.computeIfAbsent(tempHighFlushCard, k -> new ArrayList<>()).add(player);
                winner = player;
                bestHand = handTypes.highestStraightFlush;
                highestRankCard = tempHighFlushCard;

            } else if(tempHighFlushCard == highestRankCard) {
                handValues.computeIfAbsent(tempHighFlushCard, k -> new ArrayList<>()).add(player); //if 2 or more players have the same hand, add to same key
            }
        }
        List<Player> playersWithSameHand = handValues.get(highestRankCard);
        if(playersWithSameHand != null && handValues.get(highestRankCard).size() > 1) {
            winner = null;
            sameHand = handValues.get(highestRankCard);
        } else {
            sameHand = null;
        }
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
            if(tempRank > highestRank) {
                handValues.clear();  // clear the map since we have a new highest card
                handValues.computeIfAbsent(tempRank, k -> new ArrayList<>()).add(player);
                winner = player;
                bestHand = handTypes.highestFourOfAKind;
                highestRank = tempRank;

            } else if(tempRank == highestRank) {
                handValues.computeIfAbsent(highestRank, k -> new ArrayList<>()).add(player); //if 2 or more players have the same hand, add to same key
            }
        }
        List<Player> playersWithSameHand = handValues.get(highestRank);
        if(playersWithSameHand != null && handValues.get(highestRank).size() > 1) {
            winner = null;
            sameHand = handValues.get(highestRank);
        } else {
            sameHand = null;
        }
    }
    private static void highestFullHouse(List<Player> remainingPlayers, List<Cards> cards) {
        int higherThreePair = -1;
        HashMap<Integer, List<Player>> handValues = new HashMap<>();

        for (Player player : remainingPlayers) {
            int playerRankThreePair = -2; //if full house is existent, then assign the rank index and whoever maxes out the integer is the person with higher full house
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
            if (playerRankThreePair > higherThreePair) {
                handValues.clear();  // clear the map since we have a new highest card
                handValues.computeIfAbsent(playerRankThreePair, k -> new ArrayList<>()).add(player);
                winner = player;
                bestHand = handTypes.highestFullHouse;
                higherThreePair = playerRankThreePair; //if full house is non-existent, the statement will not be called since -1 is not > -1

            } else if (playerRankThreePair == higherThreePair) {
                handValues.computeIfAbsent(playerRankThreePair, k -> new ArrayList<>()).add(player); //if 2 or more players have the same hand, add to same key
            }
        }
        List<Player> playersWithSameHand = handValues.get(higherThreePair);
        if (playersWithSameHand != null && handValues.get(higherThreePair).size() > 1) {
            winner = null;
            sameHand = handValues.get(higherThreePair);
        } else {
            sameHand = null;
        }
    }
        private static void highestFlush(List<Player> remainingPlayers, List<Cards> cards) {
            int highRank = -1;
            HashMap<Integer, List<Player>> handValues = new HashMap<>();


            for (Player player : remainingPlayers) {
                int highestRank = -2;
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
                    handValues.clear();  // clear the map since we have a new highest card
                    handValues.computeIfAbsent(highestRank, k -> new ArrayList<>()).add(player);
                    winner = player;
                    bestHand = handTypes.highestFlush;
                    highRank = highestRank;

                } else if(highestRank == highRank) {
                    handValues.computeIfAbsent(highestRank, k -> new ArrayList<>()).add(player); //if 2 or more players have the same hand, add to same key
                }
            }
            List<Player> playersWithSameHand = handValues.get(highRank);
            if(playersWithSameHand != null && handValues.get(highRank).size() > 1) {
                winner = null;
                sameHand = handValues.get(highRank);
            } else {
                sameHand = null;
            }
        }

        private static void highestStraight(List<Player> remainingPlayers, List<Cards> cards) {
            int highestAscendCard = -1;
            HashMap<Integer, List<Player>> handValues = new HashMap<>();

            for (Player player : remainingPlayers) {
                int highestRank = -2;
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
                for (int i = 0; i < ranks.length; i++) {
                    if (hasNumber(player, cards, ranks[i])) {
                        consecCard++;
                        System.out.println("Player: " + player.getName() + ", Rank: " + ranks[i] + ", ConsecCard: " + consecCard);
                        if (consecCard == 5) straightEnd.add(i);
                    } else {
                        consecCard = 0;
                    }
                }

                if (!straightEnd.isEmpty()) {
                    highestRank = straightEnd.get(straightEnd.size() - 1);
                }

                if (highestRank > highestAscendCard) {
                    handValues.clear();
                    handValues.computeIfAbsent(highestRank, k -> new ArrayList<>()).add(player);
                    winner = player;
                    bestHand = handTypes.highestStraight;
                    highestAscendCard = highestRank;

                } else if (highestRank == highestAscendCard) {
                    handValues.computeIfAbsent(highestRank, k -> new ArrayList<>()).add(player);
                }
            }

            List<Player> playersWithSameHand = handValues.get(highestAscendCard);
            if (playersWithSameHand != null && playersWithSameHand.size() > 1) {
                winner = null;
                sameHand = playersWithSameHand;
            } else {
                sameHand = null;
            }
        }
        private static void highestThreeOfAKind(List<Player> remainingPlayers, List<Cards> cards) {
            int highestIndex = -1;
            HashMap<Integer, List<Player>> handValues = new HashMap<>();

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
                int getIndex = Arrays.asList(ranks).indexOf(highestRank);
                if(!highestRank.isEmpty()) {
                    if(getIndex > highestIndex) {
                        handValues.clear();  // clear the map since we have a new highest card
                        handValues.computeIfAbsent(getIndex, k -> new ArrayList<>()).add(player);
                        winner = player;
                        bestHand = handTypes.highestStraight;
                        highestIndex = getIndex;

                    } else if(getIndex == highestIndex) {
                        handValues.computeIfAbsent(getIndex, k -> new ArrayList<>()).add(player); //if 2 or more players have the same hand, add to same key
                    }
                }

            }
            List<Player> playersWithSameHand = handValues.get(highestIndex);
            if(playersWithSameHand != null && handValues.get(highestIndex).size() > 1) {
                winner = null;
                sameHand = handValues.get(highestIndex);
            } else {
                sameHand = null;
            }
        }
        private static void highestTwoPair(List<Player> remainingPlayers, List<Cards> cards) {
            int highestPairRank1 = -1;
            int highestPairRank2 = -1;
            HashMap<Integer, List<Player>> handValues = new HashMap<>();

            for (Player player : remainingPlayers) {
                List<Integer> pairs = new ArrayList<>();
                for (String rank : ranks) if(countRank(player, cards, rank) == 2) pairs.add(Arrays.toString(ranks).indexOf(rank));

                if(pairs.size() >= 2) {
                    Collections.sort(pairs);
                    int highPairRank = pairs.get(pairs.size() - 1);
                    int lowPairRank = pairs.get(pairs.size() - 2);;

                    if (highPairRank > highestPairRank1 || (highPairRank == highestPairRank1 && lowPairRank > highestPairRank2)) {
                        handValues.clear();  // clear the map since we have a new highest card
                        handValues.computeIfAbsent(highPairRank, k -> new ArrayList<>()).add(player);
                        winner = player;
                        bestHand = handTypes.highestTwoPair;

                        highestPairRank1 = highPairRank;
                        highestPairRank2 = lowPairRank;
                    } else if (highPairRank == highestPairRank1 && lowPairRank == highestPairRank2) {
                        handValues.computeIfAbsent(highPairRank, k -> new ArrayList<>()).add(player);
                    }
                }
            }
            List<Player> playersWithSameHand = handValues.get(highestPairRank1);
            if(playersWithSameHand != null && handValues.get(highestPairRank1).size() > 1) {
                winner = null;
                sameHand = handValues.get(highestPairRank1);
            } else {
                sameHand = null;
            }
        }
        private static void highestPair(List<Player> remainingPlayers, List<Cards> cards) {
            int highestPairRank = -1;
            HashMap<Integer, List<Player>> handValues = new HashMap<>();


            for (Player player : remainingPlayers) {
                for (String rank : ranks) {
                    int count = 0;
                    for (String suit : suits) {
                        if (playerHasCard(player, cards, suit, rank)) {
                            count++;
                            if (count >= 2) {
                                int pairRank = Arrays.asList(ranks).indexOf(rank);
                                if (pairRank > highestPairRank) {
                                    handValues.clear();  // clear the map since we have a new highest card
                                    handValues.computeIfAbsent(pairRank, k -> new ArrayList<>()).add(player);
                                    winner = player;
                                    bestHand = handTypes.highestStraight;
                                    highestPairRank = pairRank;

                                } else if(pairRank == highestPairRank) {
                                    handValues.computeIfAbsent(pairRank, k -> new ArrayList<>()).add(player);
                                }
                                break;
                            }
                        }
                    }
                }

            }
            List<Player> playersWithSameHand = handValues.get(highestPairRank);
            if(playersWithSameHand != null && handValues.get(highestPairRank).size() > 1) {
                winner = null;
                sameHand = handValues.get(highestPairRank);
            } else {
                sameHand = null;
            }
        }
        private static void highCard(List<Player> remainingPlayers, List<Cards> cards) {
            int highestValue = 0;
            HashMap<Integer, List<Player>> handValues = new HashMap<>();

            for (Player player : remainingPlayers) {
                for (String rank : ranks) {
                    int rankValue = Arrays.asList(ranks).indexOf(rank) + 2;

                    if (playerHasCard(player, cards, suits[0], rank) ||
                            playerHasCard(player, cards, suits[1], rank) ||
                            playerHasCard(player, cards, suits[2], rank) ||
                            playerHasCard(player, cards, suits[3], rank)) {

                        if (rankValue > highestValue) {
                            handValues.clear();  // clear the map since we have a new highest card
                            handValues.computeIfAbsent(rankValue, k -> new ArrayList<>()).add(player);
                            winner = player;
                            bestHand = handTypes.highestStraight;
                            highestValue = rankValue;
                        } else if(rankValue == highestValue) {
                            handValues.computeIfAbsent(rankValue, k -> new ArrayList<>()).add(player);
                        }
                    }
                }
            }
            List<Player> playersWithSameHand = handValues.get(highestValue);
            if(playersWithSameHand != null && handValues.get(highestValue).size() > 1) {
                winner = null;
                sameHand = handValues.get(highestValue);
            } else {
                sameHand = null;
            }
        }
    }