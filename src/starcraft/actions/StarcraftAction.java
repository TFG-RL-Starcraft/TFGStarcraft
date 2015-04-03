package starcraft.actions;

import bwapi.Game;
import bwapi.Position;
import bwapi.Unit;
import q_learning.Action;
import starcraft.Presenter;

public abstract class StarcraftAction extends Action{

	StarcraftAction(int value) {
		super(value);
	}

	//auxiliary methods and variables for execute()
	protected Game game;
	protected Unit unit;
    protected int box_size;
	
    // Implement the configureContext() method here because it's the same for all the Actions
	public void configureContext() {
		this.game = Presenter.getInstance().getGame();
		this.unit = Presenter.getInstance().getUnit();
		this.box_size = Presenter.getInstance().getBoxSize();
	}
	
	/**
	 * See if the converted to Starcraft logical position x, y is a valid position and can go up there
	 * @param x Height
	 * @param y Width
	 * @return Starcraft position or null if there is not valid
	 */
	protected Position isValid(int x, int y) {
		int BOX_LENGTH = Presenter.getInstance().getBoxSize();
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
