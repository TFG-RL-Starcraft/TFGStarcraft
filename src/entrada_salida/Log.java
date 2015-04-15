package entrada_salida;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import q_learning.QTable;
import q_learning.QTable_Array;
import starcraft.Presenter;
import starcraft.StarcraftState;

public class Log {
	
	public static void printLog(String path, String text)
	{
		FileWriter file = null;
        PrintWriter pw = null;
        
        //verifies if the file exists
        File f = new File(path);
        
        try
        {
            file = new FileWriter(path, true);
            pw = new PrintWriter(file);

            pw.println(text);
            
        } catch (Exception e) {
            
        	e.printStackTrace();
        
        } finally {
           
        try {
           
        	// Make sure that closes the file
           if (file != null)              
        	   file.close();
           
	        } catch (Exception e2) {
	              
	        	e2.printStackTrace();
	           
	        }
        }
	}
	
	/*
	 * Devuelve un ArrayList de String, con un String por línea.
	 */
	public ArrayList<String> readLog(String path)
	{
        FileReader fr = null;		
		ArrayList<String> log = new ArrayList<String>();
		
		if (path != null) {
			try {
	         // Open the file in a BufferedReader for easier reading
			 fr = new FileReader(path);
			 BufferedReader br = new BufferedReader(fr);	         
	         
	         String line;
	         while((line=br.readLine()) != null)
	         {
	        	 log.add(line);
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
		
		return log;
	}
}
