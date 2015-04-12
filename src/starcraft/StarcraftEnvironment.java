package starcraft;

import java.util.ArrayList;

import q_learning.Action;
import q_learning.Environment;
import q_learning.State;
import bwapi.Game;
import bwapi.Unit;

public class StarcraftEnvironment implements Environment{
	
	private double MAX_REWARD = 10.0;
	
	private Game game;
	private Unit unit;
	private State previousState;
	private Action previousAction;
	private boolean unitIsDead = false;
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
		if(unit.exists()){
			return new StarcraftState((int)unit.getPosition().getX()/Presenter.getInstance().getBoxSize(), 
					(int)unit.getPosition().getY()/Presenter.getInstance().getBoxSize(), game.mapWidth(), game.mapHeight());
		}else{
			return null;
		}
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
		//return !this.unit.exists();
	}

	@Override
	public boolean stateHasChanged() {		
		return !unit.isMoving();	
	}
	
	@Override
	public double getReward(State state) {		
		//If the current distance to the final is bigger than the future increase the reward
		double reward;

		// Here you must enter all the rewards of learning
		if(hasLost()) { //if the unit doesn't exist (lost game)
			reward = -1;
		} else if(hasWon()) { //if the unit reaches the goal
			reward = 1000;
		} else if(previousState() != null && previousState().getValue() == state().getValue()) { //the prev. state is the same, then the action taken doesnt changed the state (not a valid movement)
			reward = -10;
//		} else if(vTable.get(state().getValue())) { //anti-loops: the unit is in a visited state
//			reward = 0;
		} else{
			reward = getReward(state.getValue());
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
	
	private double getReward(int newState){
		double reward = 0.5;
		
		if(previousState != null){
			double currentDist = euclideanDist(previousState().getValue());
			double futureDist = euclideanDist(newState);
			
			if(currentDist!=futureDist){
				if(currentDist>futureDist){
					reward = function(currentDist);
				}else{
					System.out.println();
					reward = 0.4;
				}
			}
		}
		
		return reward;
	}
	
	private double function(double x){
		double y;

		double maxDist = Math.sqrt((Math.pow(game.mapHeight(), 2) + Math.pow(game.mapWidth(), 2)));

		double num = -(MAX_REWARD) * Double.sum(x, -1.0);
		double den = Double.sum(maxDist, -1.0);

		y = Double.sum((num/den), MAX_REWARD);

		return y;		
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
