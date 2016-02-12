package gov.ornl.stucco.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreLabel;

public class RegexHeuristicLabeler {
	
	public static CoreLabel EMPTY_CORELABEL = new CoreLabel();
	
	public static Pattern pattern0 = Pattern.compile("^[0-9\\-.]");
	public static Pattern pattern1 = Pattern.compile("[0-9x\\.\\-]");
	public static Pattern pattern2 = Pattern.compile("[0-9x\\.][0-9x\\.\\-]*");
	public static Pattern pattern3 = Pattern.compile("^[,]$|^and$");
	public static Pattern pattern4 = Pattern.compile("^\\D");
	public static Pattern pattern5 = Pattern.compile("[vV]ersion[_\\-a-zA-Z0-9]*");
	public static Pattern pattern6 = Pattern.compile("^all|every$");
	public static Pattern pattern7 = Pattern.compile("^supported$");
	public static Pattern pattern8 = Pattern.compile("^versions?|releases?$");
	public static Pattern pattern9 = Pattern.compile("^prior$");
	public static Pattern pattern10 = Pattern.compile("^to$");
	public static Pattern pattern11 = Pattern.compile("[0-9]+");
	public static Pattern pattern12 = Pattern.compile("^[A-Z]{1,3}[0-9]$");
	public static Pattern pattern13 = Pattern.compile("^pre[0-9a-zA-Z._-]*");
	public static Pattern pattern14 = Pattern.compile("^(release|[uU]pdate)[_\\-a-zA-Z0-9]*");
	public static Pattern pattern15 = Pattern.compile("^and|,$");
	public static Pattern pattern16 = Pattern.compile("\b[bB]eta|[aA]lpha\b");
	public static Pattern pattern17 = Pattern.compile("^service$",Pattern.CASE_INSENSITIVE);
	public static Pattern pattern18 = Pattern.compile("^pack$",Pattern.CASE_INSENSITIVE);
	public static Pattern pattern19 = Pattern.compile("CVE-[0-9]{4}-[0-9]{4}");
	public static Pattern pattern20 = Pattern.compile("MS[0-9]{2}-[0-9]{3}");
	public static Pattern pattern21 = Pattern.compile("");
	public static List<Pattern> patternList0 = new ArrayList<Pattern>() {{
		add(Pattern.compile("^[0-9]+(\\.|x)+[0-9a-zA-Z\\-\\.]{1,}$"));
		add(Pattern.compile("^[0-9.x]{2,}\\.+-[0-9a-zA-Z.]+$"));
		add(Pattern.compile("^[0-9\\.x]+\\.?[a-zA-Z.]+$"));
		add(Pattern.compile("^[0-9\\.x]+_[a-zA-Z0-9.]+$"));
		add(Pattern.compile("^[0-9\\.x]+\\%[0-9a-zA-Z.]+$"));
		add(Pattern.compile("^[0-9\\.x]+-([0-9.]+[a-zA-Z0-9.\\-_]*|[a-zA-Z0-9.\\-_]*[0-9.]+)$"));
		add(Pattern.compile("^[0-9a-z\\-_.]*\\%[0-9a-z\\-_.]+"));
		add(Pattern.compile("-[a-zA-Z0-9.]+$"));
		add(Pattern.compile("^alpha[_0-9a-zA-Z.]*"));
		add(Pattern.compile("^beta[_0-9a-zA-Z.]*"));
	}};
	
	public static Pattern sw_product = Pattern.compile(CyberHeuristicAnnotator.SW_PRODUCT.toString());
	public static Pattern sw_vendor = Pattern.compile(CyberHeuristicAnnotator.SW_VENDOR.toString());
	public static Pattern sw_version = Pattern.compile(CyberHeuristicAnnotator.SW_VERSION.toString());
	public static Pattern sw_symbol = Pattern.compile(CyberHeuristicAnnotator.SW_SYMBOL.toString());
	public static Pattern vuln_ms = Pattern.compile(CyberHeuristicAnnotator.VULN_MS.toString());
	public static Pattern vuln_cve = Pattern.compile(CyberHeuristicAnnotator.VULN_CVE.toString());
	public static Pattern vuln_name = Pattern.compile(CyberHeuristicAnnotator.VULN_NAME.toString());
	public static Pattern vuln_desc = Pattern.compile(CyberHeuristicAnnotator.VULN_DESC.toString());
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
