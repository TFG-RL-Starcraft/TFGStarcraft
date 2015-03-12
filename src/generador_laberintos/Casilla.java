package generador_laberintos;

import java.awt.Color;

import javax.swing.JButton;


/**
 *
 * @author usuario_local
 */
@SuppressWarnings("serial")
public class Casilla extends JButton
{
    int posX, posY;
    
    TipoCasilla tipo;
    
    public Casilla(int posx,int posy)
    {
    	this.posX = posx;
        this.posY = posy;
        this. tipo = TipoCasilla.VACIO;
    }
    
    public Casilla(Casilla c)
    { 
    	this.posX = c.getPosX();
    	this.posY = c.getPosY();
    	this.tipo = c.getTipo();
    }
    
	public boolean esMeta()
    {
        return this.tipo.compareTo(TipoCasilla.META) == 0;
    }
    public boolean esPared()
    {
        return this.tipo.compareTo(TipoCasilla.PARED) == 0;
    }
    public boolean esInicio()
    {
        return this.tipo.compareTo(TipoCasilla.INICIO) == 0;
    }
    public void setVacio()
    {
    	this.tipo = TipoCasilla.VACIO;
		this.setBackground(Color.white);
    }
    public void setInicio()
    {
    	this.tipo = TipoCasilla.INICIO;
		this.setBackground(Color.blue);
    }
    public void setMeta()
    {
        this.tipo = TipoCasilla.META;
		this.setBackground(Color.green);
    }
    public TipoCasilla getTipo()
    {
		return tipo;
	}
    public void setTipo(TipoCasilla tipo)
    {
        this.tipo = tipo;
    }
    public int getPosX()
    {
        return posX;
    }
    public int getPosY()
    {
        return posY;
    }
    public void setPared(boolean b)
    {
        if(b)
        {
        	this.tipo = TipoCasilla.PARED;
            this.setBackground(Color.black);
        }
        else
        {
        	this.tipo = TipoCasilla.VACIO;
            this.setBackground(Color.white);
        }
    }

	public void reset() {
		this.tipo = TipoCasilla.VACIO;
		this.setBackground(Color.WHITE);
	}

}
