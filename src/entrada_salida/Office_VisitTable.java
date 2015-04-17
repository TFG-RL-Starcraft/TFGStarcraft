package entrada_salida;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
}
