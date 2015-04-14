package q_learning;


public class QPlayer {

	private Environment environment;
	private QTable qTable;
	private ActionManager actionManager;
	
	public QPlayer(Environment environment, QTable qTable, ActionManager actionManager)
	{
		this.environment = environment;
		this.qTable = qTable;
		this.actionManager = actionManager;
	}
	
	// Executes one step in the learning process
	// "ramdom_choice" is the behavior in the choice of the next movement (the longest value, or taken a random possibility)
	public Action step(boolean random_choice)
	{     
		Action action = null;
		
		if(environment.stateHasChanged() && !environment.isFinalState()) {
			State state = environment.state();
	
			// Choose action
			action = getAction(state, random_choice);
	
			// Execute action
			environment.execute(action);
		}
		
		return action;
	}
	
	// Choose the best valued action between possible
	private Action getAction(State state, boolean random_choice) {

		if(!random_choice) {
			return qTable.bestAction(state);
			
		} else {			
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
	}
	
}
