package generador_laberintos;


/**
 * Clase que contiene la estructura de un laberinto (tablero) que nos servirá
 * posteriormente para poder ejecutar el algoritmo Q-Learning sobre él.
 * El laberinto está compuesto de Casillas (JButton) que tienen un tipo (INICIO, META, PARED...)
 * @author Lin
 *
 */
public class Laberinto 
{
    Casilla [][] tablero;
    Casilla inicio;
    Casilla meta ;
    int limX;
    int limY;
    
    public Laberinto(Casilla [][] tablero, Casilla inicio, Casilla meta, int limX, int limY)
    {
        this.tablero = tablero;
        this.limX = limX;
        this.limY = limY;
        this.inicio = inicio;
        this.meta = meta;
    }

    public Casilla getCasilla(int x, int y) {
		return tablero[x][y];
	}
    
    public void setCasilla(int x, int y, Casilla c) {
		this.tablero[x][y] = c;
	}
    
	public Casilla getInicio() {
		return inicio;
	}

	public void setInicio(Casilla inicio) {
		this.inicio = inicio;
		inicio.setInicio();
	}

	public Casilla getMeta() {
		return meta;
	}

	public void setMeta(Casilla meta) {
		this.meta = meta;
		meta.setMeta();
	}

	public int getLimX() {
		return limX;
	}

	public void setLimX(int limX) {
		this.limX = limX;
	}

	public int getLimY() {
		return limY;
	}

	public void setLimY(int limY) {
		this.limY = limY;
	}

	public void setPared(Casilla c, boolean b) {
		c.setPared(b);	
	}

	public void reset() {
		inicio = null;
		meta = null;
		
		for(int i = 0; i < this.limX ; i++)
        {
            for(int j = 0; j < this.limY;j++)
            {
            	tablero[i][j].reset();
                
            }
        }
		
	}

}

