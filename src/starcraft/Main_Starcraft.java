package starcraft;
import java.util.ArrayList;

import starcraft.StarcraftEnvironment.Policies;
import constants.Constants;
import entrada_salida.IO_QTable;
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
	
    private Mirror mirror = new Mirror();

    private Game game;
    private Unit marine;
    private Player self;
    //private Player enemy;
    private QLearner q;
	private QPlayer qp;    
    int numberOfFrames = 0;
    
    private static int numExper; //number of experiments

    public void run() {
        mirror.getModule().setEventListener(new DefaultBWListener() {
//            @Override
//            public void onUnitCreate(Unit unit) {
//                System.out.println("New unit " + unit.getType());
//            }

            @Override
            public void onStart() {
                game = mirror.getGame();
                self = game.self();
                
                //Use BWTA to analyze map
                //This may take a few minutes if the map is processed first time!
                System.out.println("Analyzing map...");
                BWTA.readMap();
                BWTA.analyze();
                
                marine = getMarine();
                System.out.println("Map data ready");
                
                InicializarQLearner(Constants.ALPHA, Constants.GAMMA, Constants.NUM_ITERACIONES_MAX_QLEARNER, Constants.REWARD_WON, Constants.REWARD_LOST, Policies.BASIC);								
				
			 	game.setLocalSpeed(0);
				//game.setGUI(false);
				
                //game.enableFlag(1); 	// This command allows you to manually control the units during the game.
                						//Is incompatible with the "game.setGUI(false)" command

            }

            @Override
            public void onFrame() {
                game.setTextSize(10);
                game.drawTextScreen(10, 10, "Playing as " + self.getName() + " - " + self.getRace());

        		numberOfFrames++;
                //the "average" FPS is 500 in gamespeed=0, 1000 in NoGUI, and 18/19 in normal speed, 
        		//so call the step method each 5 frames (in these 5 frames, the state would be almost the same)
                if(numberOfFrames >= 5)
                {
        			q.step(); 	//qLearner
                	//qp.step(true); 	//qPlayer "RANDOM" (probabilistic)
                	//qp.step(false); //qPlayer "FIXED" (most valued action)
        			
                	numberOfFrames = 0;
                }
                
                //game.leaveGame();
                
            }
            
	        @Override
	        public void onEnd(boolean isWinner) {
	    		System.out.println("END");
	    		//Log.printLog("log.txt", Integer.toString(numIter));
	    		IO_QTable.escribirTabla(q.qTable(), Constants.TEST_QTABLE_FILE);
	    		time_end = System.currentTimeMillis();	    		
	            System.out.println("The Experiment " + numExper + " has taken "+ ( time_end - time_start ) +" milliseconds");  
	            numExper++;
	            
	            if(numExper == 2){
                	game.leaveGame();
                }
	        }  
            
            
        });

        mirror.startGame();
    }
    
    private void InicializarQLearner(double alpha, double gamma, int num_iter_max_qlearner, double won_reward, double lost_reward, Policies policy_used) {
    	
    	numIter[0] = 0; 

    	StarcraftPresenter.setInstance(game, marine, 32, new StarcraftActionManager(), numIter);
        
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
    	numExper = 0;
    	time_start = System.currentTimeMillis();
        new Main_Starcraft().run();
        time_end = System.currentTimeMillis();
        System.out.println("The task has taken "+ ( time_end - time_start ) +" milliseconds");
    }
}