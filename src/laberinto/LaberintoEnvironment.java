package laberinto;

import java.util.ArrayList;

import constants.Constants;
import laberinto.actions.LaberintoActionManager;
import q_learning.Action;
import q_learning.Environment;
import q_learning.State;

public class LaberintoEnvironment implements Environment{
	
	public enum Policies //Enum with the possible reward policies
	{
	    BASIC, EUCLIDEAN_DISTANCE, LESS_STEPS, EUCLIDEAN_DISTANCE_AND_LESS_STEPS
	}
	
	private int tableroVisitas[][];
	
	private int ancho, alto;
	private LaberintoState init_state, init_lastState;
	private LaberintoState lastState;
	private State previousState;
	private Action previousAction;	
	private ArrayList<Integer> listaEstadosMuerto;
	private double won_reward, lost_reward;
	private int max_iter;
	private Policies policy_used;
	

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
		int x = s.getValue() % ancho;
		int y = (int)(s.getValue() / ancho);
		
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
		
		double reward = 0;
		// Here you must enter all the rewards of learning
		
		switch (this.policy_used) {
	    	case BASIC:
	    		//1. Politica básica
	    		if(hasLost()) { //if the unit doesn't exist (lost game)
	    			reward = this.lost_reward;
	    		} else if(hasWon()) { //if the unit reaches the goal
	    			reward = this.won_reward;
	    		}
	    	break;
	    	case EUCLIDEAN_DISTANCE:
	    		//2.Politica de recompensa por acercarse con la distancia euclidia
	    		if(hasLost()) { //if the unit doesn't exist (lost game)
	    			reward = this.lost_reward;
	    		} else if(hasWon()) { //if the unit reaches the goal
	    			reward = this.won_reward;
	    		} else {
	    			reward = euclideanReward(state.getValue());
	    		}
		    break;
	    	case LESS_STEPS:
	    		//3. Politica de recompensa por llegar con el menor numero de pasos a la meta
	    		if(hasLost()) { //if the unit doesn't exist (lost game)
	    			reward = this.lost_reward;
	    		} else if(hasWon()) { //if the unit reaches the goal
	    			reward = stepDependantReward();
	    		}
		    break;
	    	case EUCLIDEAN_DISTANCE_AND_LESS_STEPS:
	    		//4. Politicas 2 y 3 unidas
	    		if(hasLost()) { //if the unit doesn't exist (lost game)
	    			reward = this.lost_reward;
	    		} else if(hasWon()) { //if the unit reaches the goal
	    			reward = stepDependantReward();
	    		} else {
	    			reward = euclideanReward(state.getValue());
	    		}
		    break;
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
	
	
	
	private double stepDependantReward(){
		double A = (this.won_reward - 10.0) / Math.pow(this.max_iter, 2);		
		double reward = A * Math.pow(PresenterLaberinto.getInstance().getNumIter(), 2) + this.won_reward;		
		return reward;
	}

	private double euclideanReward(int newState){
		double reward = 1.0 - Constants.GAMMA;
		
		if(previousState != null){
			double currentDist = euclideanDist(previousState().getValue());
			double futureDist = euclideanDist(newState);
			
			if(currentDist!=futureDist){
				if(currentDist>futureDist){
					reward = 1.0 - Constants.GAMMA + (Constants.GAMMA / 1.5);
				}else{
					reward = 0.0;
				}
			}
		}
		
		return reward;
	}
	
	private double euclideanDist(int state){
		double dist = Double.MAX_VALUE;
		int actualY = (int)(state /  alto);
		int actualX = state % ancho;
									
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
