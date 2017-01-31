package statemachine;

import java.util.ArrayList;

import main.Group;
import main.Player;
import policy.IPolicy;
import prop.Decks;
import util.CommonUtils;
import util.LogMan;

public class InitialState extends State {

	public InitialState(CasinoStateMachine stateMachine) {
		super(stateMachine);
	}

	public void execute() {
		// Find a set of decks to play.
		stateMachine.mCasino.mDecks = new Decks(); // with(stateMachine.mCasino)
													// {...}
		stateMachine.mCasino.mDecks.shuffle();

		// Seat the players.
		stateMachine.mCasino.mGroup = new Group();
		stateMachine.mCasino.mGroup.setPlayers(4);

		stateMachine.transferTo(new NewRoundState(stateMachine));
	}
}
