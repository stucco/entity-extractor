package gov.ornl.stucco.entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import gov.ornl.stucco.entity.CyberEntityAnnotator.CyberAnnotation;
import gov.ornl.stucco.entity.CyberEntityAnnotator.CyberConfidenceAnnotation;
import gov.ornl.stucco.entity.CyberEntityAnnotator.CyberEntityMentionsAnnotation;
import gov.ornl.stucco.entity.heuristics.CyberHeuristicAnnotator.CyberHeuristicAnnotation;
import gov.ornl.stucco.entity.heuristics.CyberHeuristicAnnotator.CyberHeuristicMethodAnnotation;

public class EntityLabeler {
		
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
		nlpProps.setProperty("customAnnotatorClass.cyberentity", "gov.ornl.stucco.entity.CyberEntityAnnotator");
		nlpProps.setProperty("customAnnotatorClass.cyberheuristics", "gov.ornl.stucco.entity.heuristics.CyberHeuristicAnnotator");
		nlpProps.setProperty("annotators", "tokenize, ssplit, pos, cyberheuristics, cyberentity, lemma, ner, parse, depparse, dcoref");
	}
	
	
	/**
	 * @param title of the document to be annotated
	 * @param docText the raw text version of the document
	 * @return Annotation object representing the annotated document
	 */
	public Annotation getAnnotatedDoc(String title, String docText) {
		System.err.println("Annotating '" + title + "'...");
		annotatedDoc = new Annotation(docText);
		pipeline.annotate(annotatedDoc);
		return annotatedDoc;
	}
	
	
	/**
	 * @param annotatedDoc the annotated representation of the document
	 * @param title the title of the document to be used in the name of the serialized file
	 * @param source the source of the document to be used in the name of the serialized file
	 * @param dirPath the directory path to use to save the serialized file
	 */
	public static void serializeAnnotatedDoc(Annotation annotatedDoc, String title, String source, String dirPath) {
		//Check that this dirPath is a directory
		File dir = new File(dirPath);
		
		StringBuilder sb = new StringBuilder(dirPath);
		if (sb.charAt(sb.length()-1) != '/') {
			sb.append("/");
		}
		StringBuilder filenameBuilder = new StringBuilder();
		filenameBuilder.append(source);
		filenameBuilder.append("__");
		filenameBuilder.append(title);
		filenameBuilder.append(".ser.gz");
		String filename = filenameBuilder.toString().replaceAll(" ", "_");
		sb.append(filename);
		
		System.err.println("Serializing '" + filename + "'...");
		
		if (dir.exists() && dir.isDirectory()) {
			try {
				FileOutputStream outFile = new FileOutputStream(sb.toString(), false);
				GZIPOutputStream gzipStream = new GZIPOutputStream(outFile);
				ObjectOutputStream oos = new ObjectOutputStream(gzipStream);
				oos.writeObject(annotatedDoc);
				oos.flush();
				oos.close();
				gzipStream.close();
				outFile.close();
			} catch (Exception e) {
				System.err.println("ERROR: Could not write serialized annotation for '" + sb.toString() + "'.");
				e.printStackTrace();
			}
			
		}
		else {
			System.err.println("Path '" + dirPath + "' must be a directory.");
		}
	}
	
	
	public static Annotation deserializeAnnotatedDoc(String objectPath) {
		System.err.println("Deserializing '" + objectPath + "'...");
		Annotation annotatedDoc = null;
		File annotationFile = new File(objectPath);
		
		if (annotationFile.exists() && annotationFile.isFile()) {
			try {
				FileInputStream inFile = new FileInputStream(objectPath);
				GZIPInputStream gzipStream = new GZIPInputStream(inFile);
				ObjectInputStream ois = new ObjectInputStream(gzipStream);
				annotatedDoc = (Annotation) ois.readObject();
				ois.close();
				gzipStream.close();
				inFile.close();
			} catch (Exception e) {
				System.err.println("ERROR: Could not deserialized annotation for '" + objectPath + "'.");
				e.printStackTrace();
			}
		}
		else {
			System.err.println("Annotation object file '" + objectPath + "' doesn't exist, or is not a file.");
		}
		
		return annotatedDoc;
	}


	public static void main(String[] args) {
		
		
		annotateSomeSamples();
		System.exit(0);
		
		
//		String exampleText = "The software developer who inserted a major security flaw into OpenSSL 1.2.4.8, using the file foo/bar/blah.php has said the error was \"quite trivial\" despite the severity of its impact, according to a new report.  The Sydney Morning Herald published an interview today with Robin Seggelmann, who added the flawed code to OpenSSL, the world's most popular library for implementing HTTPS encryption in websites, e-mail servers, and applications. The flaw can expose user passwords and potentially the private key used in a website's cryptographic certificate (whether private keys are at risk is still being determined). This is a new paragraph about Apache Tomcat's latest update 7.0.1.";
		String exampleText = "Microsoft Windows 7 before SP1 has Sun Java cross-site scripting vulnerability Java SE in file.php (refer to CVE-2014-1234).";
//		String exampleText = "Oracle DBRM has vulnerability in ABCD plug-in via abcd.1234 (found on abcd.com).";
		EntityLabeler labeler = new EntityLabeler();
		Annotation doc = labeler.getAnnotatedDoc("My Doc", exampleText);
		
		List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
		for ( CoreMap sentence : sentences) {
			for ( CoreLabel token : sentence.get(TokensAnnotation.class)) {
				System.out.println(token.get(TextAnnotation.class) + "\t" + token.get(CyberAnnotation.class) + "\t" + token.get(CyberHeuristicAnnotation.class));
				if (token.containsKey(CyberHeuristicMethodAnnotation.class)) {
					System.out.println("\t" + token.get(CyberHeuristicMethodAnnotation.class));
				}
				if (token.containsKey(CyberConfidenceAnnotation.class)) {
					double[] probabilities = token.get(CyberConfidenceAnnotation.class);
					for (int i=0; i<probabilities.length; i++) {
						System.out.print(probabilities[i] + ", ");
					}
				}
				System.out.println();
			}
			
			System.out.println("Entities:\n" + sentence.get(CyberEntityMentionsAnnotation.class));
			
			System.out.println("Parse Tree:\n" + sentence.get(TreeAnnotation.class));		
		}
		String docDir = "/stucco/docs/";
		File dir = new File(docDir);
		if (!dir.exists()) {
			dir.mkdir();
		}
		EntityLabeler.serializeAnnotatedDoc(doc, "My Doc", "Krebs", docDir);
		
		Annotation deserDoc = EntityLabeler.deserializeAnnotatedDoc(docDir+"Krebs__My_Doc.ser.gz");
//		sentences = deserDoc.get(SentencesAnnotation.class);
//		for ( CoreMap sentence : sentences) {
//			for ( CoreLabel token : sentence.get(TokensAnnotation.class)) {
//				System.out.println(token.get(TextAnnotation.class) + "\t" + token.get(CyberAnnotation.class) + "\t" + token.get(CyberHeuristicAnnotation.class));
//				if (token.containsKey(CyberHeuristicMethodAnnotation.class)) {
//					System.out.println("\t" + token.get(CyberHeuristicMethodAnnotation.class));
//				}
//				if (token.containsKey(CyberConfidenceAnnotation.class)) {
//					double[] probabilities = token.get(CyberConfidenceAnnotation.class);
//					for (int i=0; i<probabilities.length; i++) {
//						System.out.print(probabilities[i] + ", ");
//					}
//				}
//				System.out.println();
//			}
//			
//			System.out.println("Entities:\n" + sentence.get(CyberEntityMentionsAnnotation.class));
//			
//			System.out.println("Parse Tree:\n" + sentence.get(TreeAnnotation.class));		
//		}
	}

	public static void annotateSomeSamples() 
	{
//		String exampleText = "The software developer who inserted a major security flaw into OpenSSL 1.2.4.8, using the file foo/bar/blah.php has said the error was \"quite trivial\" despite the severity of its impact, according to a new report.  The Sydney Morning Herald published an interview today with Robin Seggelmann, who added the flawed code to OpenSSL, the world's most popular library for implementing HTTPS encryption in websites, e-mail servers, and applications. The flaw can expose user passwords and potentially the private key used in a website's cryptographic certificate (whether private keys are at risk is still being determined). This is a new paragraph about Apache Tomcat's latest update 7.0.1.";
//		String exampleText = "Microsoft Windows 7 before SP1 has Sun Java cross-site scripting vulnerability Java SE in file.php (refer to CVE-2014-1234).";
//		String exampleText = "Oracle DBRM has vulnerability in ABCD plug-in via abcd.1234 (found on abcd.com).";
		EntityLabeler labeler = new EntityLabeler();
		
		
		//File directory = new File("/Users/p5r/git/relation-bootstrap/DataFiles/Training/EntityExtractedSerialized/");
		File directory = new File("/Users/p5r/Downloads/sergztexts/");
		for(File f : directory.listFiles())
		{
			if(!f.getName().endsWith(".ser.gz"))
				continue;
			
			
			String exampleText = getExampleTextFromSerGzAlreadyExtracted(f);
			
			
			Annotation doc = labeler.getAnnotatedDoc("My Doc", exampleText);
		
			List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
			for ( CoreMap sentence : sentences) {
				for ( CoreLabel token : sentence.get(TokensAnnotation.class)) {
					System.out.println(token.get(TextAnnotation.class) + "\t" + token.get(CyberAnnotation.class) + "\t" + token.get(CyberHeuristicAnnotation.class));
					if (token.containsKey(CyberHeuristicMethodAnnotation.class)) {
						System.out.println("\t" + token.get(CyberHeuristicMethodAnnotation.class));
					}
					if (token.containsKey(CyberConfidenceAnnotation.class)) {
						double[] probabilities = token.get(CyberConfidenceAnnotation.class);
						for (int i=0; i<probabilities.length; i++) {
							System.out.print(probabilities[i] + ", ");
						}
					}
					System.out.println();
				}
			
				//System.out.println("Entities:\n" + sentence.get(CyberEntityMentionsAnnotation.class));
			
				//System.out.println("Parse Tree:\n" + sentence.get(TreeAnnotation.class));	
				
				//SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
				//System.out.println("Dependencies:\n" + dependencies);
			}
			String docDir = "/Users/p5r/Downloads/newsergztexts/";
			File dir = new File(docDir);
			if (!dir.exists()) {
				dir.mkdir();
			}
			EntityLabeler.serializeAnnotatedDoc(doc, f.getName(), "sergz", docDir);
		
//			Annotation deserDoc = EntityLabeler.deserializeAnnotatedDoc(docDir+"Krebs__My_Doc.ser.gz");
//			sentences = deserDoc.get(SentencesAnnotation.class);
//			for ( CoreMap sentence : sentences) {
//				for ( CoreLabel token : sentence.get(TokensAnnotation.class)) {
//					System.out.println(token.get(TextAnnotation.class) + "\t" + token.get(CyberAnnotation.class) + "\t" + token.get(CyberHeuristicAnnotation.class));
//					if (token.containsKey(CyberHeuristicMethodAnnotation.class)) {
//						System.out.println("\t" + token.get(CyberHeuristicMethodAnnotation.class));
//					}
//					if (token.containsKey(CyberConfidenceAnnotation.class)) {
//						double[] probabilities = token.get(CyberConfidenceAnnotation.class);
//						for (int i=0; i<probabilities.length; i++) {
//							System.out.print(probabilities[i] + ", ");
//						}
//					}
//					System.out.println();
//				}
//			
//				System.out.println("Entities:\n" + sentence.get(CyberEntityMentionsAnnotation.class));
//			
//				System.out.println("Parse Tree:\n" + sentence.get(TreeAnnotation.class));		
//			}
		}
	}
	
	private static String getExampleTextFromSerGz(File f)
	{
		String result = "";
		
			Annotation deserDoc = EntityLabeler.deserializeAnnotatedDoc(f.getAbsolutePath());
			List<CoreMap> sentences = deserDoc.get(SentencesAnnotation.class);
			for (int sentencenum = 0; sentencenum < sentences.size(); sentencenum++) 
			{
				CoreMap sentence = sentences.get(sentencenum);
				
				List<CoreLabel> labels = sentence.get(TokensAnnotation.class);
				
			 	for (int i = 0; i < labels.size(); i++) 
			 	{
			 		CoreLabel token = labels.get(i);
			 		String tokenstring = token.get(TextAnnotation.class);
			 		result += " " + tokenstring;
			 	}
			 	result = result.trim() + "\n";
			}
			
		return result;
	}
	
	private static String getExampleTextFromSerGzAlreadyExtracted(File f)
	{
		String result = "";
		
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(f));
			String line;
			while((line = in.readLine()) != null)
				result += line + "\n";
			in.close();
		}catch(IOException e)
		{
			System.out.println(e);
			e.printStackTrace();
			System.exit(3);
		}
			
		return result;
	}
}
