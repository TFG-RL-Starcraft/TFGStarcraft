package constants;

public class Constants {
	public static final double ALPHA = 0.9; //learning rate -> what extent the newly acquired information will override the old information
	public static final double GAMMA = 0.1; //discount factor -> importance to future rewards
	public static final double REWARD_LOST = -1;
	public static final double REWARD_WON = 10000.0;
	public static final double REWARD_REPEATED = REWARD_LOST + (Math.abs(Math.abs(REWARD_LOST) - Math.abs(1.0 - (GAMMA * 1.0))) * 0.307);
	public static final double REWARD_KEEP_VALUE = 1.0 - (GAMMA * 1.0);	
	public static final int NUM_ITERACIONES_MAX_QLEARNER = 500; //n�mero m�ximo de iteraciones (pasos) de cada intento
	public static final int NUM_INTENTOS_APRENDIZAJE = 1500; //n�mero de veces que se realizar� el experimento con la misma QTabla. 
							//Cada intento se reinicia al "personaje" en la posici�n inicial y consta de NUM_ITERACIONES_MAX_QLEARNER pasos.
	public static final int NUM_EXPERIMENTOS = 50; //numero de experimentos completos, cada experimento consta de varios INTENTOS
							//de los cuales luego haremos una media de los datos obtenidos, para obtener las gr�ficas
	public static final boolean PRUEBAS = true;
	public static final String NAME_FILE_LOG = "log.txt";
	public static final String DEAD_STRING = "dead";
	public static final String[] TEST_FILES = {"mapa_facil.txt","mapa_normal.txt","mapa_dificil.txt"};
}
