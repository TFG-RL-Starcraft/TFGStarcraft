package starcraft;
import java.util.ArrayList;

import starcraft.StarcraftEnvironment.Policies;
import constants.Constants;
import entrada_salida.Excel;
import entrada_salida.IO_QTable;
import entrada_salida.Log;
import q_learning.Environment;
import q_learning.QLearner;
import q_learning.QPlayer;
import q_learning.QTable;
import q_learning.QTable_Array;
import q_learning.State;
import starcraft.actions.StarcraftActionManager;
import bwapi.*;
import bwta.*;

public class Main_Starcraft{

	private static long time_start, time_end;
	
	private int numIter[] = new int[1];
	private boolean just_finished[] = new boolean[1];
	
    private Mirror mirror = new Mirror();

    private Game game;
    private Unit marine;
    private Player self;
    //private Player enemy;
    private QLearner q;
	private QPlayer qp;    
    int numberOfFrames = 0;
    
    //ArrayList con las distintas políticas
	Policies[] policies = Policies.values();
	int map_indx = 0;
	int pol_indx = 0;
	int exper_index = 0;
	int intento_index = 0;
	private int max_num_intentos = Constants.NUM_INTENTOS_APRENDIZAJE; //número de veces que se realizará el experimento con la misma QTabla. 
    							//Cada intento se reinicia al "personaje" en la posición inicial y consta de NUM_ITERACIONES_MAX_QLEARNER pasos. 
    private int max_num_exper = Constants.TEST_NUM_EXPERIMENTOS; //número de experimentos completos, cada experimento consta de varios INTENTOS
    							//de los cuales luego haremos una media de los datos obtenidos, para obtener las gráficas
    String map; //name of the current map
    double[][] logFinal; //en esta variable almacenaremos los resultados finales de la media de todos los experimentos
    double[][] visitasFinal; //en esta variable almacenaremos los resultados finales de la media de todos los experimentos
    private int map_size = 64;
    
    private static boolean map_changed = false;

    public void run() {
        mirror.getModule().setEventListener(new DefaultBWListener() {
//            @Override
//            public void onUnitCreate(Unit unit) {
//                System.out.println("New unit " + unit.getType());
//            }

            @Override
            public void onStart() {
            	System.out.println("map: " + map_indx + ", polit: " + pol_indx + ", exper: " + exper_index + ", intento: " + intento_index);
                game = mirror.getGame();
                self = game.self();
                
                //Use BWTA to analyze map
                //This may take a few minutes if the map is processed first time!
                //System.out.println("Analyzing map...");
                BWTA.readMap();
                BWTA.analyze();
                               
                //System.out.println("Map data ready");
                
                if(Constants.TEST_MODE)
                {	                
                	if(intento_index == 0)
                	{
                		InicializarQLearner(Constants.ALPHA, Constants.GAMMA, Constants.TEST_NUM_ITER_MAX[map_indx], Constants.REWARD_WON, Constants.REWARD_LOST, policies[pol_indx]);	                								
                	
                		if(exper_index == 0)
                    	{
    		                logFinal = new double[max_num_exper][max_num_intentos]; //en esta variable almacenaremos los resultados finales de la media de todos los experimentos
    				    	for(int i=0; i<max_num_exper; i++)
    				    		for(int j=0; j<max_num_intentos; j++)
    				    			logFinal[i][j] = 0;
                    	
    				    	if(numIter[0] == 0 && pol_indx == 0 && !map_changed)
    				    	{
    					    	map = Constants.TEST_MAP_FILES[map_indx];
    			            	String map_path = Constants.TEST_MAPS_PATH + map + ".scm";
    			            	game.setMap(map_path);
    			            	map_changed = true;
    			            	game.restartGame();	
    				    	}
    				    	else if(numIter[0] != 0)
    				    	{
    				    		map_changed = false;
    				    	}
                    	}
                	}	                
                }
                else //!TEST_MODE
                {
                	InicializarQLearner(Constants.ALPHA, Constants.GAMMA, Constants.NUM_ITERACIONES_MAX_QLEARNER, Constants.REWARD_WON, Constants.REWARD_LOST, Policies.BASIC);
                }
                
			 	game.setLocalSpeed(0);
			 	if(Constants.TEST_MODE)	    	
			 		game.setGUI(false);
				
                //game.enableFlag(1); 	// This command allows you to manually control the units during the game.
                						//Is incompatible with the "game.setGUI(false)" command

            }

            @Override
            public void onFrame() {
            	
            	game.setTextSize(10);
                game.drawTextScreen(10, 10, "Playing as " + self.getName() + " - " + self.getRace());                
                
                //first of all, ask if the game is finished. Then we are going to restart the game.
                //in this case, restart all the variables if is needed and change the map/policy if is needed.
                if(StarcraftPresenter.getInstance().isJustFinished()) //we are going to restart the game
                {
                	intento_index++;
                	//escribe la QTabla cada 100 intentos, as� podemos hacer una repetici�n del aprendizaje con el QPlayer
                	if(intento_index%100 == 0)
                		IO_QTable.escribirTabla(q.qTable(), "qtabla_" + map + "_" + policies[pol_indx].name() + "_" + intento_index + ".txt");
            		
                	if(intento_index >= max_num_intentos) //we are going to restart the experiment
                	{        
                		intento_index = 0;
                		
                		// Almacena los datos de este experimento para luego hacer la MEDIA (/NUM_EXPERIMENTOS) de los mismos
				    	//Nos interesa almacenar: Número de pasos utilizados en llegar al final o morir (log)
					    ArrayList<String> log = Log.readLog(Constants.TEST_LOG_FILE);
					    
					    int log_index = 0;
					    for(String l: log)
					    {	
					    	if(l.compareToIgnoreCase(Constants.TEST_DEAD_STRING) == 0) //si el log es de muerto no podemos hacer la media, así que asignaremos el valor máximo
					    	{
					    		logFinal[exper_index][log_index] = (double)Constants.TEST_NUM_ITER_MAX[map_indx];
					    	}
					    	else
					    	{
					    		logFinal[exper_index][log_index] = Double.parseDouble(l);
					    	}
					    	log_index++;
					    }
					    
					    Log.deleteLog(Constants.TEST_LOG_FILE);

					    exper_index++;                	
	                	if(exper_index >= max_num_exper) //we are going to change of politic
	                	{
	                		exper_index = 0; //reinicializa el indice
	                		
	                		//Imprime el log final (HACIENDO LA MEDIA de cada linea PRIMERO)
	                		
	                		double logMedia[] = new double[max_num_intentos]; 
				    		for(int i=0; i<max_num_intentos; i++)
				    			logMedia[i] = 0;
					    	
						    for(int log_line=0; log_line<max_num_intentos; log_line++)
						    {
						    	//calcula el m�ximo de los "max_num_exper" y lo omite en la media				    	
						    	//calcula el m�nimo de los "max_num_exper" y lo omite en la media
						    	double min_value = Double.MAX_VALUE;
						    	double max_value = Double.MIN_VALUE;
						    	double acumulado = 0;
						    	for(int exper=0; exper<max_num_exper; exper++)
						    	{
						    		if(min_value > logFinal[exper][log_line])
						    			min_value = logFinal[exper][log_line];
						    		if(max_value < logFinal[exper][log_line])
						    			max_value = logFinal[exper][log_line];
						    		acumulado = acumulado+logFinal[exper][log_line];
						    	}
						    	acumulado = acumulado-min_value;
						    	acumulado = acumulado-max_value;
						    	double media = acumulado/(double)(max_num_exper-2);
						    	//hace la media con el resto de elementos
						    	for(int exper=0; exper<max_num_exper; exper++)
						    	{
						    		logMedia[log_line] = media;
						    	}
						    }

	    			    	Excel.escribirLog(logMedia, "iters_" + map + "_" + policies[pol_indx].name() + ".xlsx");
	    			    	
	                		//Reinicializa el log
	    			    	logFinal = new double[max_num_exper][max_num_intentos]; //en esta variable almacenaremos los resultados finales de la media de todos los experimentos
    				    	for(int i=0; i<max_num_exper; i++)
    				    		for(int j=0; j<max_num_intentos; j++)
    				    			logFinal[i][j] = 0;
	    			    	
	    			    	pol_indx++;
	                		if(pol_indx >= policies.length) //we are going to change the map
	                		{
	                			pol_indx = 0; //reinicializa el indice
	                			
	                			map_indx++;
	                			if(map_indx >= Constants.TEST_MAP_FILES.length) //we've finished the test
	                			{
	                				game.leaveGame();
	                				time_end = System.currentTimeMillis();
	                		        System.out.println("The complete execution took "+ (( time_end - time_start )/1000) +" seconds");
	                			}
	                			else
	                			{
	                				map = Constants.TEST_MAP_FILES[map_indx];
	                            	String map_path = Constants.TEST_MAPS_PATH + map + ".scm";
	                            	game.setMap(map_path);
	                			}
	                		}	                		
	                	}	                		                	
                	}
                	                	
                	StarcraftPresenter.getInstance().setJustFinished(false);
                	game.restartGame();
                }
                else //not restart
                {
                	numberOfFrames++;
                    //the "average" FPS is 500 in gamespeed=0, 1000 in NoGUI, and 18/19 in normal speed, 
            		//so call the step method each 5 frames (in these 5 frames, the state would be almost the same)
                    if(numberOfFrames >= 10)
                    {
                    	
    	            	if(Constants.TEST_MODE)	
    	            	{
    	        			q.step(); 	//qLearner
    	                	//qp.step(true); 	//qPlayer "RANDOM" (probabilistic)
    	                	//qp.step(false); //qPlayer "FIXED" (most valued action)
    	        			
    	                	numberOfFrames = 0;	
    	            	}
    	            	else //!TEST_MODE
    	            	{
    	        			//q.step(); 	//qLearner
    	                	qp.step(true); 	//qPlayer "RANDOM" (probabilistic)
    	                	//qp.step(false); //qPlayer "FIXED" (most valued action)
    	        			
    	                	numberOfFrames = 0;
    	            	}
                    }
                }
   
            }
            
	        @Override
	        public void onEnd(boolean isWinner) {
	        	time_end = System.currentTimeMillis();
	            System.out.println("The complete execution took "+ (( time_end - time_start )/1000) +" seconds");
	        	//System.out.println("END");
	    		//Log.printLog("log.txt", Integer.toString(numIter));
	    		//IO_QTable.escribirTabla(q.qTable(), Constants.TEST_QTABLE_FILE);
	        }  
            
            
        });

        mirror.startGame();
    }
    
    private void InicializarQLearner(double alpha, double gamma, int num_iter_max_qlearner, double won_reward, double lost_reward, Policies policy_used) {
    	
    	numIter[0] = 0; 

    	marine = getMarine();
    	StarcraftPresenter.setInstance(game, marine, 32, new StarcraftActionManager(), numIter, just_finished);
        
        ArrayList<State> finalStateList = new ArrayList<State>();
        ArrayList<Position> positions = getBalizas();
        
        for(Position p: positions) {
            State finalState = new StarcraftState((int)p.getX()/StarcraftPresenter.getInstance().getBoxSize(), (int)p.getY()/StarcraftPresenter.getInstance().getBoxSize(), game.mapWidth(), game.mapHeight());
            finalStateList.add(finalState);
        }
        
        int[][] visitTable = new int[game.mapWidth()][game.mapHeight()];
		Environment e = new StarcraftEnvironment(game, marine, finalStateList, visitTable, won_reward, lost_reward, num_iter_max_qlearner, policy_used);
		
		QTable qT = IO_QTable.leerTabla(Constants.TEST_QTABLE_FILE);
		if(qT == null) {
			qT = new QTable_Array(e.numStates(), e.numActions(), new StarcraftActionManager());
		}

		q = new QLearner(e, qT, new StarcraftActionManager(), num_iter_max_qlearner, numIter, alpha, gamma);
		qp = new QPlayer(e, qT, new StarcraftActionManager());

	}
    
    private Unit getMarine() {
    	for (Unit myUnit : self.getUnits()) {
			if (myUnit.getType() == UnitType.Terran_Marine) {
				return myUnit;
			}
		}
		return null;
	}
    
    private ArrayList<Position> getBalizas() {
    	ArrayList<Position> p = new ArrayList<Position>();
		for (Unit myUnit : self.getUnits()) {
			if (myUnit.getType().isBeacon()) {
				p.add(myUnit.getPosition());
			}
		}
		return p;
	}
    
    public static void main(String... args) {   
    	Log.deleteLog(Constants.TEST_LOG_FILE);
    	time_start = System.currentTimeMillis();
        new Main_Starcraft().run();
        time_end = System.currentTimeMillis();
        System.out.println("The complete execution took "+ (( time_end - time_start )/1000) +" seconds");
    }
}