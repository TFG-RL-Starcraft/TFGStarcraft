package q_learning;

/**
 * Enum containing the possible actions in each state
 * Every action will have a value, which will be used to index the Qtable
 *
 */
public interface Action {

	// The index value
	public int getValue();
	
	// Return a determinate Action
	public Action get(int value);
	
}
