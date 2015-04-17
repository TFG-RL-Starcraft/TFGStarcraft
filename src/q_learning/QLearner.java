package q_learning;

import constants.Constants;
import entrada_salida.Log;


public class QLearner {

	private static double ALPHA = Constants.ALPHA; //learning rate -> what extent the newly acquired information will override the old information
	public static double GAMMA = Constants.GAMMA; //discount factor -> importance to future rewards
	
	private Environment environment;
	private QTable qTable;
	private ActionManager actionManager;
	private int numIter;
	private int maxNumIter;
		
	public QLearner(Environment environment, QTable qTable, ActionManager actionManager, int maxNumIter)
	{
		this.environment = environment;
		this.qTable = qTable;
		this.actionManager = actionManager;
		this.maxNumIter = maxNumIter;
		numIter = 0;
	}
	
	// Executes one step in the learning process if the state has changed
	public Action step()
	{     
		Action newAction = null;		
	
		if(environment.stateHasChanged()) {
	
			// 1. Gets the reward of the PREVIOUS action, and Update the Q-Table of it
			
			State state = environment.previousState();
			Action action = environment.previousAction();
			State newState = environment.state();

			double reward = environment.getReward(newState);
				
			if(!environment.isFinalState() && state != null && action != null && newState!=null) { //the previous state and action will be NULL in the first iteration, 
						//and the newState can be null if the game ends; in these cases, we can't update the Q-Table			
				
				// Update Q-Table
				//Q(s,a) = Q(s,a) + alpha( r + gamma * max a'(Q(s', a')) - Q(s,a) )
				rewardIt(state,action,newState,reward);		
			}

			// 2. Ask if the current state is final, and restart in that case; else perform an action

			if(environment.isFinalState() || numIter >= maxNumIter ) {		
				
									//TODO este if/else es sólo para debug
									if( reward == -1 && numIter != 0) //reward = -1 -> ha muerto, numIter!=0 para que solo se imprima una vez
									{
										rewardIt(state,action,newState,reward);
										Log.printLog("log.txt", "dead");
									}
									else
									{
										function(state, action,newState);
										Log.printLog("log.txt", Integer.toString(numIter));
									}										

				environment.reset();
				numIter = 0;
				
			} else {
				
			// 3. Chooses and Performs a new action
				
				// Choose action
				 newAction = getAction(newState);

				// Execute action
				environment.execute(newAction);
				
				numIter++;
			}

		}
		
		return newAction;
	}
	
	private void rewardIt(State state,Action action,State newState,double reward){
		double newValue = qTable.get(state, action.getValue()) + ALPHA * (reward + GAMMA * qTable.bestQuantity(newState) - qTable.get(state, action.getValue()));	
		newValue = Math.max(0, newValue); //TODO, ver hasta qué punto tiene sentido este max
		qTable.set(state, action, newValue);	
	}
	
	private void function(State state,Action action,State newState){
		double A = (Constants.REWARD_WON - 10.0) / Math.pow(Constants.NUM_PASOS,2);
		double reward = A * Math.pow(numIter, 2) + Constants.REWARD_WON;		
		double newValue = qTable.get(state, action.getValue()) + ALPHA * (reward + GAMMA * qTable.bestQuantity(newState) - qTable.get(state, action.getValue()));	
		newValue = Math.max(0, newValue); //TODO, ver hasta qué punto tiene sentido este max
		qTable.set(state, action, newValue);
	}

	// Choose a random action considering the probabilities of each
	private Action getAction(State state) {
		double total = 0;
		for (int a = 0; a<environment.numActions(); a++) {
			total += qTable.get(state, a);
		}

		double random = Math.random() * total;

		total = 0;
		for (int a = 0; a<environment.numActions(); a++) {
			total += qTable.get(state, a);
			if (total >= random)
				return actionManager.get(a);
		}

		return null;
	}
	
	// The full QTable
	public QTable qTable() {
		return qTable;
	}

}
