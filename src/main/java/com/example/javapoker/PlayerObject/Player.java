package com.example.javapoker.PlayerObject;

import com.example.javapoker.CardsLogic.Cards;
import com.example.javapoker.GameLogic.PokerLogic;

import java.util.ArrayList;
import java.util.List;

public class Player {
    public enum BlindType {
        BIGBLIND,
        SMALLBLIND,

    }
    boolean allIn = false, inPreFlop = true;
    int chips, blindCallAmount = 0, totalBetAmount = 0;
    boolean folded;
    String name;
    BlindType blindType;
    List<Cards> hand;


    public Player(int chipCount, String playerName) {
        this.chips = chipCount;
        this.name = playerName;
        this.hand = new ArrayList<>();
    }
    public boolean isAllIn() { return this.allIn; }
    public void hand() {
        for(Cards card : hand) {}// ScreenText.printToScreen(card.rank()+" of "+card.suit());
    }

    public void AllIn() {
        // ScreenText.printToScreen(this.getName()+" went all in!");
        this.allIn = true;
        totalBetAmount += this.chips;
        PokerLogic.addToPot(this.chips);
        chips = 0;
    }
    public int getBlindCallAmount() { return this.blindCallAmount; }
    public boolean isFolded() { return this.folded; }
    public void outOfPreFlop() {
        this.blindCallAmount = 0;
        this.inPreFlop = false;
    }
    public int getChips() { return this.chips; }
    public void addToBet(int amount) { this.totalBetAmount += amount; }
    public List<Cards> getHand() { return this.hand; }
    public void addToChips(int x) { this.chips += x; }
    public void call(int initial) {
        int amountToCall = initial + (this.inPreFlop ? (30 - this.blindCallAmount) : 0);
        this.chips -= amountToCall;
        PokerLogic.addToPot(amountToCall);
    }
    public void fold(Player player) {
        this.folded = true;
        PokerLogic.removePlayer(player);
        // ScreenText.printToScreen(player.getName()+" decided to fold!");
    }
    public void addHand(Cards card) {
        this.hand.add(card);
    }
    public void blindCheck() {
        if(this.blindType != null) {

            if(this.blindType == BlindType.BIGBLIND) {
                // ScreenText.printToScreen(this.name+" is the big blind. 30 tokens are automatically deducted from balance.");
                this.chips -= 30;

            } else {
                // ScreenText.printToScreen(this.name+" is the small blind. 15 tokens are automatically deducted from balance.");
                this.chips -= 15;

            }
        }
    }
    public void reset() {
        this.hand.clear();
        this.folded = false;
        this.allIn = false;
        this.chips = 10000;
    }
    public void setBlindType(BlindType type) {
        this.blindType = type;
    }
    public void setBlindNull() {
        this.blindType = null;
    }
    public BlindType getBlindType() {
        return this.blindType;
    }
    public String getName() {
        return this.name;
    }
}