package cladimed2skos;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {

	long aux=0L;
	
	public static String getCellValue(Cell cell) {
	    switch (cell.getCellTypeEnum()) {
		case BOOLEAN:
			boolean aux2 = cell.getBooleanCellValue();
			return ""+aux2;
		case NUMERIC:
			Double aux = cell.getNumericCellValue();
			return ""+aux.intValue();
		case STRING:
			return cell.getStringCellValue();
		case _NONE:
			return cell.getStringCellValue();
		case BLANK:
			return cell.getStringCellValue();
		case ERROR:
			return cell.getStringCellValue();
		case FORMULA:
			return cell.getStringCellValue();
		default:
			return new String("");
	    }

	}

	public static Workbook getWorkbook(FileInputStream inputStream, String excelFilePath)
	        throws IOException {
	    Workbook workbook = null;
	 
	    if (excelFilePath.endsWith("xlsx")) {
	        workbook = new XSSFWorkbook(inputStream);
	    } else if (excelFilePath.endsWith("xls")) {
	        workbook = new HSSFWorkbook(inputStream);
	    } else {
	        throw new IllegalArgumentException("The specified file is not Excel file");
	    }
	 
	    return workbook;
	}

}
