package constants;

public class Constants {
	public static double QTABLE_INIT_VALUE = 1.0;
	public static double ALPHA = 0.2;
	public static double GAMMA = 0.5;
	public static double REWARD_WON = 1000000.0;
	public static double REWARD_LOSE = ( - QTABLE_INIT_VALUE - (ALPHA * GAMMA) + ALPHA) / ALPHA ;
	public static int NUM_PASOS = 2000;
	public static int NUM_EXP = 500;
	public static int REPETICIONES = 50;
}
