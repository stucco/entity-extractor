package gov.ornl.stucco.entity.models;

public class CyberEntityType {
	
	private String cyberType;
	private String cyberSubType;
	
	public CyberEntityType() {
		this("O", "O");
	}
	
	public CyberEntityType(String type, String subType) {
		this.cyberType = type;
		this.cyberSubType = subType;
	}

	public String getCyberType() {
		return cyberType;
	}

	public void setCyberType(String cyberType) {
		this.cyberType = cyberType;
	}

	public String getCyberSubType() {
		return cyberSubType;
	}

	public void setCyberSubType(String cyberSubType) {
		this.cyberSubType = cyberSubType;
	}

	@Override
	public String toString() {
		if (cyberType.equalsIgnoreCase("O")) {
			return cyberType;
		}
		return cyberType + "." + cyberSubType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cyberSubType == null) ? 0 : cyberSubType.hashCode());
		result = prime * result
				+ ((cyberType == null) ? 0 : cyberType.hashCode());
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
		CyberEntityType other = (CyberEntityType) obj;
		if (cyberSubType == null) {
			if (other.cyberSubType != null)
				return false;
		} else if (!cyberSubType.equals(other.cyberSubType))
			return false;
		if (cyberType == null) {
			if (other.cyberType != null)
				return false;
		} else if (!cyberType.equals(other.cyberType))
			return false;
		return true;
	}

}
