package q_learning;

import constants.Constants;
import entrada_salida.Log;

public class QLearner {

	private double ALPHA; //learning rate -> what extent the newly acquired information will override the old information
	private double GAMMA; //discount factor -> importance to future rewards
	
	private Environment environment;
	private QTable qTable;
	private ActionManager actionManager;
	private int[] numIter; //this value is a vector in order to simulate passing it by reference (because java pass the Integers by value always)
	private int maxNumIter;
		
	public QLearner(Environment environment, QTable qTable, ActionManager actionManager, int maxNumIter, int[] numIter, double alpha, double gamma)
	{
		this.ALPHA = alpha;
		this.GAMMA = gamma;
		this.environment = environment;
		this.qTable = qTable;
		this.actionManager = actionManager;
		this.maxNumIter = maxNumIter;
		this.numIter = numIter;
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
				
			if(state != null && action != null) { //the previous state and action will be NULL in the first iteration, 
														// in these cases, we can't update the Q-Table			
				
				// Update Q-Table with:
				//Q(s,a) = Q(s,a) + alpha( r + gamma * max a'(Q(s', a')) - Q(s,a) )
						
				double max_future_value = 0.0; //newState is null when the game ends; in this case we can't use this value in the formula; we use a 0.0 default value
				if(newState!=null) 
					max_future_value = qTable.bestQuantity(newState);
				
				double newValue = qTable.get(state, action.getValue()) + ALPHA * (reward + GAMMA * max_future_value - qTable.get(state, action.getValue()));	
				newValue = Math.max(0, newValue); //this max is to prevent negative values in the QTable
				qTable.set(state, action, newValue);
				
			}

			// 2. Ask if the current state is final, and restart in that case; else perform an action

			if(environment.isFinalState() || numIter[0] >= maxNumIter ) {		
				
									//TODO este if/else es sólo para debug
									if( reward == Constants.REWARD_LOST && numIter[0] != 0) //-> ha muerto, numIter!=0 para que solo se imprima una vez
									{
										Log.printLog(Constants.TEST_LOG_FILE, Constants.TEST_DEAD_STRING);
									}
									else
									{
										Log.printLog(Constants.TEST_LOG_FILE, Integer.toString(numIter[0]));
									}	

				environment.reset();
				numIter[0] = 0;
				
			} else {
				
			// 3. Chooses and Performs a new action
				
				// Choose action
				 newAction = getAction(newState);

				// Execute action
				environment.execute(newAction);
				
				numIter[0]++;
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
	public QTable qTable() {
		return qTable;
	}

}
