package starcraft.actions;

import bwapi.Position;

public class MoveLeft extends StarcraftAction {

	MoveLeft(int value) {
		super(value);
	}

	@Override
	public void execute() {
		int posX = (int)this.unit.getPosition().getX()/this.box_size;
		int posY = (int)this.unit.getPosition().getY()/this.box_size;
		
		posX--;
		
		Position p = isValid(posX, posY);
		
		if (p != null)
			this.unit.move(p);
	}

}
