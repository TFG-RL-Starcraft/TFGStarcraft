package entrada_salida;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Excel {
	public static void escribirTabla(int[][] Table, String path){
		if(path!=null){
			//Blank workbook
	        XSSFWorkbook workbook = null;
			try {
				workbook = new XSSFWorkbook(new FileInputStream(path));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	         
	        //Create a blank sheet
	        XSSFSheet sheet = workbook.createSheet("visit table");

	        for(int i = 0; i < Table.length; i++){
	        	Row row = sheet.createRow(i);
	        	for(int j = 0; j < Table[i].length; j++){
	        		Cell cell = row.createCell(j);
	        		cell.setCellValue(Table[i][j]);
	        	}
	        }
	        
	        try
	        {
	            //Write the workbook in file system
	            FileOutputStream out = new FileOutputStream(new File(path));
	            workbook.write(out);
	            out.close();
	            System.out.println( path +" written successfully on disk.");
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
		}
	}
	
	
	public static void escribirLog(ArrayList<String> log, String path){
		if(path!=null){
			//Blank workbook
	        XSSFWorkbook workbook = null;
			try {
				workbook = new XSSFWorkbook(new FileInputStream(path));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	         
	        //Create a blank sheet
	        XSSFSheet sheet = workbook.getSheetAt(0);
	        
	        int index = 0;
	        for(String l: log)
	        {
	        	Row row = sheet.createRow(index);
	        	Cell cell = row.createCell(0);
        		cell.setCellValue(Double.parseDouble(l));
	        	index++;
	        }
        
	        try
	        {
	            //Write the workbook in file system
	            FileOutputStream out = new FileOutputStream(new File(path));
	            workbook.write(out);
	            out.close();
	            System.out.println( path + " written successfully on disk.");
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
		}
	}
}
