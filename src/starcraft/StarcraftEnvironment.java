package starcraft;

import java.util.ArrayList;

import laberinto.PresenterLaberinto;
import constants.Constants;
import q_learning.Action;
import q_learning.Environment;
import q_learning.State;
import bwapi.Game;
import bwapi.Unit;

public class StarcraftEnvironment implements Environment{	
	private Game game;
	private Unit unit;
	private State previousState;
	private Action previousAction;
	
	//we don not need to save the current state, because we get it in runtime
	private ArrayList<Integer> finalState; //array with the hash values of all possible final states 
	
	private int tableroVisitas[][];
	private boolean visitState[][];
	
	public StarcraftEnvironment(Game game, Unit unit, ArrayList<State> finalStates) {
		this.game = game;
		this.unit = unit;
		this.previousState = null;
		this.previousAction = null;
		this.finalState = new ArrayList<Integer>();
		for(State sta : finalStates) {
			finalState.add(sta.getValue());
		}		
		
		this.visitState = new boolean[game.mapHeight()][game.mapWidth()];
		for (int i = 0; i < game.mapHeight(); i++) {
			for (int j = 0; j < game.mapWidth(); j++) {
				this.tableroVisitas[i][j] = 0;
				this.visitState[i][j] = false;
			}
		}
		
		Constants.NUM_PASOS = game.mapWidth() * game.mapHeight();
		//vTable = new VisitedStateTable(numStates());
	}

	@Override
	public int numStates() {
		return game.mapHeight() * game.mapWidth();
	}

	@Override
	public int numActions() {
		return Presenter.getInstance().getStarcraftActionManager().getNumActions();
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
		if(unit.exists()){
			updateNumberOfVisitsTable();
			return new StarcraftState((int)unit.getPosition().getX()/Presenter.getInstance().getBoxSize(), 
					(int)unit.getPosition().getY()/Presenter.getInstance().getBoxSize(), game.mapWidth(), game.mapHeight());
		}else{
			return null;
		}
	}
	
	private void updateNumberOfVisitsTable() {
		State s = state();
		int y = s.getValue() / game.mapWidth();
		int x = s.getValue() % game.mapWidth();
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
		return !Presenter.getInstance().getUnit().exists();
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
		//vTable.clear();
		game.restartGame();
	}	
	
	@Override
	public double getReward(State state) {
		// If the current distance to the final is bigger than the future
		// increase the reward
		double reward = Constants.REWARD_KEEP_VALUE;

		switch (Constants.POLICIES[Constants.POLITICA]) {
		case "Politica 0":
			reward = policy0();
			break;

		case "Politica 1":
			reward = policy1(state);
			break;

		case "Politica 2":
			reward = policy2();
			break;

		case "Politica 3":
			reward = policy3();
			break;

		case "Politica 4":
			reward = policy4(state);
			break;

		default:
			System.err.println("Out of index");
			break;
		}

		return reward;
	}

	// ---------------------- Methods that specify how the different policies work ------------------

	/**
	 * Basic policy
	 */
	private double policy0() {
		double reward = Constants.REWARD_KEEP_VALUE;
		if (hasLost()) { // if the unit doesn't exist (lost game)
			reward = Constants.REWARD_LOSE;
		} else if (hasWon()) { // if the unit reaches the goal
			reward = Constants.REWARD_WON;
		}
		return reward;
	}

	/**
	 * Euclidian distance policy
	 */
	private double policy1(State state) {
		double reward = Constants.REWARD_KEEP_VALUE;
		if (hasLost()) { // if the unit doesn't exist (lost game)
			reward = Constants.REWARD_LOSE;
		} else if (hasWon()) { // if the unit reaches the goal
			reward = Constants.REWARD_WON;
		} else {
			reward = getCloser(state.getValue());
		}
		return reward;
	}

	/**
	 * Victory function policy
	 */
	private double policy2() {
		double reward = Constants.REWARD_KEEP_VALUE;
		if (hasLost()) { // if the unit doesn't exist (lost game)
			reward = Constants.REWARD_LOSE;
		} else if (hasWon()) { // if the unit reaches the goal
			reward = functionVictory();
		}
		return reward;
	}

	/**
	 * Repeated states policy
	 */
	private double policy3() {
		double reward = Constants.REWARD_KEEP_VALUE;
		if (hasLost()) { // if the unit doesn't exist (lost game)
			reward = Constants.REWARD_LOSE;
		} else if (hasWon()) { // if the unit reaches the goal
			reward = Constants.REWARD_WON;
		} else {
			reward = repeatedState();
		}
		return reward;
	}

	/**
	 * Union of all policies
	 */
	private double policy4(State state) {
		double reward = Constants.REWARD_KEEP_VALUE;
		if (hasLost()) { // if the unit doesn't exist (lost game)
			reward = Constants.REWARD_LOSE;
		} else if (hasWon()) { // if the unit reaches the goal
			reward = functionVictory();
		} else {
			reward = getReward(state.getValue());
		}
		return reward;
	}

	// ---------------------- Private functions for the different policies ------------------

	/**
	 * Policy 1 function
	 * @param newState => state where the player will go
	 * @return reward depending if the player is reaching the goal
	 */
	private double getCloser(int newState) {
		double reward = Constants.REWARD_KEEP_VALUE;

		if (previousState != null) {
			double currentDist = euclideanDist(previousState().getValue());
			double futureDist = euclideanDist(newState);

			if (currentDist != futureDist) {
				if (currentDist > futureDist) {
					reward = Constants.REWARD_KEEP_VALUE
							+ (Constants.GAMMA * 4);
				} else {
					reward = Constants.REWARD_KEEP_VALUE;
				}
			}
		}

		return reward;
	}

	/**
	 * Policy 1
	 * @param newState => state where the player will go
	 * @return distance to the goal
	 */
	private double euclideanDist(int newState) {
		double dist = Double.MAX_VALUE;
		int actualY = newState / game.mapWidth();
		int actualX = newState % game.mapWidth();

		int futureY = previousState.getValue() / game.mapWidth();
		int futureX = previousState.getValue() % game.mapWidth();
		int x1 = Math.abs(actualY - futureY);
		int x2 = Math.abs(actualX - futureX);
		double x = Math.sqrt((Math.pow(x1, 2) + Math.pow(x2, 2)));
		if (x < dist) {
			dist = x;
		}

		return dist;
	}

	/**
	 * Policy 2 function
	 * @return reward in function of the numIter to reach the goal
	 */
	private double functionVictory() {
		double A = (Constants.REWARD_WON - 10.0)/ Math.pow(Constants.NUM_PASOS, 3);
		double reward = A* Math.pow(PresenterLaberinto.getInstance().getNumIter(), 3) + Constants.REWARD_WON;
		return reward;
	}

	/**
	 * Policy 3 function
	 * @return reward in function depending on whether the State has previously visited
	 */
	private double repeatedState() {
		double reward = Constants.REWARD_KEEP_VALUE;
		
		if (previousState != null) {
			if (!isRepeated(previousState().getValue())) {
				reward = Constants.REWARD_KEEP_VALUE + (Constants.GAMMA * 4);
			} else {
				markAsVisit(previousState().getValue());
				reward = Constants.REWARD_REPEATED;
			}
		}
		return reward;
	}

	/**
	 * Policy 3 function
	 * @param state => state where the player is
	 * @return if the previous state has been visited yet
	 */
	private boolean isRepeated(int state) {
		int actualY = state / game.mapWidth();
		int actualX = state % game.mapWidth();
		return this.visitState[actualX][actualY];
	}

	/**
	 * Policy 3 function
	 * @param state => state where the player is Mark the "state" as visited
	 */
	private void markAsVisit(int state) {
		int actualY = state / game.mapWidth();
		int actualX = state % game.mapWidth();
		this.visitState[actualX][actualY] = true;
	}

	/**
	 * Policy 4 function
	 * @param newState => State where the player will move
	 * @return reward in function of the distance to the goal and if the state has previously visited
	 */
	private double getReward(int newState) {
		double reward = Constants.REWARD_KEEP_VALUE;

		if (previousState != null) {
			double currentDist = euclideanDist(previousState().getValue());
			double futureDist = euclideanDist(newState);

			if (currentDist != futureDist) {
				if (!isRepeated(previousState().getValue())) {
					if (currentDist > futureDist) {
						reward = Constants.REWARD_KEEP_VALUE + (Constants.GAMMA * 4);
					} else {
						reward = Constants.REWARD_KEEP_VALUE;
					}
					// reward = 0.0;//------------------------------------->4milenio
				} else {
					markAsVisit(previousState().getValue());
					reward = Constants.REWARD_REPEATED;
				}
			}
		}

		return reward;
	}

}
