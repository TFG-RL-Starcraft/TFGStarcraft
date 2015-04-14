package q_learning;


/**
 * Interface to be implemented by classes that want to learn QLearning.
 * This interface provides a basis for methods to abstract the QLearning algorithm.
 *
 */
public interface Environment {

	// Possible states
	public int numStates();
	
	// Possible actions
	public int numActions();
	
	// Executes an action in the current state
	// Returns the reward
	public void execute(Action action);
	
	// Returns current state
	public State state();
	
	// Returns previous state
	public State previousState();
	
	// Returns previous action taken
	public Action previousAction();
	
	// Current state is final?
	public boolean isFinalState();
	
	// The current state has changed from the previous one
	public boolean stateHasChanged();		

	// Get the reward of a given state
	public double getReward(State state);

	// Reset to initial world state
	public void reset();

}
