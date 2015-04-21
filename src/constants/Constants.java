package constants;

public class Constants {
	public static double QTABLE_INIT_VALUE = 1.0;
	public static double ALPHA = 0.9;
	public static double GAMMA = 0.1;
	public static double REWARD_WON = Math.pow(10, 19);
	public static double REWARD_LOSE = ( - QTABLE_INIT_VALUE - (ALPHA * GAMMA) + ALPHA) / ALPHA ;
	public static double REWARD_REPEATED = 0.0;
	public static int NUM_PASOS = 2000;
	public static int NUM_EXP = 500;
	public static int REPETICIONES = 50;
	public static int POLITICA = 5;
	public static boolean PRUEBAS = true;
}
