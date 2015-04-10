package starcraft;

import java.util.ArrayList;

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
	//private VisitedStateTable vTable;
	
	public StarcraftEnvironment(Game game, Unit unit, ArrayList<State> finalStates) {
		this.game = game;
		this.unit = unit;
		this.previousState = null;
		this.previousAction = null;
		this.finalState = new ArrayList<Integer>();
		for(State sta : finalStates) {
			finalState.add(sta.getValue());
		}		
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
		return new StarcraftState((int)unit.getPosition().getX()/Presenter.getInstance().getBoxSize(), 
				(int)unit.getPosition().getY()/Presenter.getInstance().getBoxSize(), game.mapWidth(), game.mapHeight());
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
		return hasWon() || hasLost();
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
	public double getReward(State state) {
		double reward = 0; //this value could be 0.001 or very small values
		
		//If the current distance to the final is bigger than the future increase the reward
		if(getCloser(state.getValue()))
			reward = 10;
		
		//System.out.println(reward);
		// Here you must enter all the rewards of learning
		if(hasWon()) { //if the unit reaches the goal
			reward = 1000;
		} else if(hasLost()) { //if the unit doesn't exist (lost game)
			reward = -1;
		} else if(previousState() != null && previousState().getValue() == state().getValue()) { //the prev. state is the same, then the action taken doesnt changed the state (not a valid movement)
			reward = -10;
//		} else if(vTable.get(state().getValue())) { //anti-loops: the unit is in a visited state
//			reward = 0;
		}

		return reward;
	}
	
	@Override
	public void reset() {
		this.previousState = null;
		this.previousAction = null;
		game.pauseGame();
		//vTable.clear();
		game.restartGame();
	}
	
	private boolean getCloser(int newState){
		boolean reachingEnd = false;
			
		if(previousState != null){
			double currentDist = euclideanDist(previousState().getValue());
			double futureDist = euclideanDist(newState);
			
			if(currentDist>futureDist) 
				reachingEnd = true;
		}
		
		return reachingEnd;
	}
	
	private double euclideanDist(int newState){
		double dist = Double.MAX_VALUE;
		int actualY = (int)(newState /  game.mapHeight());
		int actualX = newState % game.mapWidth();
		
		for(int i = 0; i < finalState.size(); i++){					
			int futureY = (int)(finalState.get(i) /  game.mapHeight());
			int futureX = finalState.get(i) % game.mapWidth();
			int x1 = Math.abs(actualY -  futureY);
			int x2 = Math.abs(actualX -  futureX);
			double x = Math.sqrt((Math.pow(x1, 2) + Math.pow(x2, 2)));
			if(x<dist){
				dist = x;
			}
		}
		return dist;
    }

}
