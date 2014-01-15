package gov.ornl.csed.csiir.sava.stucco.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Represents an annotated sentence from unstructured text. This representation
 * includes a list of Word instances.
 *
 */
public class Sentence {
	private static ObjectMapper mapper = new ObjectMapper();
	
	@JsonProperty("sentence")
	private List<Word> wordList;
	
	public Sentence() {
		this.wordList = new ArrayList<Word>();
	}
	
	@JsonGetter("sentence")
	public List<Word> getWordList() {
		return wordList;
	}

	@JsonSetter("sentence")
	public void setWordList(List<Word> wordList) {
		this.wordList = wordList;
	}
	
	@JsonIgnore
	public void addWord(Word word) {
		wordList.add(word);
	}
	
	@JsonIgnore
	public List<String> getWordsAsStrings() {
		List<String> words = new ArrayList<String>();
		for (Word aWord : wordList) {
			words.add(aWord.getWord());
		}
		return words;
	}

	@Override
	public String toString() {
		return "Sentence [wordList=" + wordList + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((wordList == null) ? 0 : wordList.hashCode());
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
		Sentence other = (Sentence) obj;
		if (wordList == null) {
			if (other.wordList != null)
				return false;
		} else if (!wordList.equals(other.wordList))
			return false;
		return true;
	}

	public String toJSON() {
		String json = "{}";
		try {
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			json = mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return json;
	}
}
