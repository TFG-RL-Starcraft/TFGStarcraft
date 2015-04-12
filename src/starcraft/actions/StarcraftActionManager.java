package starcraft.actions;

import q_learning.ActionManager;

public class StarcraftActionManager extends ActionManager{
	
	public StarcraftActionManager() {
		map.put(0, new MoveUp(0));
		map.put(1, new MoveRight(1));
		map.put(2, new MoveDown(2));
		map.put(3, new MoveLeft(3));
		map.put(4, new MoveUpRight(4));
		map.put(5, new MoveDownRight(5));
		map.put(6, new MoveUpLeft(6));
		map.put(7, new MoveDownLeft(7));
	}
}
