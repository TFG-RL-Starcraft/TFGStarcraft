package starcraft;

import q_learning.Action;
import q_learning.Environment;
import q_learning.State;
import bwapi.Game;
import bwapi.Position;
import bwapi.Unit;

public class StarcraftEnvironment implements Environment{

	private static int BOX_LENGTH = 32;
	
	private Game game;
	private Unit unit;
	private State state;
	private State finalState;
	
	public StarcraftEnvironment(Game game, Unit unit, State finalState) {
		this.game = game;
		this.unit = unit;
		this.state = new StarcraftState((int)unit.getPosition().getX()/BOX_LENGTH, 
				(int)unit.getPosition().getY()/BOX_LENGTH, game.mapWidth(), game.mapHeight());
		this.finalState = finalState;
	}
	
	@Override
	public int numStates() {
		return game.mapHeight() * game.mapWidth();
	}

	@Override
	public int numActions() {
		return StarcraftAction.values().length;
	}

	@Override
	public double execute(Action action) {
		
		double reward = 0;
		 
		// Current position
		int posX = (int)unit.getPosition().getX()/BOX_LENGTH;
		int posY = (int)unit.getPosition().getY()/BOX_LENGTH;
		
		String action_str = ""; //aux variable to print the action taken
		
		StarcraftAction sc_action = (StarcraftAction)action;
		switch(sc_action) {
		 case MOVE_UP: 
			 posY--;
			 action_str = "ARRIBA";
		     break;
		 case MOVE_RIGHT: 
			 posX++;
			 action_str = "DERECHA";
		     break;
		 case MOVE_DOWN:
			 posY++;
			 action_str = "ABAJO";
		     break;
		 case MOVE_LEFT:
			 posX--;
			 action_str = "IZQUIERDA";
		     break;
		 default: 
			 
			 break;
		}
		
		// Here we move the units or execute the actions.	 
		// Later is a "switch" evaluating if the new State/Action would have a good/bad Reward
		// Here you must enter all the rewards of learning
		
		if (isValid(posX, posY)) {
			unit.move(new Position(posX*BOX_LENGTH, posY*BOX_LENGTH));
			
			state = new StarcraftState(posX, posY, game.mapWidth(), game.mapHeight());
			if(isFinalState()) {
				reward = 1000;
			}
		} else {
			reward = -100;
		}

		return reward;
	}

	@Override
	public State state() {
		return state;
	}
	
	@Override
	public State finalState() {
		return finalState;
	}

	@Override
	public boolean isFinalState() {
		return state.getValue() == finalState.getValue();
	}

	@Override
	public boolean stateHasChanged() {
		return !unit.isMoving();
	}
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
	// The position x,y is valid
	private boolean isValid(int x, int y) {
		if((0 <= x) && (x < game.mapWidth()) && (0 <= y) && (y < game.mapHeight())){
			boolean dontCol = true;
			int i = 0;
			Unit m;
			
			// Check if collide with other units
			while(dontCol && i < game.getAllUnits().size()){
				m = game.getAllUnits().get(i);
				if(itsInside(m.getTop(),m.getBottom(),m.getRight(),m.getLeft(), x*BOX_LENGTH, y*BOX_LENGTH)) {
					dontCol = false;
				}
				i++;
			}
			
			return dontCol;
		}else{
			return false;
		}
	}

	private boolean itsInside(int top, int bottom, int right, int left, int x, int y) {		
		return (left<=x) && (x<=right) && (bottom>=y) && (y>=top);	
	}

}
