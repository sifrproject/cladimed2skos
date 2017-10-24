package cladimed2skos;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class CladimedPreProcessor {

	Workbook workbook;

	public CladimedPreProcessor(Workbook workbook) {
		this.workbook = workbook;
	}

	public boolean execute(int sheet, Map<String, PrimaryEntry> hashMap) {

		Sheet firstSheet = workbook.getSheetAt(sheet);
		Iterator<Row> iterator = firstSheet.iterator();
		long rowNumber = 1;

		// jump header
		if (iterator.hasNext()) {
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
			hashMap.put(primaryEntry.getCode(), primaryEntry);
			if (rowsProcessed % 100 == 0) {
				System.out.println("RowsReaded: " + rowsProcessed);
			}
		}

		System.out.println("Checking for inconsistencies...");
		boolean sucess = true;
		String composedCode = "";

		for (HashMap.Entry<String, PrimaryEntry> entry : hashMap.entrySet()) {

			composedCode = entry.getValue().getF() + entry.getValue().getSf() + entry.getValue().getG() + entry.getValue().getSg();
			if (entry.getValue().getNs().length() == 1) {
				composedCode = composedCode + "0" + entry.getValue().getNs();
			} else {
				composedCode = composedCode + entry.getValue().getNs();
			}

			// begin check
			if (entry.getValue().getCode().equals("")) {
				entry.getValue().addErrorDescription("No code present.");
			}
			if (entry.getValue().getLabel().equals("")) {
				entry.getValue().addErrorDescription("No label present.");
			}
			if (!composedCode.equals(entry.getValue().getCode())) {
				entry.getValue().addErrorDescription("Code data and code format are different");
			}

			if (!entry.getValue().getErrorDescription().isEmpty()) {
				//System.out.println("Error on row: " + entry.getValue().getRowNumber());
				//System.out.println(entry.getValue().toString());
				sucess = false;
			}

		}
		

		return sucess;
	}

}
