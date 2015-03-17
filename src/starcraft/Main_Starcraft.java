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
import bwapi.Region;
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
                
                if(numIter!=0){
                	Log.printLog("log.txt", Integer.toString(numIter));
                	numIter= 0;
                }
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
				
				// INICIO - Crear estado final
                Region meta = game.getRegionAt(15, 15);
                //System.out.println(meta.getBoundsLeft() + " " + meta.getBoundsRight() + " " + meta.getBoundsTop() + " " + meta.getBoundsBottom());
                // FIN - Crear estado
                
				State ls = new StarcraftState(0, 0, game.mapWidth(), game.mapHeight());
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
            
/*            private void remakeGame(){
            	Log.printLog("log.txt", Integer.toString(numIter));
            	getMarine();            	
            	numIter = 0;
            	numExper++;
            	game.restartGame();
            	
            	State ls = new StarcraftState(0, 0, game.mapWidth(), game.mapHeight());
				Environment e = new StarcraftEnvironment(game, marine, ls);
				
				QTable qT = q.qTable();
				q = new QLearner(e, qT, StarcraftAction.MOVE_UP);
				qp = new QPlayer(e, qT);
            }*/
 
            @Override
            public void onFrame() {
                game.setTextSize(10);
                game.drawTextScreen(10, 10, "Playing as " + self.getName() + " - " + self.getRace());
                
                if(numExper==100){
                	game.leaveGame();
                }
                
                
                //if some action is done
                if (q.step() != null) 	//qLearner
                {
                	//System.out.println(marine.getPosition().getX() + " " + marine.getPosition().getY());
                	numIter++;
                }
                        
                //qp.step(); 	//qPlayer
            }
            
	        @Override
	        public void onEnd(boolean isWinner) {
	    		System.out.println("END");
	    		//Log.printLog("log.txt", Integer.toString(numIter));
	    		IO_QTable.escribirTabla(q.qTable(), "qtabla.txt");
	    		time_end = System.currentTimeMillis();	    		
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