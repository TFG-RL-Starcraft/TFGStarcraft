package starcraft;

import starcraft.actions.StarcraftActionManager;
import bwapi.Game;
import bwapi.Unit;



/**
 * Class that contains references to all the "important" application objects.
 * Once initialized class can modify references, but it has no methods "set"
 * This class makes it unnecessary to pass many parameters to other classes that use common objects.
 *
 * Uses the modified Singleton pattern, because the singleton pattern usually has not attributes,
 * What you do is initialize it with setInstance, and then collect with getInstance ().
 *
 */
public class Presenter {
	
	private static Presenter instance = null;
	
	private Game game;
    private Unit unit;
    private int box_size;
    private StarcraftActionManager starcraftActionManager;
    
    
	public Presenter(Game game, Unit unit, int box_size, StarcraftActionManager starcraftActionManager) {
		this.game = game;
		this.unit = unit;
		this.box_size = box_size;
		this.starcraftActionManager = starcraftActionManager;
	}

	/**
	 * Initialization method and instantiation of the Presenter class.
	 */
	public static void setInstance(Game game, Unit unit, int box_size, StarcraftActionManager starcraftActionManager) {
			instance = new Presenter(game, unit, box_size, starcraftActionManager);
	}	
	
	/**
	 * Returns a single instance of the class Presenter
	 * @return Class instance
	 */
	public static Presenter getInstance()
	{
		return instance;
	}
	
	//--------------------- getters, setters & other methods --------------------------------

	public Game getGame() {		
		return this.game;
	}
	
	public Unit getUnit() {		
		return this.unit;
	}
	
	public int getBoxSize() {		
		return this.box_size;
	}

	public StarcraftActionManager getStarcraftActionManager() {
		return this.starcraftActionManager;
	}
}
