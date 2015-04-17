package laberinto;

import entrada_salida.Log;


/**
 *
 * @author usuario_local
 */
public class Principal_Laberinto {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

    	Log.deleteLog("log.txt");
        VentanaLaberinto lab = new VentanaLaberinto();      
        lab.setVisible(true);
    }
    
}
