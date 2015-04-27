package gov.ornl.stucco.entity.models;

import gov.ornl.stucco.entity.models.Sentence;

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
 * Represents an annotated version of unstructured text. This representation
 * includes a list of Sentence instances.
 *
 */
public class Sentences {
	private static ObjectMapper mapper = new ObjectMapper();
	
	@JsonProperty("sentences")
	private List<Sentence> sentenceList;
	
	public Sentences() {
		sentenceList = new ArrayList<Sentence>();
	}
	
	public Sentences(List<Sentence> sentences) {
		this.sentenceList = sentences;
	}
	
	@JsonGetter("sentences")
	public List<Sentence> getSentenceList() {
		return sentenceList;
	}

	@JsonSetter("sentences")
	public void setSentenceList(List<Sentence> sentenceList) {
		this.sentenceList = sentenceList;
	}
	
	@JsonIgnore
	public void addSentence(Sentence sentence) {
		if (sentence != null) {
			sentenceList.add(sentence);
		}
	}
	
	@Override
	public String toString() {
		return "Sentences [sentenceList=" + sentenceList + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sentenceList == null) ? 0 : sentenceList.hashCode());
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
		Sentences other = (Sentences) obj;
		if (sentenceList == null) {
			if (other.sentenceList != null)
				return false;
		} else if (!sentenceList.equals(other.sentenceList))
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
