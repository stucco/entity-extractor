package gov.ornl.stucco.utils;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class FreebaseList implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("result")
	private List<FreebaseEntry> entries;
	
	@JsonIgnore
	private String listType;

	@JsonGetter("result")
	public List<FreebaseEntry> getEntries() {
		return entries;
	}

	@JsonSetter("result")
	public void setEntries(List<FreebaseEntry> entries) {
		this.entries = entries;
	}
	
	public void addEntries(FreebaseList anotherList) {
		List<FreebaseEntry> otherEntries = anotherList.getEntries();
		this.entries.addAll(otherEntries);
	}

	public String getListType() {
		return listType;
	}

	public void setListType(String listType) {
		this.listType = listType;
	}
	
	public boolean contains(String value) {
		for (FreebaseEntry entry : this.entries) {
			if (entry.contains(value)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String toString() {
		return this.listType + ": " + entries;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entries == null) ? 0 : entries.hashCode());
		result = prime * result
				+ ((listType == null) ? 0 : listType.hashCode());
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
		FreebaseList other = (FreebaseList) obj;
		if (entries == null) {
			if (other.entries != null)
				return false;
		} else if (!entries.equals(other.entries))
			return false;
		if (listType == null) {
			if (other.listType != null)
				return false;
		} else if (!listType.equals(other.listType))
			return false;
		return true;
	}
	
	

}
