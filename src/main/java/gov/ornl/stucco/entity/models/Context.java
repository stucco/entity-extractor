package gov.ornl.stucco.entity.models;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Represents the context (i.e. features) of a word within a document. The context
 * is used to train an averaged perceptron model, as well as evaluate 
 * a new word against the maximun entropy model.
 *
 */
public class Context {
	
	//Used as placeholders if the current word is the first or last in the sentence.
	public static final String START_WORD = "_START_";
	public static final String END_WORD = "_END_";
	
	//Regular expression patterns
	public static Map<Predicate, Pattern> regexMap;
	static {
		regexMap = new HashMap<Predicate, Pattern>();
		regexMap.put(Predicate.REGEX_MS, Pattern.compile("MS[0-9]{2}-[0-9]{3}"));
		regexMap.put(Predicate.REGEX_CVE, Pattern.compile("CVE-[0-9]{4}-[0-9]{4}"));
		regexMap.put(Predicate.REGEX_0, Pattern.compile("^[0-9]+(\\.|x)+[0-9a-zA-Z\\-.]+$"));
		regexMap.put(Predicate.REGEX_1, Pattern.compile("^[0-9.x]{2,}\\.+-[0-9a-zA-Z.]+$"));
		regexMap.put(Predicate.REGEX_2, Pattern.compile("^[0-9.x]+\\.?[a-zA-Z.]+$"));
		regexMap.put(Predicate.REGEX_3, Pattern.compile("^[0-9.x]+_[a-zA-Z0-9.]+$"));
		regexMap.put(Predicate.REGEX_4, Pattern.compile("^[0-9.x]+\\%[0-9a-zA-Z.]+$"));
		regexMap.put(Predicate.REGEX_5, Pattern.compile("^[0-9.x]+-([0-9.]+[a-zA-Z0-9.\\-_]*|[a-zA-Z0-9\\-_]*[0-9.]+)$"));
		regexMap.put(Predicate.REGEX_1CAP, Pattern.compile("^[A-Z]{1}"));
		regexMap.put(Predicate.REGEX_CAP, Pattern.compile("[a-z0-9]+[A-Z]+"));
		regexMap.put(Predicate.REGEX_1LB, Pattern.compile("^[0-9]"));
		regexMap.put(Predicate.REGEX_LB, Pattern.compile("^.+[0-9]"));
		regexMap.put(Predicate.REGEX_PUN, Pattern.compile("[.,\\?/\\-\\[\\]'\";:!@]"));
		regexMap.put(Predicate.REGEX__, Pattern.compile("[a-zA-Z0-9]+_"));
		regexMap.put(Predicate.REGEX_LPAR, Pattern.compile("^\\("));
		regexMap.put(Predicate.REGEX_RPAR, Pattern.compile("\\)$"));
		regexMap.put(Predicate.REGEX_RSYM, Pattern.compile("-RRB-"));
		regexMap.put(Predicate.REGEX_LSYM, Pattern.compile("-LRB-"));
		regexMap.put(Predicate.PREGEX_MS, Pattern.compile("MS[0-9]{2}-[0-9]{3}"));
		regexMap.put(Predicate.PREGEX_CVE, Pattern.compile("CVE-[0-9]{4}-[0-9]{4}"));
		regexMap.put(Predicate.PREGEX_0, Pattern.compile("^[0-9]+(\\.|x)+[0-9a-zA-Z\\-.]+$"));
		regexMap.put(Predicate.PREGEX_1, Pattern.compile("^[0-9.x]{2,}\\.+-[0-9a-zA-Z.]+$"));
		regexMap.put(Predicate.PREGEX_2, Pattern.compile("^[0-9.x]+\\.?[a-zA-Z.]+$"));
		regexMap.put(Predicate.PREGEX_3, Pattern.compile("^[0-9.x]+_[a-zA-Z0-9.]+$"));
		regexMap.put(Predicate.PREGEX_4, Pattern.compile("^[0-9.x]+\\%[0-9a-zA-Z.]+$"));
		regexMap.put(Predicate.PREGEX_5, Pattern.compile("^[0-9.x]+-([0-9.]+[a-zA-Z0-9.\\-_]*|[a-zA-Z0-9\\-_]*[0-9.]+)$"));
		regexMap.put(Predicate.PREGEX_1CAP, Pattern.compile("^[A-Z]{1}"));
		regexMap.put(Predicate.PREGEX_CAP, Pattern.compile("[a-z0-9]+[A-Z]+"));
		regexMap.put(Predicate.PREGEX_1LB, Pattern.compile("^[0-9]"));
		regexMap.put(Predicate.PREGEX_LB, Pattern.compile("^.+[0-9]"));
		regexMap.put(Predicate.PREGEX_PUN, Pattern.compile("[.,\\?/\\-\\[\\]'\";:!@]"));
		regexMap.put(Predicate.PREGEX__, Pattern.compile("[a-zA-Z0-9]+_"));
		regexMap.put(Predicate.PREGEX_LPAR, Pattern.compile("^\\("));
		regexMap.put(Predicate.PREGEX_RPAR, Pattern.compile("\\)$"));
		regexMap.put(Predicate.PREGEX_RSYM, Pattern.compile("-RRB-"));
		regexMap.put(Predicate.PREGEX_LSYM, Pattern.compile("-LRB-"));
	}
	
	
	// Used to keep the context ordering consistent because order is important in OpenNLP
	public enum Predicate {
		//first 6 characters of current word
		PRE("prefix"),
		//last 6 characters of current word
		SUF("suffix"),
		//current word to be labeled
		W("word"),
		//pos tag of current word
		P("pos"),
		//current word's heuristic label
		HEURISTIC("heuristic-label"),
		//current word matches regular expressions
		REGEX_MS("regex-MS"),
		REGEX_CVE("regex-CVE"),
		REGEX_0("regex-0"),
		REGEX_1("regex-1"),
		REGEX_2("regex-2"),
		REGEX_3("regex-3"),
		REGEX_4("regex-4"),
		REGEX_5("regex-5"),
		REGEX_1CAP("regex-1stCap"),
		REGEX_CAP("regex-intCap"),
		REGEX_1LB("regex-1st#"),
		REGEX_LB("regex-int#"),
		REGEX_PUN("regex-punct"),
		REGEX__("regex-under"),
		REGEX_LPAR("regex-lPar"),
		REGEX_RPAR("regex-rPar"),
		REGEX_RSYM("regex-rParSy"),
		REGEX_LSYM("regex-lParSy"),
		//previous word matches regular expressions
		PREGEX_MS("p_regex-MS"),
		PREGEX_CVE("p_regex-CVE"),
		PREGEX_0("p_regex-0"),
		PREGEX_1("p_regex-1"),
		PREGEX_2("p_regex-2"),
		PREGEX_3("p_regex-3"),
		PREGEX_4("p_regex-4"),
		PREGEX_5("p_regex-5"),
		PREGEX_1CAP("p_regex-1stCap"),
		PREGEX_CAP("p_regex-intCap"),
		PREGEX_1LB("p_regex-1st#"),
		PREGEX_LB("p_regex-int#"),
		PREGEX_PUN("p_regex-punct"),
		PREGEX__("p_regex-under"),
		PREGEX_LPAR("p_regex-lPar"),
		PREGEX_RPAR("p_regex-rPar"),
		PREGEX_RSYM("p_regex-rParSy"),
		PREGEX_LSYM("p_regex-lParSy");
		
		private String predicateLabel;
		
		private Predicate(String predicate) {
			this.predicateLabel = predicate;
		}
		
		public String getPredicateLabel() {
			return predicateLabel;
		}
	}

	private Map<Predicate, String> contextMap;
	
	/**
	 * @param currentWord the word
	 * @param pos the current word's pos
	 */
	public Context(String currentWord, String pos, String heuristicAnnotation, String previousWord) {
		contextMap = new EnumMap<Predicate, String>(Predicate.class);
		contextMap.put(Predicate.W, currentWord);
		contextMap.put(Predicate.P, pos);
		contextMap.put(Predicate.HEURISTIC, heuristicAnnotation);
		
		String prefix = currentWord;
		if (currentWord.length() >= 6) {
			prefix = currentWord.substring(0, 6);
		}
		contextMap.put(Predicate.PRE, prefix);
		
		String suffix = currentWord;
		if (currentWord.length() >= 6) {
			suffix = currentWord.substring(currentWord.length()-6);
		}
		contextMap.put(Predicate.SUF, suffix);
		
		//loop through regexes for current word, then previous word
		for (Predicate pred : Context.regexMap.keySet()) {
			Pattern regex = Context.regexMap.get(pred);
			if (pred.getPredicateLabel().startsWith("regex_")) {
				contextMap.put(pred, Boolean.toString(regex.matcher(currentWord).matches()));
			}
			else {
				contextMap.put(pred, Boolean.toString(regex.matcher(previousWord).matches()));
			}
		}
	}

	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (Predicate pred : contextMap.keySet()) {
			sb.append(contextMap.get(pred));
			sb.append(" ");
		}
		
		return sb.toString().trim();
	}
	
	
	public String[] toArray() {
		String[] contextArray = new String[contextMap.size()];
		contextArray = contextMap.values().toArray(contextArray);
		return contextArray;
	}
	
}
