package laberinto;

import q_learning.State;
import generador_laberintos.Casilla;

public class LaberintoState extends Casilla implements State{

	private static final long serialVersionUID = 1L;
	
	int width, height;
	
	public LaberintoState(int posx,int posy, int width, int height)
    {
		super(posx,posy);
        this.width = width;
        this.height = height;
    }
    
    public LaberintoState(Casilla c)
    { 
    	super(c);
    	this.width = c.getWidth();
    	this.height = c.getHeight();
    }
    
    public int getWidth()
    {
        return width;
    }
    public int getHeight()
    {
        return height;
    }
    

	@Override
	public int getValue() {
		return this.getPosY() * width + this.getPosX();
	}
}
