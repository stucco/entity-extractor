package gov.ornl.stucco.entity.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import gov.ornl.stucco.entity.CyberHeuristicAnnotator;
import gov.ornl.stucco.entity.EntityLabeler;
import gov.ornl.stucco.entity.CyberEntityAnnotator.CyberAnnotation;
import gov.ornl.stucco.entity.CyberHeuristicAnnotator.CyberHeuristicAnnotation;
import gov.ornl.stucco.entity.RegexHeuristicLabeler;

public class RegexContext {
	
	public enum WordKey {
		P2_Word,
		P_Word,
		Word,
		N_Word,
		N2_Word,
		N3_Word,
		N4_Word
	}
	
	public enum LabelKey {
		P2_Label,
		P_Label,
		Label,
		N_Label,
		N2_Label,
		N3_Label,
		N4_Label
	}
	
	// Test a single- or multiple-word phrase against multiple patterns
	//	The list of patterns represent an OR operation (i.e. the word/phrase is considered
	//	a match if it matches pattern0 or pattern1 etc.)
	//	The exception to this is if there are NOT patterns involved,
	//	then all the patterns use AND.
	private Map<List<WordKey>, List<Pattern>> wordRegexMap;
	// Map defining which of the pattern(s) should match and which should not
	//	This map corresponds to wordRegexMap's Pattern list
	//	The default is false - this is NOT a notOp; the word(s) need to match the pattern
	private Map<List<Pattern>, List<Boolean>> notOpMap;
	// Test a word's cyber-domain label against a pattern
	//	All key's must match their corresponding pattern to label the set of tokens
	private Map<LabelKey, Pattern> labelRegexMap;
	// Provide the word's cyber-domain label if it matches
	//	all these pattern criteria
	private Map<LabelKey, CyberEntityType> annotationMap;

	public RegexContext() {
		this.wordRegexMap = new HashMap<List<WordKey>, List<Pattern>>();
		this.notOpMap = new HashMap<List<Pattern>, List<Boolean>>();
		this.labelRegexMap = new HashMap<LabelKey, Pattern>();
		this.annotationMap = new HashMap<LabelKey, CyberEntityType>();
	}
	
	
	public Map<List<WordKey>, List<Pattern>> getWordRegexMap() {
		return wordRegexMap;
	}


	public void setWordRegexMap(Map<List<WordKey>, List<Pattern>> wordRegexMap) {
		this.wordRegexMap = wordRegexMap;
		setDefaultNotOpMap();
	}
	
	
	public void addWordPatternLists(List<WordKey> keyList, List<Pattern> patternList) {
		this.wordRegexMap.put(keyList, patternList);
		setDefaultNotOpList(patternList);
	}
	
	
	public void addWordPatternList(WordKey key, List<Pattern> patternList) {
		List<WordKey> keyList = new ArrayList<WordKey>();
		keyList.add(key);
		addWordPatternLists(keyList, patternList);
	}
	
	
	public void addWordPattern(WordKey key, Pattern pattern) {
		List<WordKey> keyList = new ArrayList<WordKey>();
		keyList.add(key);
		List<Pattern> patternList = new ArrayList<Pattern>();
		patternList.add(pattern);
		addWordPatternLists(keyList, patternList);
	}
	
	
	public void addWordListPattern(List<WordKey> keyList, Pattern pattern) {
		List<Pattern> patternList = new ArrayList<Pattern>();
		patternList.add(pattern);
		addWordPatternLists(keyList, patternList);
	}


	public Map<List<Pattern>, List<Boolean>> getNotOpMap() {
		return notOpMap;
	}

	
	public void setNotOpMap(Map<List<Pattern>, List<Boolean>> notOpMap) {
		this.notOpMap = notOpMap;
	}
	
	
	public void addNotOpLists(List<WordKey> keyList, List<Boolean> notOpList) {
		this.notOpMap.put(this.wordRegexMap.get(keyList), notOpList);
	}
	
	
	public void addPatternLists(List<WordKey> keyList, List<Pattern> patternList, List<Boolean> notOpList) {
		this.wordRegexMap.put(keyList, patternList);
		this.notOpMap.put(patternList, notOpList);
	}


	public Map<LabelKey, Pattern> getLabelRegexMap() {
		return labelRegexMap;
	}


	public void setLabelRegexMap(Map<LabelKey, Pattern> labelRegexMap) {
		this.labelRegexMap = labelRegexMap;
	}

	
	public void addLabelRegex(LabelKey key, Pattern pattern) {
		this.labelRegexMap.put(key, pattern);
	}
	

	public Map<LabelKey, CyberEntityType> getHeuristicLabelMap() {
		return annotationMap;
	}


	public void setHeuristicLabelMap(Map<LabelKey, CyberEntityType> cyberLabelMap) {
		this.annotationMap = cyberLabelMap;
	}
	
	
	public void addHeuristicLabel(LabelKey key, CyberEntityType entity) {
		this.annotationMap.put(key, entity);
	}

	
	public void setDefaultNotOpList(List<Pattern> patternList) {
		List<Boolean> boolList = new ArrayList<Boolean>();
		for (int i=0; i<patternList.size(); i++) {
			boolList.add(Boolean.FALSE);
		}
		this.notOpMap.put(patternList, boolList);
	}
	
	
	public void setDefaultNotOpMap() {
		for (List<Pattern> key : this.wordRegexMap.values()) {
			setDefaultNotOpList(key);
		}
	}
	
	
	public boolean evaluate(List<CoreLabel> tokens) {
		for (List<WordKey> regexKeyList : wordRegexMap.keySet()) {
			// obtain the index of the words to be used in this regex and gather the token instances
			//	of the corresponding indices
			int low, high;
			low = high = regexKeyList.get(0).ordinal();
			for (WordKey wk : regexKeyList) {
				if (wk.ordinal() < low) {
					low = wk.ordinal();
				}
				if (wk.ordinal() > high) {
					high = wk.ordinal();
				}
			}
			List<CoreLabel> sublist = tokens.subList(low, high+1);
			// If one of the tokens is an EMPTY_CORELABEL,
			//	then, this regex will not match
			if (sublist.contains(RegexHeuristicLabeler.EMPTY_CORELABEL)) {
				return false;
			}
			String compareString = getPhrase(sublist);
			List<Pattern> patternList = wordRegexMap.get(regexKeyList);
			List<Boolean> notOpList = notOpMap.get(patternList);
			// If this list of patterns includes a NOT pattern, then
			//	treat as AND operations between
			// Otherwise, treat pattern list as OR
			if (notOpList.contains(Boolean.TRUE)) {
				for (int i=0; i<patternList.size(); i++) {
					Pattern pattern = patternList.get(i);
					Boolean notOp = notOpList.get(i);
					// If the pattern is not (!pattern) and the pattern does not match,
					//		or the pattern is (!pattern) and the pattern does match,
					//		then this set of tokens will NOT be labeled as defined
					// Otherwise, keep checking
					if (((!notOp.booleanValue()) && (!pattern.matcher(compareString).matches())) || ((notOp.booleanValue()) && (pattern.matcher(compareString).matches()))) {
						// Not match
						return false;
					}
				}
			}
			else {
				boolean foundMatch = false;
				for (int i=0; i<patternList.size(); i++) {
					Pattern pattern = patternList.get(i);
					if (pattern.matcher(compareString).matches()) {
						foundMatch = true;
						break;
					}
				}
				if (!foundMatch) {
					return false;
				}
			}
		}
		
		//Now check the labelRegexMap
		for (LabelKey key : labelRegexMap.keySet()) {
			CoreLabel token = tokens.get(key.ordinal());
			if (token.containsKey(CyberHeuristicAnnotation.class)) {
				String compareString = token.get(CyberHeuristicAnnotation.class).toString();
				// If the token's label is not labeled with the defined pattern,
				//		then this set of tokens will NOT be labeled
				// Otherwise, keep checking
				if (!labelRegexMap.get(key).matcher(compareString).matches()) {
					return false;
				}
			}
			// If the current token is an EMPTY_CORELABEL, then this regex does not match
			else {
				return false;
			}
		}
		
		// Set of tokens match all defined patterns, so label as appropriate
		annotate(tokens);
		
		return true;
	}
	
	
	private void annotate(List<CoreLabel> tokens) {
		for (LabelKey key : annotationMap.keySet()) {
			CoreLabel token = tokens.get(key.ordinal());
			token.set(CyberHeuristicAnnotation.class, annotationMap.get(key));
		}
	}
	
	
	private String getPhrase(List<CoreLabel> tokens) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<tokens.size(); i++) {
			CoreLabel token = tokens.get(i);
			sb.append(token.getString(TextAnnotation.class));
			if (i < tokens.size() - 1) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}
	
	
	@Override
	public String toString() {
		return "RegexContext [wordRegexMap=" + wordRegexMap + ", notOpMap="
				+ notOpMap + ", labelRegexMap=" + labelRegexMap
				+ ", annotationMap=" + annotationMap + "]";
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((annotationMap == null) ? 0 : annotationMap.hashCode());
		result = prime * result
				+ ((labelRegexMap == null) ? 0 : labelRegexMap.hashCode());
		result = prime * result
				+ ((notOpMap == null) ? 0 : notOpMap.hashCode());
		result = prime * result
				+ ((wordRegexMap == null) ? 0 : wordRegexMap.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RegexContext other = (RegexContext) obj;
		if (annotationMap == null) {
			if (other.annotationMap != null)
				return false;
		} else if (!annotationMap.equals(other.annotationMap))
			return false;
		if (labelRegexMap == null) {
			if (other.labelRegexMap != null)
				return false;
		} else if (!labelRegexMap.equals(other.labelRegexMap))
			return false;
		if (notOpMap == null) {
			if (other.notOpMap != null)
				return false;
		} else if (!notOpMap.equals(other.notOpMap))
			return false;
		if (wordRegexMap == null) {
			if (other.wordRegexMap != null)
				return false;
		} else if (!wordRegexMap.equals(other.wordRegexMap))
			return false;
		return true;
	}

	

	public static void main(String[] args) {
		RegexContext rc = new RegexContext();
//		rc.addRegex(ContextKey.Word, RegexHeuristicLabeler.pattern0);
//		rc.addRegex(ContextKey.Label, RegexHeuristicLabeler.sw_product);
//		rc.addLabel(LabelKey.Word, CyberHeuristicAnnotator.SW_VERSION);
		
		String exampleText = "Microsoft Windows 7 before SP1 has Oracle Java Runtime Environment cross-site scripting vulnerability in file.php (refer to CVE-2014-1234).";
		EntityLabeler labeler = new EntityLabeler();
		Annotation doc = labeler.getAnnotatedDoc("My Doc", exampleText);
		
		List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
		for ( CoreMap sentence : sentences) {
//			
			for ( CoreLabel token : sentence.get(TokensAnnotation.class)) {
				String word = token.get(TextAnnotation.class);
				if (word.equalsIgnoreCase("Windows") || word.equalsIgnoreCase("7")) {
					token.set(CyberHeuristicAnnotation.class, CyberHeuristicAnnotator.SW_PRODUCT);
				}
				else if (word.equalsIgnoreCase("before") || word.equalsIgnoreCase("SP1")) {
					token.set(CyberHeuristicAnnotation.class, CyberHeuristicAnnotator.SW_VERSION);
				}
				else if (word.equalsIgnoreCase("has") || word.equalsIgnoreCase("in")) {
					token.set(CyberHeuristicAnnotation.class, CyberHeuristicAnnotator.O);
				}
				else if (word.equalsIgnoreCase("cross-site") || word.equalsIgnoreCase("scripting") || word.equalsIgnoreCase("vulnerability")) {
					token.set(CyberHeuristicAnnotation.class, CyberHeuristicAnnotator.VULN_DESC);
				}
				else if (word.equalsIgnoreCase("file.php")) {
					token.set(CyberHeuristicAnnotation.class, CyberHeuristicAnnotator.FILE_NAME);
				}
				else if (word.equalsIgnoreCase("CVE-2014-1234")) {
					token.set(CyberHeuristicAnnotation.class, CyberHeuristicAnnotator.VULN_CVE);
				}
//				System.out.println(token.get(TextAnnotation.class) + "\t" + token.get(PartOfSpeechAnnotation.class) + "\t" + token.get(CyberAnnotation.class));
			}
		}
		
		for ( CoreMap sentence : sentences) {
			List<CoreLabel> tokenList = sentence.get(TokensAnnotation.class);
			for ( int i=0; i<tokenList.size(); i++) {
				
				List<CoreLabel> regexInput = new ArrayList<CoreLabel>();
				CoreLabel token = tokenList.get(i);
				System.out.println("Before: " + token.index() + "\t" + token.get(TextAnnotation.class) + "\t" + token.get(CyberHeuristicAnnotation.class));
//				int j = i;
//				while (j < 2) {
//					regexInput.add(RegexHeuristicLabeler.EMPTY_CORELABEL);
//					j = j + 1;
//				}
//				if (i-2 >= 0) {
//					regexInput.add(tokenList.get(i-2));
//				}
//				if (i-1 >= 0) {
//					regexInput.add(tokenList.get(i-1));
//				}
//				regexInput.add(token);
//				if (i+1 < tokenList.size()) {
//					regexInput.add(tokenList.get(i+1));
//				}
//				if (i+2 < tokenList.size()) {
//					regexInput.add(tokenList.get(i+2));
//				}
//				if (i+3 < tokenList.size()) {
//					regexInput.add(tokenList.get(i+3));
//				}
//				if (i+4 < tokenList.size()) {
//					regexInput.add(tokenList.get(i+4));
//				}
//				
//				while (regexInput.size() < 7) {
//					regexInput.add(RegexHeuristicLabeler.EMPTY_CORELABEL);
//				}
//				
//				rc.label(regexInput);
//				
//				System.out.println("After: " + token.index() + "\t" + token.get(TextAnnotation.class) + "\t" + token.get(CyberHeuristicAnnotation.class));
			}
		}

	}

}
