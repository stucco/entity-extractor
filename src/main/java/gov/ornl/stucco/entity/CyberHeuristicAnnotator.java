package gov.ornl.stucco.entity;

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.pipeline.Annotator.Requirement;
import edu.stanford.nlp.util.ArraySet;
import edu.stanford.nlp.util.StringUtils;
import gov.ornl.stucco.entity.CyberEntityAnnotator.CyberAnnotation;
import gov.ornl.stucco.entity.CyberEntityAnnotator.CyberEntityType;
import gov.ornl.stucco.utils.FreebaseList;
import gov.ornl.stucco.utils.ListLoader;

public class CyberHeuristicAnnotator implements Annotator {
	public static final String STUCCO_CYBER_HEURISTICS = "cyberheuristics";
	public static final Requirement CYBER_HEURISTICS_REQUIREMENT = new Requirement(STUCCO_CYBER_HEURISTICS);
	
//	public static final String LABEL = "O";
//	
//	public enum CyberEntityType {
//		SW_Vendor,
//		SW_Product,
//		SW_Version,
//		SW_Symbol,
//		VULN_MS,
//		VULN_Name,
//		VULN_Desc //same as relevant term		
//	}
	
	private static String swInfoList = "src/main/resources/lists/software_info.json";
	private static String swDevList = "src/main/resources/lists/software_developers.json";
	private static String osList = "src/main/resources/lists/operating_systems.json";
	private static String relTermsList = "src/main/resources/lists/relevant_terms.txt";
	
	private String listFile;
	private FreebaseList swProductList;
	private FreebaseList swVendorList;
	private Set<String> relevantTermsList;
	
	
	public CyberHeuristicAnnotator(String className) {
		this(className, StringUtils.argsToProperties("-swProducts", swInfoList, "-swVendors", swDevList, "-swOS", osList, "-vulnDesc", relTermsList));
	}
	
	public CyberHeuristicAnnotator(String className, Properties config) {
		listFile = config.getProperty("swProducts", swInfoList);
		System.err.println("Loading sw_products list from '" + listFile + "'");
		swProductList = ListLoader.loadFreebaseList(listFile, CyberEntityType.SW_Product.toString());
		
		listFile = config.getProperty("swVendors", swDevList);
		System.err.println("Loading sw_vendors list from '" + listFile + "'");
		swVendorList = ListLoader.loadFreebaseList(listFile, CyberEntityType.SW_Vendor.toString());
		
		listFile = config.getProperty("swOS", osList);
		System.err.println("Loading sw_products (os) list from '" + listFile + "'");
		FreebaseList temp = ListLoader.loadFreebaseList(listFile, CyberEntityType.SW_Product.toString());
		//os names are considered software products for now, so add them to the same list
		if (temp != null) {
			swProductList.addEntries(temp);
		}
		
		listFile = config.getProperty("vulnDesc", relTermsList);
		System.err.println("Loading vuln_description list from '" + listFile + "'");
		relevantTermsList = ListLoader.loadTextList(listFile);
	}

	@Override
	public void annotate(Annotation annotation) {
		System.err.println("Annotating with heuristic cyber labels ... ");
		if (annotation.has(SentencesAnnotation.class)) {
			List<CoreLabel> tokens = annotation.get(TokensAnnotation.class);
			for (CoreLabel token : tokens) {
				if (swProductList.contains(token.get(TextAnnotation.class))) {
					token.set(CyberHeuristicAnnotation.class, CyberEntityType.SW_Product);
				}
				else if (swVendorList.contains(token.get(TextAnnotation.class))) {
					token.set(CyberHeuristicAnnotation.class, CyberEntityType.SW_Vendor);
				}
				else if (relevantTermsList.contains(token.get(TextAnnotation.class))) {
					token.set(CyberHeuristicAnnotation.class, CyberEntityType.VULN_Desc);
				}
				else {
					token.set(CyberHeuristicAnnotation.class, CyberEntityType.O);
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
	 * The CyberAnnotation key for getting the STUCCO cyber label of a token.
	 *
	 * This key is set on token annotations.
	 */
	public static class CyberHeuristicAnnotation implements CoreAnnotation<CyberEntityType> {
		public Class<CyberEntityType> getType() {
			return CyberEntityType.class;
		}
	}
}
