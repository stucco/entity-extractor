package gov.ornl.stucco.entity.models;

import edu.stanford.nlp.ie.machinereading.structure.EntityMention;
import edu.stanford.nlp.ie.machinereading.structure.Span;
import edu.stanford.nlp.util.CoreMap;

public class CyberEntityMention extends EntityMention {

	private static final long serialVersionUID = 1L;

	public CyberEntityMention(String objectId, CoreMap sentence, Span extentSpan, Span headSpan, String type, String subtype, String mentionType) {
		super(objectId, sentence, extentSpan, headSpan, type, subtype, mentionType);
//		normalizeType();
	}
	
	private void normalizeType() {
		if (this.type.equalsIgnoreCase("sw")) {
			this.type = "software";
		}
		else if (this.type.equalsIgnoreCase("vuln")) {
			this.type = "vulnerability";
		}
	}

}
