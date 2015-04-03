package q_learning;


/**
 * Every action will have a value, which will be used to index the Qtable
 *
 */
public abstract class Action {

	//Action value
    private final int value;
	
    protected Action(int value) { 
        this.value = value;
	}
	
    // The index value
	public int getValue() {
		return this.value;
	}

	// Configure the context to easily execute the action
	public abstract void configureContext();
	
	// Execute the Action
	public abstract void execute();
	
}
