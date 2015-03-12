package laberinto;

import q_learning.Action;

public enum LaberintoAction implements Action{
	
	MOVE_UP(0),
	MOVE_RIGHT(1),
	MOVE_DOWN(2),
	MOVE_LEFT(3);
	
    private final int value;
	
    LaberintoAction(int value) { 
        this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public Action get(int value) {
		return LaberintoAction.values()[value];		
	}

}
