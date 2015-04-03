package laberinto;

import laberinto.actions.LaberintoActionManager;

public class PresenterLaberinto {

private static PresenterLaberinto instance = null;
	
	private VentanaLaberinto game;
	private int ancho, alto;
    private LaberintoActionManager laberintoActionManager;
    boolean terminado;
    
	public PresenterLaberinto(VentanaLaberinto game, LaberintoActionManager laberintoActionManager, boolean terminado, int ancho, int alto) {
		this.game = game;
		this.laberintoActionManager = laberintoActionManager;
		this.terminado = terminado;
		this.ancho = ancho;
		this.alto = alto;
	}

	public static void setInstance(VentanaLaberinto game, LaberintoActionManager laberintoActionManager, boolean terminado, int ancho, int alto) {
			instance = new PresenterLaberinto(game, laberintoActionManager, terminado, ancho, alto);
	}	

	public static PresenterLaberinto getInstance()
	{
		return instance;
	}
	
	//--------------------- getters, setters & other methods --------------------------------

	public VentanaLaberinto getGame() {		
		return this.game;
	}

	public LaberintoActionManager getLaberintoActionManager() {
		return this.laberintoActionManager;
	}

	public int getAncho() {
		return this.ancho;
	}

	public int getAlto() {
		return this.alto;
	}

	public boolean getTerminado() {
		return this.terminado;
	}
}
