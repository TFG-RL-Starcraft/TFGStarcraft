package starcraft;
import java.util.ArrayList;

import starcraft.StarcraftEnvironment.SC_Policies;
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
	SC_Policies[] policies = SC_Policies.values();
	private int max_num_intentos = Constants.NUM_INTENTOS_APRENDIZAJE; //número de veces que se realizará el experimento con la misma QTabla. 
	private int num_intentos = 0;			

    public void run() {
        mirror.getModule().setEventListener(new DefaultBWListener() {

            @Override
            public void onStart() {
            	game = mirror.getGame();
                self = game.self();
                
                //Use BWTA to analyze map
                //This may take a few minutes if the map is processed first time!
                System.out.println("Analyzing map...");
                BWTA.readMap();
                BWTA.analyze();
                               
                System.out.println("Map data ready");

            	InicializarQLearner(Constants.ALPHA, Constants.GAMMA, Constants.NUM_ITERACIONES_MAX_QLEARNER, Constants.REWARD_WON, Constants.REWARD_LOST, Constants.STARCRAFT_USED_POLICY);
                
			 	game.setLocalSpeed(0);	    	
			 	game.setGUI(Constants.GUI_MODE);
			 	
			 	time_start = System.currentTimeMillis();				
            }

            @Override
            public void onFrame() {
            	
            	game.setTextSize(10);
                game.drawTextScreen(10, 10, "Playing as " + self.getName() + " - " + self.getRace());                
                
                if(StarcraftPresenter.getInstance().isJustFinished())
                {
                	num_intentos++;
                	if(num_intentos >= max_num_intentos) 
                	{
                		game.leaveGame();
                	}
                	else
                	{
                		StarcraftPresenter.getInstance().setJustFinished(false);
                    	game.restartGame(); 
                	}             	
                }
                
               	numberOfFrames++;
                //the "average" FPS is 500 in gamespeed=0, 1000 in NoGUI, and 18/19 in normal speed, 
        		//so call the step method each 5 frames (in these 5 frames, the state would be almost the same)
                if(numberOfFrames >= 10)
                {
                	
	            	if(Constants.LEARNING_MODE)	
	            	{
	        			q.step(); 	//qLearner
	            	}
	            	else //!PLAYER_MODE
	            	{
	                	qp.step(Constants.QPLAYER_MODE); 	//qPlayer "RANDOM" or "FIXED"
	            	}
	            	
	            	numberOfFrames = 0;
                }
                
            }
            
	        @Override
	        public void onEnd(boolean isWinner) {
	        	IO_QTable.escribirTabla(q.qTable(), Constants.TEST_QTABLE_FILE);
	        	time_end = System.currentTimeMillis();
	            System.out.println("This iteration (" + num_intentos +  ") took "+ (( time_end - time_start )/1000) +" seconds");
	        }  
            
            
        });

        mirror.startGame();
    }
    
    private void InicializarQLearner(double alpha, double gamma, int num_iter_max_qlearner, double won_reward, double lost_reward, SC_Policies policy_used) {
    	
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
    	long total_start = System.currentTimeMillis();
        new Main_Starcraft().run();
        long total_end = System.currentTimeMillis();
        System.out.println("The complete execution took "+ (( total_end - total_start )/1000) +" seconds");
    }
}