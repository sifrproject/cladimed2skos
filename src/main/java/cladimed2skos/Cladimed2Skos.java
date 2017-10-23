package cladimed2skos;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Cladimed2Skos {

	public Cladimed2Skos() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		
		JFileChooser chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        "XLS and XLSX Excel Files", "xls", "xlsx");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	       System.out.println("You chose to open this file: " +
	            chooser.getSelectedFile().getName());
	    }
		
		CladimedPreProcessor cpp = new CladimedPreProcessor();
		cpp.execute();
		

	}

}
