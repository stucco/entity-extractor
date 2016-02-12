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
	
	public enum ContextKey {
		P2_Word,
		P_Word,
		Word,
		N_Word,
		N2_Word,
		N3_Word,
		N4_Word,
		P2_Label,
		P_Label,
		Label,
		N_Label,
		N2_Label,
		N3_Label,
		N4_Label
	}
	
	public enum LabelKey {
		P2_Word,
		P_Word,
		Word,
		N_Word,
		N2_Word,
		N3_Word,
		N4_Word
	}
	
	private static final int WORD_LABEL_INDEX = 6;
	private static final int CURRENT_CORELABEL_INDEX = 2;
	
	private Map<ContextKey, List<Pattern>> contextRegexMap;
	private Map<LabelKey, CyberEntityType> contextLabelMap;
	
	
	public RegexContext() {
		contextRegexMap = new HashMap<ContextKey, List<Pattern>>();
		contextLabelMap = new HashMap<LabelKey, CyberEntityType>();
	}
	
	
	public void addRegex(ContextKey context, Pattern regex) {
		List<Pattern> list = new ArrayList<Pattern>();
		if (contextRegexMap.containsKey(context)) {
			list = contextRegexMap.get(context);
		}
		if (list != null) {
			list.add(regex);
		}
		
		contextRegexMap.put(context, list);
	}
	
	
	public void addRegex(ContextKey context, List<Pattern> regexList) {
		List<Pattern> list = new ArrayList<Pattern>();
		if (contextRegexMap.containsKey(context)) {
			list = contextRegexMap.get(context);
		}
		if (list != null) {
			list.addAll(regexList);
		}
		
		contextRegexMap.put(context, list);
	}
	
	
	public void addRegexMap(Map<ContextKey, List<Pattern>> newMap) {
		contextRegexMap.putAll(newMap);
	}

	
	public void addLabel(LabelKey context, CyberEntityType label) {
		contextLabelMap.put(context, label);
	}
	
	
	public void addLabelMap(Map<LabelKey, CyberEntityType> labelMap) {
		contextLabelMap.putAll(labelMap);
	}
	
	
	public boolean label(List<CoreLabel> tokensSpan) {

		for (ContextKey key : contextRegexMap.keySet()) {
			int index = key.ordinal();
			List<Pattern> regexList = contextRegexMap.get(key);
			CoreLabel token = null;
			if (index <= WORD_LABEL_INDEX) {
				token = tokensSpan.get(index);
			}
			else {
				token = tokensSpan.get(index-WORD_LABEL_INDEX-1);
			}
			if (!token.equals(RegexHeuristicLabeler.EMPTY_CORELABEL)) {
				String compareString = "";
				if (index <= WORD_LABEL_INDEX) {
					compareString = token.get(TextAnnotation.class);
				}
				else {
					compareString = (token.get(CyberHeuristicAnnotation.class)).toString();
				}
				
				boolean aMatch = false;
				for (Pattern regex : regexList) {
					if (regex.matcher(compareString).matches()) {
						aMatch = true;
						break;
					}
				}
				
				if (!aMatch) {
					return false;
				}
			}
		}
		
		//all keys match at least one corresponding regex, so label tokens
		annotate(tokensSpan);
		
		return true;
	}
	
	
	private void annotate(List<CoreLabel> tokensSpan) {
		for (LabelKey key : contextLabelMap.keySet()) {
			int index = key.ordinal();
			CoreLabel token = tokensSpan.get(index);
			if (!token.equals(RegexHeuristicLabeler.EMPTY_CORELABEL)) {
				token.set(CyberHeuristicAnnotation.class, contextLabelMap.get(key));
			}
		}
	}
	
	
	public static void main(String[] args) {
		RegexContext rc = new RegexContext();
		rc.addRegex(ContextKey.Word, RegexHeuristicLabeler.pattern0);
		rc.addRegex(ContextKey.Label, RegexHeuristicLabeler.sw_product);
		rc.addLabel(LabelKey.Word, CyberHeuristicAnnotator.SW_VERSION);
		
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
					token.set(CyberHeuristicAnnotation.class, CyberHeuristicAnnotator.SW_SYMBOL);
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
