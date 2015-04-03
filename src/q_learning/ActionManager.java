package q_learning;

import java.util.HashMap;

public abstract class ActionManager {
	
	protected HashMap<Integer, Action> map = new HashMap<Integer, Action>();

	public Action get(int value) {
		return map.get(value);		
	}
	
	public int getNumActions() {
		return map.size();
	}
}
