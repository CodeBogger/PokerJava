package com.example.javapoker;

import javafx.beans.property.ReadOnlyObjectWrapper;

import java.util.*;
import static com.example.javapoker.Player.BlindType;
import java.util.Scanner;

public class PokerLogic {
    static List<Cards> cardsList = new ArrayList<>();
    public static List<Player> players = new ArrayList<>();
    public static List<Player> currentPlayers = new ArrayList<>();
    static Player littleBlind;
    static Player bigBlind;
    public static void gameStart() {
         addPlayers();
         setBlinds();
         Scanner scan = new Scanner(System.in);

        while(!Menu.buttonClicked) {
            Stack<Cards> deck = Deck.initializeDeck();
            preFlopAndFlopHandling(deck, scan);
            turnRiverHandling(deck, scan);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            reset();
        }
    }
    private static void giveHands(Stack<Cards> deck) {
       for(Player player : players) {
           player.addHand(deck.pop());
           player.addHand(deck.pop());
       }
    }
    private static void turnRiverHandling(Stack<Cards> deck, Scanner scan) {
        theTurn(deck, scan);
        theRiver(deck, scan);
    }
    private static void reset() {
        cardsList.clear();
        for(Player c : players) {
            c.reset();
        }
        currentPlayers = new ArrayList<>(players);
        changeBlinds();
    }
    public static void preFlopAndFlopHandling(Stack<Cards> deck, Scanner scan) {
        gamePlayers(currentPlayers);
        giveHands(deck);
        checkBlinds();
        TurnLogic.preFlopChoices(players, scan, players.indexOf(bigBlind));
        flop(deck);

        System.out.println("\n\n");
        System.out.println("YOUR HAND: ");
        players.get(0).hand();

        TurnLogic.turns(currentPlayers, scan);
    }
    public static void gamePlayers(List<Player> list) { list.addAll(players); }
    public static void changeBlinds() {
        int littleBlindIndex = players.indexOf(littleBlind);
        int bigBlindIndex = players.indexOf(bigBlind);

        littleBlind.setBlindNull();
        bigBlind.setBlindNull();

        int nextLittleBlindIndex = (littleBlindIndex + 1) % players.size();
        int nextBigBlindIndex = (bigBlindIndex + 1) % players.size();

        littleBlind = players.get(nextLittleBlindIndex);
        bigBlind = players.get(nextBigBlindIndex);

        players.get(nextLittleBlindIndex).setBlindType(BlindType.SMALLBLIND);
        players.get(nextBigBlindIndex).setBlindType(BlindType.BIGBLIND);
    }
    public static void addPlayers() {
        players.add(new PlayerUser(1000, "USER"));
        for (int i = 1; i <= 4; i++) {
            String iString = Integer.toString(i);
            players.add(new PlayerBot(1000, "BOT" + iString));
        }
    }
    public static void setBlinds() {
        Random rand = new Random();
        int randIndex = rand.nextInt(0, players.size());

        players.get(randIndex).setBlindType(BlindType.SMALLBLIND);
        littleBlind = players.get(randIndex);

        int nextBlindIndex = (randIndex + 1) % players.size();
        players.get(nextBlindIndex).setBlindType(BlindType.BIGBLIND);
        bigBlind = players.get(nextBlindIndex);
    }
    public static void flop(Stack<Cards> deck) {
        deck.pop();

        System.out.println("THE FLOP:\n");
        for(int i=0; i<3; i++) {
            cardsList.add(deck.pop());
            deck.pop();
            System.out.println(cardsList.get(i).rank()+" of "+cardsList.get(i).suit());
        }
    }
    public static void checkBlinds() { for(Player player : players) player.blindCheck(); }
    public static void theTurn(Stack<Cards> deck, Scanner scan) {
        cardsList.add(deck.pop());
        int last = cardsList.size() - 1;
        System.out.println("The turn is: " + cardsList.get(last).rank() + " of " + cardsList.get(last).suit());

        TurnLogic.turns(currentPlayers, scan);

    }
    public static void theRiver(Stack<Cards> deck, Scanner scan) {
        cardsList.add(deck.pop());
        int last = cardsList.size() - 1;
        System.out.println("The river is: " + cardsList.get(last).rank() + " of " + cardsList.get(last).suit());

        TurnLogic.turns(currentPlayers, scan);
    }
}

