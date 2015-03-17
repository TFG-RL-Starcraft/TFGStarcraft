package starcraft;

import q_learning.Action;

public enum StarcraftAction implements Action{

	MOVE_UP(0),
	MOVE_RIGHT(1),
	MOVE_DOWN(2),
	MOVE_LEFT(3),
	MOVE_UP_RIGHT(4),
	MOVE_DOWN_RIGHT(5),
	MOVE_UP_LEFT(6),
	MOVE_DOWN_LEFT(7);
	
    private final int value;
	
    StarcraftAction(int value) { 
        this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public Action get(int value) {
		return StarcraftAction.values()[value];		
	}
}
