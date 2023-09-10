package com.example.javapoker;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Player {
    public enum BlindType {
        BIGBLIND,
        SMALLBLIND,

    }
    boolean allIn = false, inPreFlop = true;
    int chips, blindCallAmount = 0;
    boolean folded, calledBB = false, calledLB = false;
    String name;
    BlindType blindType;
    List<Cards> hand;


    public Player(int chipCount, String playerName) {
        this.chips = chipCount;
        this.name = playerName;
        this.hand = new ArrayList<>();

    }
    public boolean isAllIn() { return this.allIn; }
    public void hand() { for(Cards card : hand) System.out.println(card.rank()+" of "+card.suit()); }

    public void AllIn() {
        System.out.println(this.getName()+" went all in!");
        this.allIn = true;
        PokerLogic.pot += this.chips;
        chips = 0;
    }
    public void call(int initial) {
        int amountToCall = initial + (this.inPreFlop ? (30 - this.blindCallAmount) : 0);
        this.chips -= amountToCall;
        PokerLogic.pot += amountToCall;
    }
    public void fold(Player player) {
        this.folded = true;
        PokerLogic.currentPlayers.remove(player);
        System.out.println(player.getName()+" decided to fold!");
    }
    public void addHand(Cards card) {
        this.hand.add(card);
    }
    public void blindCheck() {
        if(this.blindType != null) {

            if(this.blindType == BlindType.BIGBLIND) {
                System.out.println(this.name+" is the big blind. 30 tokens are automatically deducted from balance.");
                this.chips -= 30;

            } else {
                System.out.println(this.name+" is the small blind. 15 tokens are automatically deducted from balance.");
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