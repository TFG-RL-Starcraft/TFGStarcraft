package laberinto;

import java.util.ArrayList;

import laberinto.actions.LaberintoActionManager;
import q_learning.Action;
import q_learning.Environment;
import q_learning.State;

public class LaberintoEnvironment implements Environment{
	
	private int tableroVisitas[][];
	
	private double MAX_REWARD = 10.0;
	
	private int ancho, alto;
	private LaberintoState init_state, init_lastState;
	private LaberintoState lastState;
	private State previousState;
	private Action previousAction;	
	private ArrayList<Integer> listaEstadosMuerto;

	public LaberintoEnvironment(int ancho, int alto, LaberintoState state, LaberintoState lastState, int[][] tableroVisitas, ArrayList<Integer> listaEstadosMuerto) {
		this.ancho = ancho;
		this.alto = alto;
		this.init_state = state;
		this.init_lastState = lastState;
		this.lastState = lastState;
		this.previousState = null;
		this.previousAction = null;
		this.listaEstadosMuerto = listaEstadosMuerto;
		
		this.tableroVisitas = tableroVisitas;
		for(int i = 0; i < alto; i++){
			for(int j = 0; j < ancho; j++){
				tableroVisitas[i][j] = 0;
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
		updateNumberOfVisitsTable();
		
		// Update the previous state and action before modifying the current state
		this.previousState = state();
		this.previousAction = action;
		
		action.configureContext();
		action.execute();
	}
	
	private void updateNumberOfVisitsTable(){
		State s = state();	
		int y = (int)(s.getValue() /  alto);
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
	public double getReward(State state) {
		//If the current distance to the final is bigger than the future increase the reward
		double reward = 0;

		// Here you must enter all the rewards of learning
		if(hasLost()) { //if the unit doesn't exist (lost game)
			reward = -1;
		} else if(hasWon()) { //if the unit reaches the goal
			reward = 1000;
		} /*else if(previousState() != null && previousState().getValue() == state().getValue()) { //the prev. state is the same, then the action taken doesnt changed the state (not a valid movement)
			reward = -10;
//				} else if(vTable.get(state().getValue())) { //anti-loops: the unit is in a visited state
//					reward = 0;
		} else{
			reward = getReward(state.getValue());
		}*/	
		
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

	private double getReward(int newState){
		double reward = 0.5;
		
		if(previousState != null){
			double currentDist = euclideanDist(previousState().getValue());
			double futureDist = euclideanDist(newState);
			
			if(currentDist!=futureDist){
				if(currentDist>futureDist){
					reward = function(currentDist);
				}else{
					reward = 0.4;
				}
			}
		}
		
		return reward;
	}
	
	private double function(double x){
		double y;

		double maxDist = Math.sqrt((Math.pow(alto, 2) + Math.pow(ancho, 2)));

		double num = -(MAX_REWARD) * Double.sum(x, -1.0);
		double den = Double.sum(maxDist, -1.0);

		y = Double.sum((num/den), MAX_REWARD);

		return y;		
	}
	
	private double euclideanDist(int newState){
		double dist = Double.MAX_VALUE;
		int actualY = (int)(newState /  alto);
		int actualX = newState % ancho;
									
		int futureY = (int)(lastState.getValue() /  alto);
		int futureX = lastState.getValue() % ancho;
		int x1 = Math.abs(actualY -  futureY);
		int x2 = Math.abs(actualX -  futureX);
		double x = Math.sqrt((Math.pow(x1, 2) + Math.pow(x2, 2)));
		if(x<dist){
			dist = x;
		}
		
		return dist;
    }
	
}
