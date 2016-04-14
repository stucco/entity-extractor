# Entity Extraction
Library to identify and label cyber-domain entities from unstructured text. This library makes use of [Stanford's CoreNLP] (http://nlp.stanford.edu/software/corenlp.shtml) and [Apache's OpenNLP] (https://opennlp.apache.org) libraries.

## Entity Types
* Software
	* Vendor
	* Product
	* Version
* File
	* Name
* Function
	* Name
* Vulnerability
	* Name
	* Description
	* CVE
	* MS

## Input
* User-created Apache OpenNLP perceptron model file in binary format that represents a cyber-domain entity model
* Default CoreNLP models for tokenizing, part-of-speech tagging, sentence splitting, etc.
* Text content of document to be annotated with cyber labels

## Output
An Annotation object that represents the document as a map, where annotator classnames are keys. The document map includes the following values:

* Text: original raw text
* Sentences: list of sentences
  * Sentence: map representing one sentence
    * Token: word within the sentence
    * POSTag: part-of-speech tag
    * CyberEntity: cyber domain label for the token
  * ParseTree: sentence structure as a tree


## Usage
	EntityLabeler labeler = new EntityLabeler();
	Annotation doc = labeler.getAnnotatedDoc("My Doc", exampleText);

	List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
	for ( CoreMap sentence : sentences) {
		for ( CoreLabel token : sentence.get(TokensAnnotation.class)) {
			System.out.println(token.get(TextAnnotation.class) + "\t" + token.get(PartOfSpeechAnnotation.class) + "\t" + token.get(CyberAnnotation.class));
		}
		
		System.out.println("Parse Tree:\n" + sentence.get(TreeAnnotation.class));			
	}

See CoreNLP's [JavaDocs] (http://nlp.stanford.edu/nlp/javadoc/javanlp/) and [Usage section] (http://nlp.stanford.edu/software/corenlp.shtml) for more information.

## Test
TODO: Need to add functionality tests.
	mvn test

## License
This software is freely distributable under the terms of the MIT License.

Copyright (c) UT-Battelle, LLC (the "Original Author")

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS, THE U.S. GOVERNMENT, OR UT-BATTELLE BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.