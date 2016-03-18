package gov.ornl.stucco.entity;

import java.io.DataInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import opennlp.perceptron.BinaryPerceptronModelReader;
import opennlp.perceptron.PerceptronModel;
import edu.stanford.nlp.ie.machinereading.structure.Span;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.util.ArraySet;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.ErasureUtils;
import edu.stanford.nlp.util.StringUtils;
import gov.ornl.stucco.entity.CyberHeuristicAnnotator.CyberHeuristicAnnotation;
import gov.ornl.stucco.entity.models.Context;
import gov.ornl.stucco.entity.models.CyberEntityMention;
import gov.ornl.stucco.entity.models.CyberEntityType;

public class CyberEntityAnnotator implements Annotator {
	public static final String STUCCO_CYBER_ENTITY = "cyberentity";
	public static final Requirement CYBER_ENTITY_REQUIREMENT = new Requirement(STUCCO_CYBER_ENTITY);

	private static String modelFilePath = "models/ORNL-Domain-perceptron.bin";
	private String cyberModelFile;
	private PerceptronModel cyberModel;
//	private Map<String, CyberEntityType> cyberDictionary;
	
	
	public CyberEntityAnnotator(String className) {
		this(className, StringUtils.argsToProperties("-model", modelFilePath));
	}
	
	public CyberEntityAnnotator(String className, Properties config) {
		cyberModelFile = config.getProperty("model", modelFilePath);
		
		System.err.println("Loading model from '" + cyberModelFile + "'");
		try {
			cyberModel = (PerceptronModel) (new BinaryPerceptronModelReader(new DataInputStream(CyberEntityAnnotator.class.getClassLoader().getResourceAsStream(cyberModelFile)))).getModel();
		} catch (Exception e) {
			try {
				cyberModel = (PerceptronModel) (new BinaryPerceptronModelReader(new File(cyberModelFile))).getModel();
			} catch (Exception ex) {
				System.err.println("Could not load cyber model from '" + cyberModelFile + "'.");
				ex.printStackTrace();
				System.exit(1);
			}
		}
	}

	@Override
	public void annotate(Annotation annotation) {
		System.err.println("Annotating with cyber labels ... ");
		Map<Integer, List<CyberEntityMention>> entityMentionsMap = new HashMap<Integer, List<CyberEntityMention>>();
		
		if (annotation.has(SentencesAnnotation.class)) {
			List<CoreLabel> tokens = annotation.get(TokensAnnotation.class);
			for (int i=0; i<tokens.size(); i++) {
				CoreLabel token = tokens.get(i);
				String word = token.get(TextAnnotation.class);
				String pos = token.getString(PartOfSpeechAnnotation.class);
				CyberEntityType heuristicLabel = token.get(CyberHeuristicAnnotation.class);
				CyberEntityType label = CyberHeuristicAnnotator.O;
				// Check if it's in the token map based on the training data
				
				
				// Use the heuristic label if not "O"
				if (!heuristicLabel.equals(CyberHeuristicAnnotator.O)) {
					label = heuristicLabel;
				}
				// Otherwise, evaluate the context in the MEM (Apache OpenNLP)
				else {
					//if there is a previous word
					String previousWord = Context.START_WORD;
					if (i-1 >= 0) {
						previousWord = tokens.get(i-1).get(TextAnnotation.class);
					}
					Context context = new Context(word, pos, heuristicLabel.toString(), previousWord);
					double[] results = cyberModel.eval(context.toArray());
					String cyberLabel = cyberModel.getBestOutcome(results);
					if (cyberLabel.contains(".")) {
						int index = cyberLabel.indexOf(".");
						String type = cyberLabel.substring(0, index);
						String subType = cyberLabel.substring(index + 1);
						label = new CyberEntityType(type, subType);
					}
				}
				
				if (label != null) {				
					//annotate the token with the new cyber label
					token.set(CyberAnnotation.class, label);
					
					if (!label.equals(CyberHeuristicAnnotator.O)) {
						//Create new EntityMentions or add to existing one
						int sentenceIndex = token.sentIndex();
						CoreMap sentence = annotation.get(SentencesAnnotation.class).get(sentenceIndex);
						//token indexing starts at 1, while span indexing starts at 0
						Span cyberSpan = new Span(token.index()-1, token.index());
						
						CyberEntityMention cyberMention = new CyberEntityMention(CyberEntityMention.makeUniqueId(), sentence, cyberSpan, cyberSpan, label.getCyberType(), label.getCyberSubType(), null);
						
						//Add this EntityMentions to the list for its corresponding sentence
						List<CyberEntityMention> sentEntityList = entityMentionsMap.get(Integer.valueOf(sentenceIndex));
						if (sentEntityList == null) {
							sentEntityList = new ArrayList<CyberEntityMention>();
						}
						
						if (sentEntityList.size() > 1) {
							CyberEntityMention latestCyberMention = sentEntityList.get(sentEntityList.size()-1);
							if ((latestCyberMention.labelEquals(cyberMention, true)) && (cyberSpan.start() == latestCyberMention.getHeadTokenEnd())) {
								latestCyberMention.getHead().expandToInclude(cyberSpan);
							}
							else {
								sentEntityList.add(cyberMention);
							}
						}
						else {
							sentEntityList.add(cyberMention);
						}
						
						//update the sentence's EntityMention list
						entityMentionsMap.put(Integer.valueOf(sentenceIndex), sentEntityList);
					}
				}
				
			}
			
			//set the EntityMention key for the sentence
			for (Integer sentIndex : entityMentionsMap.keySet()) {
				CoreMap sentence = annotation.get(SentencesAnnotation.class).get(sentIndex.intValue());
				sentence.set(CyberEntityMentionsAnnotation.class, ((List<CyberEntityMention>) entityMentionsMap.get(sentIndex)));
			}
			
		}
	}

	@Override
	public Set<Requirement> requirementsSatisfied() {
		return Collections.unmodifiableSet(new ArraySet<Requirement>(CYBER_ENTITY_REQUIREMENT));
	}

	@Override
	public Set<Requirement> requires() {
		Set<Requirement> prerequisites = new ArraySet<Requirement>();
//		prerequisites.addAll(Annotator.TOKENIZE_SSPLIT_POS);
		prerequisites.add(CyberHeuristicAnnotator.CYBER_HEURISTICS_REQUIREMENT);
		return Collections.unmodifiableSet(prerequisites);
	}
	
	  /**
	   * The CyberAnnotation key for getting the STUCCO cyber label of a token.
	   *
	   * This key is set on token annotations.
	   */
	  public static class CyberAnnotation implements CoreAnnotation<CyberEntityType> {
	    public Class<CyberEntityType> getType() {
	      return CyberEntityType.class;
	    }
	  }
	  
	  /**
	   * The CyberEntityAnnotation key for getting the STUCCO cyber entities of a sentence.
	   *
	   * This key is set on the sentence annotations.
	   */
	  public static class CyberEntityMentionsAnnotation implements CoreAnnotation<List<CyberEntityMention>> {
	    public Class<List<CyberEntityMention>> getType() {
	      return ErasureUtils.uncheckedCast(List.class);
	    }
	  }

}
