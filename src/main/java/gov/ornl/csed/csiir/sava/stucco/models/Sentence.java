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
 * includes a list of words, part of speech tags, I-O-B formatting tags, and domain-specific terms.
 *
 */
public class Sentence {
	private static ObjectMapper mapper = new ObjectMapper();
	
	@JsonProperty("words")
	private List<String> wordList;
	@JsonProperty("pos")
	private List<String> posList;
	@JsonProperty("iob")
	private List<String> iobList;
	@JsonProperty("domainLabels")
	private List<String> domainLabelList;
	@JsonProperty("domainScores")
	private List<Double> domainScoreList;
	
	public Sentence() {
		this.wordList = new ArrayList<String>();
		this.posList = new ArrayList<String>();
		this.iobList = new ArrayList<String>();
		this.domainLabelList = new ArrayList<String>();
		this.domainScoreList = new ArrayList<Double>();
	}
	
	@JsonGetter("words")
	public List<String> getWordList() {
		return wordList;
	}

	@JsonSetter("words")
	public void setWordList(List<String> wordList) {
		this.wordList = wordList;
	}
	
	@JsonIgnore
	public void addWord(String word) {
		wordList.add(word);
	}

	@JsonGetter("pos")
	public List<String> getPosList() {
		return posList;
	}

	@JsonSetter("pos")
	public void setPosList(List<String> posList) {
		this.posList = posList;
	}
	
	@JsonIgnore
	public void addPos(String pos) {
		posList.add(pos);
	}

	@JsonGetter("iob")
	public List<String> getIOBList() {
		return iobList;
	}

	@JsonSetter("iob")
	public void setIOBList(List<String> iobList) {
		this.iobList = iobList;
	}
	
	@JsonIgnore
	public void addIOB(String iob) {
		iobList.add(iob);
	}

	@JsonGetter("domainLabels")
	public List<String> getDomainLabelList() {
		return domainLabelList;
	}

	@JsonSetter("domainLabels")
	public void setDomainLabelList(List<String> domainLabelList) {
		this.domainLabelList = domainLabelList;
	}
	
	@JsonIgnore
	public void addDomainLabel(String domainLabel) {
		domainLabelList.add(domainLabel);
	}

	@JsonGetter("domainScores")
	public List<Double> getDomainScoreList() {
		return domainScoreList;
	}

	@JsonSetter("domainScores")
	public void setDomainScoreList(List<Double> domainScoreList) {
		this.domainScoreList = domainScoreList;
	}
	
	@JsonIgnore
	public void addDomainScore(double score) {
		domainScoreList.add(new Double(score));
	}

	@Override
	public String toString() {
		return "Sentence {\n wordList=" + wordList + ", \n posList=" + posList
				+ ", \n iobList=" + iobList + ", \n domainLabelList="
				+ domainLabelList + ", \n domainScoreList=" + domainScoreList
				+ "\n }";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domainLabelList == null) ? 0 : domainLabelList.hashCode());
		result = prime * result + ((domainScoreList == null) ? 0 : domainScoreList.hashCode());
		result = prime * result + ((iobList == null) ? 0 : iobList.hashCode());
		result = prime * result + ((posList == null) ? 0 : posList.hashCode());
		result = prime * result + ((wordList == null) ? 0 : wordList.hashCode());
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
		if (domainLabelList == null) {
			if (other.domainLabelList != null)
				return false;
		} else if (!domainLabelList.equals(other.domainLabelList))
			return false;
		if (domainScoreList == null) {
			if (other.domainScoreList != null)
				return false;
		} else if (!domainScoreList.equals(other.domainScoreList))
			return false;
		if (iobList == null) {
			if (other.iobList != null)
				return false;
		} else if (!iobList.equals(other.iobList))
			return false;
		if (posList == null) {
			if (other.posList != null)
				return false;
		} else if (!posList.equals(other.posList))
			return false;
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
