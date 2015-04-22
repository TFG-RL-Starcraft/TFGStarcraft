package laberinto;

import java.util.ArrayList;

import constants.Constants;
import laberinto.actions.LaberintoActionManager;
import q_learning.Action;
import q_learning.Environment;
import q_learning.State;

public class LaberintoEnvironment implements Environment {

	private int tableroVisitas[][];

	private int ancho, alto;
	private LaberintoState init_state, init_lastState;
	private LaberintoState lastState;
	private State previousState;
	private Action previousAction;
	private ArrayList<Integer> listaEstadosMuerto;

	private boolean visitState[][];

	public LaberintoEnvironment(int ancho, int alto, LaberintoState state,LaberintoState lastState, int[][] tableroVisitas,ArrayList<Integer> listaEstadosMuerto) {
		this.ancho = ancho;
		this.alto = alto;
		this.init_state = state;
		this.init_lastState = lastState;
		this.lastState = lastState;
		this.previousState = null;
		this.previousAction = null;
		this.listaEstadosMuerto = listaEstadosMuerto;

		this.tableroVisitas = tableroVisitas;
		this.visitState = new boolean[alto][ancho];
		for (int i = 0; i < alto; i++) {
			for (int j = 0; j < ancho; j++) {
				this.tableroVisitas[i][j] = 0;
				this.visitState[i][j] = false;
			}
		}
		
		Constants.NUM_PASOS = ancho * alto;
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
		updateNumberOfVisitsTable();

		// Update the previous state and action before modifying the current state
		this.previousState = state();
		this.previousAction = action;

		action.configureContext();
		action.execute();
	}

	private void updateNumberOfVisitsTable() {
		State s = state();
		int y = (int) (s.getValue() / alto);
		int x = s.getValue() % ancho;
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

		for (int i = 0; i < alto; i++) {
			for (int j = 0; j < ancho; j++) {
				this.visitState[i][j] = false;
			}
		}
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
		int actualY = newState / ancho;
		int actualX = newState % ancho;

		int futureY = lastState.getValue() / ancho;
		int futureX = lastState.getValue() % ancho;
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
		int actualY = state / ancho;
		int actualX = state % ancho;
		return this.visitState[actualX][actualY];
	}

	/**
	 * Policy 3 function
	 * @param state => state where the player is Mark the "state" as visited
	 */
	private void markAsVisit(int state) {
		int actualY = state / ancho;
		int actualX = state % ancho;
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
						reward = Constants.REWARD_KEEP_VALUE
								+ (Constants.GAMMA * 4);
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