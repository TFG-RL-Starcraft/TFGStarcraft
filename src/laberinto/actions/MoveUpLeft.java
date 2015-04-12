package laberinto.actions;

import bwapi.Position;

public class MoveUpLeft extends LaberintoAction {

	MoveUpLeft(int value) {
		super(value);
	}

	@Override
	public void execute() {
		// Current position
		int posX = getState().getPosX();
		int posY = getState().getPosY();
 
		posY--;
		posX--;
		
		//mueve la casilla y comprueba si es el final
		if (esValida(posX, posY))
		{
			getGame().mover(posX, posY);
		}
	}

}
