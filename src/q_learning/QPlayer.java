package q_learning;


public class QPlayer {

	private Environment environment;
	private QTable qTable;
	private Action action;
	
	public QPlayer(Environment environment, QTable qTable, Action action)
	{
		this.environment = environment;
		this.qTable = qTable;
		this.action = action;
	}
	
	// Executes one step in the learning process
	public Action step()
	{     
		Action action = null;
		
		if(environment.stateHasChanged() && !environment.isFinalState()) {
			State state = environment.state();
	
			// Choose action
			action = getAction(state);
	
			// Execute action
			environment.execute(action);
		}
		
		return action;
	}
	
	// Choose the best valued action between possible
	private Action getAction(State state) {

		return qTable.bestAction(state);
		/*
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

		return null;*/
	}
	
}
