package gov.ornl.stucco.entity.models;

import java.util.EnumMap;
import java.util.Map;

/**
 * Represents the context (i.e. features) of a word within a document. The context
 * is used to train an averaged perceptron model, which is then used to assign 
 * a new cyber label to a word.
 *
 */
public class Context {
	
	// Used to keep the context ordering consistent because order is important
	public enum Predicate {
		//first 6 characters of current word
		PRE("prefix"),
		//last 6 characters of current word
		SUF("suffix"),
		//label of two words previous
//		PPL("pp_label"),
		//label of previous word
//		PL("p_label"),
		//combo of labels of both previous words
//		PPLPL("p2_label"),
		//combo of label of previous word and current word
//		PLW("plabel_word"),
		//two words previous
//		PPW("pp_word"),
		//previous word
		PW("p_word"),
		//current word to be labeled
		W("word"),
		//next word
//		NW("n_word"),
		//word after next
//		NNW("nn_word"),
		//pos tag of two words previous
//		PPP("pp_pos"),
		//pos tag of previous word
//		PP("p_pos"),
		//pos tag of current word
		P("pos"),
		//pos tag of next word
//		NP("n_pos"),
		//pos tag of word after next
//		NNP("nn_pos");
		//current word found in a gazetteer
		GAZ("gazetteer"),
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
	public Context(String currentWord, String pos) {
		contextMap = new EnumMap<Predicate, String>(Predicate.class);
		contextMap.put(Predicate.W, currentWord);
		contextMap.put(Predicate.P, pos);
		
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
		
		
	}

	
	/**
	 * Add more context for the current word by adding information about
	 * the word that appears right before it in the sentence.
	 * 
	 * @param prevWord the word that appears right before the currentWord
	 * @param prevPOS the part of speech tag for the previous word
	 * @param prevLabel the cyber label assigned to the previous word
	 */
//	public void setPreviousContext(String prevWord, String prevPOS, String prevLabel) {
//		contextMap.put(Predicate.PW, prevWord);
//		contextMap.put(Predicate.PP, prevPOS);
//		contextMap.put(Predicate.PL, prevLabel);
//	}
	
	
	/**
	 * Add more context for the current word by adding information about
	 * the word that appears two words before it in the sentence.
	 * 
	 * @param prevPrevWord the word that appears two words before the currentWord
	 * @param prevPrevPOS the part of speech tag for the second previous word
	 * @param prevPrevLabel the cyber label assigned to the second previous word
	 */
//	public void setPPreviousContext(String prevPrevWord, String prevPrevPOS, String prevPrevLabel) {
//		contextMap.put(Predicate.PPW, prevPrevWord);
//		contextMap.put(Predicate.PPP, prevPrevPOS);
//		contextMap.put(Predicate.PPL, prevPrevLabel);
//	}
	
		
	/**
	 * Add more context for the current word by adding information about
	 * the two cyber labels that were assigned to the two previous words.
	 */
//	public void set2PreviousLabels() {
//		if (contextMap.containsKey(Predicate.PPL) && contextMap.containsKey(Predicate.PL)) {
//			String previous2Labels = contextMap.get(Predicate.PPL).concat("__").concat(contextMap.get(Predicate.PL));
//			contextMap.put(Predicate.PPLPL, previous2Labels);
//		}
//	}
	
	/**
	 * Add more context for the current word by adding information about
	 * the previous word's cyber label and the current word.
	 */
//	public void setPreviousLabelAndWord() {
//		if (contextMap.containsKey(Predicate.PL)) {
//			String pLAndW = contextMap.get(Predicate.PL).concat("__").concat(contextMap.get(Predicate.W));
//			contextMap.put(Predicate.PLW, pLAndW);
//		}
//	}
	
	/**
	 * Add more context for the current word by adding information about
	 * the word that appears right after it in the sentence.
	 * 
	 * @param nextWord the word that appears right after the currentWord
	 * @param nextPOS the part of speech tag for the next word
	 */
//	public void setNextContext(String nextWord, String nextPOS) {
//		contextMap.put(Predicate.NW, nextWord);
//		contextMap.put(Predicate.NP, nextPOS);
//	}
	
	/**
	 * Add more context for the current word by adding information about
	 * the word that appears two words after it in the sentence.
	 * 
	 * @param nextNextWord the word that appears two words after the currentWord
	 * @param nextNextPOS the part of speech tag for the second next word
	 */
//	public void setNNextContext(String nextNextWord, String nextNextPOS) {
//		contextMap.put(Predicate.NNW, nextNextWord);
//		contextMap.put(Predicate.NNP, nextNextPOS);
//	}
	
	
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
