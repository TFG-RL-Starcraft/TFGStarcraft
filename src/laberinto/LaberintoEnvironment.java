package laberinto;

import q_learning.Action;
import q_learning.Environment;
import q_learning.State;

public class LaberintoEnvironment implements Environment{

	private int ancho, alto;
	private LaberintoState init_state, init_lastState;
	private LaberintoState state, lastState;
	VentanaLaberinto game;

	public LaberintoEnvironment(VentanaLaberinto game, int ancho, int alto, LaberintoState state, LaberintoState lastState) {
		this.game = game;
		this.ancho = ancho;
		this.alto = alto;
		this.init_state = state;
		this.init_lastState = lastState;
		this.state = state;
		this.lastState = lastState;
	}
	
	@Override
	public int numStates() {
		return ancho * alto;
	}

	@Override
	public int numActions() {
		return LaberintoAction.values().length;
	}

	@Override
	public double execute(Action action) {
		
		double reward = 0;
		 
		// Current position
		int posX = state.getPosX();
		int posY = state.getPosY();
		
		String action_str = "";
		
		LaberintoAction lab_action = (LaberintoAction)action;
		switch(lab_action) {
		 case MOVE_UP: 
			 posY--;
			 action_str = "ARRIBA";
		     break;
		 case MOVE_RIGHT: 
			 posX++;
			 action_str = "DERECHA";
		     break;
		 case MOVE_DOWN:
			 posY++;
			 action_str = "ABAJO";
		     break;
		 case MOVE_LEFT:
			 posX--;
			 action_str = "IZQUIERDA";
		     break;
		 default: 
			 
			 break;
		}
		
		//mueve la casilla y comprueba si es el final
		if (esValida(posX, posY))
		{
			game.mover(posX, posY);
		}
		
		if (esValida(posX, posY)) {
			state = new LaberintoState(posX, posY, ancho, alto);
			if(isFinalState()) {
				reward = 1000;
			} 
		}
		
		return reward;
	}

	@Override
	public State state() {
		return state;
	}

	@Override
	public boolean isFinalState() {
		return state.getValue() == lastState.getValue();
	}

	@Override
	public void reset() {
		this.state = this.init_state;
		this.lastState = this.init_lastState;
	}

	private boolean esValida(int x, int y) {
		return (0 <= x) && (x < ancho) &&
				(0 <= y) && (y < alto) && 
				!game.getCasilla(x, y).esPared();
	}

	@Override
	public boolean stateHasChanged() {
		// TODO Auto-generated method stub
		return false;
	}
}
