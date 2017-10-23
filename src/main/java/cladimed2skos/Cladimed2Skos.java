package cladimed2skos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.ss.usermodel.Workbook;

public class Cladimed2Skos {
	
	public static final int CLASSIFICATION_SIMPLE=1;
	public static final int CLASSIFICATION_COMPLETE=2;
	

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
	       System.out.println(""+chooser.getSelectedFile().getAbsolutePath());
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
		System.out.println("Checking Clasification Simple");
		boolean simple = cpp.execute(CLASSIFICATION_SIMPLE);
		System.out.println("Checking Clasification Complete");
		boolean complete = cpp.execute(CLASSIFICATION_COMPLETE);
		
		if(simple && complete){
			// success: proceed conversion
			System.out.println("Integrity Check End with Sucess! No erros!");
		}else{
			System.out.println("Integrity erro: conversion aborted!");
			// Fail: abort conversion
		}
		

	}

}
