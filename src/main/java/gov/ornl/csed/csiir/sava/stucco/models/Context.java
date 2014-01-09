package gov.ornl.csed.csiir.sava.stucco.models;

import java.util.EnumMap;
import java.util.Map;

/**
 * Represents the context (i.e. features) of a word within a sentence. The context
 * is used to train a perceptron model, and is used by a perceptron model to assign 
 * a new label to a word.
 *
 */
public class Context {
	
	// Used to keep the context ordering consistent because order is important
	public enum Predicate {
		PPL("pp_label"),
		PL("p_label"),
		PPLPL("p2_label"),
		PPI("pp_iob"),
		PI("p_iob"),
		I("iob"),
		NI("n_iob"),
		NNI("nn_iob"),
		PPIPI("p2_iob"),
		W("word"),
		PLW("plabel_word"),
		PIW("piob_word"),
		PPW("pp_word"),
		PW("p_word"),
		NW("n_word"),
		NNW("nn_word"),
		PPP("pp_pos"),
		PP("p_pos"),
		P("pos"),
		NP("n_pos"),
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
	//default label
	private String targetLabel = "O";
	//target is used during training, but unknown when evaluating a word
	private boolean hasTarget = true;
	
	/**
	 * @param currentWord the word
	 */
	public Context(String currentWord) {
		contextMap = new EnumMap<Predicate, String>(Predicate.class);
		contextMap.put(Predicate.W, currentWord);
	}
	
	
	/**
	 * @param currentWord the word
	 * @param wordPOS the part of speech tag
	 * @param wordIOB the I-O-B format tag if training; otherwise, null
	 */
	public Context(String currentWord, String wordPOS, String wordIOB) {
		contextMap = new EnumMap<Predicate, String>(Predicate.class);
		contextMap.put(Predicate.W, currentWord);
		contextMap.put(Predicate.P, wordPOS);
		
		if (wordIOB != null) {
			targetLabel = wordIOB;
		}
	}
	
	/**
	 * @param currentWord the word
	 * @param wordPOS the part of speech tag
	 * @param wordIOB the I-O-B format tag
	 * @param domainLabel the domain-specific term, if training; otherwise, null
	 */
	public Context(String currentWord, String wordPOS, String wordIOB, String domainLabel) {
		contextMap = new EnumMap<Predicate, String>(Predicate.class);
		contextMap.put(Predicate.W, currentWord);
		contextMap.put(Predicate.P, wordPOS);
		contextMap.put(Predicate.I, wordIOB);
		
		if (domainLabel != null) {
			targetLabel = domainLabel;
		}
	}
	
	/**
	 * Add more context for the current word by adding information about
	 * the word that appeared right before it in the sentence.
	 * 
	 * @param prevWord the word that appeared right before the currentWord
	 * @param prevPOS the part of speech tag assigned to the previous word
	 * @param prevIOB the I-O-B format tag for the previous word
	 */
	public void setPreviousIOB(String prevWord, String prevPOS, String prevIOB) {
		contextMap.put(Predicate.PW, prevWord);
		contextMap.put(Predicate.PP, prevPOS);
		contextMap.put(Predicate.PI, prevIOB);
	}
	
	/**
	 * Add more context for the current word by adding information about
	 * the word that appeared right before it in the sentence.
	 * 
	 * @param prevWord the word that appeared right before the currentWord
	 * @param prevPOS the part of speech tag for the previous word
	 * @param prevIOB the I-O-B format tag assigned to the previous word
	 * @param prevLabel the domain-specific term assigned to the previous word
	 */
	public void setPreviousLabel(String prevWord, String prevPOS, String prevIOB, String prevLabel) {
		contextMap.put(Predicate.PW, prevWord);
		contextMap.put(Predicate.PP, prevPOS);
		contextMap.put(Predicate.PI, prevIOB);
		contextMap.put(Predicate.PL, prevLabel);
	}
	
	/**
	 * Add more context for the current word by adding information about
	 * the word that appeared two words before it in the sentence.
	 * 
	 * @param prevPrevWord the word that appeared two words before the currentWord
	 * @param prevPrevPOS the part of speech tag for the second previous word
	 * @param prevPrevIOB the I-O-B format tag assigned to the second previous word
	 */
	public void setPPreviousIOB(String prevPrevWord, String prevPrevPOS, String prevPrevIOB) {
		contextMap.put(Predicate.PPW, prevPrevWord);
		contextMap.put(Predicate.PPP, prevPrevPOS);
		contextMap.put(Predicate.PPI, prevPrevIOB);
	}
	
	/**
	 * Add more context for the current word by adding information about
	 * the word that appeared two words before it in the sentence.
	 * 
	 * @param prevPrevWord the word that appeared two words before the currentWord
	 * @param prevPrevPOS the part of speech tag for the second previous word
	 * @param prevPrevIOB the I-O-B format tag assigned to the second previous word
	 * @param prevPrevLabel the domain-specific term assigned to the second previous word
	 */
	public void setPPreviousLabel(String prevPrevWord, String prevPrevPOS, String prevPrevIOB, String prevPrevLabel) {
		contextMap.put(Predicate.PPW, prevPrevWord);
		contextMap.put(Predicate.PPP, prevPrevPOS);
		contextMap.put(Predicate.PPI, prevPrevIOB);
		contextMap.put(Predicate.PPL, prevPrevLabel);
	}
	
	/**
	 * Add more context for the current word by adding information about
	 * the two I-O-B format tags that were assigned to the two previous words.
	 */
	public void set2PreviousIOB() {
		if (contextMap.containsKey(Predicate.PPI) && contextMap.containsKey(Predicate.PI)) {
			String previous2IOBs = contextMap.get(Predicate.PPI).concat("__").concat(contextMap.get(Predicate.PI));
			contextMap.put(Predicate.PPIPI, previous2IOBs);
		}
	}
	
	/**
	 * Add more context for the current word by adding information about
	 * the previous word's I-O-B format tag and the current word.
	 */
	public void setPreviousIOBAndWord() {
		if (contextMap.containsKey(Predicate.PI)) {
			String pIAndW = contextMap.get(Predicate.PI).concat("__").concat(contextMap.get(Predicate.W));
			contextMap.put(Predicate.PIW, pIAndW);
		}
	}
	
	/**
	 * Add more context for the current word by adding information about
	 * the two domain-specific terms that were assigned to the two previous words.
	 */
	public void set2PreviousLabel() {
		if (contextMap.containsKey(Predicate.PPL) && contextMap.containsKey(Predicate.PL)) {
			String previous2Labels = contextMap.get(Predicate.PPL).concat("__").concat(contextMap.get(Predicate.PL));
			contextMap.put(Predicate.PPLPL, previous2Labels);
		}
	}
	
	/**
	 * Add more context for the current word by adding information about
	 * the previous word's domain-specific label and the current word.
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
	public void setNextWord(String nextWord, String nextPOS) {
		contextMap.put(Predicate.NW, nextWord);
		contextMap.put(Predicate.NP, nextPOS);
	}
	
	/**
	 * Add more context for the current word by adding information about
	 * the word that appears right after it in the sentence.
	 * 
	 * @param nextWord the word that appears right after the currentWord
	 * @param nextPOS the part of speech tag for the next word
	 * @param nextIOB the I-O-B format tag assigned to the next word
	 */
	public void setNextIOB(String nextWord, String nextPOS, String nextIOB) {
		contextMap.put(Predicate.NW, nextWord);
		contextMap.put(Predicate.NP, nextPOS);
		contextMap.put(Predicate.NI, nextIOB);
	}
	
	/**
	 * Add more context for the current word by adding information about
	 * the word that appears two words after it in the sentence.
	 * 
	 * @param nextNextWord the word that appears two words after the currentWord
	 * @param nextNextPOS the part of speech tag for the second next word
	 */
	public void setNNextWord(String nextNextWord, String nextNextPOS) {
		contextMap.put(Predicate.NNW, nextNextWord);
		contextMap.put(Predicate.NNP, nextNextPOS);
	}
	
	/**
	 * Add more context for the current word by adding information about
	 * the word that appears two words after it in the sentence.
	 * 
	 * @param nextNextWord the word that appears two words after the currentWord
	 * @param nextNextPOS the part of speech tag for the second next word
	 * @param nextNextIOB the I-O-B format tag assigned to the second next word
	 */
	public void setNNextIOB(String nextNextWord, String nextNextPOS, String nextNextIOB) {
		contextMap.put(Predicate.NNW, nextNextWord);
		contextMap.put(Predicate.NNP, nextNextPOS);
		contextMap.put(Predicate.NNI, nextNextIOB);
	}
	
	/**
	 * @return the assigned label when for training a model
	 */
	public String getTargetLabel() {
		return targetLabel;
	}
	
	/**
	 * @param label the label to be assigned when training a model
	 */
	public void setTargetLabel(String label) {
		this.targetLabel = label;
	}
	
	/**
	 * @param hasTarget true if this is context for training; otherwise, false
	 */
	public void setHasTarget(boolean hasTarget) {
		this.hasTarget = hasTarget;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		if (hasTarget) {
			sb.append(targetLabel);
			sb.append(" ");
		}
		
		for (Predicate pred : contextMap.keySet()) {
			sb.append(contextMap.get(pred));
			sb.append(" ");
		}
		
		return sb.toString().trim();
	}
	
}
