package gov.ornl.stucco.heurstics.utils;

import gov.ornl.stucco.entity.heuristics.CyberHeuristicAnnotator;
import gov.ornl.stucco.entity.models.CyberEntityType;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class will store a map of tokens (i.e. words)
 * to a unique cyber label, if during training, every instance
 * of the token had only one ground-truth label.
 * 
 * @author k5y
 *
 */
public class TokenCyberLabelMap implements Serializable {

	private static final long serialVersionUID = 4L;
	
	private Map<String, CyberEntityType> labelMap;
	
	public TokenCyberLabelMap() {
		labelMap = new HashMap<String, CyberEntityType>();
	}
	
	public Map<String, CyberEntityType> getLabelMap() {
		return labelMap;
	}

	public void setLabelMap(Map<String, CyberEntityType> labelMap) {
		this.labelMap = labelMap;
	}
	
	public void addLabel(String token, CyberEntityType label) {
		labelMap.put(token, label);
	}
	
	public boolean contains(String token) {
		return labelMap.containsKey(token);
	}
	
	public CyberEntityType getLabel(String token) {
		if (contains(token)) {
			return ((CyberEntityType)labelMap.get(token));
		}
		return null;
	}
	
	public void checkLabels(String token, CyberEntityType newLabel) {
		CyberEntityType currentLabel = getLabel(token);
		if (currentLabel == null) {
			addLabel(token, newLabel);
		}
		else if ((currentLabel != null) && (!currentLabel.equals(newLabel))) {
			this.labelMap.remove(token);
		}
	}
	
	public void cleanMap() {
		Map<String, CyberEntityType> tempMap = new HashMap<String, CyberEntityType>();
		tempMap.putAll(labelMap);
		for (String token : tempMap.keySet()) {
			if ((tempMap.get(token)).equals(CyberHeuristicAnnotator.O)) {
				this.labelMap.remove(token);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void loadMap(String mapFile) {
		try {
			FileInputStream inStream = new FileInputStream(mapFile);
			ObjectInputStream objStream = new ObjectInputStream(inStream);
			labelMap = ((HashMap<String, CyberEntityType>) objStream.readObject());
			objStream.close();
			inStream.close();
			System.err.println("Token-to-Label map loaded from '" + mapFile + "'");
		} catch (IOException ex) {
			System.err.println("WARNING: Token-to-Label map could not be loaded from '" + mapFile + "'");
			ex.printStackTrace();
		} catch (ClassNotFoundException clex) {
			System.err.println("WARNING: Token-to-Label map could not be loaded from '" + mapFile + "'");
			clex.printStackTrace();
		}
	}
	
	public void saveMap(String mapFile) {
		try {
			FileOutputStream outStream = new FileOutputStream(mapFile);
			ObjectOutputStream objStream = new ObjectOutputStream(outStream);
			objStream.writeObject(labelMap);
			objStream.close();
			outStream.close();
			System.err.println("Token-to-Label map saved as '" + mapFile + "'");
		} catch (IOException ex) {
			System.err.println("WARNING: Token-to-Label map could not be saved as '" + mapFile + "'");
			ex.printStackTrace();
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String token : this.labelMap.keySet()) {
			sb.append(token);
			sb.append("-->");
			sb.append(this.labelMap.get(token));
			sb.append("\n");
		}
		return sb.toString();
	}
}
