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
public class StarcraftPresenter {
	
	private static StarcraftPresenter instance = null;
	
	private Game game;
    private Unit unit;
    private int box_size;
    private StarcraftActionManager starcraftActionManager;
    private int numIter[];
    private boolean just_finished[];
    
	public StarcraftPresenter(Game game, Unit unit, int box_size, StarcraftActionManager starcraftActionManager, int[] numIter, boolean[] just_finished) {
		this.game = game;
		this.unit = unit;
		this.box_size = box_size;
		this.starcraftActionManager = starcraftActionManager;
		this.numIter = numIter;
		this.just_finished = just_finished;
	}

	/**
	 * Initialization method and instantiation of the Presenter class.
	 * @param is_just_finished 
	 */
	public static void setInstance(Game game, Unit unit, int box_size, StarcraftActionManager starcraftActionManager,int[] numIter, boolean[] is_just_finished) {
			instance = new StarcraftPresenter(game, unit, box_size, starcraftActionManager, numIter, is_just_finished);
	}	
	
	/**
	 * Returns a single instance of the class Presenter
	 * @return Class instance
	 */
	public static StarcraftPresenter getInstance()
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
	
	public int getNumIter(){
		return this.numIter[0];
	}
	
	public boolean isJustFinished(){
		return this.just_finished[0];
	}

	public void setJustFinished(boolean finished) {
		this.just_finished[0] = finished;
	}
}
