package starcraft;

import q_learning.State;

public class StarcraftState implements State {

	private int value;
	
	public StarcraftState(int value) {
		this.value = value;
	}
	
	public StarcraftState(int posX, int posY, int width, int height) {
		this.value = posY * width + posX;
	}
	
	@Override
	public int getValue() {
		return this.value;
	}

}
