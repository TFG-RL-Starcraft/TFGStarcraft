package laberinto.actions;

import q_learning.ActionManager;

public class LaberintoActionManager extends ActionManager{
	
	public LaberintoActionManager() {
		map.put(0, new MoveUp(0));
		map.put(1, new MoveRight(1));
		map.put(2, new MoveDown(2));
		map.put(3, new MoveLeft(3));
<<<<<<< HEAD
=======
		map.put(4, new MoveUpLeft(4));
		map.put(5, new MoveUpRight(5));
		map.put(6, new MoveDownLeft(6));
		map.put(7, new MoveDownRight(7));
>>>>>>> 0bee5d6d052a73c8643021f5ad1781658b5c4b99
	}
}
