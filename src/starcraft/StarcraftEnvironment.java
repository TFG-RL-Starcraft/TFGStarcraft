package starcraft;

import java.util.ArrayList;
import laberinto.PresenterLaberinto;
import q_learning.Action;
import q_learning.Environment;
import q_learning.State;
import bwapi.Game;
import bwapi.Unit;

public class StarcraftEnvironment implements Environment{	
	
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
	private double won_reward, lost_reward;
	private int max_iter;
	private Policies policy_used;
	
	private Game game;
	private Unit unit;
	private State previousState;
	private Action previousAction;
	
	private State init_state;
	private State lastState;
	
	//we don not need to save the current state, because we'll get it in runtime
	private ArrayList<Integer> finalState; //array with the hash values of all possible final states 	
	
	private int default_reward = 0;
	
	public StarcraftEnvironment(Game game, Unit unit, ArrayList<State> finalStates, int[][] tableroVisitas, double won_reward, double lost_reward, int max_iter, Policies policy_used) {
		this.game = game;
		this.unit = unit;
		this.previousState = null;
		this.previousAction = null;
		this.finalState = new ArrayList<Integer>();
		for(State sta : finalStates) {
			finalState.add(sta.getValue());
		}		
		
		this.init_state = state();
		this.lastState = finalStates.get(0);
		
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
		
		this.visitState = new boolean[game.mapHeight()][game.mapWidth()];
		for (int i = 0; i < game.mapHeight(); i++) {
			for (int j = 0; j < game.mapWidth(); j++) {
				this.visitState[i][j] = false;
			}
		}
	}

	@Override
	public int numStates() {
		return game.mapHeight() * game.mapWidth();
	}

	@Override
	public int numActions() {
		return StarcraftPresenter.getInstance().getStarcraftActionManager().getNumActions();
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

	@Override
	public State state() {
		if(unit.exists()){
			return new StarcraftState((int)unit.getPosition().getX()/StarcraftPresenter.getInstance().getBoxSize(), 
					(int)unit.getPosition().getY()/StarcraftPresenter.getInstance().getBoxSize(), game.mapWidth(), game.mapHeight());
		}else{
			return null;
		}
	}
	
	private void updateNumberOfVisitsTable(int s) {
		int x = s % game.mapWidth();
		int y = (int)(s / game.mapWidth());
		
		tableroVisitas[x][y]++;
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
		return finalState.contains(state().getValue());
	}
	
	private boolean hasLost() {
		return !unit.exists();
	}

	@Override
	public boolean stateHasChanged() {		
		return !unit.isMoving();	
	}
	
	@Override
	public void reset() {
		this.previousState = null;
		this.previousAction = null;
		game.pauseGame();
		game.restartGame();
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
	
	private double euclideanDist(int state1, int state2) {
		int x1 = state1 % game.mapWidth();
		int y1 = (int)(state1 /game.mapWidth());
	
		int x2 = state2 % game.mapWidth();
		int y2 = (int)(state2 / game.mapWidth());
			
		int x_dist = Math.abs(x1 - x2);
		int y_dist = Math.abs(y1 - y2);
		
		return Math.sqrt((Math.pow(x_dist, 2) + Math.pow(y_dist, 2)));
	}

	/**
	 * @param state => state where the player is
	 * @return if the state has been visited yet
	 */
	private boolean isRepeated(int state) {
		int actualX = state % game.mapWidth();
		int actualY = state / game.mapWidth();		
		return this.visitState[actualX][actualY];
	}

	/**
	 * @param state => state where the player is Mark the "state" as visited
	 */
	private void markAsVisited(int state) {
		int actualX = state % game.mapWidth();
		int actualY = state / game.mapWidth();		
		this.visitState[actualX][actualY] = true;
	}
}
