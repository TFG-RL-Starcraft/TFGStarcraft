package constants;

import laberinto.LaberintoEnvironment.Policies;
import starcraft.StarcraftEnvironment.SC_Policies;

public class Constants {
	//Q-Learning options/variables
	public static final double ALPHA = 0.9; //learning rate -> what extent the newly acquired information will override the old information
	public static final double GAMMA = 0.1; //discount factor -> importance to future rewards
	
	public static final double REWARD_LOST = -1;
	public static final double REWARD_WON = 10000.0;
	
	public static final Policies LABERINTO_USED_POLICY = Policies.BASIC;
	public static final SC_Policies STARCRAFT_USED_POLICY = SC_Policies.BASIC;
	
	public static final int NUM_ITERACIONES_MAX_QLEARNER = 100; //max number of iterations (steps) on each attempt
	public static final int NUM_INTENTOS_APRENDIZAJE = 500; //number of attempts with the same QTable values
							//each attempt restart the "agent" to the start position, and have NUM_ITERACIONES_MAX_QLEARNER steps.	
	
	public static final boolean LEARNING_OR_PLAYING_MODE = true; //true = QLearner; false = QPlayer
	public static final boolean QPLAYER_MODE = true; //true = "RANDOM" (probabilistic); false = "FIXED" (most valued action)
	
	//Logic labyrinth file
	public static final String LABERINTO_FILE = "laberinto.txt";
	//public static final String STARCRAFT_MAP_PATH = "C:\\Program Files (x86)\\StarCraft\\Maps\\mapa_facil";
		//this path not being used -> choose the StarCraft map from Starcraft the menu, or configure it in the BWAPI.ini
	
	//Enable or disable the GUI in Starcraft (slower or faster)
	public static final boolean STARCRAFT_GUI_MODE_ENABLED = true;
	
	//Name of the debug file and the QTable file
	public static final String TEST_LOG_FILE = "log.txt";
	public static final String TEST_QTABLE_FILE = "qtabla.txt";
	
}
