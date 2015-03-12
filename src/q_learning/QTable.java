package q_learning;


/**
 * Interface to the structure that contains the "table" Q(s,a) -> Quantity(state, action)
 *
 */
public interface QTable {

	// Clear the Quantities table
	public void clear();
	
	// Returns the number of states
	public int getStates();
	
	// Returns the number of actions
	public int getActions();
	
	// Returns the Quantity of a pair (state, action)
	public double get(State state, int a);
	
	// Set the Quantity of a pair (state, action)
	public void set(State state, Action action, double quantity);

	// the best Quantity of all the possible actions in a given state
	public double bestQuantity(State state);
	
	// the best Action of all the possible in a given state (or the first one in case of a tie)
	public Action bestAction(State state);
}
