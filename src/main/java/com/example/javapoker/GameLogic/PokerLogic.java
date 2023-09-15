package com.example.javapoker.GameLogic;

import com.example.javapoker.Graphics.OutputSystem;
import com.example.javapoker.PlayerObject.PlayerBot;
import com.example.javapoker.PlayerObject.PlayerUser;
import com.example.javapoker.CardsLogic.Cards;
import com.example.javapoker.CardsLogic.Deck;
import com.example.javapoker.PlayerObject.Player;

import java.util.*;

import static com.example.javapoker.PlayerObject.Player.BlindType;

public class PokerLogic {
    static List<Cards> cardsList = new ArrayList<>();
    static List<Player> currentPlayers = new ArrayList<>(), sameHandCase = new ArrayList<>(), players = new ArrayList<>();
    static Player littleBlind, bigBlind, winner = null, user = null;
    static int pot;
    public static void gameStart() {
        addPlayers();
        setBlinds();
        Scanner scan = new Scanner(System.in);
        OutputSystem.print("GAME STARTED");

        while(winner != null) {
            OutputSystem.print("GAME IN LOOP");
            Stack<Cards> deck = Deck.initializeDeck();
            preFlopAndFlopHandling(deck, scan);
            turnRiverHandling(deck, scan, currentPlayers);

            sleep();
            if(setWinner() == -1) {
                OutputSystem.print("SAME HAND SIZE: "+ WinLogic.sameHand.size());
                sameHandCase = WinLogic.sameHand;
                SplitLogic.split(sameHandCase, pot);
            }

            sleep();
            reset();
        }
    }
    public static void removePlayer(Player player) { currentPlayers.remove(player); }
    public static void addToPot(int amount) { pot += amount; }
    private static void sleep() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private static void getUserHand() {
        OutputSystem.print("\n----------------Your hand----------------");
        user.hand();
        OutputSystem.print("----------------Your hand----------------\n");
    }
    private static int setWinner() {
        winner = WinLogic.winStart(currentPlayers, cardsList);
        if(SidePot.sidePots.containsKey(winner)) SidePot.sidePotWinner(currentPlayers);
        if(winner == null) return -1;

        OutputSystem.print("\n\n "+winner.getName()+" IS THE WINNER! \n");

        winner.addToChips(pot);
        OutputSystem.print("PLAYERS IN LIST: "+currentPlayers.size());

        for(Player player : currentPlayers) {
            if(winner == player) continue;
            OutputSystem.print("\n-------------------------------");
            OutputSystem.print(player.getName()+"'s hand: ");
            player.hand();
            OutputSystem.print("\n-------------------------------\n");
        }

        OutputSystem.print("---- The River ----");
        for(Cards card : cardsList) OutputSystem.print(card.rank()+" of "+card.suit());
        OutputSystem.print("---- The River ----");

        OutputSystem.print("\n---- Player's Hand ----");
        for(Cards card : winner.getHand()) OutputSystem.print(card.rank()+" of "+card.suit());
        OutputSystem.print("---- Player's Hand ----\n\n");
        return 0;
    }
    private static void giveHands(Stack<Cards> deck, List<Player> currentPlayers) {
        int numPlayers = currentPlayers.size(), startIndex = currentPlayers.indexOf(littleBlind);

        for(int i=0; i<numPlayers; i++) {
            int index = (startIndex + i) % numPlayers;
            currentPlayers.get(index).addHand(deck.pop());
            currentPlayers.get(index).addHand(deck.pop());
        }
    }
    private static void turnRiverHandling(Stack<Cards> deck, Scanner scan, List<Player> gamePlayers) {
        if(gamePlayers.size() == 1) return;

        TurnLogic.turns(gamePlayers, scan);
        theTurn(deck, scan);

        TurnLogic.turns(gamePlayers, scan);
        theRiver(deck, scan);
    }
    private static void reset() {
        cardsList.clear();
        for(Player c : players) c.reset();
        currentPlayers = players;
        changeBlinds();
        winner = null;
        pot = 0;
    }
    public static void preFlopAndFlopHandling(Stack<Cards> deck, Scanner scan) {
        gamePlayers();
        giveHands(deck, currentPlayers);
        checkBlinds();

        if(!user.isFolded()) getUserHand();
        TurnLogic.preFlopChoices(currentPlayers, scan, (currentPlayers.indexOf(littleBlind) + 2) % players.size());
        flop(deck);

        if(!user.isFolded()) getUserHand();
        TurnLogic.turns(currentPlayers, scan);
    }
    public static void gamePlayers() {
        currentPlayers = new ArrayList<>(players);
    }
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
        players.add(new PlayerUser(10000, "USER"));
        user = players.get(0);
        for (int i = 1; i <= 4; i++) {
            String iString = Integer.toString(i);
            players.add(new PlayerBot(10000, "BOT" + iString));
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

        OutputSystem.print("THE FLOP:\n");
        for(int i=0; i<3; i++) {
            cardsList.add(deck.pop());
            deck.pop();
            OutputSystem.print(cardsList.get(i).rank()+" of "+cardsList.get(i).suit());
        }
    }
    public static void checkBlinds() { for(Player player : players) player.blindCheck(); }
    public static void theTurn(Stack<Cards> deck, Scanner scan) {
        cardsList.add(deck.pop());
        int last = cardsList.size() - 1;
        OutputSystem.print("The turn is: " + cardsList.get(last).rank() + " of " + cardsList.get(last).suit());

        if(!user.isFolded()) getUserHand();
        TurnLogic.turns(currentPlayers, scan);

    }
    public static void theRiver(Stack<Cards> deck, Scanner scan) {
        cardsList.add(deck.pop());
        int last = cardsList.size() - 1;
        OutputSystem.print("The river is: " + cardsList.get(last).rank() + " of " + cardsList.get(last).suit());

        if(!user.isFolded()) getUserHand();
        TurnLogic.turns(currentPlayers, scan);
    }
}