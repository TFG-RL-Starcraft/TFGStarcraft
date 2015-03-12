package generador_laberintos;

/**
 * Enumerado con los tipos de casilla posibles.
 * Mantienen un valor entero para cuando exportemos el laberinto a un fichero.
 * @author Lin
 *
 */
public enum TipoCasilla
{
	VACIO(0),
	PARED(1),
	INICIO(2),
	META(3);
	
    private final int valor;
	
    TipoCasilla(int valor) { 
        this.valor = valor;
	}
	
	public int getValor()
	{
		return this.valor;
	}
}
