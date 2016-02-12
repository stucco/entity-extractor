package gov.ornl.stucco.entity;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import gov.ornl.stucco.entity.CyberEntityAnnotator.CyberAnnotation;
import gov.ornl.stucco.entity.CyberEntityAnnotator.CyberEntityMentionsAnnotation;
import gov.ornl.stucco.entity.CyberHeuristicAnnotator.CyberHeuristicAnnotation;

public class EntityLabeler {
//	public static final String PREV_WORD = "_PREVIOUS_";
//	public static final String NEXT_WORD = "_NEXT_";
//	public static final String POS = "_POS_";
//	public static final String LABEL = "O";
		
	private StanfordCoreNLP pipeline;
	private Properties nlpProps;
	private Annotation annotatedDoc;
	
	public EntityLabeler() {
		nlpProps = new Properties();
		this.setProperties();
		pipeline = new StanfordCoreNLP(this.nlpProps);
	}
	
	
	public EntityLabeler(Properties nlpProps) {
		this.nlpProps = nlpProps;
		pipeline = new StanfordCoreNLP(this.nlpProps);
	}
	
	
	private void setProperties() {
//		nlpProps.setProperty("customAnnotatorClass.cyberentity", "gov.ornl.stucco.entity.CyberEntityAnnotator");
		nlpProps.setProperty("customAnnotatorClass.cyberheuristics", "gov.ornl.stucco.entity.CyberHeuristicAnnotator");
		nlpProps.setProperty("annotators", "tokenize, ssplit, pos, cyberheuristics, lemma, ner, parse"/*, dcoref"*/);
//		nlpProps.setProperty("ssplit.boundaryMultiTokenRegex", "/\\./, /[A-Z]\\w*/");
//		nlpProps.setProperty("ssplit.boundaryTokenRegex", "/^\\.$/");
//		nlpProps.setProperty("ssplit.boundariesToDiscard", "/^[a-z][A-Za-z0-9_]*/");
//		nlpProps.setProperty("tokenize.whitespace", "true");
	}
	
	
	public Annotation getAnnotatedDoc(String title, String docText) {
		System.err.println("Annotating '" + title + "'...");
		annotatedDoc = new Annotation(docText);
		pipeline.annotate(annotatedDoc);
		return annotatedDoc;
	}


	public static void main(String[] args) {
		String exampleText = "The software developer who inserted a major security flaw into OpenSSL 1.2.4.8, using the file foo/bar/blah.php has said the error was \"quite trivial\" despite the severity of its impact, according to a new report.  The Sydney Morning Herald published an interview today with Robin Seggelmann, who added the flawed code to OpenSSL, the world's most popular library for implementing HTTPS encryption in websites, e-mail servers, and applications. The flaw can expose user passwords and potentially the private key used in a website's cryptographic certificate (whether private keys are at risk is still being determined). This is a new paragraph about Apache Tomcat's latest update 7.0.1.";
		EntityLabeler labeler = new EntityLabeler();
		Annotation doc = labeler.getAnnotatedDoc("My Doc", exampleText);
		
		List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
		for ( CoreMap sentence : sentences) {
			for ( CoreLabel token : sentence.get(TokensAnnotation.class)) {
				System.out.println(token.index() + "\t" + token.get(TextAnnotation.class) + "\t" + token.get(PartOfSpeechAnnotation.class) + "\t" + token.get(CyberHeuristicAnnotation.class));
			}
			
//			System.out.println("Entities:\n" + sentence.get(CyberEntityMentionsAnnotation.class) );
			
//			System.out.println("Parse Tree:\n" + sentence.get(TreeAnnotation.class));
//			System.out.println("Semantic Graph:\n" + sentence.get(CollapsedCCProcessedDependenciesAnnotation.class));			
		}
		
//		Map<Integer,CorefChain> chainMap = doc.get(CorefChainAnnotation.class);
//		System.out.println("Coref Chains");
//		for (Integer key : chainMap.keySet()) {
//			System.out.println(key + "\t-->\t" + chainMap.get(key));
//		}
		
	}

}
