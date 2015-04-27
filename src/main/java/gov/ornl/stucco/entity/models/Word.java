package gov.ornl.stucco.entity.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Represents an annotated word from a sentence, where the properties include
 * the word itself, its part of speech tag, the I-O-B formatting tag, the 
 * domain-specific term assigned to the word, and the probability that the assigned domain
 * term is appropriate.
 *
 */
public class Word {
	private static ObjectMapper mapper = new ObjectMapper();
	
	private String word;
	private String pos;
//	private String iob;
	private String domainLabel;
	private double domainScore;
	
	public Word() {
		this.word = "";
		this.pos = "";
//		this.iob = "";
		this.domainLabel = "";
		this.domainScore = 0.0;
	}
	
	public Word(String word) {
		this.word = word;
		this.pos = "";
//		this.iob = "";
		this.domainLabel = "";
		this.domainScore = 0.0;
	}
	
	public Word(String word, String pos, String iob, String domainLabel, double domainScore) {
		this.word = word;
		this.pos = pos;
//		this.iob = iob;
		this.domainLabel = domainLabel;
		this.domainScore = domainScore;
	}
	
	public Word(String word, String pos, String domainLabel, double domainScore) {
		this.word = word;
		this.pos = pos;
		this.domainLabel = domainLabel;
		this.domainScore = domainScore;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

//	public String getIob() {
//		return iob;
//	}
//
//	public void setIob(String iob) {
//		this.iob = iob;
//	}

	public String getDomainLabel() {
		return domainLabel;
	}

	public void setDomainLabel(String domainLabel) {
		this.domainLabel = domainLabel;
	}

	public double getDomainScore() {
		return domainScore;
	}

	public void setDomainScore(double domainScore) {
		this.domainScore = domainScore;
	}

	@Override
	public String toString() {
		return "Word [word=" + word + ", pos=" + pos /*+ ", iob=" + iob*/
				+ ", domainLabel=" + domainLabel + ", domainScore="
				+ domainScore + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domainLabel == null) ? 0 : domainLabel.hashCode());
		long temp;
		temp = Double.doubleToLongBits(domainScore);
		result = prime * result + (int) (temp ^ (temp >>> 32));
//		result = prime * result + ((iob == null) ? 0 : iob.hashCode());
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		result = prime * result + ((word == null) ? 0 : word.hashCode());
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
		Word other = (Word) obj;
		if (domainLabel == null) {
			if (other.domainLabel != null)
				return false;
		} else if (!domainLabel.equals(other.domainLabel))
			return false;
		if (domainScore != other.domainScore)
			return false;
//		if (iob == null) {
//			if (other.iob != null)
//				return false;
//		} else if (!iob.equals(other.iob))
//			return false;
		if (pos == null) {
			if (other.pos != null)
				return false;
		} else if (!pos.equals(other.pos))
			return false;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
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
