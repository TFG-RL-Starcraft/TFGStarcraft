package laberinto.actions;

import laberinto.LaberintoState;
import laberinto.PresenterLaberinto;
import laberinto.VentanaLaberinto;
import q_learning.Action;

public abstract class LaberintoAction extends Action{
	
	LaberintoAction(int value) {
		super(value);
	}

	//auxiliary methods and variables for execute()
	private VentanaLaberinto game;
	private LaberintoState state;
	
	// Implement the configureContext() method here because it's the same for all the Actions
	public void configureContext() {
		this.game = PresenterLaberinto.getInstance().getGame();
		this.state = game.getEstadoActual();
	}
	
	public VentanaLaberinto getGame() {
		return this.game;
	}
	
	public LaberintoState getState() {
		return this.state;
	}
	
	protected boolean esValida(int x, int y) {
		int ancho = PresenterLaberinto.getInstance().getAncho();
		int alto = PresenterLaberinto.getInstance().getAlto();
		return (0 <= x) && (x < ancho) &&
				(0 <= y) && (y < alto) && 
				!game.getCasilla(x, y).esPared();
	}

}
