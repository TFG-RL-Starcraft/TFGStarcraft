package laberinto.actions;

import q_learning.ActionManager;

public class LaberintoActionManager extends ActionManager{
	
	public LaberintoActionManager() {
		map.put(0, new MoveUp(0));
		map.put(1, new MoveRight(1));
		map.put(2, new MoveDown(2));
		map.put(3, new MoveLeft(3));
	}
}
