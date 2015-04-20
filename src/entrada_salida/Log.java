package entrada_salida;

import java.io.FileWriter;
import java.io.PrintWriter;

public class Log {
	
	public static void printLog(String path, String text)
	{
		FileWriter file = null;
        PrintWriter pw = null;
        
        //verifies if the file exists
        //File f = new File(path);
        
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
}
