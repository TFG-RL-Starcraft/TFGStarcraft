package generador_laberintos;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Ventana que gestiona todo el proceso de creaci�n del laberinro, carga y guardado.
 * @author Lin
 *
 */
//TODO Como l�neas futuras se podr�a intentar independizar la vista del negocio.
@SuppressWarnings("serial")
public class VentanaGeneradorLaberintos extends javax.swing.JFrame {

    /**
     * Creates new form NewJFrame
     */
    public static final int xboton = 10;
    public static final int yboton = 10;
    public static final int xInicio = 150;
    public static final int yInicio = 50;
       
    //Mantenemos una variable con el laberinto (tablero)
    private Laberinto laberinto; //guarda la referencia a toda la estructura del ejercicio
    
    public VentanaGeneradorLaberintos() {
        initComponents();
    }

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbPaso = new javax.swing.JLabel();
        btSalvarLaberinto = new javax.swing.JButton();
        btCargarLaberinto = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btReset = new javax.swing.JButton();
        btSeleccionarEnemigos = new javax.swing.JButton();
        jTextFieldX = new javax.swing.JTextField();
        jTextFieldY = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        
        seleccionandoEnemigos = false;

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        
        btSalvarLaberinto.setText("SALVAR LABERINTO");
        btSalvarLaberinto.setEnabled(false);
        btSalvarLaberinto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSalvarLaberintoActionPerformed(evt);
            }
        });
        
        btCargarLaberinto.setText("CARGAR LABERINTO");
        btCargarLaberinto.setEnabled(false);
        btCargarLaberinto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	btCargarLaberintoActionPerformed(evt);
            }
        });

        jLabel1.setText("Seleccione la casilla de inicio");

        btReset.setText("Reset");
        btReset.setEnabled(false);
        btReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btResetActionPerformed(evt);
            }
        });

        
        btSeleccionarEnemigos.setText("Seleccionar enemigos");
        btSeleccionarEnemigos.setEnabled(false);
        btSeleccionarEnemigos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	btSeleccionarEnemigosActionPerformed(evt);
            }
        });
        
        jTextFieldX.setText("15");
        jTextFieldX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldXActionPerformed(evt);
            }
        });

        jTextFieldY.setText("15");

        jLabel2.setText("x");

        jButton1.setText("Generar Tablero");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextFieldX, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldY, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(btReset)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btSalvarLaberinto)
                        .addGap(31, 31, 31)
                        .addComponent(btCargarLaberinto)
                        .addGap(31, 31, 31)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(btSeleccionarEnemigos))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(168, 168, 168)
                        .addComponent(lbPaso)))
                .addContainerGap(302, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btSalvarLaberinto)
                    .addComponent(btCargarLaberinto)
                    .addComponent(jLabel1)
                    .addComponent(btReset)
                    .addComponent(jButton1)
                    .addComponent(btSeleccionarEnemigos))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(lbPaso))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))))
                .addContainerGap(516, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
 
    private void btSalvarLaberintoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEmpezarActionPerformed
        salvarLaberintoEnFichero();
    }//GEN-LAST:event_btEmpezarActionPerformed
    
   private void btCargarLaberintoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEmpezarActionPerformed
        cargarLaberintoDesdeFichero();
    }//GEN-LAST:event_btEmpezarActionPerformed

	private void btResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btResetActionPerformed
        reset();
        
    }//GEN-LAST:event_btResetActionPerformed
	
	private void btSeleccionarEnemigosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btResetActionPerformed
		seleccionandoEnemigos = !seleccionandoEnemigos;
		if(seleccionandoEnemigos)
		{
			btSeleccionarEnemigos.setText("seleccionar paredes");
			jLabel1.setText("Seleccionando ENEMIGOS");
		}
			
		else
		{
			btSeleccionarEnemigos.setText("seleccionar enemigos");
			jLabel1.setText("Seleccionando PAREDES");
		}
			
    }//GEN-LAST:event_btResetActionPerformed

    private void jTextFieldXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldXActionPerformed
    }//GEN-LAST:event_jTextFieldXActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        generarTablero(Integer.valueOf(jTextFieldX.getText()), Integer.valueOf(jTextFieldY.getText()));        
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * End of the Constructor
     */
    
  
    private void generarTablero(int x, int y) {
    	jButton1.setEnabled(false);
        jTextFieldX.setEnabled(false);
        jTextFieldY.setEnabled(false);
        btCargarLaberinto.setEnabled(true);
        btSalvarLaberinto.setEnabled(true);
        btReset.setEnabled(true);    	
        
        //Inicializa el laberinto
        laberinto = new Laberinto(new Casilla[x][y], null, null, x, y);
        
        //Inicializa las casillas visualmente y las a�ade a la ventana y al laberinto
        for(int i = 0; i < x ; i++)
        {
            for(int j = 0; j < y;j++)
            {
            	Casilla c = new Casilla(i,j);              
                c.setBounds(xboton*i+xInicio, yboton*j+yInicio, xboton, yboton);
                c.setVisible(true);
                c.setBackground(Color.WHITE);
                c.addActionListener(new ActionListener() 
                {
                    public void actionPerformed(ActionEvent e) {
                        Casilla c_pulsada = (Casilla)e.getSource();                        
                        cambiarCasilla(c_pulsada);                        
                    }

                });
                
                this.add(c);                
                laberinto.setCasilla(i, j, c);
            }
        }
        
        repaint();
	}

    /* CAmbia el estado de una casilla al hacer click */
    private void cambiarCasilla(Casilla c) {
		if(laberinto.getInicio() == null) // si a�n no esta asignado el inicio
		{               
            laberinto.setInicio(c);
            this.repaint();
                           
            jLabel1.setText("Seleccione la meta");
		} 
		else if(laberinto.getMeta() == null) // si a�n no esta asignada la meta
        {
            if(!c.esInicio())
            {        
                laberinto.setMeta(c);
                this.repaint();
                
                btSeleccionarEnemigos.setEnabled(true);
                
                jLabel1.setText("Seleccionando PAREDES");
            }
        }
		else //si ya estan asignados inicio y meta, asigna las paredes o Enemigos
        {
            if(!c.esInicio() && !c.esMeta())
            {
            	if(seleccionandoEnemigos)  
            	{//si es enemigo la pone vacio, y si es vacio lo pone enemigo (si es pared no hace nada)
            		if(!c.esPared())	            	
            			laberinto.setEnemigo(c, !c.esEnemigo());
            	} else {//si es pared la pone vacio, y si es vacio lo pone pared (si es enemigo no hace nada)
            		if(!c.esEnemigo())
            			laberinto.setPared(c, !c.esPared());
            	}
                
                this.repaint();
            }
        }

	}
    
	public void reset()
    {
        jLabel1.setText("Seleccione la casilla de inicio");
        laberinto.reset();
        this.repaint();
    }

    private void salvarLaberintoEnFichero() {
		
    	int maxX = Integer.valueOf(jTextFieldX.getText());
        int maxY = Integer.valueOf(jTextFieldY.getText());
    	
    	FileWriter fichero = null;
        PrintWriter pw = null;
        try
        {
            fichero = new FileWriter("laberinto.txt");
            pw = new PrintWriter(fichero);
 
            
            pw.println(maxX + "," + maxY);            
            for(int j = 0; j < maxY;j++)
            {
                for(int i = 0; i < maxX ; i++)
                {
                	//guarda el valor del Enumerado Tipo
                	pw.print(laberinto.getCasilla(i, j).getTipo().getValor());
                	
                	if(i != maxX-1)
                		pw.print(",");
                	else
                		pw.println();
                }            
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
    
    private void cargarLaberintoDesdeFichero() {
    	//borramos el laberinto anterior
    	int maxX = Integer.valueOf(jTextFieldX.getText());
        int maxY = Integer.valueOf(jTextFieldY.getText());
    	
    	for(int j = 0; j < maxY;j++)
        {
            for(int i = 0; i < maxX ; i++)
            {
            	laberinto.getCasilla(i, j).setEnabled(false);
            	laberinto.getCasilla(i, j).setVisible(false);
            	laberinto.setCasilla(i, j, null);
            }            
        }
    	
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
            jTextFieldX.setText(dim[0]);
            jTextFieldY.setText(dim[1]);
            
            generarTablero(Integer.parseInt(dim[0]), Integer.parseInt(dim[1])); //aqu� genera el tablero y vuelve a dar valor a las variables locales
            jLabel1.setText("Seleccione la casilla de inicio");
            
            //generamos el resto de casillas
            int cont_Y = 0;
            while((linea=br.readLine())!=null)
            {
            	String cas[] = linea.split(",");
            	for(int i = 0; i < maxX ; i++)
            	{
            		if(Integer.parseInt(cas[i]) == TipoCasilla.PARED.getValor())
            		{
            			laberinto.setPared(laberinto.getCasilla(i, cont_Y), true);
            			//a.setPared(this.tablero[i][cont_Y], true);
                        //setPared(this.tablero[i][cont_Y], true);
            		}
            		else if(Integer.parseInt(cas[i]) == TipoCasilla.ENEMIGO.getValor())
                	{
            			laberinto.setEnemigo(laberinto.getCasilla(i, cont_Y), true);
            			//a.setEnemigo(this.tablero[i][cont_Y], true);
                        //setEnemigo(this.tablero[i][cont_Y], true);
                	}
            		else if(Integer.parseInt(cas[i]) == TipoCasilla.INICIO.getValor())
                	{
                        //inicio = this.tablero[i][cont_Y];
                        //setInicio(inicio);
                	}
                	else if(Integer.parseInt(cas[i]) == TipoCasilla.META.getValor())
                	{
                		//meta = this.tablero[i][cont_Y];
                        //setMeta(meta);
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
    
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btReset;
    private javax.swing.JButton btSalvarLaberinto;
    private javax.swing.JButton btCargarLaberinto;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jTextFieldX;
    private javax.swing.JTextField jTextFieldY;
    private javax.swing.JLabel lbPaso;
    
    private javax.swing.JButton btSeleccionarEnemigos;
    private boolean seleccionandoEnemigos;
    // End of variables declaration//GEN-END:variables
}
