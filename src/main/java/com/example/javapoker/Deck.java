package com.example.javapoker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Deck {
    private static Stack<Cards> cards;
    public static Stack<Cards> initializeDeck() {
        cards = new Stack<>();
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};

        for (String suit : suits) {
            for (String rank : ranks) {
                Cards card = new Cards(suit, rank);
                cards.push(card);
            }
        }
        shuffle(cards);
        return cards;
    }
    public static void shuffle(Stack<Cards> cards) {
        Collections.shuffle(cards);
    }

}