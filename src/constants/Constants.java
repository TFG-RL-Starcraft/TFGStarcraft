package constants;

public class Constants {
	public static double QTABLE_INIT_VALUE = 1.0;
	public static double ALPHA = 0.9;
	public static double GAMMA = 0.1;
	public static double REWARD_WON = Math.pow(10, 19);
	public static double REWARD_LOSE = ( - QTABLE_INIT_VALUE - (ALPHA * GAMMA * QTABLE_INIT_VALUE) + (ALPHA * QTABLE_INIT_VALUE)) / ALPHA ;
	public static double REWARD_REPEATED = REWARD_LOSE + (Math.abs(Math.abs(REWARD_LOSE) - Math.abs(QTABLE_INIT_VALUE - (GAMMA * QTABLE_INIT_VALUE))) * 0.307);
	public static double REWARD_KEEP_VALUE = QTABLE_INIT_VALUE - (GAMMA * QTABLE_INIT_VALUE);
	public static int NUM_PASOS = 2000;
	public static int NUM_EXP = 500;
	public static int REPETICIONES = 1;
	public static int POLITICA;
	public static String[] POLICIES = { 
		"Politica 0",
		"Politica 1",
		"Politica 2",
		"Politica 3",
		"Politica 4"
	};
	public static int NUM_POLITICAS = POLICIES.length;
	public static boolean PRUEBAS = true;
	public static String NAME_FILE_LOG = "log.txt";
	public static String[] TEST_FILES = {"mapa_facil.txt","mapa_normal.txt","mapa_dificil.txt"};
	public static int CURRENT_TEST_FILE;
}
