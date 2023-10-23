package com.example.javapoker.GameLogic;

import com.example.javapoker.CardsLogic.Cards;
import com.example.javapoker.PlayerObject.Player;

import java.util.*;

public class WinLogic {
    public enum handTypes {
        highCard, highestPair, highestTwoPair, highestThreeOfAKind, highestStraight, highestFlush, highestFullHouse, highestFourOfAKind, highestStraightFlush, royalFlush,
    }
    static final String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
    static final String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};
    static Player winner = null;
    static handTypes bestHand = null;
    static List<Player> sameHand = null;

    public static Player winStart(List<Player> remainingPlayers, List<Cards> cards) {
        if (remainingPlayers.size() == 1) return remainingPlayers.get(0);

        getHandValues(remainingPlayers, cards); //get hand values in ascending order to prioritize higher hands with higher value
        //get highest hand
        // ScreenText.printToScreen("WINNERS HAND: " + bestHand.toString().toUpperCase());
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
    private static int getIndex(String rank) {
        for(int i = 0; i< WinLogic.ranks.length; i++) if(Objects.equals(WinLogic.ranks[i], rank)) return i;
        return -1;
    }
    private static int countRank(Player player, List<Cards> cards, String rank) {
        long countInHand = player.getHand().stream().filter(card -> card.rank().equals(rank)).count();
        long countInCommunity = cards.stream().filter(card -> card.rank().equals(rank)).count();
        return (int) (countInHand + countInCommunity);
    }
    private static boolean playerHasCard(Player player, List<Cards> cards, String suit, String rank) {
        return player.getHand().stream().anyMatch(card -> card.suit().equals(suit) && card.rank().equals(rank)) ||
                cards.stream().anyMatch(card -> card.suit().equals(suit) && card.rank().equals(rank));
    }
    private static boolean kicker(List<Cards> cards, List<Player> players, Map<Integer, List<Player>> map, Map<Player, List<Integer>> pairs) {

        // To store the highest rank found among the cards in 'cards'.
        int highestInDraw = -1;

        // A list to store the highest rank of each player.
        List<Integer> highestHand = new ArrayList<>();

        // Find the highest rank among the 'cards'.
        for(Cards card : cards) highestInDraw = Math.max(highestInDraw, getIndex(card.rank()));

        // Iterate over each player and determine the highest rank in their hand.
        for (Player player : players) {
            int currMax = -1;
            List<Integer> playerPairs = pairs.getOrDefault(player, new ArrayList<>());

            // For each card in the player's hand, update the currMax to the highest rank.
            for (Cards card : player.getHand()) {
                int pair = getIndex(card.rank());
                if(playerPairs.contains(pair)) continue;
                currMax = Math.max(currMax, getIndex(card.rank()));
            }
            highestHand.add(currMax); // Add the highest rank of the current player to 'highestHand'.
        }

        // Determine the players having the highest kicker card.
        int max = -1;
        List<Integer> maxKickerIndexes = new ArrayList<>();
        for(int value : highestHand) {
            if(value == max) {
                maxKickerIndexes.add(highestHand.indexOf(value));
            } else if(value > max) {
                maxKickerIndexes.clear();
                maxKickerIndexes.add(highestHand.indexOf(value));
            }
        }

        // If there's a tie based on the kicker card.
        if(maxKickerIndexes.size() != 1) {
            map.clear();
            map.computeIfAbsent(max, key -> {
                List<Player> playerList = new ArrayList<>();
                for(int index : maxKickerIndexes) playerList.add(players.get(maxKickerIndexes.get(index)));
                return playerList;
            });
            return true;
        }

        // If the highest card in the deck is greater or equal to any player's highest card, then it's a split.
        if(highestInDraw >= Collections.max(highestHand)) return false;

        // If the above condition fails, then there's a clear winner.
        map.clear();
        int indexMax = highestHand.indexOf(Collections.max(highestHand)); // Get the index of the player with the highest rank card.
        Player winningHand = players.get(indexMax); // Get the player corresponding to that index.

        winner = winningHand; // Assign the winning player.
        map.computeIfAbsent(Collections.max(highestHand), key -> {
            List<Player> playerList = new ArrayList<>();
            playerList.add(winningHand);
            return playerList;
        });
        return true;
    }
    private static boolean hasNumber(Player player, List<Cards> cards, String rank) {
        return player.getHand().stream().anyMatch(card -> card.rank().equals(rank)) || cards.stream().anyMatch(card -> card.rank().equals(rank));
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
        Map<Integer, List<Player>> handValues = new HashMap<>();

        for (Player player : remainingPlayers) {
            int tempHighFlushCard = -1;
            boolean hasStraightFlush = false;

            for (String suit : suits) {
                    int count = 0;
                    for(String rank : ranks) {
                        if(playerHasCard(player, cards, rank, suit)) {
                            count++;
                            if(count >= 5) {
                                hasStraightFlush = true;
                                tempHighFlushCard = getIndex(rank);
                            }
                        }
                    }
            }
            if(hasStraightFlush && tempHighFlushCard == highestRankCard) {
                handValues.computeIfAbsent(tempHighFlushCard, k -> new ArrayList<>()).add(player); //if 2 or more players have the same hand, add to same key

            } else if (tempHighFlushCard > highestRankCard) {
                handValues.clear();  // clear the map since we have a new highest card

                handValues.computeIfAbsent(tempHighFlushCard, k -> {
                    List<Player> temp = new ArrayList<>();
                    temp.add(player);
                    return temp;
                });


                winner = player;
                bestHand = handTypes.highestStraightFlush;
                highestRankCard = tempHighFlushCard;
            }
        }
        if(handValues.isEmpty()) return;
        int sameHandSize = handValues.get(highestRankCard).size();

        if (sameHandSize > 1) {
            winner = null;
            sameHand = null;
            sameHand = handValues.get(highestRankCard);
        } else if (sameHandSize == 1) sameHand = null;
    }

    private static void highestFourOfAKind(List<Player> remainingPlayers, List<Cards> cards) {
        int highestRank = -1;
        Map<Integer, List<Player>> handValues = new HashMap<>();

        for (Player player : remainingPlayers) {
            int tempRank = -1;
            boolean hasFour = false;

            for (String rank : ranks) {
                if (countRank(player, cards, rank) >= 4) {
                    tempRank = getIndex(rank);
                    hasFour = true;
                }
            }
            if (hasFour && tempRank == highestRank) {
                handValues.computeIfAbsent(highestRank, k -> new ArrayList<>()).add(player); //if 2 or more players have the same hand, add to same key

            } else if (tempRank > highestRank) {
                handValues.clear();  // clear the map since we have a new highest card

                handValues.computeIfAbsent(tempRank, k -> {
                    List<Player> temp = new ArrayList<>();
                    temp.add(player);
                    return temp;
                });

                winner = player;
                bestHand = handTypes.highestFourOfAKind;
                highestRank = tempRank;
            }
        }
        if(handValues.isEmpty()) return;
        int sameHandSize = handValues.get(highestRank).size();

        if (sameHandSize > 1) {
            winner = null;
            sameHand = null;
            sameHand = handValues.get(highestRank);
        } else if (sameHandSize == 1) sameHand = null;
    }

    private static void highestFullHouse(List<Player> remainingPlayers, List<Cards> cards) {
        int higherThreePair = -1;
        Map<Integer, List<Player>> handValues = new HashMap<>();

        for (Player player : remainingPlayers) {
            int playerRankThreePair = -1; //if full house is existent, then assign the rank index and whoever maxes out the integer is the person with higher full house
            int playerRankTwoPair = -1;
            boolean hasFullHouse;

            for (String rank : ranks) {
                int current = getIndex(rank);
                if(countRank(player, cards, rank) == 3) {
                    playerRankThreePair = current;
                } else if(countRank(player, cards, rank) == 2) {
                    playerRankTwoPair = current;
                }
            }
            hasFullHouse = playerRankTwoPair != -1 && playerRankThreePair != -1 && playerRankTwoPair != playerRankThreePair;
            if (hasFullHouse && playerRankThreePair == higherThreePair) {
                handValues.computeIfAbsent(playerRankThreePair, k -> new ArrayList<>()).add(player); //if 2 or more players have the same hand, add to same key

            } else if (hasFullHouse && playerRankThreePair > higherThreePair) {
                handValues.clear();  // clear the map since we have a new highest card

                handValues.computeIfAbsent(playerRankThreePair, k -> {
                    List<Player> temp = new ArrayList<>();
                    temp.add(player);
                    return temp;
                });

                winner = player;
                bestHand = handTypes.highestFullHouse;
                higherThreePair = playerRankThreePair; //if full house is non-existent, the statement will not be called since -1 is not > -1
            }
        }
        if(handValues.isEmpty()) return;
        int sameHandSize = handValues.get(higherThreePair).size();

        if (sameHandSize > 1) {
            winner = null;
            sameHand = null;
            sameHand = handValues.get(higherThreePair);
        } else if (sameHandSize == 1) sameHand = null;
    }

    private static void highestFlush(List<Player> remainingPlayers, List<Cards> cards) {
        int highestRank = -1;
        Map<Integer, List<Player>> handValues = new HashMap<>();

        for (Player player : remainingPlayers) {
            int highFlushCard = -1;
            boolean hasFlush = false;

            for(String suit : suits) {
                int suitCount = 0;

                for(String rank : ranks) {
                    if(playerHasCard(player, cards, suit, rank)) {
                        suitCount++;

                        if(suitCount >= 5) {
                            highFlushCard = getIndex(rank);
                            hasFlush = true;
                        }
                    }
                }
            }
            if(hasFlush && highFlushCard == highestRank) {
                handValues.computeIfAbsent(highFlushCard, k -> new ArrayList<>()).add(player); //if 2 or more players have the same hand, add to same key

            } else if (highFlushCard > highestRank) {
                handValues.clear();  // clear the map since we have a new highest card
                handValues.computeIfAbsent(highestRank, k -> {
                    List<Player> temp = new ArrayList<>();
                    temp.add(player);
                    return temp;
                });

                winner = player;
                bestHand = handTypes.highestFlush;
                highestRank = highFlushCard;
            }
        }
        if(handValues.isEmpty()) return;
        int sameHandSize = handValues.get(highestRank).size();

        if (sameHandSize > 1) {
            winner = null;
            sameHand = null;
            sameHand = handValues.get(highestRank);
        } else if (sameHandSize == 1) sameHand = null;
    }

    private static void highestStraight(List<Player> remainingPlayers, List<Cards> cards) {
        int highestAscendCard = -1;
        Map<Integer, List<Player>> handValues = new HashMap<>();

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

            int consecutiveCount = 0;
            for (String rank : ranks) {
                if (hasNumber(player, cards, rank)) {
                    consecutiveCount++;
                    if (consecutiveCount >= 5) {
                        hasStraight = true;
                        straightEnd.add(getIndex(rank));
                    }
                } else {
                    consecutiveCount = 0;
                }
            }
            Collections.sort(straightEnd);
            if (hasStraight) highestRank = straightEnd.get(straightEnd.size() - 1);

            if (hasStraight && highestRank == highestAscendCard) {
                handValues.computeIfAbsent(highestRank, k -> new ArrayList<>()).add(player);

            } else if (highestRank > highestAscendCard) {
                handValues.clear();

                handValues.computeIfAbsent(highestRank, k -> {
                    List<Player> temp = new ArrayList<>();
                    temp.add(player);
                    return temp;
                });
                winner = player;
                bestHand = handTypes.highestStraight;
                highestAscendCard = highestRank;
            }
        }
        if(handValues.isEmpty()) return;
        int sameHandSize = handValues.get(highestAscendCard).size();

        if (sameHandSize > 1) {
            winner = null;
            sameHand = handValues.get(highestAscendCard);
        } else if (sameHandSize == 1) sameHand = null;
    }

    private static void highestThreeOfAKind(List<Player> remainingPlayers, List<Cards> cards) {
        int highestIndex = -1;
        Map<Integer, List<Player>> handValues = new HashMap<>();
        Map<Player, List<Integer>> playerPairs = new HashMap<>();

        for (Player player : remainingPlayers) {
            int currHighestRank = -1;
            boolean hasThree = false;

            for (String rank : ranks) {
                if (countRank(player, cards, rank) >= 3) {
                    hasThree = true;
                    currHighestRank = getIndex(rank);
                }
            }
            if (hasThree && highestIndex == currHighestRank) {
                handValues.computeIfAbsent(currHighestRank, k -> new ArrayList<>()).add(player); //if 2 or more players have the same hand, add to same key
                int finalCurrHighestRank = currHighestRank;

                playerPairs.computeIfAbsent(player, k -> {
                    List<Integer> threePair = new ArrayList<>();
                    threePair.add(finalCurrHighestRank);
                    return threePair;
                });

            } else if (currHighestRank > highestIndex) {
                playerPairs.clear();
                handValues.clear();  // clear the map since we have a new highest card

                handValues.computeIfAbsent(currHighestRank, k -> {
                    List<Player> tempPlayer = new ArrayList<>();
                    tempPlayer.add(player);
                    return tempPlayer;
                });

                int highestFinal = currHighestRank;
                playerPairs.computeIfAbsent(player, k -> Collections.singletonList(highestFinal));

                winner = player;
                bestHand = handTypes.highestThreeOfAKind;
                highestIndex = currHighestRank;
            }
        }
        if(handValues.isEmpty()) return;
        int sameHandSize = handValues.get(highestIndex).size();

        if (sameHandSize > 1) {
            if(kicker(cards, handValues.get(highestIndex), handValues, playerPairs)) {
                sameHand = null;
                return;
            }
            winner = null;
            sameHand = null;
            sameHand = handValues.get(highestIndex);
        } else if (sameHandSize == 1) sameHand = null;
    }

    private static void highestTwoPair(List<Player> remainingPlayers, List<Cards> cards) {
        int highestPairRank1 = -1;
        Map<Integer, List<Player>> handValues = new HashMap<>();
        Map<Player, List<Integer>> playerPairs = new HashMap<>();

        for (Player player : remainingPlayers) {
            List<Integer> pairs = new ArrayList<>();
            boolean hasTwoPair = false;
            int highPairRank = -1;

            for (String rank : ranks) {
                if (countRank(player, cards, rank) == 2) pairs.add(getIndex(rank));

                if (pairs.size() >= 2) {
                    int highest = pairs.get(0);
                    for(int i=1; i<pairs.size(); i++) highest = Math.max(highest, pairs.get(i));
                    highPairRank = highest;
                    hasTwoPair = true;
                }

                List<Player> playersWithSameRank = handValues.get(highestPairRank1);
                if (hasTwoPair && highPairRank == highestPairRank1 && (playersWithSameRank == null || !playersWithSameRank.contains(player))) {

                    handValues.computeIfAbsent(highPairRank, k -> new ArrayList<>()).add(player);
                    playerPairs.putIfAbsent(player, pairs);

                } else if (highPairRank > highestPairRank1) {
                    handValues.clear();  // clear the map since we have a new highest card
                    playerPairs.clear();

                    handValues.computeIfAbsent(highPairRank, k -> {
                        List<Player> tempPlayers = new ArrayList<>();
                        tempPlayers.add(player);
                        return tempPlayers;
                    });
                    playerPairs.putIfAbsent(player, pairs);

                    winner = player;
                    bestHand = handTypes.highestTwoPair;

                    highestPairRank1 = highPairRank;
                }

            }
        }
        if(handValues.isEmpty()) return;
        int sameHandSize = handValues.get(highestPairRank1).size();

        if (sameHandSize > 1) {
            if(kicker(cards, handValues.get(highestPairRank1), handValues, playerPairs)) {
                /*
                in kicker method the map "hand-values" is changed based on a condition
                if condition changes (player list size), that means someone with a higher kicker wins
                 */
                sameHand = null;
                return;
            }

            winner = null;
            sameHand = null;
            sameHand = handValues.get(highestPairRank1);
        } else if (sameHandSize == 1) sameHand = null;
    }
    private static void highestPair(List<Player> remainingPlayers, List<Cards> cards) {
        int highestPairRank = -1;
        Map<Integer, List<Player>> handValues = new HashMap<>();
        Map<Player, List<Integer>> playerPairs = new HashMap<>();

        for (Player player : remainingPlayers) {
            int pairRank = -1;
            boolean hasPair = false;

            for (String rank : ranks) {
                int count = 0;
                for (String suit : suits) {
                    if (playerHasCard(player, cards, suit, rank)) {
                        count++;
                        if (count >= 2) {
                            pairRank = getIndex(rank);
                            hasPair = true;
                        }
                    }
                }
            }
            if (pairRank == highestPairRank && hasPair) {
                handValues.computeIfAbsent(pairRank, k -> new ArrayList<>()).add(player);

                List<Integer> temp = new ArrayList<>();
                temp.add(pairRank);
                playerPairs.putIfAbsent(player, temp);

            } else if (pairRank > highestPairRank) {
                playerPairs.clear();
                handValues.clear();  // clear the map since we have a new highest card

                handValues.computeIfAbsent(pairRank, k -> {
                    List<Player> tempPlayers = new ArrayList<>();
                    tempPlayers.add(player);
                    return tempPlayers;
                });
                int highFinal = pairRank;
                playerPairs.computeIfAbsent(player, k -> Collections.singletonList(highFinal));

                winner = player;
                bestHand = handTypes.highestPair;
                highestPairRank = pairRank;
            }

        }
        if(handValues.isEmpty()) return;
        int sameHandSize = handValues.get(highestPairRank).size();

        if (sameHandSize > 1) {
            //the function will skip if there is no high card value > any hand and their ranks
            if(kicker(cards, handValues.get(highestPairRank), handValues, playerPairs)) {
                sameHand = null;
                return;
            }

            winner = null;
            sameHand = handValues.get(highestPairRank);
        } else if (sameHandSize == 1) sameHand = null;
    }

    private static void highCard(List<Player> remainingPlayers, List<Cards> cards) {
        int highestValue = -1;
        Map<Integer, List<Player>> handValues = new HashMap<>();
        Map<Player, List<Integer>> playerPairs = new HashMap<>();

        for (Player player : remainingPlayers) {
            int tempHighest = -1;

            for (Cards card : cards) {
                int curr = getIndex(card.rank());
                tempHighest = Math.max(curr, tempHighest);
            }

            int firstCardRank = getIndex(player.getHand().get(0).rank());
            int secondCardRank = getIndex(player.getHand().get(1).rank());
            tempHighest = Math.max(firstCardRank, secondCardRank);

            if (tempHighest == highestValue && tempHighest != -1) {
                handValues.computeIfAbsent(tempHighest, k -> new ArrayList<>()).add(player);

                List<Integer> pair = new ArrayList<>();
                pair.add(tempHighest);
                playerPairs.putIfAbsent(player, pair);

            } else if (tempHighest > highestValue) {
                playerPairs.clear();
                handValues.clear();  // clear the map since we have a new highest card

                handValues.computeIfAbsent(tempHighest, k -> {
                    List<Player> tempPlayers = new ArrayList<>();
                    tempPlayers.add(player);
                    return tempPlayers;
                });

                int highestFinal = tempHighest;
                playerPairs.computeIfAbsent(player, k -> Collections.singletonList(highestFinal));

                winner = player;
                bestHand = handTypes.highCard;
                highestValue = tempHighest;
            }

        }
        if(handValues.isEmpty()) return;
        int sameHandSize = handValues.get(highestValue).size();

        if (sameHandSize > 1) {
            if(kicker(cards, remainingPlayers, handValues, playerPairs)) {
                sameHand = null;
                return;
            }
            winner = null;
            sameHand = handValues.get(highestValue);
        } else if (sameHandSize == 1) sameHand = null;
    }
}