# Entity Extraction
Library to identify and extract domain-specific entities from unstructured text. This library makes use of Apache's OpenNLP software. See the [Javadocs] (https://github.com/stucco/entity-extractor/doc/index.html) for more details.

## Input
Apache's OpenNLP perceptron models in binary format.

* en-sent.bin - built-in model file to detect sentence boundaries; downloaded from the [OpenNLP wiki] (http://opennlp.sourceforge.net/models-1.5)
* en-pos-perceptron.bin - built-in model file to assign part of speech tags; downloaded from the [OpenNLP wiki] (http://opennlp.sourceforge.net/models-1.5)
* test-IOB-perceptron.bin - built-in model file to assign I-O-B formatting tags; created by training a perceptron model on an [annotated cyber security corpus] (https://github.com/stucco/auto-labeled-corpus); used as the default model for testing
* test-Domain-perceptron.bin - built-in model file used to label domain-specific entities; created by training a perceptron model on an [annotated cyber security corpus] (https://github.com/stucco/auto-labeled-corpus); used as the default model for testing
* user-created perceptron model file in binary format that represents an I-O-B formatting model
* user-created perceptron model file in binary format that represents a domain-specific entities model

## Output
JSON-formatted string representing the annotated version of the unstructured text. Example JSON string:

	{
  		"sentences" : [ 
  		 {
    		"words" : [ "Microsoft", "Windows", "XP", "." ],
    		"pos" : [ "NNP", "NNP", "NNP", "." ],
    		"iob" : [ "B", "B", "O", "O" ],
    		"domainLabels" : [ "sw.vendor", "sw.product", "O", "O" ],
    		"domainScores" : [ 0.3083600176497029, 0.29496901049795676, 0.30469849374496444, 0.3035644073881222 ]
  		 }, 
  		 {
    		"words" : [ "Apple", "Mac", "OS", "X", "." ],
    		"pos" : [ "NNP", "NNP", "NNP", "NNP", "." ],
    		"iob" : [ "B", "B", "I", "I", "O" ],
    		"domainLabels" : [ "sw.vendor", "sw.product", "sw.product", "sw.product", "sw.version" ],
    		"domainScores" : [ 0.3023481844622021, 0.2960099268790269, 0.29650331085403353, 0.29420049883757027, 0.278111912834388 ]
  		 },
  		 ...
  		]
	}


## Usage
 	EntityExtractor entityExtractor = new EntityExtractor("/path/to/IOB_perceptron_model.bin", "/path/to/Domain_perceptron_model.bin");
 	String json = entityExtractor.getAnnotatedTextAsJson("Microsoft Windows XP. Apple Mac OS X.");
 			
 	//transform the JSON string into a Sentences instance
 	ObjectMapper mapper = new ObjectMapper();
 	Sentences sentences = mapper.readValue(json, Sentences.class);