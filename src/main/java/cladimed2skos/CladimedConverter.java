package cladimed2skos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;

public class CladimedConverter {
	
	private Map<String,PrimaryEntry> simpleHash;
	private Map<String,PrimaryEntry> completeHash;
	private JFileChooser chooser;
	private String destinationFolder;
	
	public static final String SKOS_COMCEPT_OPEN_PRE="<skos:Concept rdf:ID=\"http://cladimed.com/cladimed#";
	// code
	public static final String SKOS_COMCEPT_OPEN_POS="\">";
	public static final String SKOS_NOTATION_OPEN="<skos:notation rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">";
	// code
	public static final String SKOS_NOTATION_CLOSE="</skos:notation>";
	public static final String SKOS_CLASSIF_SIMPLE="<skos:inScheme rdf:resource=URI_ClassifSimple />";
	public static final String SKOS_CLASSIF_COMPLETE="<skos:inScheme rdf:resource=URI_ClassifComplete />";
	public static final String SKOS_DCT_OPEN="<dct:created rdf:datatype=\"http://www.w3.org/2001/XMLSchema#date\">";
	// date
	public static final String SKOS_DCT_CLOSE="</dct:created>";
	
	public static final String SKOS_PREF_LABEL_OPEN="<skos:prefLabel xml:lang=\"fr\">";
	// label lower case
	public static final String SKOS_PREF_LABEL_CLOSE="</skos:prefLabel>";
	public static final String SKOS_HIDDEN_LABEL_OPEN="<skos:hiddenLabel xml:lang=\"fr\">";
	// label upper case
	public static final String SKOS_HIDDEN_LABEL_CLOSE="</skos:hiddenLabel>";


	
	public static final String SKOS_COMCEPT_CLOSE="</skos:Concept>";
	
	public static final String RDF_CLOSE="</rdf:RDF>";

	
	
	// conditionals
	
	// concept with column B empity
	public static final String SKOS_TOP_CONCEPT_OF_SIMPLE="<skos:topConceptOf rdf:resource=URI_ClassifSimple />";
	public static final String SKOS_TOP_CONCEPT_OF_COMPLETE="<skos:topConceptOf rdf:resource=URI_ClassifComplete />";	 	
	
	// broader concept (0::1>
	public static final String SKOS_BROADER_OPEN="<skos:broader rdf:resource=\"http://cladimed.com/cladimed#";
	// broader code
	public static final String SKOS_BROADER_CLOSE="\"/>";
	
	// narrower concept (0::n)
	public static final String SKOS_NARROWER_OPEN="<skos:narrower rdf:resource=\"http://cladimed.com/cladimed#";
	// broader code
	public static final String SKOS_NARROWER_CLOSE="\"/>";
	
	public static final String NEW2017 = "Nouveau 2017";
	public static final String ABANDON2016 = "Abandon décembre 2016";
	public static final String MODIFIED2016 = "Modification dénomination décembre 2016";
	public static final String MODIFIEDAT = "2016-12-31";
	public static final String DOCTODATE2016 = "2016";	
	public static final String DOCTODATE2017 = "2017";	
	
	public static final String OWL_DEPRECATED = "<owl:deprecated rdf:resource=\"http://www.w3.org/2002/07/owl#deprecated\">true</owl:deprecated>";
	
	public static final String DATE_MODIFIED_OPEN = "<dcterms:modified rdf:datatype=\"http://www.w3.org/2001/XMLSchema#date\">";
	public static final String DATE_MODIFIED_CLOSE = "</dcterms:modified>";
	
	public static final String SKOS_CHANGE_NOTE_OPEN = "<skos:changeNote rdf:resource=\"http://www.w3.org/2004/02/skos/core#changeNote\">";
	
	public static final String SKOS_CHANGE_NOTE_CLOSE = "</skos:changeNote>";
	
	public static final String PROV_INVALIDATED_AT_OPEN = "<prov:invalidatedAtTime rdf:resource=\"http://www.w3.org/ns/prov#invalidatedAtTime\">";
	
	public static final String PROV_INVALIDATED_AT_CLOSE = "</prov:invalidatedAtTime>";
	
	

	
	public CladimedConverter(Map<String,PrimaryEntry> simpleHash,Map<String,PrimaryEntry> completeHash) {
		this.simpleHash = simpleHash;
		this.completeHash = completeHash;
	}
	
	public void execute(){
		
		destinationFolder = selectDestinationFolder();
		
		System.out.println("Simple: "+simpleHash.size());
		System.out.println("Completed: "+completeHash.size());
		
		//Simple Classification
		System.out.println();
		System.out.println("Begin conversion...");
		processSingleDestinationFile();
		System.out.println("Done...");
		
	
		
	}

	// deprecated in favor of processSingleDestinationFile()
	public void processSimpleClassification(){
		
		PrimaryEntry item = null;

		
		// find broader and narrowers
		
		for (HashMap.Entry<String, PrimaryEntry> entry : simpleHash.entrySet()) {
			item = entry.getValue();
			//System.out.println(item.toString());
			
			if(item.getG().isEmpty()){
				item.setBroaderConcept(item.getF());
				if(simpleHash.get(item.getF())!=null){
					simpleHash.get(item.getF()).addNarrowerConcepts(item.getCode());
				}else{
					item.addErrorDescription("No broader concept founded");
				}
			}else if(item.getSg().isEmpty()){
				item.setBroaderConcept(item.getF()+item.getSf());
				if(simpleHash.get(item.getF()+item.getSf())!=null){
					simpleHash.get(item.getF()+item.getSf()).addNarrowerConcepts(item.getCode());
				}else{
					// Hotfix for concepts with no broader
					item.setBroaderConcept(item.getF());
					simpleHash.get(item.getF()).addNarrowerConcepts(item.getCode());
				}
			}else if(item.getNs().isEmpty()){
				item.setBroaderConcept(item.getF()+item.getSf()+item.getG());
				if(simpleHash.get(item.getF()+item.getSf()+item.getG())!=null){
					simpleHash.get(item.getF()+item.getSf()+item.getG()).addNarrowerConcepts(item.getCode());
				}else{
					// Hotfix for concept without broader
					item.setBroaderConcept(item.getF()+item.getSf());
					if(simpleHash.get(item.getF()+item.getSf())!=null){
						simpleHash.get(item.getF()+item.getSf()).addNarrowerConcepts(item.getCode());
					}else{
						item.setBroaderConcept(item.getF());
						simpleHash.get(item.getF()).addNarrowerConcepts(item.getCode());
					}				}
			}else {
				item.setBroaderConcept(item.getF()+item.getSf()+item.getG()+item.getSg());
			}
		}
		
//		for (HashMap.Entry<String, PrimaryEntry> entry : simpleHash.entrySet()) {
//			item = entry.getValue();
//			if(!item.getErrorDescription().isEmpty()){
//				System.out.println(item.getErrorDescription()+" - "+item.toString());
//				
//			}
//		}
		
		// Generate FILE
		String filePath = destinationFolder+"/cladimed_simple_classif_skos.txt";
		System.out.println("File: "+ filePath);
		File file = new File(filePath);
		String line="";
		int size=1;
		
		try{
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8")); 
			for (HashMap.Entry<String, PrimaryEntry> entry : simpleHash.entrySet()) {
				item = entry.getValue();
				if(item.getErrorDescription().isEmpty()){
					line = SKOS_COMCEPT_OPEN_PRE + item.getCode()+SKOS_COMCEPT_OPEN_POS;
					line += SKOS_NOTATION_OPEN + item.getCode()+ SKOS_NOTATION_CLOSE;
					line += SKOS_DCT_OPEN + "2016" + SKOS_DCT_CLOSE;
					line += SKOS_PREF_LABEL_OPEN + item.getLabel().toLowerCase()+SKOS_PREF_LABEL_CLOSE;
					line += SKOS_HIDDEN_LABEL_OPEN + item.getLabel().toUpperCase()+SKOS_HIDDEN_LABEL_CLOSE;
					line += SKOS_CLASSIF_SIMPLE;
					// variable part
					if(item.getSf().isEmpty()){
					line += SKOS_TOP_CONCEPT_OF_SIMPLE;	
					}else{
						line += SKOS_BROADER_OPEN + item.getBroaderConcept()+SKOS_BROADER_CLOSE;
					}
					if(item.getNarrowerConcepts().size()>0){
						for(String narrower: item.getNarrowerConcepts()){
							line += SKOS_NARROWER_OPEN + narrower + SKOS_NARROWER_CLOSE;
						}
					}
					line += SKOS_COMCEPT_CLOSE;
					out.write(line); 
			        out.newLine();          
			        size++;
					
				}
			}				        
		out.close();
		}catch(IOException e){
			e.printStackTrace();
		}		  
		System.out.println("File sucessully generated: "+size+" lines");
		
	}

	// deprecated in favor of processSingleDestinationFile()
	public void processCompleteClassification(){
		
		PrimaryEntry item = null;

		
		// find broader and narrowers
		
		for (HashMap.Entry<String, PrimaryEntry> entry : completeHash.entrySet()) {
			item = entry.getValue();
			//System.out.println(item.toString());
			
			if(item.getG().isEmpty()){
				item.setBroaderConcept(item.getF());
				if(completeHash.get(item.getF())!=null){
					completeHash.get(item.getF()).addNarrowerConcepts(item.getCode());
				}else{
					item.addErrorDescription("No broader concept founded");
				}
			}else if(item.getSg().isEmpty()){
				item.setBroaderConcept(item.getF()+item.getSf());
				if(completeHash.get(item.getF()+item.getSf())!=null){
					completeHash.get(item.getF()+item.getSf()).addNarrowerConcepts(item.getCode());
				}else{
					// Hotfix for concept without broader
					item.setBroaderConcept(item.getF());
					completeHash.get(item.getF()).addNarrowerConcepts(item.getCode());
				}
			}else if(item.getNs().isEmpty()){
				item.setBroaderConcept(item.getF()+item.getSf()+item.getG());
				if(completeHash.get(item.getF()+item.getSf()+item.getG())!=null){
					completeHash.get(item.getF()+item.getSf()+item.getG()).addNarrowerConcepts(item.getCode());
				}else{
					// Hotfix for concept without broader
					item.setBroaderConcept(item.getF()+item.getSf());
					if(completeHash.get(item.getF()+item.getSf())!=null){
						completeHash.get(item.getF()+item.getSf()).addNarrowerConcepts(item.getCode());
					}else{
						item.setBroaderConcept(item.getF());
						completeHash.get(item.getF()).addNarrowerConcepts(item.getCode());
					}
				}
			}else {
				item.setBroaderConcept(item.getF()+item.getSf()+item.getG()+item.getSg());
			}
		}
		
		// Show Errors
//		for (HashMap.Entry<String, PrimaryEntry> entry : completeHash.entrySet()) {
//			item = entry.getValue();
//			if(!item.getErrorDescription().isEmpty()){
//				System.out.println(item.getErrorDescription()+" - "+item.toString());
//				
//			}
//		}
		
		
		// generate file
		String filePath = destinationFolder+"/cladimed_complete_classif_skos.txt";
		System.out.println("File: "+filePath);
		File file = new File(filePath);
		String line="";
		int size=1;
		
		try{
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8")); 
			for (HashMap.Entry<String, PrimaryEntry> entry : completeHash.entrySet()) {
				item = entry.getValue();
				if(item.getErrorDescription().isEmpty()){
					line = SKOS_COMCEPT_OPEN_PRE + item.getCode()+SKOS_COMCEPT_OPEN_POS;
					line += SKOS_NOTATION_OPEN + item.getCode()+ SKOS_NOTATION_CLOSE;
					line += SKOS_DCT_OPEN;
					if(item.getObs1().equalsIgnoreCase(NEW2017)){
						line += "2017";
					}else{
						line += "2016";
					}
					line +=SKOS_DCT_CLOSE;
					line += SKOS_PREF_LABEL_OPEN + item.getLabel().toLowerCase()+SKOS_PREF_LABEL_CLOSE;
					line += SKOS_HIDDEN_LABEL_OPEN + item.getLabel().toUpperCase()+SKOS_HIDDEN_LABEL_CLOSE;
					line += SKOS_CLASSIF_COMPLETE;
					if(simpleHash.get(item.getCode())!=null){
						line += SKOS_CLASSIF_SIMPLE;
					}
					// variable part
					if(item.getSf().isEmpty()){
					line += SKOS_TOP_CONCEPT_OF_COMPLETE;	
					}else{
						line += SKOS_BROADER_OPEN + item.getBroaderConcept()+SKOS_BROADER_CLOSE;
					}
					if(item.getNarrowerConcepts().size()>0){
						for(String narrower: item.getNarrowerConcepts()){
							line += SKOS_NARROWER_OPEN + narrower + SKOS_NARROWER_CLOSE;
						}
					}
					if(item.getObs1().equalsIgnoreCase(ABANDON2016)){
						line += OWL_DEPRECATED;
					}
					if(item.getObs1().equalsIgnoreCase(MODIFIED2016)){
						
						line += DATE_MODIFIED_OPEN + MODIFIEDAT + DATE_MODIFIED_CLOSE;
						line += SKOS_HIDDEN_LABEL_OPEN +item.getObs2().toUpperCase()+ SKOS_HIDDEN_LABEL_CLOSE;
						
					}
					line += SKOS_COMCEPT_CLOSE;
					out.write(line); 
			        out.newLine();          
			        size++;
					
				}
			}				        
		out.close();
		}catch(IOException e){
			e.printStackTrace();
		}		  
		System.out.println("File sucessully generated: "+size+" lines");
		
		
	}

	private String selectDestinationFolder(){
		chooser = new JFileChooser(); 
	    chooser.setCurrentDirectory(new java.io.File("."));
	    chooser.setDialogTitle("Please Select Destination Folder");
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    chooser.setAcceptAllFileFilterUsed(false);   
	    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) { 
	    	return chooser.getSelectedFile().toString();
	    } else {
	    	return "";
	    }
	}
	

	private void processSingleDestinationFile(){
		
		PrimaryEntry item = null;

		
		// find broader and narrowers for single classification
		
		for (HashMap.Entry<String, PrimaryEntry> entry : simpleHash.entrySet()) {
			item = entry.getValue();
			//System.out.println(item.toString());
			
			if(item.getG().isEmpty()){
				item.setBroaderConcept(item.getF());
				if(simpleHash.get(item.getF())!=null){
					simpleHash.get(item.getF()).addNarrowerConcepts(item.getCode());
				}else{
					item.addErrorDescription("No broader concept founded");
				}
			}else if(item.getSg().isEmpty()){
				item.setBroaderConcept(item.getF()+item.getSf());
				if(simpleHash.get(item.getF()+item.getSf())!=null){
					simpleHash.get(item.getF()+item.getSf()).addNarrowerConcepts(item.getCode());
				}else{
					// Hotfix for concepts with no broader
					item.setBroaderConcept(item.getF());
					simpleHash.get(item.getF()).addNarrowerConcepts(item.getCode());
				}
			}else if(item.getNs().isEmpty()){
				item.setBroaderConcept(item.getF()+item.getSf()+item.getG());
				if(simpleHash.get(item.getF()+item.getSf()+item.getG())!=null){
					simpleHash.get(item.getF()+item.getSf()+item.getG()).addNarrowerConcepts(item.getCode());
				}else{
					// Hotfix for concept without broader
					item.setBroaderConcept(item.getF()+item.getSf());
					if(simpleHash.get(item.getF()+item.getSf())!=null){
						simpleHash.get(item.getF()+item.getSf()).addNarrowerConcepts(item.getCode());
					}else{
						item.setBroaderConcept(item.getF());
						simpleHash.get(item.getF()).addNarrowerConcepts(item.getCode());
					}				}
			}else {
				item.setBroaderConcept(item.getF()+item.getSf()+item.getG()+item.getSg());
			}
		}
		
		// find broader and narrowers for single classification

		for (HashMap.Entry<String, PrimaryEntry> entry : completeHash.entrySet()) {
			item = entry.getValue();
			//System.out.println(item.toString());
			
			if(item.getG().isEmpty()){
				item.setBroaderConcept(item.getF());
				if(completeHash.get(item.getF())!=null){
					completeHash.get(item.getF()).addNarrowerConcepts(item.getCode());
				}else{
					item.addErrorDescription("No broader concept founded");
				}
			}else if(item.getSg().isEmpty()){
				item.setBroaderConcept(item.getF()+item.getSf());
				if(completeHash.get(item.getF()+item.getSf())!=null){
					completeHash.get(item.getF()+item.getSf()).addNarrowerConcepts(item.getCode());
				}else{
					// Hotfix for concept without broader
					item.setBroaderConcept(item.getF());
					completeHash.get(item.getF()).addNarrowerConcepts(item.getCode());
				}
			}else if(item.getNs().isEmpty()){
				item.setBroaderConcept(item.getF()+item.getSf()+item.getG());
				if(completeHash.get(item.getF()+item.getSf()+item.getG())!=null){
					completeHash.get(item.getF()+item.getSf()+item.getG()).addNarrowerConcepts(item.getCode());
				}else{
					// Hotfix for concept without broader
					item.setBroaderConcept(item.getF()+item.getSf());
					if(completeHash.get(item.getF()+item.getSf())!=null){
						completeHash.get(item.getF()+item.getSf()).addNarrowerConcepts(item.getCode());
					}else{
						item.setBroaderConcept(item.getF());
						completeHash.get(item.getF()).addNarrowerConcepts(item.getCode());
					}
				}
			}else {
				item.setBroaderConcept(item.getF()+item.getSf()+item.getG()+item.getSg());
			}
		}
		
		
		// Generate FILE
		String filePath = destinationFolder+"/cladimed.rdf";
		String headerFilePath = destinationFolder+"/cladimed_header_metadata.txt";
		System.out.println("File: "+ filePath);
		File file = new File(filePath);
		File headerFile = new File(headerFilePath);
		String line="";
		String sCurrentLine="";
		int size=1;
		
	
		
		try{
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8")); 
			FileReader fr = new FileReader(headerFile);
			BufferedReader br = new BufferedReader(fr);

			// Adding header metadata
			
			while ((sCurrentLine = br.readLine()) != null) {
				out.write(sCurrentLine); 
		        out.newLine();          
		        size++;
			}			
			
			
			// Generate SIMPLE SCHEMA
			for (HashMap.Entry<String, PrimaryEntry> entry : simpleHash.entrySet()) {
				item = entry.getValue();
				if(item.getErrorDescription().isEmpty()){
					line = SKOS_COMCEPT_OPEN_PRE + item.getCode()+SKOS_COMCEPT_OPEN_POS;
					// Issue 2 for abandon on LABEL
					if(item.getLabel().toUpperCase().indexOf("ABAN")>-1 && item.getObs1().isEmpty()){
						line += SKOS_PREF_LABEL_OPEN + item.getCode().toLowerCase()+SKOS_PREF_LABEL_CLOSE;
						line += SKOS_HIDDEN_LABEL_OPEN + item.getCode().toUpperCase()+SKOS_HIDDEN_LABEL_CLOSE;
						line += OWL_DEPRECATED;
						line += SKOS_CHANGE_NOTE_OPEN+item.getLabel()+SKOS_CHANGE_NOTE_CLOSE;
					}else{
						line += SKOS_PREF_LABEL_OPEN + item.getLabel().toLowerCase()+SKOS_PREF_LABEL_CLOSE;
						line += SKOS_HIDDEN_LABEL_OPEN + item.getLabel().toUpperCase()+SKOS_HIDDEN_LABEL_CLOSE;
					}
					if(item.getLabel().toUpperCase().indexOf("ABAN")>-1 && !item.getObs1().isEmpty()){
						if(item.getObs1().equalsIgnoreCase(ABANDON2016)){
							line += PROV_INVALIDATED_AT_OPEN+MODIFIEDAT+PROV_INVALIDATED_AT_CLOSE;
						}
					}
					// END issue 2
					line += SKOS_NOTATION_OPEN + item.getCode()+ SKOS_NOTATION_CLOSE;
					line += SKOS_DCT_OPEN + DOCTODATE2016 + SKOS_DCT_CLOSE;
					line += SKOS_CLASSIF_SIMPLE;
					// variable part
					if(item.getSf().isEmpty()){
					line += SKOS_TOP_CONCEPT_OF_SIMPLE;	
					}else{
						line += SKOS_BROADER_OPEN + item.getBroaderConcept()+SKOS_BROADER_CLOSE;
					}
					if(item.getNarrowerConcepts().size()>0){
						for(String narrower: item.getNarrowerConcepts()){
							line += SKOS_NARROWER_OPEN + narrower + SKOS_NARROWER_CLOSE;
						}
					}
					line += SKOS_COMCEPT_CLOSE;
				
					
					out.write(line); 
			        out.newLine();          
			        size++;
					
				}
			}
			
			// Generate COMPLETE SCHEMA
			
			for (HashMap.Entry<String, PrimaryEntry> entry : completeHash.entrySet()) {
				item = entry.getValue();
				if(item.getErrorDescription().isEmpty()){
					line = SKOS_COMCEPT_OPEN_PRE + item.getCode()+SKOS_COMCEPT_OPEN_POS;
					// Issue 2 for abandon on LABEL
					if(item.getLabel().toUpperCase().indexOf("ABAN")>-1 && item.getObs1().isEmpty()){
						line += SKOS_PREF_LABEL_OPEN + item.getCode().toLowerCase()+SKOS_PREF_LABEL_CLOSE;
						line += SKOS_HIDDEN_LABEL_OPEN + item.getCode().toUpperCase()+SKOS_HIDDEN_LABEL_CLOSE;
						line += OWL_DEPRECATED;
						line += SKOS_CHANGE_NOTE_OPEN+item.getLabel()+SKOS_CHANGE_NOTE_CLOSE;
					}else{
						line += SKOS_PREF_LABEL_OPEN + item.getLabel().toLowerCase()+SKOS_PREF_LABEL_CLOSE;
						line += SKOS_HIDDEN_LABEL_OPEN + item.getLabel().toUpperCase()+SKOS_HIDDEN_LABEL_CLOSE;
					}
					if(item.getLabel().toUpperCase().indexOf("ABAN")>-1 && !item.getObs1().isEmpty()){
						if(item.getObs1().equalsIgnoreCase(ABANDON2016)){
							line += PROV_INVALIDATED_AT_OPEN+MODIFIEDAT+PROV_INVALIDATED_AT_CLOSE;
						}
					}
					// END issue 2
					line += SKOS_NOTATION_OPEN + item.getCode()+ SKOS_NOTATION_CLOSE;
					line += SKOS_DCT_OPEN;
					if(item.getObs1().equalsIgnoreCase(NEW2017)){
						line += DOCTODATE2017;
					}else{
						line += DOCTODATE2016;
					}
					line +=SKOS_DCT_CLOSE;
					// lines below commented due the issue 2 
//					line += SKOS_PREF_LABEL_OPEN + item.getLabel().toLowerCase()+SKOS_PREF_LABEL_CLOSE;
//					line += SKOS_HIDDEN_LABEL_OPEN + item.getLabel().toUpperCase()+SKOS_HIDDEN_LABEL_CLOSE;
					line += SKOS_CLASSIF_COMPLETE;
					if(simpleHash.get(item.getCode())!=null){
						line += SKOS_CLASSIF_SIMPLE;
					}
					// variable part
					if(item.getSf().isEmpty()){
					line += SKOS_TOP_CONCEPT_OF_COMPLETE;	
					}else{
						line += SKOS_BROADER_OPEN + item.getBroaderConcept()+SKOS_BROADER_CLOSE;
					}
					if(item.getNarrowerConcepts().size()>0){
						for(String narrower: item.getNarrowerConcepts()){
							line += SKOS_NARROWER_OPEN + narrower + SKOS_NARROWER_CLOSE;
						}
					}
					if(item.getObs1().equalsIgnoreCase(ABANDON2016)){
						line += OWL_DEPRECATED;
					}
					if(item.getObs1().equalsIgnoreCase(MODIFIED2016)){
						
						line += DATE_MODIFIED_OPEN + MODIFIEDAT + DATE_MODIFIED_CLOSE;
						line += SKOS_HIDDEN_LABEL_OPEN +item.getObs2().toUpperCase()+ SKOS_HIDDEN_LABEL_CLOSE;
						
					}
					line += SKOS_COMCEPT_CLOSE;

					
					out.write(line); 
			        out.newLine();          
			        size++;
					
				}
			}			
		// close file
		line = RDF_CLOSE;
		out.write(line);	
		out.close();
		}catch(IOException e){
			e.printStackTrace();
		}		  
		System.out.println("File sucessully generated: "+size+" lines");		
		
		

		// tags with "ABAND"
//		for (HashMap.Entry<String, PrimaryEntry> entry : simpleHash.entrySet()) {
//			item = entry.getValue();
//			if(item.getLabel().toUpperCase().indexOf("ABAN")>-1){
//				System.out.println("Simple   Concept: "+item.getCode()+" Label: "+item.getLabel()+" Message: "+item.getObs1()+" Obs: "+item.getObs2() );
//			}
//		}		
//		
//		// tags with "ABAND"
//		for (HashMap.Entry<String, PrimaryEntry> entry : completeHash.entrySet()) {
//			item = entry.getValue();
//			if(item.getLabel().toUpperCase().indexOf("ABAN")>-1){
//				System.out.println("Complete Concept: "+item.getCode()+" Label: "+item.getLabel()+" Message: "+item.getObs1()+" Obs: "+item.getObs2());
//			}
//		}		
		
		
		
	}
	
}
