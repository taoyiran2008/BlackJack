package com.taoyr.blackjack.policy;

import com.taoyr.blackjack.gameworld.Card;
import com.taoyr.blackjack.gameworld.Decks;
import com.taoyr.blackjack.gameworld.Player;

/**
 * Currently not support spit, insurance. TODO: Synchronize for concurrency
 * sake.
 */
public class PolicyImpl extends PolicyAdapter {

	/** Policy for a player */

	@Override
	public void hit(Player player, Card card) {
		addCard(player, card);
	}

	@Override
	public void stand(Player player) {
		// Do nothing just end the turn and wait for the upcoming result.
		player.status = STATUS_END_TURN;
	}

	@Override
	public boolean doubleWager(Player player, Card card) {
		// Double the bet.
		if (!player.thrownBet(player.betInBox)) {
			return false;
		} else {
			// Take a single card and finish.
			player.status = STATUS_END_TURN;
			// We took a shot for big money, still may bust out.
			addCard(player, card);
			return true;
		}
	}

	@Override
	public void surrender(Player player) {
		// Give up a half-bet and retire from the game like we are busted.
		player.status = STATUS_BUST;
		player.reduceBet(player.betInBox / 2);
	}

	private void addCard(Player player, Card card) {
		player.cards.add(card);
		// If we got an ACE, firstly max it's value by using value 11, if bust, fall back to
		// use value 1 to be failsafe.
		if (player.getTotalValue() + card.value > IPolicy.BLACK_JACK_VALUE) {
			for (Card c : player.cards) {
				if (Decks.ACE_NAME.equals(c.displayName)) {
					c.value = Decks.HARD_HAND_VALUE;
				}
			}
		}
		if (player.getTotalValue() < BLACK_JACK_VALUE) {
			if (player.getCardsNumber() == HIGH_FIVE_NUMBER) {
				player.status = STATUS_HIGH_FIVE;
			} else {
				player.status = STATUS_STAND_BY;
			}
		} else if (player.getTotalValue() == BLACK_JACK_VALUE) {
			player.status = STATUS_BLACKJACK;
		} else {
			player.status = STATUS_BUST;
		}
	}

	/** Policy for a dealer */

	private static final int LIMITED_VALUE = 17;

	@Override
	public void draw(Player player, Card card) {
		addCardForDealer(player, card);
	}

	private void addCardForDealer(Player player, Card card) {
		player.cards.add(card);
		int totalValue = player.getTotalValue();
		if (player.getTotalValue() < BLACK_JACK_VALUE) {
			if (player.getCardsNumber() == HIGH_FIVE_NUMBER) {
				player.status = STATUS_HIGH_FIVE;
			}
			if (totalValue < LIMITED_VALUE) {
				player.status = STATUS_STAND_BY; // still hungry
			} else {
				player.status = STATUS_END_TURN; // I'm full
			}
		} else if (player.getTotalValue() == BLACK_JACK_VALUE) {
			player.status = STATUS_BLACKJACK;
		} else {
			player.status = STATUS_BUST;
		}
	}

	/** Policy for both player and dealer */

	private static final double RATIO_FOR_BLACKJACK = 2; // 1.2
	private static final double RATIO_FOR_HIGH_FIVE = 3; // 1.5

	@Override
	public void check(Player firstPlayer, Player secondPlayer) {
		if (firstPlayer.status == IPolicy.STATUS_STAND_BY
				|| firstPlayer.status == IPolicy.STATUS_OUT) {
			// Not in checking state yet.
			return;
		}
		// Only two cases: 1) check(player, dealer) in PlayerRoundState since dealer's cards can
		// not be seen. 2) check(dealer, player) in DealerRoundState.
		if (firstPlayer.type == Player.PLAYER_TYPE_NORMAL
				&& secondPlayer.type == Player.PLAYER_TYPE_DEALER) {
			if (firstPlayer.status == IPolicy.STATUS_BLACKJACK) {
				// Win bonus 2x.
				int moneyDelta = (int) ((RATIO_FOR_BLACKJACK - 1) * firstPlayer.betInBox);
				firstPlayer.thrownBet(moneyDelta);
				firstPlayer.winMoney(secondPlayer);
				firstPlayer.endTurn();
			} else if (firstPlayer.status == IPolicy.STATUS_HIGH_FIVE) {
				// Win bonus 3x.
				int moneyDelta = (int) ((RATIO_FOR_HIGH_FIVE - 1) * firstPlayer.betInBox);
				firstPlayer.thrownBet(moneyDelta);
				firstPlayer.winMoney(secondPlayer);
				firstPlayer.endTurn();
			} else if (firstPlayer.status == IPolicy.STATUS_BUST) {
				// This is why endTurn should not integrated in winMoney.
				secondPlayer.winMoney(firstPlayer);
				firstPlayer.endTurn();
			} /*else if (firstPlayer.status == IPolicy.STATUS_END_TURN) {
				if (firstPlayer.getTotalValue() > secondPlayer.getTotalValue()) {
					firstPlayer.winMoney(secondPlayer);
				} else if (firstPlayer.getTotalValue() < secondPlayer.getTotalValue()) {
					secondPlayer.winMoney(firstPlayer);
				}
			}*/
		} else if (firstPlayer.type == Player.PLAYER_TYPE_DEALER
				&& secondPlayer.type == Player.PLAYER_TYPE_NORMAL) {
			if (firstPlayer.status == IPolicy.STATUS_BLACKJACK) {
				// Win bonus 2x.
				int moneyDelta = (int) ((RATIO_FOR_BLACKJACK - 1) * secondPlayer.betInBox);
				secondPlayer.thrownBet(moneyDelta);
				firstPlayer.winMoney(secondPlayer);
				firstPlayer.endTurn();
				secondPlayer.endTurn();
			} else if (firstPlayer.status == IPolicy.STATUS_HIGH_FIVE) {
				// Win bonus 3x.
				int moneyDelta = (int) ((RATIO_FOR_HIGH_FIVE - 1) * firstPlayer.betInBox);
				secondPlayer.thrownBet(moneyDelta);
				firstPlayer.winMoney(secondPlayer);
				firstPlayer.endTurn();
				secondPlayer.endTurn();
			} else if (firstPlayer.status == IPolicy.STATUS_BUST) {
				secondPlayer.winMoney(firstPlayer);
				firstPlayer.endTurn();
				secondPlayer.endTurn();
			} else if (firstPlayer.status == IPolicy.STATUS_END_TURN) {
				if (firstPlayer.getTotalValue() > secondPlayer.getTotalValue()) {
					firstPlayer.winMoney(secondPlayer);
				} else if (firstPlayer.getTotalValue() < secondPlayer.getTotalValue()) {
					secondPlayer.winMoney(firstPlayer);
				} else {
					// Pull.
				}
				firstPlayer.endTurn();
				secondPlayer.endTurn();
			}
		}
	}
}
