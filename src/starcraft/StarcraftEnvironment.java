package starcraft;

import java.util.ArrayList;

import entrada_salida.Log;
import q_learning.Action;
import q_learning.Environment;
import q_learning.State;
import q_learning.VisitedStateTable;
import bwapi.Game;
import bwapi.Position;
import bwapi.Unit;

public class StarcraftEnvironment implements Environment{

	private static int BOX_LENGTH = 32;
	
	private Game game;
	private Unit unit;
	private State state;
	private ArrayList<Integer> finalState; //array with the hash values of all possible final states 
	private VisitedStateTable vTable;
	
	public StarcraftEnvironment(Game game, Unit unit, ArrayList<State> finalStates) {
		this.game = game;
		this.unit = unit;
		this.state = new StarcraftState((int)unit.getPosition().getX()/BOX_LENGTH, 
				(int)unit.getPosition().getY()/BOX_LENGTH, game.mapWidth(), game.mapHeight());
		this.finalState = new ArrayList<Integer>();
		for(State sta : finalStates) {
			finalState.add(sta.getValue());
		}		
		vTable = new VisitedStateTable(numStates());
	}
	
	@Override
	public int numStates() {
		return game.mapHeight() * game.mapWidth();
	}

	@Override
	public int numActions() {
		return StarcraftAction.values().length;
	}
	
	public void setUnit(Unit u){
		unit = u;
	}

	@Override
	public double execute(Action action) {
		
		double reward = 0; //this value could be 0.001 or very small values

		if(!hasLost()) {
			// Current position
			int posX = (int)unit.getPosition().getX()/BOX_LENGTH;
			int posY = (int)unit.getPosition().getY()/BOX_LENGTH;
			vTable.set(this.state.getValue(),true);
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
			     
			 case MOVE_UP_LEFT: 
				 posY--;
				 posX--;
				 action_str = "ARRIBA-IZQUIERDA";
			     break;
			 case MOVE_UP_RIGHT: 
				 posX++;
				 posY--;
				 action_str = "ARRIBA-DERECHA";
			     break;
			 case MOVE_DOWN_LEFT:
				 posY++;
				 posX--;
				 action_str = "ABAJO-IZQUIERDA";
			     break;
			 case MOVE_DOWN_RIGHT:
				 posX++;
				 posY++;
				 action_str = "ABAJO-DERECHA";
			     break;
			         
			     
			 default: 
				 
				 break;
			}
			
			// Here we move the units or execute the actions.	 
			// Later is a "switch" evaluating if the new State/Action would have a good/bad Reward
			// Here you must enter all the rewards of learning
			Position p = isValid(posX, posY);
			
			if (p != null) {
				unit.move(p);
				
				state = new StarcraftState(posX, posY, game.mapWidth(), game.mapHeight());
				if(isFinalState()) {
					reward = 100000;
				}
				if(vTable.get(state.getValue())){
					reward = 0;
				}
			} else { //is not a valid move
				reward = -10;
			}
		} else { //if the unit doesn't exist (lost game)
			reward = -1;
		}

		return reward;
	}

	@Override
	public State state() {
		return state;
	}

	@Override
	public boolean isFinalState() {
		return hasWon() || hasLost();
	}
	
	private boolean hasWon() {
		return finalState.contains(state.getValue());
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
		game.pauseGame();
		vTable.clear();
		game.restartGame();
	}
	
	/**
	 * See if the converted to Starcraft logical position x, y is a valid position and can go up there
	 * @param x Height
	 * @param y Width
	 * @return Starcraft position or null if there is not valid
	 */
	private Position isValid(int x, int y) {
		Position p = new Position(x*BOX_LENGTH+(BOX_LENGTH/2), y*BOX_LENGTH+(BOX_LENGTH/2));
		if((0 <= x) && (x < game.mapWidth()*BOX_LENGTH) && (0 <= y) && (y < game.mapHeight()*BOX_LENGTH)
				&& game.hasPath(unit.getPosition(), p)){
			boolean dontCol = true;
			int i = 0;
			Unit m;
			
			// Check if collide with other units
			while(dontCol && i < game.getAllUnits().size()){
				m = game.getAllUnits().get(i);
				if(!m.getType().isBeacon() && itsInside(m.getTop(),m.getBottom(),m.getRight(),m.getLeft(), x*BOX_LENGTH+(BOX_LENGTH/2), y*BOX_LENGTH+(BOX_LENGTH/2))) {
					return null;
				}
				i++;
			}
			
			return p;
		}else{
			return null;
		}
	}

	private boolean itsInside(int top, int bottom, int right, int left, int x, int y) {		
		return (left<=x) && (x<=right) && (bottom>=y) && (y>=top);	
	}

}
