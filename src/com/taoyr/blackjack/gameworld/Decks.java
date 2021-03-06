package com.taoyr.blackjack.gameworld;

import java.util.LinkedList;
import java.util.Random;

import com.taoyr.blackjack.util.Logger;

public class Decks {

    public static final String ACE_NAME = "A";
    public static final int SOFT_HAND_VALUE = 11;
    public static final int HARD_HAND_VALUE = 1;

    private static final String[] NAMES = {"2", "3", "4", "5", "6", "7", "8",
        "9", "10", "J", "Q", "K", ACE_NAME};
    private static final int MAX_SIZE = 52;
    // If a player holds an ace valued as 11, the hand is called "soft"
    private static final int FACE_CARD_VALUE = 10;
    
    // private static final HashMap<String, Integer> mValueMap = new HashMap<>();
    
    private LinkedList<Card> cards;
    
    private LinkedList<Card> initCards() {
        LinkedList<Card> initCards = new LinkedList<>();
        for (int i = 0; i < NAMES.length; i++) { // 15
            for (CardType cardType : CardType.values()) { // 4
                Card card = new Card(NAMES[i], getValueFromName(NAMES[i]), false, cardType);
                initCards.add(card);
            }
        }
        return initCards;
    }

    public void shuffle() {
        cards = new LinkedList<>();
        LinkedList<Card> initCards = initCards();
        Random random = new Random();
        int maxIndex = initCards.size();
        for (int i = 0; i < MAX_SIZE; i++) {
            int index = random.nextInt(maxIndex);
            Card card = initCards.remove(index);
            cards.add(card);
            maxIndex--;
        }
    }
    
    /**
     * Number cards count as their natural value; the jack, queen, and king (also known as
     * "face cards") count as 10; aces are valued as either 1 or 11 according to the player's
     * choice.
     */
    private int getValueFromName(String name) {
        int value;
        try {
            value = Integer.parseInt(name);
        } catch (NumberFormatException e) {
            // Face cards (kings, queens, and jacks) are counted as ten points.
            if (ACE_NAME.equals(name)) {
                return SOFT_HAND_VALUE; // Player can choose to be soft hand later
            } else {
                return FACE_CARD_VALUE;
            }
        }
        return value;
    }

    public LinkedList<Card> getCards() {
        return cards;
    }
    
    public Card deal(boolean faceup) {
        if (cards == null || cards.size() < 1) {
            Logger.logDebug("no card can be delivered!");
            return null;
        }
        Card card = cards.pop();
        if (faceup) {
            card.visible = true;
        }
        return card;
    }
}
