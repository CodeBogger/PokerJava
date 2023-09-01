package com.example.javapoker;

import java.util.*;

import static com.example.javapoker.Player.BlindType;

public class PokerLogic {
    static List<Cards> cardsList = new ArrayList<>();
    public static List<Player> players = new ArrayList<>();
    public static List<Player> currentPlayers = new ArrayList<>();
    static Player littleBlind;
    static Player bigBlind;
    static int pot, sidePot;
    public static Player winner = null;
    public static Player user = null;
    public static void gameStart() {
        addPlayers();
        setBlinds();
        Scanner scan = new Scanner(System.in);

        while(!Menu.buttonClicked || winner != null) {
            Stack<Cards> deck = Deck.initializeDeck();
            preFlopAndFlopHandling(deck, scan);
            turnRiverHandling(deck, scan, currentPlayers);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            setWinner();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            reset();
        }
    }

    private static void getUserHand() {
        System.out.println("\n");
        user.hand();
        System.out.println("\n");
    }
    private static void setWinner() {
        winner = WinLogic.winStart(currentPlayers, cardsList);
        System.out.println("\n\n "+winner.getName()+" IS THE WINNER! \n");
        winner.chips += pot;

        System.out.println("---- The River ----");
        for(Cards card : cardsList) System.out.println(card.rank()+" of "+card.suit());
        System.out.println("---- The River ----");

        System.out.println("\n---- Player's Hand ----");
        for(Cards card : winner.hand) System.out.println(card.rank()+" of "+card.suit());
        System.out.println("---- Player's Hand ----");
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

        if(!user.folded) getUserHand();
        TurnLogic.preFlopChoices(currentPlayers, scan, currentPlayers.indexOf(littleBlind));
        flop(deck);

        if(!user.folded) getUserHand();
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

        if(!user.folded) getUserHand();
        TurnLogic.turns(currentPlayers, scan);

    }
    public static void theRiver(Stack<Cards> deck, Scanner scan) {
        cardsList.add(deck.pop());
        int last = cardsList.size() - 1;
        System.out.println("The river is: " + cardsList.get(last).rank() + " of " + cardsList.get(last).suit());

        if(!user.folded) getUserHand();
        TurnLogic.turns(currentPlayers, scan);
    }
}