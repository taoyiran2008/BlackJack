package com.taoyr.blackjack.gameworld;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.taoyr.blackjack.policy.IPolicy;
import com.taoyr.blackjack.policy.PolicyAdapter;
import com.taoyr.blackjack.policy.PolicyImpl;

/**
 * Basic data structure for a player(dealer) play games in a casino to describe
 * his/her information.
 * 
 * Player is the leading role, he got a wide variety of options to make, like he
 * can hit, stand, double or do something else according to the Policy he
 * complied with.
 * 
 * However dealer is the supporting role, he got far less options to make,
 * basically he just deal the cards to everyone, which in our case is taken over
 * by a Casino instance, but still he can draw cards. Good news is that people
 * in a casino take turns to be a dealer.
 */
public class Player {

	public static final int PLAYER_TYPE_NORMAL = 1;
	public static final int PLAYER_TYPE_DEALER = 2;

	public ArrayList<Card> cards = new ArrayList<>(5); // 10 by default, but 5 is sufficient for us
	public int totalMoney = 0;
	public int betInBox = 0;
	public int id = -1;
	public String name = "anonymous";
	public int status = IPolicy.STATUS_STAND_BY;
	public int type = PLAYER_TYPE_NORMAL;
	public String result = "TBD";
	// Protect the important member variable for security sake.
	private IPolicy policyImpl = new PolicyImpl();
	private static AtomicInteger idGenerator = new AtomicInteger(0);

	public Player() {
		this.id = generateId();
	}

	public Player(int totalMoney, String name, int type) {
		this.totalMoney = totalMoney;
		this.name = name;
		this.type = type;
		this.id = generateId();
	}

	public int getTotalValue() {
		int value = 0;
		for (Card card : cards) {
			value += card.value;
		}
		return value;
	}

	public int getCardsNumber() {
		return cards.size();
	}

	/**
	 * Thrown money in the betting box.
	 */
	public boolean thrownBet(int money) {
		if (totalMoney >= money) {
			totalMoney -= money;
			betInBox += money;
			return true;
		} else {
			// Running out of cash.
			return false;
		}
	}

	public void reduceBet(int money) {
		totalMoney += money;
		betInBox -= money;
	}
	
	public void winMoney(Player player) {
		if (type == PLAYER_TYPE_NORMAL && player.type == PLAYER_TYPE_DEALER) {
			totalMoney += betInBox * 2;
			betInBox = 0;
			player.totalMoney -= betInBox;
		} else if (type == PLAYER_TYPE_DEALER && player.type == PLAYER_TYPE_NORMAL) {
			totalMoney += player.betInBox;
			player.betInBox = 0;
		}
		result = "Win";
		player.result = "Lose";
	}
	
	public void endTurn() {
		status = IPolicy.STATUS_OUT;
		if ("TBD".equals(result)) {
			result = "Push";
		}
		showCards();
	}
	
	public void showCards() {
		for (Card card : cards) {
			if (card.visible == false) {
				card.visible = true;
			}
		}
	}
	
	public boolean startTurn(int bet) {
		if (!thrownBet(bet)) {
		    status = IPolicy.STATUS_OUT;
			return false;
		}
		status = IPolicy.STATUS_STAND_BY;
		return true;
	}
	
	private static int generateId() {
		return idGenerator.getAndIncrement();
	}

	/** Methods implemented by the IPolicy instance */
	public void hit(Card card) {
		policyImpl.hit(this, card);
	}

	public void stand() {
		policyImpl.stand(this);
	}

	public void doubleWager(Card card) {
		policyImpl.doubleWager(this, card);
	}

	public void surrender() {
		policyImpl.surrender(this);
	}

	public void split() {
		policyImpl.split(this);
	}

	public void insurance() {
		policyImpl.insurance(this);
	}

	public void draw(Card card) {
		policyImpl.draw(this, card);
	}
	
	public void check(Player player) {
		policyImpl.check(this, player);
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		if (type == PLAYER_TYPE_DEALER) {
			str.append("dealer(" + name+ ")\n");
		} else {
			str.append("player(" + name+ ")\n");
		}
		str.append("\t\t id: " + id + "\n");
		str.append("\t\t totalMoney: " + totalMoney + "\n");
		str.append("\t\t betInBox: " + betInBox + "\n");
		str.append("\t\t status: " + getStatusDescription() + "\n");
		str.append("\t\t cards: " + cards + "\n");
		str.append("\t\t cardsNumber: " + getCardsNumber() + "\n");
		str.append("\t\t totalValue: " + getTotalValue() + "\n");
		str.append("\t\t winLose: " + getWinLose() + "\n");
		if (status == IPolicy.STATUS_OUT) {
			str.append("\t\t winLose: " + result + "\n");
		} else {
			str.append("\t\t winLose: TBD \n");
		}
		return str.toString();
	}
	
	public String getWinLose() {
	    if (status == IPolicy.STATUS_OUT) {
	        return result;
        } else {
            return "TBD";
        }
	}
	
	public String getStatusDescription() {
		switch (status) {
		case IPolicy.STATUS_BLACKJACK:
			return "blackjack";
		case IPolicy.STATUS_BUST:
			return "bust";
		case IPolicy.STATUS_END_TURN:
			return "end turn";
		case IPolicy.STATUS_HIGH_FIVE:
			return "high five";
		case IPolicy.STATUS_OUT:
			return "out";
		case IPolicy.STATUS_STAND_BY:
			return "standby";
		default:
			return "undefined";
		}
	}
}
