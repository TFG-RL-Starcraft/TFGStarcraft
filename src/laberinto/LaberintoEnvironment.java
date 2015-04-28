package laberinto;

import java.util.ArrayList;

import laberinto.actions.LaberintoActionManager;
import q_learning.Action;
import q_learning.Environment;
import q_learning.State;

public class LaberintoEnvironment implements Environment{
	
	public enum Policies //Enum with the possible reward policies
	{
	    BASIC, 
	    EUCLIDEAN_DISTANCE, 
	    LESS_STEPS, 
	    EUCLIDEAN_DISTANCE_AND_LESS_STEPS,
	    NOT_REPEATED_STATES
	}
	
	private int tableroVisitas[][];
	private boolean visitState[][];
	private int ancho, alto;
	private LaberintoState init_state, init_lastState;
	private LaberintoState lastState;
	private State previousState;
	private Action previousAction;
	private ArrayList<Integer> listaEstadosMuerto;
	private double won_reward, lost_reward;
	private int max_iter;
	private Policies policy_used;
	
	private int default_reward = 0;
	

	public LaberintoEnvironment(int ancho, int alto, LaberintoState state, LaberintoState lastState, int[][] tableroVisitas, ArrayList<Integer> listaEstadosMuerto, double won_reward, double lost_reward, int max_iter, Policies policy_used) {
		this.ancho = ancho;
		this.alto = alto;
		this.init_state = state;
		this.init_lastState = lastState;
		this.lastState = lastState;
		this.previousState = null;
		this.previousAction = null;
		this.listaEstadosMuerto = listaEstadosMuerto;
		this.won_reward = won_reward;
		this.lost_reward = lost_reward;
		this.max_iter = max_iter;
		this.policy_used = policy_used;
		
		this.tableroVisitas = tableroVisitas;
		for(int i = 0; i < tableroVisitas.length; i++){
			for(int j = 0; j < tableroVisitas[i].length; j++){
				tableroVisitas[i][j] = 0;
			}
		}
		
		this.visitState = new boolean[alto][ancho];
		for(int i = 0; i < alto; i++){
			for(int j = 0; j < ancho; j++){
				this.visitState[i][j] = false;
			}
		}
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

		// First update the table with the number of visits
		updateNumberOfVisitsTable(state().getValue());

		// Update the previous state and action before modifying the current state
		this.previousState = state();
		this.previousAction = action;

		action.configureContext();
		action.execute();
	}
	
	private void updateNumberOfVisitsTable(int s){
		int x = s % ancho;
		int y = (int)(s / ancho);
		
		tableroVisitas[x][y]++;
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
		return hasLost() || hasWon();
	}

	private boolean hasWon() {
		return state().getValue() == lastState.getValue();
	}

	private boolean hasLost() {
		return listaEstadosMuerto.contains(state().getValue());
	}

	@Override
	public boolean stateHasChanged() {
		return true;
	}

	@Override
	public void reset() {
		this.previousState = null;
		this.previousAction = null;
		PresenterLaberinto.getInstance().getGame().setEstadoActual(init_state);
		this.lastState = this.init_lastState;
		PresenterLaberinto.getInstance().getGame().setTerminado(true);
	}
	
	@Override
	public double getReward(State state) {
		
		double reward = this.default_reward; //default = 0
		// Here you must enter all the rewards of learning
		
		switch (this.policy_used) {
	    	case BASIC:
	    		reward = basicPolicy();	
	    		break;
	    		
	    	case EUCLIDEAN_DISTANCE:
	    		reward = euclideanDistancePolicy(state);
	    		break;
	    		
	    	case LESS_STEPS:
	    		reward = lessStepsPolicy();
	    		break;
	    		
	    	case EUCLIDEAN_DISTANCE_AND_LESS_STEPS:
	    		//4. Politicas 2 y 3 unidas
	    		reward = euclideanDistanceLessStepsPolicy(state);
	    		break;
	    		
	    	case NOT_REPEATED_STATES:
	    		reward = notRepeatedStatesPolicy(state);
	    		break;
	    		
		}
		
		return reward;
	}	
	
	// ---------------------- Methods that specify how the different policies work ------------------

	/**
	 * 1. Basic Policy
	 * gets a reward when you win and when you lose
	 */
	private double basicPolicy() {
		double reward = this.default_reward;
		if(hasLost()) { //if the unit doesn't exist (lost game)
			reward = this.lost_reward;
		} else if(hasWon()) { //if the unit reaches the goal
			reward = this.won_reward;
		}
		return reward;
	}
	
	/**
	 * 2. Euclidean Distance Policy
	 * gets a reward proportional to how close it is to the goal, only if it approaches
	 */
	private double euclideanDistancePolicy(State state) {
		double reward = this.default_reward;
		if(hasLost()) { //if the unit doesn't exist (lost game)
			reward = this.lost_reward;
		} else if(hasWon()) { //if the unit reaches the goal
			reward = this.won_reward;
		} else {
			reward = euclideanReward(state.getValue());
		}
		return reward;
	}
	
	/**
	 * 3. Less Steps Policy
	 * gets a reward proportional to the number of steps used for reaching the goal
	 */
	private double lessStepsPolicy() {
		double reward = this.default_reward;
		if(hasLost()) { //if the unit doesn't exist (lost game)
			reward = this.lost_reward;
		} else if(hasWon()) { //if the unit reaches the goal
			reward = stepDependantReward();
		}
		return reward;
	}
	
	/**
	 * 4. Euclidean Distance & Less Steps Policy
	 * it's is the union of policy 2 and 3
	 */
	private double euclideanDistanceLessStepsPolicy(State state) {
		double reward = this.default_reward;
		if(hasLost()) { //if the unit doesn't exist (lost game)
			reward = this.lost_reward;
		} else if(hasWon()) { //if the unit reaches the goal
			reward = stepDependantReward();
		} else {
			reward = euclideanReward(state.getValue());
		}
		return reward;
	}
	
	/**
	 * 5. Not Repeated Steps Policy
	 * gets a reward depending on whether the State has previously visited,
	 * trying to avoid repeated states (loops)
	 */
	private double notRepeatedStatesPolicy(State state) {
		double reward = this.default_reward;
		if(hasLost()) { //if the unit doesn't exist (lost game)
			reward = this.lost_reward;
		} else if(hasWon()) { //if the unit reaches the goal
			reward = this.won_reward;
		} else {
			if (!isRepeated(state.getValue())) {
				reward = this.default_reward + (won_reward/25000.0);
			} else {
				markAsVisited(state.getValue());
			}
		}
		return reward;
	}	

	// ---------------------- Auxiliary functions for the different policies ------------------
	
	private double stepDependantReward() {
		double A = (this.won_reward - 10.0) / Math.pow(this.max_iter, 2);		
		double reward = A * Math.pow(PresenterLaberinto.getInstance().getNumIter(), 2) + this.won_reward;		
		return reward;
	}

	private double euclideanReward(int newState) {
		double reward = 1.0;
		
		if(previousState != null) {
			double previousDist = euclideanDist(previousState().getValue(), lastState.getValue());
			double currentDist = euclideanDist(newState, lastState.getValue());
			double totalDist = euclideanDist(init_state.getValue(), lastState.getValue());
			
			if(previousDist > currentDist) { // if it's closer -> the proportional reward of 1.0 (more reward when more approaches)
				reward = reward - reward*(currentDist-totalDist);
			}else{ // if it's further -> 0.0 reward
				reward = 0.0;
			}
		}

		return reward;
	}
	
	private double euclideanDist(int state1, int state2){
		int x1 = state1 % ancho;
		int y1 = (int)(state1 /ancho);
	
		int x2 = state2 % ancho;
		int y2 = (int)(state2 / ancho);
			
		int x_dist = Math.abs(x1 - x2);
		int y_dist = Math.abs(y1 - y2);
		
		return Math.sqrt((Math.pow(x_dist, 2) + Math.pow(y_dist, 2)));
	}

	
	/**
	 * @param state => state where the player is
	 * @return if the state has been visited yet
	 */
	private boolean isRepeated(int state) {
		int actualX = state % ancho;
		int actualY = state / ancho;		
		return this.visitState[actualX][actualY];
	}

	/**
	 * @param state => state where the player is Mark the "state" as visited
	 */
	private void markAsVisited(int state) {
		int actualX = state % ancho;
		int actualY = state / ancho;		
		this.visitState[actualX][actualY] = true;
	}
}