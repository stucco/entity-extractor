# Entity Extraction
Library to identify and extract domain-specific entities from unstructured text. This library makes use of Apache's OpenNLP software. See the [Javadocs] (https://github.com/stucco/entity-extractor/tree/master/doc) for more details.

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
  		"sentences" : [ {
    		"sentence" : [ {
      			"word" : "Microsoft",
      			"pos" : "NNP",
      			"iob" : "B",
      			"domainLabel" : "sw.vendor",
      			"domainScore" : 0.3083600176497029
    		}, {
      			"word" : "Windows",
      			"pos" : "NNP",
      			"iob" : "B",
      			"domainLabel" : "sw.product",
      			"domainScore" : 0.29496901049795676
    		}, {
      			"word" : "XP",
      			"pos" : "NNP",
      			"iob" : "O",
      			"domainLabel" : "O",
      			"domainScore" : 0.30469849374496444
    		}, {
     			 "word" : ".",
      			"pos" : ".",
      			"iob" : "O",
      			"domainLabel" : "O",
      			"domainScore" : 0.3035644073881222
    		} ]
  		}, {
    		"sentence" : [ {
      			"word" : "Apple",
      			"pos" : "NNP",
      			"iob" : "B",
      			"domainLabel" : "sw.vendor",
      			"domainScore" : 0.3023481844622021
   			}, 
   				...
   			]
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