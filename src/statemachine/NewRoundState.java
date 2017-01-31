package statemachine;

import java.util.ArrayList;

import main.Group;
import main.Player;
import policy.IPolicy;
import prop.Decks;
import util.CommonUtils;
import util.LogMan;

public class NewRoundState extends State {

	public NewRoundState(CasinoStateMachine stateMachine) {
		super(stateMachine);
	}

	public void execute() {
		// Reset cards.
		stateMachine.mCasino.mDecks.shuffle();
		stateMachine.mCasino.mGroup.nextRound();

		ArrayList<Player> list = stateMachine.mCasino.mGroup.getPlayersInOrder();
		for (Player player : list) {
			if (player == stateMachine.mCasino.mGroup.currentPlayer) {
				// Ask active player to choose the policy to use.
				LogMan.logCrutial("Please throw your money into the betting box");
				int money = CommonUtils.readCommandFromConsole();
				if (!player.startTurn(money)) {
					LogMan.logCrutial("Error: not enough cash! for " + player.name);
					continue;
				}
			} else {
				// TODO: AI support.
				LogMan.logDebug("use default 10RMB for bet as for non-active player");
				if (!player.startTurn(IPolicy.BET_MONEY_BOTTOM)) {
					LogMan.logCrutial("Error: not enough cash! for " + player.name);
					continue;
				}
			}
		}

		stateMachine.transferTo(new FirstRoundState(stateMachine));
	}
}
