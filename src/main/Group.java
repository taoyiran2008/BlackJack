package main;

import java.util.ArrayList;

import policy.IPolicy;
import util.LogMan;

/**
 * Could be several groups play at the same time in a casino.
 */
public class Group {
	// Players take turns to be the dealer, after each set the role of dealer
	// shifted to it's right-hand player.
	public static final int ROUNDS_EACH_SET = 3;
	public int set = 0;
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
			LogMan.logCrutial("the specified number should greater than 1 and no more than 5");
			return;
		}
		players = new ArrayList<>(num);
		dealerIndex = 0;
		// Dealer is a rich guy.
		for (int i = 0; i < num; i++) {
			// The first player is initialized as the dealer.
			int type = ((i == dealerIndex) ? Player.PLAYER_TYPE_DEALER
					: Player.PLAYER_TYPE_NORMAL);
			if (i == dealerIndex) {
				dealer = new Player(1000, NAMES[i], type);
				players.add(dealer);
			} else if (i == dealerIndex + 1) {
				currentPlayer = new Player(100, NAMES[i], type);
				players.add(currentPlayer);
			} else {
				players.add(new Player(100, NAMES[i], type));
			}
		}
	}

	public int getPlayersNumber() {
		return players.size();
	}

	private void shiftDealer() {
		int originalIndex = dealerIndex;

		if (dealerIndex < getPlayersNumber() - 1) {
			dealerIndex++;
		} else {
			dealerIndex = 0;
		}
		// Shift roles.
		if (originalIndex > getPlayersNumber()) { // In case player has been removed
			originalIndex = 0;
		}
		players.get(originalIndex).type = Player.PLAYER_TYPE_NORMAL;
		players.get(dealerIndex).type = Player.PLAYER_TYPE_DEALER;
		dealer = players.get(dealerIndex);
	}

	public void nextRound() {
		// Reset cards.
		for (Player player : players) {
			player.cards = new ArrayList<>(5);
		}
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
		if (isDealerRemoved)
			shiftDealer();

		if (round < ROUNDS_EACH_SET) {
			round++;
		} else {
			round = 0;
			set++;
			shiftDealer();
		}
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
