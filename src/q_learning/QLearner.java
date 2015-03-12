package q_learning;


public class QLearner {

	private static double ALPHA = 0.2; //learning rate -> what extent the newly acquired information will override the old information
	private static double GAMMA = 0.5; //discount factor -> importance to future rewards
	private static double RANDOM_ACTION_PROB = 0.1;
	
	private Environment environment;
	private QTable qTable;
	private Action action;
		
	public QLearner(Environment environment, QTable qTable, Action action)
	{
		this.environment = environment;
		this.qTable = qTable;
		this.action = action;
	}
	
	// Executes one step in the learning process if the state has changed
	public Action step()
	{     
		Action action = null;
		
		if(environment.stateHasChanged()) {
			State state = environment.state();
	
			// Choose action
			 action = getAction(state);
	
			// Execute action
			double reward = environment.execute(action);
			State newState = environment.state();
			
			// Update Q-Table
			//Q(s,a) = Q(s,a) + alpha( r + gamma * max a'(Q(s', a')) - Q(s,a) )
			double newValue = qTable.get(state, action.getValue()) + ALPHA * (reward + GAMMA * qTable.bestQuantity(newState) - qTable.get(state, action.getValue()));
			newValue = Math.max(0, newValue); //TODO, ver si tiene sentido este max
			qTable.set(state, action, newValue);	
		}
		
		return action;
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
				return action.get(a);
		}

		return null;
	}
	
	// The full QTable
	public QTable qTable() {
		return qTable;
	}

}
