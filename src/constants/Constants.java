package constants;

public class Constants {
	public static final double ALPHA = 0.9;
	public static final double GAMMA = 0.1;
	public static final double REWARD_LOST = -1;
	public static final double REWARD_WON = 1000.0;
	public static final int NUM_ITERACIONES_MAX_QLEARNER = 500; //n�mero m�ximo de iteraciones (pasos) de cada intento
	public static final int NUM_INTENTOS_APRENDIZAJE = 1500; //n�mero de veces que se realizar� el experimento con la misma QTabla. 
							//Cada intento se reinicia al "personaje" en la posici�n inicial y consta de NUM_ITERACIONES_MAX_QLEARNER pasos.
	public static final int NUM_EXPERIMENTOS = 1; //numero de experimentos completos, cada experimento consta de varios INTENTOS
							//de los cuales luego haremos una media de los datos obtenidos, para obtener las gr�ficas
}
