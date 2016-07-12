package gov.ornl.stucco.entity.heuristics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.util.ArraySet;
import edu.stanford.nlp.util.StringUtils;
import gov.ornl.stucco.entity.models.CyberEntityType;
import gov.ornl.stucco.heurstics.utils.FreebaseList;
import gov.ornl.stucco.heurstics.utils.ListLoader;
import gov.ornl.stucco.heurstics.utils.TokenCyberLabelMap;

/**
 * This is the annotator in the pipeline that uses heuristics to label the cyber domain entities.
 *
 */
public class CyberHeuristicAnnotator implements Annotator {
	public static final String STUCCO_CYBER_HEURISTICS = "cyberheuristics";
	public static final Requirement CYBER_HEURISTICS_REQUIREMENT = new Requirement(STUCCO_CYBER_HEURISTICS);
	
	
	// Heuristic methods used to date
	public static enum HEURISTIC_METHOD {
		DICTIONARY,		// list of known entities
		REGEX,			// regular expressions
		TRAINING_MAP	// map created during training for tokens with recurring unique labels
	};
	
	public static final CyberEntityType O = new CyberEntityType();
	public static final CyberEntityType SW_VENDOR = new CyberEntityType("sw", "vendor");
	public static final CyberEntityType SW_PRODUCT = new CyberEntityType("sw", "product");
	public static final CyberEntityType SW_VERSION = new CyberEntityType("sw", "version");
	public static final CyberEntityType FILE_NAME = new CyberEntityType("file", "name");
	public static final CyberEntityType FUNCTION_NAME = new CyberEntityType("function", "name");
	public static final CyberEntityType VULN_MS = new CyberEntityType("vuln", "ms");
	public static final CyberEntityType VULN_NAME = new CyberEntityType("vuln", "name");
	public static final CyberEntityType VULN_DESC = new CyberEntityType("vuln", "description");
	public static final CyberEntityType VULN_CVE = new CyberEntityType("vuln", "cve");
	
	private static String swInfoList = "dictionaries/software_info.json";
	private static String swDevList = "dictionaries/software_developers.json";
	private static String osList = "dictionaries/operating_systems.json";
	private static String relTermsList = "dictionaries/relevant_terms.txt";
	private static String labelMapPath = "dictionaries/token_label_map.ser";
	
	private String listFile;
	private FreebaseList swProductList;
	private FreebaseList swVendorList;
	private Set<String> relevantTermsList;
	private TokenCyberLabelMap labelMap;
	private RegexHeuristicLabeler regexLabeler;
	
	
	public CyberHeuristicAnnotator(String className) {
		this(className, StringUtils.argsToProperties("-swProducts", swInfoList, "-swVendors", swDevList, "-swOS", osList, "-vulnDesc", relTermsList, "-labelMap", labelMapPath));
	}
	
	public CyberHeuristicAnnotator(String className, Properties config) {
		listFile = config.getProperty("swProducts", swInfoList);
		System.err.println("Loading sw_products list from '" + listFile + "'");
		swProductList = ListLoader.loadFreebaseList(listFile, SW_PRODUCT.toString());
		
		listFile = config.getProperty("swVendors", swDevList);
		System.err.println("Loading sw_vendors list from '" + listFile + "'");
		swVendorList = ListLoader.loadFreebaseList(listFile, SW_VENDOR.toString());
		
		listFile = config.getProperty("swOS", osList);
		System.err.println("Loading sw_products (os) list from '" + listFile + "'");
		FreebaseList temp = ListLoader.loadFreebaseList(listFile, SW_PRODUCT.toString());
		//os names are considered software products for now, so add them to the same list
		if (temp != null) {
			swProductList.addEntries(temp);
		}
		
		listFile = config.getProperty("vulnDesc", relTermsList);
		System.err.println("Loading vuln_description list from '" + listFile + "'");
		relevantTermsList = ListLoader.loadTextList(listFile);
		
		listFile = config.getProperty("labelMap", labelMapPath);
		labelMap = new TokenCyberLabelMap();
		labelMap.loadMap(listFile);
		
		regexLabeler = new RegexHeuristicLabeler();
	}

	@Override
	public void annotate(Annotation annotation) {
		System.err.println("Annotating with heuristic cyber labels ... ");
		if (annotation.has(SentencesAnnotation.class)) {
			//Known entities heuristics
			List<CoreLabel> tokens = annotation.get(TokensAnnotation.class);
			for (CoreLabel token : tokens) {
				if (swVendorList.contains(token.get(TextAnnotation.class))) {
					token.set(CyberHeuristicAnnotation.class, SW_VENDOR);
					token.set(CyberHeuristicMethodAnnotation.class, HEURISTIC_METHOD.DICTIONARY);
				}
				else if (swProductList.contains(token.get(TextAnnotation.class))) {
					token.set(CyberHeuristicAnnotation.class, SW_PRODUCT);
					token.set(CyberHeuristicMethodAnnotation.class, HEURISTIC_METHOD.DICTIONARY);
				}
				else if (relevantTermsList.contains(token.get(TextAnnotation.class))) {
					token.set(CyberHeuristicAnnotation.class, VULN_DESC);
					token.set(CyberHeuristicMethodAnnotation.class, HEURISTIC_METHOD.DICTIONARY);
				}
				else {
					token.set(CyberHeuristicAnnotation.class, O);
				}
			}
			for (int i=0; i<tokens.size()-1; i++) {
				CoreLabel token1 = tokens.get(i);
				CoreLabel token2 = tokens.get(i+1);
				String lookupPhrase =  token1.get(TextAnnotation.class) + " " + token2.get(TextAnnotation.class);
				if (swProductList.contains(lookupPhrase)) {
					if (token1.get(CyberHeuristicAnnotation.class).equals(O)) {
						token1.set(CyberHeuristicAnnotation.class, SW_PRODUCT);
						token1.set(CyberHeuristicMethodAnnotation.class, HEURISTIC_METHOD.DICTIONARY);
					}
					if (token2.get(CyberHeuristicAnnotation.class).equals(O)) {
						token2.set(CyberHeuristicAnnotation.class, SW_PRODUCT);
						token2.set(CyberHeuristicMethodAnnotation.class, HEURISTIC_METHOD.DICTIONARY);
					}
				}
				else if (swVendorList.contains(lookupPhrase)) {
					if (token1.get(CyberHeuristicAnnotation.class).equals(O)) {
						token1.set(CyberHeuristicAnnotation.class, SW_VENDOR);
						token1.set(CyberHeuristicMethodAnnotation.class, HEURISTIC_METHOD.DICTIONARY);
					}
					if (token2.get(CyberHeuristicAnnotation.class).equals(O)) {
						token2.set(CyberHeuristicAnnotation.class, SW_VENDOR);
						token2.set(CyberHeuristicMethodAnnotation.class, HEURISTIC_METHOD.DICTIONARY);
					}
				}
				else if (relevantTermsList.contains(lookupPhrase)) {
					if (token1.get(CyberHeuristicAnnotation.class).equals(O)) {
						token1.set(CyberHeuristicAnnotation.class, VULN_DESC);
						token1.set(CyberHeuristicMethodAnnotation.class, HEURISTIC_METHOD.DICTIONARY);
					}
					if (token2.get(CyberHeuristicAnnotation.class).equals(O)) {
						token2.set(CyberHeuristicAnnotation.class, VULN_DESC);
						token2.set(CyberHeuristicMethodAnnotation.class, HEURISTIC_METHOD.DICTIONARY);
					}
				}
			}
			for (int i=0; i<tokens.size()-2; i++) {
				CoreLabel token1 = tokens.get(i);
				CoreLabel token2 = tokens.get(i+1);
				CoreLabel token3 = tokens.get(i+2);
				String lookupPhrase =  token1.get(TextAnnotation.class) + " " + token2.get(TextAnnotation.class) + " " + token3.get(TextAnnotation.class);
				if (swProductList.contains(lookupPhrase)) {
					if (token1.get(CyberHeuristicAnnotation.class).equals(O)) {
						token1.set(CyberHeuristicAnnotation.class, SW_PRODUCT);
						token1.set(CyberHeuristicMethodAnnotation.class, HEURISTIC_METHOD.DICTIONARY);
					}
					if (token2.get(CyberHeuristicAnnotation.class).equals(O)) {
						token2.set(CyberHeuristicAnnotation.class, SW_PRODUCT);
						token2.set(CyberHeuristicMethodAnnotation.class, HEURISTIC_METHOD.DICTIONARY);
					}
					if (token3.get(CyberHeuristicAnnotation.class).equals(O)) {
						token3.set(CyberHeuristicAnnotation.class, SW_PRODUCT);
						token3.set(CyberHeuristicMethodAnnotation.class, HEURISTIC_METHOD.DICTIONARY);
					}
				}
				else if (swVendorList.contains(lookupPhrase)) {
					if (token1.get(CyberHeuristicAnnotation.class).equals(O)) {
						token1.set(CyberHeuristicAnnotation.class, SW_VENDOR);
						token1.set(CyberHeuristicMethodAnnotation.class, HEURISTIC_METHOD.DICTIONARY);
					}
					if (token2.get(CyberHeuristicAnnotation.class).equals(O)) {
						token2.set(CyberHeuristicAnnotation.class, SW_VENDOR);
						token2.set(CyberHeuristicMethodAnnotation.class, HEURISTIC_METHOD.DICTIONARY);
					}
					if (token3.get(CyberHeuristicAnnotation.class).equals(O)) {
						token3.set(CyberHeuristicAnnotation.class, SW_VENDOR);
						token3.set(CyberHeuristicMethodAnnotation.class, HEURISTIC_METHOD.DICTIONARY);
					}
				}
				else if (relevantTermsList.contains(lookupPhrase)) {
					if (token1.get(CyberHeuristicAnnotation.class).equals(O)) {
						token1.set(CyberHeuristicAnnotation.class, VULN_DESC);
						token1.set(CyberHeuristicMethodAnnotation.class, HEURISTIC_METHOD.DICTIONARY);
					}
					if (token2.get(CyberHeuristicAnnotation.class).equals(O)) {
						token2.set(CyberHeuristicAnnotation.class, VULN_DESC);
						token2.set(CyberHeuristicMethodAnnotation.class, HEURISTIC_METHOD.DICTIONARY);
					}
					if (token3.get(CyberHeuristicAnnotation.class).equals(O)) {
						token3.set(CyberHeuristicAnnotation.class, VULN_DESC);
						token3.set(CyberHeuristicMethodAnnotation.class, HEURISTIC_METHOD.DICTIONARY);
					}
				}
			}			
			
			//Regex heuristics
			for (int i=0; i<tokens.size(); i++) {
				List<CoreLabel> tokenSublist = new ArrayList<CoreLabel>();
				if (i == 0) {
					tokenSublist.add(RegexHeuristicLabeler.EMPTY_CORELABEL);
					tokenSublist.add(RegexHeuristicLabeler.EMPTY_CORELABEL);
					tokenSublist.addAll(tokens.subList(0, (Math.min(tokens.size(),5))));
					if (tokens.size() < 5) {
						for (int k=0; k<5-tokens.size(); k++) {
							tokenSublist.add(RegexHeuristicLabeler.EMPTY_CORELABEL);
						}
					}
				}
				else if (i == 1) {
					tokenSublist.add(RegexHeuristicLabeler.EMPTY_CORELABEL);
					tokenSublist.addAll(tokens.subList(0, (Math.min(tokens.size(),6))));
					if (tokens.size() < 6) {
						for (int k=0; k<6-tokens.size(); k++) {
							tokenSublist.add(RegexHeuristicLabeler.EMPTY_CORELABEL);
						}
					}
				}
				else {
					tokenSublist.addAll(tokens.subList(i-2, (Math.min(tokens.size(),i+5))));
					if (i == tokens.size()-1) {
						tokenSublist.add(RegexHeuristicLabeler.EMPTY_CORELABEL);
					}
					if (i >= tokens.size()-2) {
						tokenSublist.add(RegexHeuristicLabeler.EMPTY_CORELABEL);
					}
					if (i >= tokens.size()-3) {
						tokenSublist.add(RegexHeuristicLabeler.EMPTY_CORELABEL);
					}
					if (i >= tokens.size()-4) {
						tokenSublist.add(RegexHeuristicLabeler.EMPTY_CORELABEL);
					}
				}
				regexLabeler.annotate(tokenSublist);
			}
			
			//Token-to-unique-label map check
			for (CoreLabel token : tokens) {
				if ((token.get(CyberHeuristicAnnotation.class).equals(O)) && (labelMap.contains(token.get(TextAnnotation.class)))) {
					CyberEntityType uniqueLabel = labelMap.getLabel(token.get(TextAnnotation.class));
					token.set(CyberHeuristicAnnotation.class, uniqueLabel);
					token.set(CyberHeuristicMethodAnnotation.class, HEURISTIC_METHOD.TRAINING_MAP);
				}
			}
		}
	}

	@Override
	public Set<Requirement> requirementsSatisfied() {
		return Collections.unmodifiableSet(new ArraySet<Requirement>(CYBER_HEURISTICS_REQUIREMENT));
	}

	@Override
	public Set<Requirement> requires() {
		return Annotator.TOKENIZE_SSPLIT_POS;
	}

	/**
	 * The CyberHeuristicAnnotation key for getting the STUCCO cyber heuristic label of a token.
	 *
	 * This key is set on token annotations.
	 */
	public static class CyberHeuristicAnnotation implements CoreAnnotation<CyberEntityType> {
		public Class<CyberEntityType> getType() {
			return CyberEntityType.class;
		}
	}
	
	/**
	 * The CyberHeuristicMethodAnnotation key for getting the STUCCO cyber heuristic method that labeled a token.
	 *
	 * This key is set on token annotations.
	 */
	public static class CyberHeuristicMethodAnnotation implements CoreAnnotation<gov.ornl.stucco.entity.heuristics.CyberHeuristicAnnotator.HEURISTIC_METHOD> {
		public Class<gov.ornl.stucco.entity.heuristics.CyberHeuristicAnnotator.HEURISTIC_METHOD> getType() {
			return gov.ornl.stucco.entity.heuristics.CyberHeuristicAnnotator.HEURISTIC_METHOD.class;
		}
	}
}
