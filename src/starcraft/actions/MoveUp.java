package starcraft.actions;

import bwapi.Position;
import starcraft.Presenter;

public class MoveUp extends StarcraftAction {

	MoveUp(int value) {
		super(value);
	}

	@Override
	public void execute() {
		int BOX_LENGTH = Presenter.getInstance().getBoxSize();
		int posX = (int)getUnit().getPosition().getX()/BOX_LENGTH;
		int posY = (int)getUnit().getPosition().getY()/BOX_LENGTH;
		
		posY--;
		
		Position p = isValid(posX, posY);
		
		if (p != null)
			getUnit().move(p);
	}

}
