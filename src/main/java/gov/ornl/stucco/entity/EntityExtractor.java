package gov.ornl.stucco.entity;

import gov.ornl.stucco.entity.models.Context;
import gov.ornl.stucco.entity.models.Sentence;
import gov.ornl.stucco.entity.models.Sentences;
import gov.ornl.stucco.entity.models.Word;

import java.io.DataInputStream;
import java.io.File;
import java.util.List;

import opennlp.perceptron.BinaryPerceptronModelReader;
import opennlp.perceptron.PerceptronModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Identify and annotate domain-specific entities from unstructured text.
 * 
 * <p>
 * <pre>
 * -- Example Usage --
 * {@code
 * 	public static void main(String[] args) {
 *	  ObjectMapper mapper = new ObjectMapper();
 *	  try {
 *   	EntityExtractor entityExtractor;
 *		if (args.length != 2) {
 *			entityExtractor = new EntityExtractor();
 *		}
 *		else {
 *			entityExtractor = new EntityExtractor(args[0], args[1]);
 *		}
 *		String json = entityExtractor.getAnnotatedTextAsJson("Microsoft Windows XP. Apple Mac OS X.");
 *			
 *		//transform the JSON string into a Sentences instance
 *		Sentences sentences = mapper.readValue(json, Sentences.class);
 *	  } catch (Exception e) {
 *		e.printStackTrace();
 *	  }	
 *	}
 * }
 * </pre>
 * </p>
 *
 */
public class EntityExtractor {
	public static final String PREV_WORD = "_PREVIOUS_";
	public static final String NEXT_WORD = "_NEXT_";
	public static final String POS = "_POS_";
	public static final String IOB = "O";
	public static final String LABEL = "O";
	
	private static final String sentenceModelFile = "en-sent.bin";
	private static final String posModelFile = "en-pos-perceptron.bin";
	
	private SentenceDetectorME sentenceDetector;
	private SimpleTokenizer tokenizer;
	private POSTaggerME posTagger;
	//default perceptron model files in binary format
	private String iobModelFile = "test-IOB-Perceptron.bin";
	private String domainModelFile = "test-Domain-Perceptron.bin";
	private PerceptronModel iobModel;
	private PerceptronModel domainModel;
	
	/**
	 * @param iobModelFile the IOB perceptron model file in binary format
	 * @param domainModelFile the domain label perceptron model file in binary format
	 * @throws Exception if the model files cannot be read in
	 */
	public EntityExtractor(String iobModelFile, String domainModelFile) throws Exception {
		this.iobModelFile = iobModelFile;
		this.domainModelFile = domainModelFile;
		try {
			sentenceDetector = new SentenceDetectorME(new SentenceModel(EntityExtractor.class.getClassLoader().getResourceAsStream(sentenceModelFile)));
			tokenizer = SimpleTokenizer.INSTANCE;
			posTagger = new POSTaggerME(new POSModel(EntityExtractor.class.getClassLoader().getResourceAsStream(posModelFile)));
			iobModel = (PerceptronModel) (new BinaryPerceptronModelReader(new File(iobModelFile))).getModel();
			domainModel = (PerceptronModel) (new BinaryPerceptronModelReader(new File(domainModelFile))).getModel();
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	/**
	 * @throws Exception if the default model files cannot be read in
	 * 
	 */
	public EntityExtractor() throws Exception {
		try {
			sentenceDetector = new SentenceDetectorME(new SentenceModel(EntityExtractor.class.getClassLoader().getResourceAsStream(sentenceModelFile)));
			tokenizer = SimpleTokenizer.INSTANCE;
			posTagger = new POSTaggerME(new POSModel(EntityExtractor.class.getClassLoader().getResourceAsStream(posModelFile)));
			iobModel = (PerceptronModel) (new BinaryPerceptronModelReader(new DataInputStream(EntityExtractor.class.getClassLoader().getResourceAsStream(this.iobModelFile)))).getModel();
			domainModel = (PerceptronModel) (new BinaryPerceptronModelReader(new DataInputStream(EntityExtractor.class.getClassLoader().getResourceAsStream(this.domainModelFile)))).getModel();
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	/**
	 * Parse the text into separate sentences.
	 * 
	 * @param text the document's content
	 * @return an array of sentences
	 */
	public String[] getSentences(String text) {
		String[] sentences = sentenceDetector.sentDetect(text);
		return sentences;
	}
	
	
	/**
	 * Parse a sentence into separate words.
	 * 
	 * @param sent the sentence string
	 * @return a Sentence instance representing the sentence string
	 */
	public Sentence tokenize(String sent) {
		Sentence sentence = new Sentence();
		String[] tokens = tokenizer.tokenize(sent);
		for (int i=0; i<tokens.length; i++) {
			Word aWord = new Word(tokens[i]);
			sentence.addWord(aWord);
		}
		return sentence;
	}
	
	
	/**
	 * Tag each word of a sentence with its part of speech. A sentence
	 * must be tokenized before it can be tagged with part of speech.
	 * 
	 * @param sentence the instance representing a sentence string
	 * @return the Sentence instance with the addition of part of speech tags 
	 */
	public Sentence getPOS(Sentence sentence) {
		List<String> wordList = sentence.getWordsAsStrings();
		if (!wordList.isEmpty()) {
			String[] wordArray = new String[wordList.size()];
			wordArray = wordList.toArray(wordArray);
	
			List<Word> words = sentence.getWordList();
			String[] posArray = posTagger.tag(wordArray);
			for (int i=0; i<posArray.length; i++) {
				Word aWord = words.get(i);
				aWord.setPos(posArray[i]);
			}
		}
		else {
			System.err.println("The sentence could not be tagged with parts of speech. Make sure the sentence has been tokenized first.");
		}
		
		return sentence;
	}
	
	
	/**
	 * Annotate each word of a sentence using I-O-B formatting. I-O-B formatting
	 * requires the sentence to be tokenized and tagged with parts of speech first.
	 * 
	 * @param sentence the instance representing a sentence
	 * @return the Sentence instance with the addition of I-O-B format tags
	 */
	public Sentence getIOB(Sentence sentence) {
		List<Word> words = sentence.getWordList();
		
		if (!words.isEmpty()) {
			for (int i=0; i<words.size(); i++) {
				Word aWord = words.get(i);
				Context context = new Context(aWord.getWord(), aWord.getPos(), null);
				context.setHasTarget(false);
				if (i-2 >= 0) {
					Word prevWord2 = words.get(i-2);
					context.setPPreviousIOB(prevWord2.getWord(), prevWord2.getPos(), prevWord2.getIob());
				}
				else {
					context.setPPreviousIOB(PREV_WORD, POS, IOB);
				}
				if (i-1 >= 0) {
					Word prevWord = words.get(i-1);
					context.setPreviousIOB(prevWord.getWord(), prevWord.getPos(), prevWord.getIob());
				}
				else {
					context.setPreviousIOB(PREV_WORD, POS, IOB);
				}
				if (i+1 < words.size()) {
					Word nextWord = words.get(i+1);
					context.setNextWord(nextWord.getWord(), nextWord.getPos());
				}
				else {
					context.setNextWord(NEXT_WORD, POS);
				}
				if (i+2 < words.size()) {
					Word nextWord2 = words.get(i+2);
					context.setNNextWord(nextWord2.getWord(), nextWord2.getPos());
				}
				else {
					context.setNNextWord(NEXT_WORD, POS);
				}
				context.set2PreviousIOB();
				context.setPreviousIOBAndWord();
				
				//evaluate the word and context to get the model's guess of iob tag
				String[] contextArray = context.toString().split(" ");
				double[] results = iobModel.eval(contextArray);
				String iobLabel = iobModel.getBestOutcome(results);
				aWord.setIob(iobLabel);
			}
		}
		else {
			System.err.println("The sentence could not be annotated with I-O-B formatting tags. Make sure the sentence has been tokenized and tagged with parts of speech first.");
		}
		
		return sentence;
	}
	
	
	/**
	 * Label each word of a sentence with a domain-specific label. The sentence
	 * must be tokenized, tagged with parts of speech, and formatted with I-O-B
	 * tags before it can be given a domain-specific label.
	 * 
	 * @param sentence the instance representing a sentence
	 * @return the Sentence instance with the addition of domain-specific labels
	 */
	public Sentence getLabels(Sentence sentence) {
		List<Word> words = sentence.getWordList();
		
		if (!words.isEmpty()) {
			for (int i=0; i<words.size(); i++) {
				Word aWord = words.get(i);
				
				Context context = new Context(aWord.getWord(), aWord.getPos(), aWord.getIob(), null);
				context.setHasTarget(false);
				if (i-2 >= 0) {
					Word prevWord2 = words.get(i-2);
					context.setPPreviousLabel(prevWord2.getWord(), prevWord2.getPos(), prevWord2.getIob(), prevWord2.getDomainLabel());
				}
				else {
					context.setPPreviousLabel(PREV_WORD, POS, IOB, LABEL);
				}
				if (i-1 >= 0) {
					Word prevWord = words.get(i-1);
					context.setPreviousLabel(prevWord.getWord(), prevWord.getPos(), prevWord.getIob(), prevWord.getDomainLabel());
				}
				else {
					context.setPreviousLabel(PREV_WORD, POS, IOB, LABEL);
				}
				if (i+1 < words.size()) {
					Word nextWord = words.get(i+1);
					context.setNextIOB(nextWord.getWord(), nextWord.getPos(), nextWord.getIob());
				}
				else {
					context.setNextIOB(NEXT_WORD, POS, IOB);
				}
				if (i+2 < words.size()) {
					Word nextWord2 = words.get(i+2);
					context.setNNextIOB(nextWord2.getWord(), nextWord2.getPos(), nextWord2.getIob());
				}
				else {
					context.setNNextIOB(NEXT_WORD, POS, IOB);
				}
				context.set2PreviousLabel();
				context.setPreviousLabelAndWord();
				context.setPreviousIOBAndWord();
				
				String[] contextArray = context.toString().split(" ");
				double[] results = domainModel.eval(contextArray);
				String domainLabel = domainModel.getBestOutcome(results);
				aWord.setDomainLabel(domainLabel);
				aWord.setDomainScore(results[domainModel.getIndex(domainLabel)]);
			}
		}
		else {
			System.err.println("The sentence could not be labeled with domain-specific terms. Make sure the sentence has been tokenized, tagged with parts of speech, and annotated with I-O-B format tags first.");
		}
		
		return sentence;
	}
	
	
	/**
	 * Execute the entire annotation process on the text. This process includes
	 * parsing into separate sentences, tokenizing each sentence into words, and annotating
	 * each word with a part of speech tag, I-O-B format tag, and domain-specific label.
	 * 
	 * @param text the text to be annotated
	 * @return a Sentences instance that contains all the Sentence instances from the text
	 */
	public Sentences getAnnotatedText(String text) {
		Sentences sentences = new Sentences();
		
		//break up into separate sentences
		String[] sentencesArray = getSentences(text);
		
		for (int i=0; i<sentencesArray.length; i++) {
			String sentence = sentencesArray[i];
		
			//tokenize sentence
			Sentence sent = tokenize(sentence);
			
			//tag with parts of speech
			sent = getPOS(sent);
			
			//use I-O-B format to annotate sentence
			sent = getIOB(sent);
			
			//label each word with domain=specific term
			sent = getLabels(sent);
			
			//get JSON representation of the Sentence instance
			sentences.addSentence(sent);
		}
		
		return sentences;
	}
	
	
	/**
	 * Execute the entire annotation process on the text.
	 * @see #getAnnotatedText(String)
	 * 
	 * @param text the text to be annotated
	 * @return a JSON-formatted string representing the document's text and corresponding annotations
	 */
	public String getAnnotatedTextAsJson(String text) {
		Sentences sentences = getAnnotatedText(text);
		
		return sentences.toJSON();
	}
	

	/**
	 * Example code that gets the annotated text in JSON-formatted, then
	 * parses the Sentence instances from the JSON.
	 * 
	 * @param args the absolute paths to the I-O-B-format perceptron model, followed by
	 * the domain-specific perceptron model; if not provided then the default testing models 
	 * will be used
	 * 
	 * Note: all perceptron models must be in binary format
	 */
	public static void main(String[] args) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			EntityExtractor entityExtractor;
			if (args.length != 2) {
				entityExtractor = new EntityExtractor();
			}
			else {
				entityExtractor = new EntityExtractor(args[0], args[1]);
			}
			String json = entityExtractor.getAnnotatedTextAsJson("Microsoft Windows XP. Apple Mac OS X.");
			System.out.println(json);
			
			//transform the JSON string into a Sentences instance
			@SuppressWarnings("unused")
			Sentences sentences = mapper.readValue(json, Sentences.class);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

}
