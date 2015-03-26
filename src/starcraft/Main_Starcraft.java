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
    private Player enemy;
    private QLearner q;
    private QPlayer qp;
    private int maxIter = 1500;
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
                
                //Player p1 = game.enemies().get(0); //esto hace que no funcione si no encuentra ningún enemigo ¡¡¡TENER CUIDADO!!!

              /*  for(Player p : game.enemies()){
                	if(p.getUnits().size()>0)
                	{
                		enemy = p;
                	}
                }
                
                System.out.println("numero enemigos " + enemy.getUnits().get(0).getType().toString());*/
//                Log.printLog("log.txt", Integer.toString(numIter));
                
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
                
                Position p = getBaliza();
                State ls = new StarcraftState((int)p.getX()/32, (int)p.getY()/32, game.mapWidth(), game.mapHeight());
                
                // FIN - Crear estado
                
				//State ls = new StarcraftState(0, 0, game.mapWidth(), game.mapHeight());
				Environment e = new StarcraftEnvironment(game, marine, ls);
				
				QTable qT = IO_QTable.leerTabla("qtabla.txt");
				if(qT == null) {
					qT = new QTable_Array(e.numStates(), e.numActions(), StarcraftAction.MOVE_UP);
				}
				q = new QLearner(e, qT, StarcraftAction.MOVE_UP,maxIter);
				qp = new QPlayer(e, qT);
				
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
                	//qp.step(); 	//qPlayer
        			
                	numberOfFrames = 0;
                }

            }
            
	        @Override
	        public void onEnd(boolean isWinner) {
	    		System.out.println("END");
	    		//Log.printLog("log.txt", Integer.toString(numIter));
	    		IO_QTable.escribirTabla(q.qTable(), "qtabla.txt");
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
    
    private void getMarine() {
		for (Unit myUnit : self.getUnits()) {
			if (myUnit.getType() == UnitType.Terran_Marine) {
				marine = myUnit;
			}
		}
	}
    
    private Position getBaliza() {
    	Position p = null;
		for (Unit myUnit : self.getUnits()) {
			if (myUnit.getType().isBeacon()) {
				p = myUnit.getPosition();
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