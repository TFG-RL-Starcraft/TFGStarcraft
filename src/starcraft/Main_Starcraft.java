package starcraft;
import entrada_salida.IO_QTable;
import entrada_salida.Log;
import q_learning.Action;
import q_learning.Environment;
import q_learning.QLearner;
import q_learning.QPlayer;
import q_learning.QTable;
import q_learning.QTable_Array;
import q_learning.State;
import bwapi.*;
import bwta.*;

public class Main_Starcraft{

	private static long time_start, time_end;
	
    private Mirror mirror = new Mirror();

    private Game game;
    private Unit marine;
    private Player self;
    private QLearner q;
    private QPlayer qp;
    
    private int numIter; //number of iterations for a experiment
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
                
                getMarine();
                System.out.println("Map data ready");
//                System.out.println("HEIGHT: " + game.mapHeight() + " WIDTH: " + game.mapWidth());
//				System.out.println("MarineX: " + marine.getPosition().getX() / 32 + " MarineY: "
//						+ marine.getPosition().getY() / 32);
				
				
				State ls = new StarcraftState(1, 1, game.mapWidth(), game.mapHeight());
				Environment e = new StarcraftEnvironment(game, marine, ls);
				
				QTable qT = IO_QTable.leerTabla("qtabla.txt");
				if(qT == null) {
					qT = new QTable_Array(e.numStates(), e.numActions(), StarcraftAction.MOVE_UP);
				}
				q = new QLearner(e, qT, StarcraftAction.MOVE_UP);
				qp = new QPlayer(e, qT);
				numIter = 0;
				
				game.setLocalSpeed(0);
				//game.setGUI(false);
				
                //game.enableFlag(1); 	// This command allows you to manually control the units during the game.
                						//Is incompatible with the "game.setGUI(false)" command
				
				
            }
 
            @Override
            public void onFrame() {
                game.setTextSize(10);
                game.drawTextScreen(10, 10, "Playing as " + self.getName() + " - " + self.getRace());

                //if some action is done
                if (q.step() != null) 	//qLearner
                {
                	numIter++;
                }
                
                //qp.step(); 	//qPlayer
            }
            
	        @Override
	        public void onEnd(boolean isWinner) {
	    		System.out.println("END");
	    		Log.printLog("log.txt", Integer.toString(numIter));
	    		//q.endOfGame();
	    		IO_QTable.escribirTabla(q.qTable(), "qtabla.txt");
	    		time_end = System.currentTimeMillis();	    		
	    		numExper++;
	            System.out.println("The Experiment " + numExper + " has taken "+ ( time_end - time_start ) +" milliseconds");  
	        }  
            
            
        });

        mirror.startGame();
    }
    
    private void getMarine() {
		for (Unit myUnit : self.getUnits()) {
			if (myUnit.getType() == UnitType.Terran_Marine) {
				marine = myUnit;
			}
		}
	}
    
    public static void main(String... args) {   
    	numExper = 0;
    	time_start = System.currentTimeMillis();
        new Main_Starcraft().run();
        time_end = System.currentTimeMillis();
        System.out.println("The task has taken "+ ( time_end - time_start ) +" milliseconds");
    }
}