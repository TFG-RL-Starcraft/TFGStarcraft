package laberinto;

import entrada_salida.Log;
import entrada_salida.Excel;
import generador_laberintos.Casilla;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import laberinto.actions.LaberintoActionManager;
import q_learning.Environment;
import q_learning.QLearner;
import q_learning.QPlayer;
import q_learning.QTable;
import q_learning.QTable_Array;

@SuppressWarnings("serial")
public class VentanaLaberinto extends javax.swing.JFrame {
	
	//Variables creadas por mí, para generar la ventana
    public static final int xboton = 10; //ancho y alto de las celdas
    public static final int yboton = 10;
    public static final int xInicio = 150; //posicio de comienzo del tablero
    public static final int yInicio = 50;
    
    public static final int VACIO = 0;
    public static final int PARED = 1;
    public static final int INICIO = 2;
    public static final int META = 3;
    public static final int ENEMIGO = 4;
    
    public static final int NUM_ITERACIONES_MAX_QLEARNER = 200; //número máximo de iteraciones (pasos) de cada intento
    public static final int NUM_INTENTOS_APRENDIZAJE = 500; //número de veces que se realizará el experimento con la misma QTabla. 
    							//Cada intento se reinicia al "personaje" en la posición inicial y consta de NUM_ITERACIONES_MAX_QLEARNER pasos. 
    public static final int NUM_EXPERIMENTOS = 100; //numero de experimentos completos, cada experimento consta de varios INTENTOS
    							//de los cuales luego haremos una media de los datos obtenidos, para obtener las gráficas
    
    private Casilla tablero[][]; //arraylist de JButtons para crear el tablero
    private Casilla salida;
    private Casilla meta;  
    
    private LaberintoState estado_actual;
    private boolean terminado;
    
    private QLearner q; //guarda la referencia a toda la estructura del ejercicio
    private Environment env;
    QTable qT;  
    int[][] tableroVisitas; //tabla en la que guardamos cuantas veces se pasa por cada estado
    
    ArrayList<Integer> listaEnemigos; //arraylist con los indices de los estados de las casillas con enemigo
    
    int maxX = 15; //casillas máximas en horizontal y vertical
    int maxY = 15;
     
    // Variables declaration - do not modify
    private javax.swing.JButton btEmpezar;
    private javax.swing.JButton btCargarLaberinto;
   // End of variables declaration
	
    public VentanaLaberinto() {
        initComponents();

    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    private void initComponents() {

        btEmpezar = new javax.swing.JButton();
        btCargarLaberinto = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btEmpezar.setText("Empezar");
        btEmpezar.setEnabled(false);
        btEmpezar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEmpezarActionPerformed(evt);
            }
        });
        btEmpezar.setBounds(10, 10, 105, 30);
        btEmpezar.setVisible(true);
        this.add(btEmpezar);
        
        btCargarLaberinto.setText("Cargar Laberinto");
        btCargarLaberinto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	btCargarLaberintoActionPerformed(evt);
            }
        });
        btCargarLaberinto.setBounds(115, 10, 150, 30);
        btCargarLaberinto.setVisible(true);
        this.add(btCargarLaberinto);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        
        this.setSize(800, 600);
        
        generarLaberinto();

    }

    
    private void btEmpezarActionPerformed(java.awt.event.ActionEvent evt) {

    	double[] logFinal = new double[NUM_INTENTOS_APRENDIZAJE]; //en esta variable almacenaremos los resultados finales de la media de todos los experimentos
    	for(int d=0; d<NUM_INTENTOS_APRENDIZAJE; d++)
    		logFinal[d] = 0;
    	
        //Realiza NUM_EXPERIMENTOS pruebas y va almacenando la media de los resultados        
    	for(int i=0; i<NUM_EXPERIMENTOS; i++)
    	{
        	//Por el momento vamos a omitir el tiempo de ejecución, ya que no es relevante.
        	//Esto es porque para medir la eficacia de los algoritmosmediremos el número de iteraciones
            long start = System.currentTimeMillis();
    		
    		// 1.Inicializa y "resetea" las variables y tablas
    		InicializarQLearner();
    		
    		// 2.Ejecuta el experimento (que consta de muchos intentos seguidos del proceso de aprendizaje)
		    //Realiza NUM_INTENTOS_APRENDIZAJE llamadas al método step de QLearner con las misma QTabla  
		    for(int j=0; j<NUM_INTENTOS_APRENDIZAJE; j++)
		    {
		    	//Ejecuta el experimento hasta llegar a la meta, morir o llegar al NUM_ITERACIONES
		    	//Como la aplicación del laberinto no se ejecuta en un bucle infinito como el Starcraft
				//Tenemos que definir de alguna forma un bucle "infinito"
				//Lo hacemos mediante la varible "terminado" a la que el LaberintoEnvironment puede acceder.
		    	terminado = false;
		    	while(!terminado)
		    		q.step();
		    }
		    
		    // 3.Almacena los datos haciendo la MEDIA (/NUM_EXPERIMENTOS) de los mismos
		    	//Nos interesa almacenar: 1. Número de pasos utilizados en llegar al final o morir (log)
		    							//2. Número de veces que se accede a cada estado (tableroVisitas)
		    							//3. Valor de de la QTabla para cada acción en cada estado (QTable)
		    
		    ArrayList<String> log = Log.readLog("log.txt");
		    
		    int log_index = 0;
		    for(String l: log)
		    {	
		    	if(l.compareToIgnoreCase("dead") == 0) //si el log es de muerto no podemos hacer la media, así que asignaremos el valor máximo
		    	{
		    		logFinal[log_index] = logFinal[log_index] + (double)NUM_ITERACIONES_MAX_QLEARNER/(double)NUM_EXPERIMENTOS;
		    	}
		    	else
		    	{
		    		logFinal[log_index] = logFinal[log_index] + Double.parseDouble(l)/(double)NUM_EXPERIMENTOS;
		    	}
		    	log_index++;
		    }
		    
		    Log.deleteLog("log.txt");
 
		    
		    long end = System.currentTimeMillis();
	        long res = end - start;
	        System.out.println("EXPERIMENTO " + i + " TARDÓ : " + res/1000.0 + "segs.");
    	}
    	
        //Imprime el log final
    	Excel.escribirLog(logFinal, "log.xlsx");
              
		//Imprime el mejor camino
        imprimeMejorCamino();  
        
        //añade a pantalla los valores de la QTable
        imprimeValoresQTabla(); 
        
        //imprime el excel con la tabla de los estados visitados
        imprimeTablaVisitas();
    }

	private void btCargarLaberintoActionPerformed(ActionEvent evt) {
    	InicializarTablero();
    	btEmpezar.setEnabled(true);
	}


    private void InicializarQLearner() {
    	LaberintoState casilla_inicial = new LaberintoState(salida.getPosX(), salida.getPosY(), maxX, maxY);
        LaberintoState casilla_final = new LaberintoState(meta.getPosX(), meta.getPosY(), maxX, maxY);
        this.estado_actual = new LaberintoState(casilla_inicial, maxX, maxY); 
        tableroVisitas = new int[maxY][maxX];
        PresenterLaberinto.setInstance(this, new LaberintoActionManager(), terminado, maxX, maxY);
        env = new LaberintoEnvironment(maxX, maxY, casilla_inicial, casilla_final, tableroVisitas, listaEnemigos);
        qT = new QTable_Array(env.numStates(), env.numActions(), new LaberintoActionManager());        
        q = new QLearner(env, qT, new LaberintoActionManager(), NUM_ITERACIONES_MAX_QLEARNER); //INICIALIZA LA ESTRUCTURA PARA EL ALGORITMO
       
	}


	private void InicializarTablero() {
    	//borramos el laberinto anterior
    	for(int j = 0; j < maxY;j++)
        {
            for(int i = 0; i < maxX ; i++)
            {
            	this.tablero[i][j].setEnabled(false);
            	this.tablero[i][j].setVisible(false);
            	this.tablero[i][j] = null;
            }            
        }
    	listaEnemigos = new ArrayList<Integer>();
    	
    	//leemos el fichero y generamos el nuevo laberinto
    	FileReader fichero = null;
    	BufferedReader br = null;
        try
        {
            fichero = new FileReader("laberinto.txt");
            br = new BufferedReader(fichero);
 
            String linea;
            linea=br.readLine();

            //generamos un tablero de las dimensioes de la primera linea
            String dim[] = linea.split(",");
            maxX = Integer.parseInt(dim[0]);
            maxY = Integer.parseInt(dim[1]);
            
            generarLaberinto(); //aquí genera el tablero y vuelve a dar valor a las variables locales
            
            //generamos el resto de casillas
            int cont_Y = 0;
            while((linea=br.readLine())!=null)
            {
            	String cas[] = linea.split(",");
            	for(int i = 0; i < maxX ; i++)
            	{
            		if(Integer.parseInt(cas[i]) == PARED)
            		{
                        setPared(this.tablero[i][cont_Y]);
            		}
                	else if(Integer.parseInt(cas[i]) == INICIO)
                	{
                		salida = this.tablero[i][cont_Y];
                        setSalida(salida);
                	}
                	else if(Integer.parseInt(cas[i]) == META)
                	{
                		meta = this.tablero[i][cont_Y];
                        setMeta(meta);
                	}
                	else if(Integer.parseInt(cas[i]) == ENEMIGO)
                	{
                		setEnemigo(this.tablero[i][cont_Y]);
                		listaEnemigos.add(new LaberintoState(i, cont_Y, maxX, maxY).getValue());
                	}

            	}
            	cont_Y++;
            }
  
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
           try {
           // Nuevamente aprovechamos el finally para 
           // asegurarnos que se cierra el fichero.
           if (null != fichero)
              fichero.close();
           } catch (Exception e2) {
              e2.printStackTrace();
           }
        }
	}


	private void generarLaberinto() {

        tablero = new Casilla [maxX][maxY];
        /* Inicializa las casillas visualmente y las aÃ±ade a la ventana*/
        for(int i = 0; i < maxX ; i++)
        {
            for(int j = 0; j < maxY;j++)
            {
                tablero[i][j] = new Casilla(i,j);
                tablero[i][j].setBounds(xboton*i+xInicio, yboton*j+yInicio, xboton, yboton);
                tablero[i][j].setVisible(true);
                tablero[i][j].setBackground(Color.WHITE);              
                this.add(tablero[i][j]);
                tablero[i][j].addActionListener(new ActionListener() 
                {
                    public void actionPerformed(ActionEvent e) {
                        Casilla c = (Casilla)e.getSource();
                        System.out.println(c.getText());
                    }
                });
            }
        }

        repaint();
    }
    
    
    public void setSalida(Casilla salida)
    {
    	salida.setInicio();
    	salida.setBackground(Color.blue);
        
        this.repaint();
    }
    
    
    public void setMeta(Casilla meta)
    {
    	meta.setMeta();
    	meta.setBackground(Color.green);
        
        this.repaint();
    }
    

    public void setPared(Casilla pared)
    {    
    	pared.setPared(true);
    	pared.setBackground(Color.black);
       
        this.repaint();
    }
    
    
    public void setEnemigo(Casilla pared)
    {    
    	pared.setEnemigo(true);
    	pared.setBackground(Color.red);
       
        this.repaint();
    }
	
	
    public Casilla getCasilla(int x, int y)
    {
        return this.tablero[x][y];
    }

 
    private void imprimeTablaVisitas(){
    	Excel.escribirTabla(tableroVisitas,"visitMap.xlsx");
    }
    
    
    private void imprimeValoresQTabla() {
    	for(int i = 0; i < maxX ; i++)
        {
            for(int j = 0; j < maxY;j++)
            {
            	tablero[i][j].setText("UP: " + Double.toString(qT.get(new LaberintoState(i,j,maxX,maxY), 0)) + 
            			", RIGHT: " + Double.toString(qT.get(new LaberintoState(i,j,maxX,maxY), 1)) + 
            			", DOWN: " + Double.toString(qT.get(new LaberintoState(i,j,maxX,maxY), 2)) + 
            			", LEFT: " + Double.toString(qT.get(new LaberintoState(i,j,maxX,maxY), 3)) + "\n" +
            			"UP-LEFT: " + Double.toString(qT.get(new LaberintoState(i,j,maxX,maxY), 4)) + 
            			", UP-RIGHT: " + Double.toString(qT.get(new LaberintoState(i,j,maxX,maxY), 5)) + 
            			", DOWN-LEFT: " + Double.toString(qT.get(new LaberintoState(i,j,maxX,maxY), 6)) + 
            			", DOWN-RIGHT: " + Double.toString(qT.get(new LaberintoState(i,j,maxX,maxY), 7))             			
            			);
            }
        }	
	}
    
    
    private void imprimeMejorCamino() {
    	LimpiarTablero();
    	env.reset();
    	QPlayer qp = new QPlayer(env, qT, new LaberintoActionManager());

    	//Ejecuta el player hasta llegar a la meta
        while(!env.isFinalState())
        {
        	qp.step(false);       	
        }	
	}
    

	private void LimpiarTablero() {
		for(int i = 0; i < maxX ; i++)
    	{
			for(int j = 0; j < maxY ; j++)
			{
	    		if(tablero[i][j].esPared())
	    		{
	    			tablero[i][j].setBackground(Color.black);
	    		}
	        	else if(tablero[i][j].esInicio())
	        	{
	        		tablero[i][j].setBackground(Color.blue);
	        	}
	        	else if(tablero[i][j].esMeta())
	        	{
	        		tablero[i][j].setBackground(Color.green);
	        	}
	        	else if(tablero[i][j].esEnemigo())
	        	{
	        		tablero[i][j].setBackground(Color.red);
	        	}
	        	else
	        	{
	        		tablero[i][j].setBackground(Color.white);
	        	}
			}
    	}
	}


	public void mover(int posX, int posY) {
		tablero[posX][posY].setBackground(Color.yellow);
		estado_actual = new LaberintoState(posX, posY, maxX, maxY); 
	}


	public LaberintoState getEstadoActual() {
		return estado_actual;
	}


	public void setEstadoActual(LaberintoState State) {
		this.estado_actual = State;
	}


	public void setTerminado(boolean b) {
		this.terminado = b;
	}
    
}
