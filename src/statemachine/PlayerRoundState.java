package statemachine;

import java.util.ArrayList;

import main.Player;
import policy.IPolicy;
import util.CommonUtils;
import util.LogMan;

public class PlayerRoundState extends State {

	public PlayerRoundState(CasinoStateMachine stateMachine) {
		super(stateMachine);
	}

	public void execute() {
		ArrayList<Player> list = stateMachine.mCasino.mGroup
				.getPlayersInOrder();
		boolean endChoose = true;
		boolean allOut = true;
		for (Player player : list) {
			if (player == stateMachine.mCasino.mGroup.currentPlayer
					&& player.status == IPolicy.STATUS_STAND_BY) {
				// Ask active player to choose the policy to use.
				LogMan.logCrutial(
						"Please choose your command(1-hit, 2-stand, 3-double, 4-surrender): ");
				int cmd = CommonUtils.readCommandFromConsole();
				switch (cmd) {
				case 1:
					player.hit(stateMachine.mCasino.mDecks.deal(true));
					break;
				case 2:
					player.stand();
					break;
				case 3:
					player.doubleWager(stateMachine.mCasino.mDecks.deal(true));
					break;
				case 4:
					player.surrender();
					break;
				default:
					LogMan.logCrutial(
							"you have chosen an invalid command, use default command(hit)");
					player.hit(stateMachine.mCasino.mDecks.deal(true));
					break;
				}
			} else if (player.status == IPolicy.STATUS_STAND_BY) {
				// TODO: AI support.
				LogMan.logDebug("use default command(hit) for non-active player");
				player.hit(stateMachine.mCasino.mDecks.deal(true));
			}
			player.check(stateMachine.mCasino.mGroup.dealer);
			if (player.status == IPolicy.STATUS_STAND_BY) {
				// If players can choose, then no need to figure out allOut.
				endChoose = false;
			} else {
				if (player.status == IPolicy.STATUS_END_TURN) {
					allOut = false;
				}
			}
		}
		LogMan.logCrutial("player round");
		stateMachine.mCasino.printPlayersInfo();
		
		if (endChoose) {
			LogMan.logCrutial("all players end choice");
			if (allOut) {
				LogMan.logCrutial("all players are out");
				stateMachine.transferTo(new NewRoundState(stateMachine));
			} else {
				stateMachine.transferTo(new DealerRoundState(stateMachine));
			}
		} else {
			stateMachine.transferTo(this);
		}
	}
}
