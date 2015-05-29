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
		//label of two words previous
		PPL("pp_label"),
		//label of previous word
		PL("p_label"),
		//combo of labels of both previous words
		PPLPL("p2_label"),
		//combo of label of previous word and current word
		PLW("plabel_word"),
		//two words previous
		PPW("pp_word"),
		//previous word
		PW("p_word"),
		//current word to be labeled
		W("word"),
		//next word
		NW("n_word"),
		//word after next
		NNW("nn_word"),
		//pos tag of two words previous
		PPP("pp_pos"),
		//pos tag of previous word
		PP("p_pos"),
		//pos tag of current word
		P("pos"),
		//pos tag of next word
		NP("n_pos"),
		//pos tag of word after next
		NNP("nn_pos");		
		
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
	}

	
	/**
	 * Add more context for the current word by adding information about
	 * the word that appears right before it in the sentence.
	 * 
	 * @param prevWord the word that appears right before the currentWord
	 * @param prevPOS the part of speech tag for the previous word
	 * @param prevLabel the cyber label assigned to the previous word
	 */
	public void setPreviousContext(String prevWord, String prevPOS, String prevLabel) {
		contextMap.put(Predicate.PW, prevWord);
		contextMap.put(Predicate.PP, prevPOS);
		contextMap.put(Predicate.PL, prevLabel);
	}
	
	
	/**
	 * Add more context for the current word by adding information about
	 * the word that appears two words before it in the sentence.
	 * 
	 * @param prevPrevWord the word that appears two words before the currentWord
	 * @param prevPrevPOS the part of speech tag for the second previous word
	 * @param prevPrevLabel the cyber label assigned to the second previous word
	 */
	public void setPPreviousContext(String prevPrevWord, String prevPrevPOS, String prevPrevLabel) {
		contextMap.put(Predicate.PPW, prevPrevWord);
		contextMap.put(Predicate.PPP, prevPrevPOS);
		contextMap.put(Predicate.PPL, prevPrevLabel);
	}
	
		
	/**
	 * Add more context for the current word by adding information about
	 * the two cyber labels that were assigned to the two previous words.
	 */
	public void set2PreviousLabels() {
		if (contextMap.containsKey(Predicate.PPL) && contextMap.containsKey(Predicate.PL)) {
			String previous2Labels = contextMap.get(Predicate.PPL).concat("__").concat(contextMap.get(Predicate.PL));
			contextMap.put(Predicate.PPLPL, previous2Labels);
		}
	}
	
	/**
	 * Add more context for the current word by adding information about
	 * the previous word's cyber label and the current word.
	 */
	public void setPreviousLabelAndWord() {
		if (contextMap.containsKey(Predicate.PL)) {
			String pLAndW = contextMap.get(Predicate.PL).concat("__").concat(contextMap.get(Predicate.W));
			contextMap.put(Predicate.PLW, pLAndW);
		}
	}
	
	/**
	 * Add more context for the current word by adding information about
	 * the word that appears right after it in the sentence.
	 * 
	 * @param nextWord the word that appears right after the currentWord
	 * @param nextPOS the part of speech tag for the next word
	 */
	public void setNextContext(String nextWord, String nextPOS) {
		contextMap.put(Predicate.NW, nextWord);
		contextMap.put(Predicate.NP, nextPOS);
	}
	
	/**
	 * Add more context for the current word by adding information about
	 * the word that appears two words after it in the sentence.
	 * 
	 * @param nextNextWord the word that appears two words after the currentWord
	 * @param nextNextPOS the part of speech tag for the second next word
	 */
	public void setNNextContext(String nextNextWord, String nextNextPOS) {
		contextMap.put(Predicate.NNW, nextNextWord);
		contextMap.put(Predicate.NNP, nextNextPOS);
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
