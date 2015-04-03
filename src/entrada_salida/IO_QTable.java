package entrada_salida;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter; 
import java.io.PrintWriter;
import java.util.StringTokenizer;

import q_learning.QTable;
import q_learning.QTable_Array;
import starcraft.Presenter;
import starcraft.StarcraftState;

public class IO_QTable {
	
	
	public static void escribirTabla(QTable qTable, String path){
		FileWriter fw = null;
        PrintWriter pw = null;
        
        if(path != null) {
	        try {
	            fw = new FileWriter(path);
	            pw = new PrintWriter(fw);
	            
	            pw.println(qTable.getStates() + "\n" + qTable.getActions());
	            
	            for(int i = 0; i < qTable.getStates();i++){
	            	for(int j = 0; j < qTable.getActions(); j++){
	            		pw.print(qTable.get(new StarcraftState(i), j));
	            		if(j!=qTable.getActions()-1){
	            			pw.print(" ");
	            		}
	            	}
	            	pw.println();
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	           try {
	           // Nuevamente aprovechamos el finally para 
	           // asegurarnos que se cierra el fichero.
	           if (null != fw)
	              fw.close();
	           } catch (Exception e2) {
	              e2.printStackTrace();
	           }
	        }
        }
	}
	
	public static QTable leerTabla(String path){
		FileReader fr = null;		
		QTable qTable = null;
		
		if (path != null) {
			try {
	         // Open the file in a BufferedReader for easier reading
			 fr = new FileReader(path);
			 BufferedReader br = new BufferedReader(fr);	         
	         
	         String line = br.readLine();
	         int num_states = Integer.parseInt(line);
	         line = br.readLine();
	         int num_actions = Integer.parseInt(line);
	         qTable = new QTable_Array(num_states,num_actions, Presenter.getInstance().getStarcraftActionManager());
	         
	         int i=0;
	         while((line=br.readLine()) != null){
	        	double quantity[] = parse(line);
	        	 
            	for(int j = 0; j < quantity.length; j++){
            		qTable.set(new StarcraftState(i), Presenter.getInstance().getStarcraftActionManager().get(j), quantity[j]);
            	}
	            i++;
	         }
	         
	         
	      } catch(Exception e) {
	         e.printStackTrace();
	      } finally {
	         // Make sure that closes the file
	         try {                    
	            if(fr != null) {   
	               fr.close();     
	            }                  
	         } catch(Exception e2) { 
	            e2.printStackTrace();
	         }
	      }
		}
		
		return qTable;
	}

	private static double[] parse(String s)
	{
		StringTokenizer st = new StringTokenizer(s," ");
		
		int num= st.countTokens();
		String tokens[] = new String[num];
		int i=0;
		while(st.hasMoreTokens())
		{
			tokens[i]= st.nextToken();
			i++;
		}
		
		double d[]  = new double[num];
		
		for(int j = 0; j < num;j++)
		{
			d[j]= Double.valueOf(tokens[j]);
		}	
	
		
		return d;
	}
}