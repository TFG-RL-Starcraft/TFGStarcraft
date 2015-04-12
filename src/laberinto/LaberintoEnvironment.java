package laberinto;

import laberinto.actions.LaberintoActionManager;
import q_learning.Action;
import q_learning.Environment;
import q_learning.State;

public class LaberintoEnvironment implements Environment{

	private int ancho, alto;
	private LaberintoState init_state, init_lastState;
	private LaberintoState lastState;
	private State previousState;
	private Action previousAction;	

	public LaberintoEnvironment(int ancho, int alto, LaberintoState state, LaberintoState lastState) {
		this.ancho = ancho;
		this.alto = alto;
		this.init_state = state;
		this.init_lastState = lastState;
		this.lastState = lastState;
		this.previousState = null;
		this.previousAction = null;
	}
	
	@Override
	public int numStates() {
		return ancho * alto;
	}

	@Override
	public int numActions() {
		return new LaberintoActionManager().getNumActions();
	}

	@Override
	public void execute(Action action) {
		
		// Update the previous state and action before modifying the current state
		this.previousState = state();
		this.previousAction = action;
		
		action.configureContext();
		action.execute();
	}

	@Override
	public State state() {
		return PresenterLaberinto.getInstance().getGame().getEstadoActual();
	}
	
	@Override
	public State previousState() {
		return previousState;
	}
	
	@Override
	public Action previousAction() {
		return previousAction;
	}

	@Override
	public boolean isFinalState() {
		return state().getValue() == lastState.getValue();
	}

	@Override
	public boolean stateHasChanged() {		
		return true;
	}
	
	@Override
	public double getReward(State state) {
		
		double reward = 0; //this value could be 0.001 or very small values
			
		// Here you must enter all the rewards of learning
		
		if(isFinalState()) { //if the unit reaches the goal
			reward = 1000;
		} 
		
		return reward;
	}
	
	@Override
	public void reset() {
		this.previousState = null;
		this.previousAction = null;
		PresenterLaberinto.getInstance().getGame().setEstadoActual(init_state);
		this.lastState = this.init_lastState;
		PresenterLaberinto.getInstance().getGame().setTerminado(true);
	}

	
}
