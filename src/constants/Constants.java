package constants;

public class Constants {
	public static final double ALPHA = 0.9; //learning rate -> what extent the newly acquired information will override the old information
	public static final double GAMMA = 0.1; //discount factor -> importance to future rewards
	public static final double REWARD_LOST = -1;
	public static final double REWARD_WON = 10000.0;
	public static final int NUM_ITERACIONES_MAX_QLEARNER = 500; //número máximo de iteraciones (pasos) de cada intento
	public static final int NUM_INTENTOS_APRENDIZAJE = 1500; //número de veces que se realizará el experimento con la misma QTabla. 
							//Cada intento se reinicia al "personaje" en la posición inicial y consta de NUM_ITERACIONES_MAX_QLEARNER pasos.
	public static final int NUM_EXPERIMENTOS = 1; //numero de experimentos completos, cada experimento consta de varios INTENTOS
							//de los cuales luego haremos una media de los datos obtenidos, para obtener las gráficas
}
