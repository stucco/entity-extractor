package gov.ornl.stucco.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreLabel;

public class RegexHeuristicLabeler {
	
	public static CoreLabel EMPTY_CORELABEL = new CoreLabel();
	
	public static Pattern pattern0 = Pattern.compile("^[0-9\\-.]");
	public static Pattern pattern1 = Pattern.compile("^(before|through) [0-9x\\.-]");
	public static Pattern pattern2 = Pattern.compile("[0-9x\\.][0-9x\\.\\-]* and earlier$");
	public static Pattern pattern3 = Pattern.compile("^([,]|and) (and )?[0-9]$");
	public static Pattern pattern4 = Pattern.compile("\\(\\)$");
	public static Pattern pattern5 = Pattern.compile("^[vV]ersion[_\\-a-zA-Z0-9]* [0-9]");
	public static Pattern pattern6 = Pattern.compile("^(all|every) (supported )?(versions?|releases?)$");
	public static Pattern pattern7 = Pattern.compile("^prior to$");
	public static Pattern pattern8 = Pattern.compile("^java ((runtime environment)|(web start)|(for business)|(system web (server)?)) [0-9.\\-_]+$", Pattern.CASE_INSENSITIVE);
	public static Pattern pattern9 = Pattern.compile("^java system access manager [0-9.\\-_]+$", Pattern.CASE_INSENSITIVE);
	public static Pattern pattern10 = Pattern.compile("^java (plug-in|se|ee|me) [0-9.\\-_]+$", Pattern.CASE_INSENSITIVE);
	public static Pattern pattern11 = Pattern.compile("[0-9]+");
	public static Pattern pattern12 = Pattern.compile("^j2se[0-9]*$", Pattern.CASE_INSENSITIVE);
	public static Pattern pattern13 = Pattern.compile("^pre[0-9a-zA-Z._-]* [0-9]");
	public static Pattern pattern14 = Pattern.compile("^(release|[uU]pdate)[_\\-a-zA-Z0-9]* [0-9]");
	public static Pattern pattern15 = Pattern.compile("JavaVM");
	public static Pattern pattern16 = Pattern.compile("\b[bB]eta|[aA]lpha\b");
	public static Pattern pattern17 = Pattern.compile("^service pack [0-9]$",Pattern.CASE_INSENSITIVE);
	public static Pattern pattern18 = Pattern.compile("^Java [A-Z]");
	public static Pattern pattern19 = Pattern.compile("CVE-[0-9]{4}-[0-9]{4}");
	public static Pattern pattern20 = Pattern.compile("MS[0-9]{2}-[0-9]{3}");
	public static Pattern pattern21 = Pattern.compile("^[0-9\\-._]+$");
	public static Pattern pattern22 = Pattern.compile("^(([a-zA-Z0-9\\_\\.]*[a-z0-9]+[A-Z]+)|([a-zA-Z0-9\\_\\.]*[A-Za-z0-9\\.]+\\_[a-zA-Z0-9\\.]+)) (and|or) (([a-zA-Z0-9\\_\\.]*[a-z0-9]+[A-Z]+)|([a-zA-Z0-9\\_\\.]*[A-Za-z0-9\\.]+\\_[a-zA-Z0-9\\.]+)) (function|parameter|method)");
	public static Pattern pattern23 = Pattern.compile("^function in \\.[a-zA-Z0-9]{1,4}$");
	public static Pattern pattern24 = Pattern.compile("^\\.[a-zA-Z0-9]{1,4} (files?|scripts?)$");
	public static Pattern pattern25 = Pattern.compile("^([a-zA-Z0-9.\\-_/]+\\.[a-zA-Z0-9]{1,4})$");
	public static Pattern pattern26 = Pattern.compile("\\.com|\\.org|\\.net|\\.mobi$");
	public static Pattern pattern27 = Pattern.compile("[a-z]\\.[A-Z]");
	public static Pattern pattern28 = Pattern.compile("^[A-Z]+ (component|plugin|plug-in)");
	public static Pattern pattern29 = Pattern.compile("^Oracle [A-Z]+");
	public static Pattern pattern30 = Pattern.compile("^WebKit$");
	public static Pattern pattern31 = Pattern.compile("^, and$");
	
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
		add(Pattern.compile("^[A-Z]{1,3}[0-9]$"));
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
