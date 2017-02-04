package com.taoyr.blackjack.gameworld;

import java.util.ArrayList;

import com.taoyr.blackjack.policy.IPolicy;
import com.taoyr.blackjack.util.Logger;

/**
 * Could be several groups play at the same time in a casino.
 */
public class Group {
	public int set = 1;
	public int round = 0;
	public Player dealer;
	public Player currentPlayer;
	public ArrayList<Player> players = new ArrayList<>();

	private int dealerIndex = 0;

	private static final String[] NAMES = { "Richard", "Tom", "Jerry", "Mayun", "Obama" };

	/**
	 * Players coming into the house, we can get started.
	 */
	public void setPlayers(int num) {
		if (num <= 1 || num > 5) {
			Logger.logDebug("the specified number should greater than 1 and no more than 5");
			return;
		}
		players = new ArrayList<>(num);
		dealerIndex = 0;
		// Dealer is a rich guy.
		for (int i = 0; i < num; i++) {
			// The first player is initialized as the dealer.
			if (i == dealerIndex) {
				dealer = new Player(100, NAMES[i], Player.PLAYER_TYPE_DEALER);
				players.add(dealer);
			} else if (i == dealerIndex + 1) { // dealer's right hand
				currentPlayer = new Player(100, NAMES[i], Player.PLAYER_TYPE_PLAYER);
				players.add(currentPlayer);
			} else {
				players.add(new Player(100, NAMES[i], Player.PLAYER_TYPE_PLAYER));
			}
		}
	}

	public int getPlayersNumber() {
		return players.size();
	}

    public int getMinBetInBettingBox() {
        int total = 0;
        for (Player player : getPlayersInOrder()) {
            if (player.status != IPolicy.STATUS_OUT) {
                total += IPolicy.BET_MONEY_BOTTOM;
            }
        }
        return total;
    }

	public int getTotalBetInBettingBox() {
	    int total = 0;
	    for (Player player : getPlayersInOrder()) {
	        total += player.betInBox;
        }
	    return total;
	}

    public int getTotalMoneyInPool() {
        int total = 0;
        for (Player player : getPlayersInOrder()) {
            total += player.betInBox;
            total += player.totalMoney;
        }
        total += dealer.totalMoney;
        return total;
    }

    public Player getWinner() {
        Player playerStandBy = null;
        for (Player player : players) {
            if (player.status != IPolicy.STATUS_OUT) {
                if (playerStandBy == null) {
                    playerStandBy = player;
                } else {
                    // Still got two active players.
                    return null;
                }
            }
        }
        return playerStandBy;
    }

	public void shiftDealer() {
		int originalIndex = dealerIndex;

		// Find a round of circle to get a qualified dealer.
		// The right hand player will be the one if he got enough money.
		// Or the dealer would pass on to the next one until we get a qualified player.
		// The worst chance is that we go a circle back to the dealer himself.
		for (int i = 0; i < getPlayersNumber() - 1; i++) {
		    if (dealerIndex < getPlayersNumber() - 1) {
	            dealerIndex++;
	        } else {
	            dealerIndex = 0;
	        }
		    // Shift roles.
	        //if (originalIndex > getPlayersNumber()) { // In case player has been removed
	        //  originalIndex = 0;
	        //}
		    if (isDealerQualified(players.get(dealerIndex))) {
	            players.get(originalIndex).type = Player.PLAYER_TYPE_PLAYER;
	            players.get(dealerIndex).type = Player.PLAYER_TYPE_DEALER;
	            dealer = players.get(dealerIndex);
	            break;
	        };
        }
	}
	
	public boolean isDealerQualified(Player player) {
	    if (player.totalMoney < getMinBetInBettingBox()) {
            return false;
        }
	    return true;
	}

	public void nextRound() {
		for (Player player : players) {
		    // Reset cards.
			player.cards = new ArrayList<>(5);
			// Retrieve bet.(NewRoundState -> NewRoundState)
			player.retrieveBet();
		}
		// removeBankruptPlayers();

		if (round < IPolicy.ROUNDS_EACH_SET) {
			round++;
		} else {
			round = 1;
			set++;
			shiftDealer();
		}
	}

	private void removeBankruptPlayers() {
	 // Check to see if anyone should be kicked out for bankrupt.
        ArrayList<Integer> ids = new ArrayList<>(); // Avoid ConcurrentModificationException.
        boolean isDealerRemoved = false;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).totalMoney < IPolicy.BET_MONEY_BOTTOM) {
                ids.add(i);
                if (!isDealerRemoved && i == dealerIndex) {
                    isDealerRemoved = true;
                }
            }
        }
        for (Integer i : ids) {
            players.remove(i);
        }
        if (isDealerRemoved) shiftDealer();
	}

	/**
	 * Get a player list sorted from the dealer's left to far right.
	 */
	public ArrayList<Player> getPlayersInOrder() {
		ArrayList<Player> list = new ArrayList<>();
		int index = dealerIndex - 1; // left hand
		while (index != dealerIndex) {
			if (index < 0) {
				index = getPlayersNumber() - 1;
				if (index == dealerIndex) {
					break; // dead loop for case(dealerIndex=getPlayersNumber()-1)
				}
			}
			list.add(players.get(index));
			index--;
		}
		return list;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("group info\n");
		str.append("\t set: " + set + "\n");
		str.append("\t round: " + round + "\n");
		str.append("\t dealer: " + dealer + "\n");
		str.append("\t active player: " + currentPlayer + "\n");
		for (Player player : getPlayersInOrder()) {
			if (player != currentPlayer) {
				str.append("\t ai player: " + player + "\n");
			}
		}
		return str.toString();
	}
}
