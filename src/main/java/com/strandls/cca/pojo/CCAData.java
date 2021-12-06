package com.strandls.cca.pojo;

import java.util.Map;

public class CCAData extends BaseEntity {

	private String shortName;

	private Map<String, CCAFieldValue> ccaFieldValues;

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Map<String, CCAFieldValue> getCcaFieldValues() {
		return ccaFieldValues;
	}

	public void setCcaFieldValues(Map<String, CCAFieldValue> ccaFieldValues) {
		this.ccaFieldValues = ccaFieldValues;
	}

	public CCAData overrideFieldData(CCAData ccaData) {
		for (Map.Entry<String, CCAFieldValue> e : ccaData.getCcaFieldValues().entrySet()) {
			this.ccaFieldValues.put(e.getKey(), e.getValue());
		}
		return this;
	}

}
