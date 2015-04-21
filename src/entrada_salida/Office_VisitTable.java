package entrada_salida;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import constants.Constants;

public class Office_VisitTable {	
	public static void escribirTabla(int[][] Table, String path){
		if(path!=null){
			//Blank workbook
	        XSSFWorkbook workbook = null;
			try {
				workbook = new XSSFWorkbook(new FileInputStream(path));
			} catch (FileNotFoundException e1) {
				workbook = new XSSFWorkbook();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        int nSheets = workbook.getNumberOfSheets();
	        //Create a blank sheet
	        XSSFSheet sheet = workbook.createSheet("visit table".concat(Integer.toString(nSheets)));

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
	            System.out.println("visitMap.xlsx written successfully on disk.");
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
		}
	}
	
	public static void escribirLog(String path,int pasos,int iter){
		if(path!=null){
			//Blank workbook
	        XSSFWorkbook workbook = null;
			try {
				workbook = new XSSFWorkbook(new FileInputStream(path));
			} catch (FileNotFoundException e1) {
				workbook = new XSSFWorkbook();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

	        //Create a blank sheet
	        XSSFSheet sheet = workbook.getSheet("Hoja1");
	        if(sheet==null)
	        	sheet = workbook.createSheet("Hoja1");
	        
	        Row row = sheet.getRow(iter);
	        
	        if(row!=null){
		        Cell c = row.getCell(0);
		        if(c!=null){
			        int g = (int)c.getNumericCellValue();
			        g = g + pasos;	        
			        c.setCellValue(g);
		        }else{
		        	c = row.createCell(0);
		        	int g = (int)c.getNumericCellValue();
			        g = g + pasos;	        
			        c.setCellValue(g);
		        }
	        }else{
	        	row = sheet.createRow(iter);
	        	Cell c = row.createCell(0);
	        	c.setCellValue(pasos);
	        }
	        
	        try
	        {
	            //Write the workbook in file system
	            FileOutputStream out = new FileOutputStream(new File(path));
	            workbook.write(out);
	            out.close();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
		}
	}
	
	public static void convierteLog(String path,String fichero){
		if(path!=null && fichero!=null){
			//Blank workbook
	        XSSFWorkbook workbook = null;
			try {
				workbook = new XSSFWorkbook(new FileInputStream(path));
			} catch (FileNotFoundException e1) {
				workbook = new XSSFWorkbook();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			XSSFSheet sheet = workbook.getSheet("Hoja1");
			if(sheet==null)
				sheet = workbook.createSheet("Hoja1");
	        
	        FileReader fr = null;
	        
	        int log[] = new int[Constants.NUM_EXP];
	        
	        try {
				fr = new FileReader(fichero);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			BufferedReader br = new BufferedReader(fr);	         				 
			String line;
			
	        
	        for(int j = 0; j < Constants.REPETICIONES; j++){
		        for(int i = 0; i < Constants.NUM_EXP; i++){
		        	line = null;
		        	try {
						line = br.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					if(line!=null){
						if(line.equalsIgnoreCase("dead")){
							log[i] = 0;
						}else{
							log[i] = log[i] + Integer.parseInt(line);
						}
					}
		        }
	        }
	        
	        for(int i = 0; i < Constants.NUM_EXP; i++){
				Row row = sheet.getRow(i);
				if(row==null)
					row = sheet.createRow(i);    
				Cell c = row.getCell(2);
				if(c==null)
					c = row.createCell(2);
				log[i] = log[i] / Constants.REPETICIONES;
				c.setCellValue(log[i]);     
	        }
	        
	        try
	        {
	            //Write the workbook in file system
	            FileOutputStream out = new FileOutputStream(new File(path));
	            workbook.write(out);
	            out.close();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
		}
	}
}
