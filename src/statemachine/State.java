package statemachine;

import util.LogMan;

public abstract class State {
    
    CasinoStateMachine stateMachine;
    
    public State(CasinoStateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }
    
    public void enter() {
        LogMan.logDebug(getClass().getSimpleName() + ": enter()");
    }

    public void exist() {
        LogMan.logDebug(getClass().getSimpleName() + ": exist()");
    }

    public abstract void execute();
}