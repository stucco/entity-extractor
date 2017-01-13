package gov.ornl.stucco.heurstics.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class FreebaseEntry implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("name")
	private String name;

	@JsonProperty("/common/topic/alias")
	private List<String> aliases;

	@JsonGetter("name")
	public String getName() {
		return name;
	}

	@JsonSetter("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonGetter("/common/topic/alias")
	public List<String> getAliases() {
		return aliases;
	}

	@JsonSetter("/common/topic/alias")
	public void setAliases(List<String> newAliases) {
		this.aliases = newAliases;
	}
	
	public void addAlias(String newAlias) {
		this.aliases.add(newAlias);
	}
	
	public boolean contains(String value) {
		if ((value != null) && (name != null) && (value.equalsIgnoreCase(name))) {
			return true;
		}
		else if (value != null) {
			for (String alias : this.aliases) {
				if (value.equalsIgnoreCase(alias)) {
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public String toString() {
		return "FreebaseEntry [name=" + name + ", aliases=" + aliases + "]\n";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aliases == null) ? 0 : aliases.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		FreebaseEntry other = (FreebaseEntry) obj;
		if (aliases == null) {
			if (other.aliases != null)
				return false;
		} else if (!aliases.equals(other.aliases))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	

}
