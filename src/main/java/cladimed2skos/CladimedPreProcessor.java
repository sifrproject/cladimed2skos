package cladimed2skos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class CladimedPreProcessor {

	Workbook workbook;

	public CladimedPreProcessor(Workbook workbook) {
		this.workbook = workbook;
	}

	public boolean execute(int sheet) {

		List<PrimaryEntry> list = new ArrayList<PrimaryEntry>();
		Sheet firstSheet = workbook.getSheetAt(sheet);
		Iterator<Row> iterator = firstSheet.iterator();
		long rowNumber=1;
		
		// jump header
		if(iterator.hasNext()){
			iterator.next();
			rowNumber++;
		}

		PrimaryEntry primaryEntry;
		int rowsProcessed = 0;
		while (iterator.hasNext()) {
			Row nextRow = iterator.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			primaryEntry = new PrimaryEntry();
			rowsProcessed++;
			
			while (cellIterator.hasNext()) {
				Cell nextCell = cellIterator.next();
				int columnIndex = nextCell.getColumnIndex();

				switch (columnIndex) {
				case 0:
					primaryEntry.setF(ExcelUtil.getCellValue(nextCell));
					break;
				case 1:
					primaryEntry.setSf(ExcelUtil.getCellValue(nextCell));
					break;
				case 2:
					primaryEntry.setG(ExcelUtil.getCellValue(nextCell));
					break;
				case 3:
					primaryEntry.setSg(ExcelUtil.getCellValue(nextCell));
					break;
				case 4:
					primaryEntry.setNs(ExcelUtil.getCellValue(nextCell));
					break;
				case 5:
					primaryEntry.setCode(ExcelUtil.getCellValue(nextCell));
					break;
				case 6:
					primaryEntry.setLabel(ExcelUtil.getCellValue(nextCell));
					break;
				case 7:
					primaryEntry.setObs1(ExcelUtil.getCellValue(nextCell));
					break;
				case 8:
					primaryEntry.setObs2(ExcelUtil.getCellValue(nextCell));
					break;
				case 9:
					primaryEntry.setObs3(ExcelUtil.getCellValue(nextCell));
					break;
				}

			}
			primaryEntry.setRowNumber(rowNumber++);
			list.add(primaryEntry);
			if(rowsProcessed%100==0){
				System.out.println("RowsReaded: "+rowsProcessed);
			}
		}
		
		System.out.println("Checking for inconsistencies...");
		boolean sucess=true;
		String composedCode="";
		for(PrimaryEntry pe:list){
			composedCode = pe.getF()+pe.getSf()+pe.getG()+pe.getSg();
			if(pe.getNs().length() == 1){
				composedCode = composedCode + "0"+pe.getNs();
			}else{
				composedCode = composedCode +pe.getNs();
			}
			
			// begin check
			if(pe.getCode().equals("")){
				pe.addErrorDescription("No code present.");
			}
			if(pe.getLabel().equals("")){
				pe.addErrorDescription("No label present.");
			}
			if(!composedCode.equals(pe.getCode())){
				pe.addErrorDescription("Code data and code format are different");
			}
			
			if(!pe.getErrorDescription().isEmpty()){
				System.out.println("Error on row: "+pe.getRowNumber());
				System.out.println(pe.toString());
				sucess=false;
			}
			
		}
		
		

		return sucess;
	}
	
	

}
