package laberinto;

import laberinto.actions.LaberintoActionManager;

public class PresenterLaberinto {

private static PresenterLaberinto instance = null;
	
	private VentanaLaberinto game;
	private int ancho, alto;
    private LaberintoActionManager laberintoActionManager;
    boolean terminado;
    private int numIter;
    private int numExp;
    
	public PresenterLaberinto(VentanaLaberinto game, LaberintoActionManager laberintoActionManager, boolean terminado, int ancho, int alto,int numiter,int numExp) {
		this.game = game;
		this.laberintoActionManager = laberintoActionManager;
		this.terminado = terminado;
		this.ancho = ancho;
		this.alto = alto;
		this.numIter = numiter;
		this.numExp = numExp;
	}

	public static void setInstance(VentanaLaberinto game, LaberintoActionManager laberintoActionManager, boolean terminado, int ancho, int alto,int numIter, int numExp) {
			instance = new PresenterLaberinto(game, laberintoActionManager, terminado, ancho, alto,numIter,numExp);
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
	
	public int getNumIter(){
		return this.numIter;
	}
	
	public int getNumExp(){
		return this.numExp;
	}
}
