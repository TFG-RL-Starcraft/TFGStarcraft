package constants;

import laberinto.LaberintoEnvironment.Policies;

public class Constants {
	public static final double ALPHA = 0.9; //learning rate -> what extent the newly acquired information will override the old information
	public static final double GAMMA = 0.1; //discount factor -> importance to future rewards
	
	public static final double REWARD_LOST = -1;
	public static final double REWARD_WON = 10000.0;
	public static final double REWARD_REPEATED = REWARD_LOST + (Math.abs(Math.abs(REWARD_LOST) - Math.abs(1.0 - (GAMMA * 1.0))) * 0.307);
	public static final double REWARD_KEEP_VALUE = 1.0 - (GAMMA * 1.0);	
	
	public static final int NUM_ITERACIONES_MAX_QLEARNER = 2500; //número máximo de iteraciones (pasos) de cada intento
	public static final int NUM_INTENTOS_APRENDIZAJE = 1000; //número de veces que se realizará el experimento con la misma QTabla. 
							//Cada intento se reinicia al "personaje" en la posición inicial y consta de NUM_ITERACIONES_MAX_QLEARNER pasos.	
	
	public static final String MAP_FILE = "laberinto.txt";
	public static final Policies USED_POLICY = Policies.BASIC;
	
	//Constantes empleadas sólo en los test
	public static final boolean TEST_MODE = true;
	public static final int TEST_NUM_EXPERIMENTOS = 10; //numero de experimentos completos, cada experimento consta de varios INTENTOS
	//de los cuales luego haremos una media de los datos obtenidos, para obtener las gráficas
	public static final String TEST_LOG_FILE = "log.txt";
	public static final String TEST_QTABLE_FILE = "qtabla.txt";
	public static final String TEST_DEAD_STRING = "dead";
	public static final String TEST_MAPS_PATH = "C:\\Program Files (x86)\\StarCraft\\Maps\\";
	public static final String[] TEST_MAP_FILES = {"mapa_facil","mapa_normal","mapa_dificil"};
	public static final int[] TEST_NUM_ITER_MAX = {100, 500, 2500}; //numero de iteraciones maximo por cada mapa (en orden)
}
