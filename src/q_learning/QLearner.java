package q_learning;

import entrada_salida.Log;


public class QLearner {

	private static double ALPHA = 0.2; //learning rate -> what extent the newly acquired information will override the old information
	private static double GAMMA = 0.5; //discount factor -> importance to future rewards
	
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
			State newState = environment.state();
			State state = environment.previousState();
			Action action = environment.previousAction();

			double reward = environment.getReward(newState);
				
			if(state != null && action != null && newState!=null) { //the previous state and action will be NULL in the first iteration,
				//in that case, we can't update the Q-Table			
				// Update Q-Table
				//Q(s,a) = Q(s,a) + alpha( r + gamma * max a'(Q(s', a')) - Q(s,a) )
				double newValue = qTable.get(state, action.getValue()) + ALPHA * (reward + GAMMA * qTable.bestQuantity(newState) - qTable.get(state, action.getValue()));	
				newValue = Math.max(0, newValue); //TODO, ver hasta qu� punto tiene sentido este max
				qTable.set(state, action, newValue);				
			}

			// 2. Ask if the current state is final, and restart in that case; else perform an action

			if(environment.isFinalState() || numIter >= maxNumIter ) {		
				
									//TODO este if/else es s�lo para debug
									if( reward == -1 && numIter != 0) //reward = -1 -> ha muerto, numIter!=0 para que solo se imprima una vez
									{
										Log.printLog("log.txt", "dead");
									}
									else
									{
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
	public int[][] visitTable() {
		return environment.getVisitTable();
	}
	
	// The full QTable
	public QTable qTable() {
		return qTable;
	}

}
