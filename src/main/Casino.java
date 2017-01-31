package main;

import prop.Decks;
import statemachine.CasinoStateMachine;
import statemachine.InitialState;
import util.LogMan;

public class Casino {

	public Decks mDecks;
	public Group mGroup;
	public CasinoStateMachine mStateMachine;

	public void startBusiness() {
		mStateMachine = new CasinoStateMachine(this);
		mStateMachine.setInitialState(new InitialState(mStateMachine));
		// Time consuming job is handled in a working thread.
		new Thread(new Runnable() {
			@Override
			public void run() {
				mStateMachine.process();
			}
		}).start();
	}

	public void printPlayersInfo() {
		LogMan.logCrutial(mGroup.toString());
	}
}
