package com.example.javapoker;

import java.util.ArrayList;
import java.util.List;
public class Player {
    public static enum BlindType {
        BIGBLIND,
        SMALLBLIND,

    }
    boolean folded;
    int chips;
    String name;
    BlindType blindType;
    List<Cards> hand;

    public Player(int chipCount, String playerName) {
        this.chips = chipCount;
        this.name = playerName;
        this.hand = new ArrayList<>();

    }
    public void hand() {
        for(Cards card : hand) System.out.println(card.rank()+" of "+card.suit());
        System.out.println("\n\n");
    }
    public void fold() { this.folded = true; }
    private void getHand() {
        for(Cards c : hand) {
            System.out.println(c.rank()+", OF "+c.suit());
        }
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
    }
    public void setBlindType(BlindType type) { this.blindType = type; }
    public void setBlindNull() { this.blindType = null; }
    public BlindType getBlindType() { return this.blindType; }
    public String getName() { return this.name; }
}
