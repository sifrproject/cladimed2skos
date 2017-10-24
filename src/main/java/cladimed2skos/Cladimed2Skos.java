package cladimed2skos;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.ss.usermodel.Workbook;

public class Cladimed2Skos {
	
	public static final int CLASSIFICATION_SIMPLE=1;
	public static final int CLASSIFICATION_COMPLETE=2;
	public Map<String,PrimaryEntry> simpleClassification;
	public Map<String,PrimaryEntry> completeClassification;
	
	

	public Cladimed2Skos() {
		simpleClassification = new HashMap<String,PrimaryEntry>();
		completeClassification = new HashMap<String,PrimaryEntry>();
	}

	public static void main(String[] args) {
		
		
		Cladimed2Skos c2s = new Cladimed2Skos();
		c2s.executeConversion();
		

	}
	
	public void executeConversion(){
		
		JFileChooser chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        "XLS and XLSX Excel Files", "xls", "xlsx");
	    chooser.setFileFilter(filter);
	    chooser.setDialogTitle("Please CLADIMED Excel file");
	    int returnVal = chooser.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	       System.out.println("You chose to open this file: " +
	            chooser.getSelectedFile().getAbsolutePath());
	    }
	    

	    
	    FileInputStream inputStream = null;
	    Workbook workbook=null;
		try {
			inputStream = new FileInputStream(new File(chooser.getSelectedFile().getAbsolutePath()));
			System.out.print("Opening File...");
			workbook = ExcelUtil.getWorkbook(inputStream, chooser.getSelectedFile().getAbsolutePath());
			System.out.println(" Done!");
		} catch (IOException  e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    

	    // Verify CLADIMED hierarchy integrity
		CladimedPreProcessor cpp = new CladimedPreProcessor(workbook);
		System.out.println("Integrity Check Initiated...");		
		System.out.println("Checking Clasification Simple...");
		boolean simple = cpp.execute(CLASSIFICATION_SIMPLE,simpleClassification);
		System.out.println("Done: "+simpleClassification.size()+" concepts processed");
		System.out.println("Checking Clasification Complete...");
		boolean complete = cpp.execute(CLASSIFICATION_COMPLETE,completeClassification);
		System.out.println("Done: "+completeClassification.size()+" concepts processed");
		
		CladimedConverter cc = new CladimedConverter(simpleClassification,completeClassification);
		
		cc.execute();
		System.out.println("Conversion finished.");
		
		try {
			workbook.close();
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
