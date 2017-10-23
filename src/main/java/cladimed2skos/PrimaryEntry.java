package cladimed2skos;

public class PrimaryEntry {

	public PrimaryEntry() {
		f="";
		sf="";
		g="";
		sg="";
		ns="";
		code="";
		label="";
		obs1="";
		obs2="";
		obs3="";
		errorDescription="";
	}

	private String f;
	private String sf;
	private String g;
	private String sg;
	private String ns;
	private String code;
	private String label;
	private String obs1;
	private String obs2;
	private String obs3;
	private long rowNumber;
	private String errorDescription;
	
	
	
	
	
	public String getErrorDescription() {
		return errorDescription;
	}
	public void addErrorDescription(String errorDescription) {
		this.errorDescription = this.errorDescription+"\n"+errorDescription;
	}
	public long getRowNumber() {
		return rowNumber;
	}
	public void setRowNumber(long rowNumber) {
		this.rowNumber = rowNumber;
	}
	public String getF() {
		return f;
	}
	public void setF(String f) {
		this.f = f.trim().toUpperCase();
	}
	public String getSf() {
		return sf;
	}
	public void setSf(String sf) {
		this.sf = sf.trim().toUpperCase();
	}
	public String getG() {
		return g;
	}
	public void setG(String g) {
		this.g = g.trim().toUpperCase();
	}
	public String getSg() {
		return sg;
	}
	public void setSg(String sg) {
		this.sg = sg.trim().toUpperCase();
	}
	public String getNs() {
		return ns;
	}
	public void setNs(String ns) {
		this.ns = ns.trim().toUpperCase();
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code.trim().toUpperCase();
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label.trim().toUpperCase();
	}
	public String getObs1() {
		return obs1;
	}
	public void setObs1(String obs1) {
		this.obs1 = obs1.trim().toUpperCase();
	}
	public String getObs2() {
		return obs2;
	}
	public void setObs2(String obs2) {
		this.obs2 = obs2.trim().toUpperCase();
	}
	public String getObs3() {
		return obs3;
	}
	public void setObs3(String obs3) {
		this.obs3 = obs3.trim().toUpperCase();
	}
	@Override
	public String toString() {
		return "PrimaryEntry [f=" + f + ", sf=" + sf + ", g=" + g + ", sg=" + sg + ", ns=" + ns + ", code=" + code
				+ ", label=" + label + ", obs1=" + obs1 + ", obs2=" + obs2 + ", obs3=" + obs3 + ", rowNumber="
				+ rowNumber + ", errorDescription=" + errorDescription + "]";
	}	
	
	
}
