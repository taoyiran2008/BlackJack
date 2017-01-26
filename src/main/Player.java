package main;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


import policy.IPolicy;
import prop.Card;

/**
 * Basic data structure for a player(dealer) play games in a casino
 * to describe his/her information.
 * 
 * Player is the leading role, he got a wide variety of options to make, like he can hit, stand,
 * double or do something else according to the Policy he complied with.
 * 
 * However dealer is the supporting role, he got far less options
 * to make, basically he just deal the cards to everyone, which in our case is taken over by
 * a Casino instance, but still he can draw cards.
 * Good news is that people in a casino take turns to be a dealer.
 */
public class Player {

    public static final int PLAYER_TYPE_NORMAL = 1;
    public static final int PLAYER_TYPE_DEALER = 2;

    public ArrayList<Card> cards = new ArrayList<>(5); // 10 by default, but 5 is sufficient for us
    public int totalMoney = 0;
    public int betInBox = 0;
    public int id = -1;
    public String name = "anonymous";
    public int status = IPolicy.STATUS_IM_OK;
    public int type = PLAYER_TYPE_NORMAL;
    // Protect the important member variable for security sake.
    private IPolicy policyImpl;
    private static AtomicInteger idGenerator = new AtomicInteger(0);
    
    public Player() {
        this.id = generateId();
    }

    public Player(int totalMoney, int betInBox, String name, int type) {
        this.totalMoney = totalMoney;
        this.betInBox = betInBox;
        this.name = name;
        this.type = type;
        this.id = generateId();
    }

    public final void setPolicy(IPolicy policy) {
        policyImpl = policy;
    }
    
    public final IPolicy getPolicy() {
        return policyImpl;
    }
    
    public final int getTotalValue() {
        int value = 0;
        for (Card card : cards) {
            value += card.value;
        }
        return value;
    }
    
    public final int getCardsNumber() {
        return cards.size();
    }
    
    /**
     * Thrown money in the betting box.
     */
    public final boolean thrownMoney(int money) {
        if (totalMoney >= money) {
            totalMoney -= money;
            betInBox += money;
            return true;
        } else {
            // Running out of cash.
            return false;
        }
    }
    
    public final void takeBackMoney(int money) {
        totalMoney += money;
        betInBox -= money;
    }
    
    public final int generateId() {
        return idGenerator.getAndIncrement();
    }
}
