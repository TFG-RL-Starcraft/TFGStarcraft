package laberinto.actions;

public class MoveRight extends LaberintoAction {

	MoveRight(int value) {
		super(value);
	}

	@Override
	public void execute() {
		// Current position
		int posX = getState().getPosX();
		int posY = getState().getPosY();
 
		posX++;

		//mueve la casilla y comprueba si es el final
		if (esValida(posX, posY))
		{
			getGame().mover(posX, posY);
		}
	}
}
