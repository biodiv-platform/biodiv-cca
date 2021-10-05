package com.strandls.cca.pojo;

public class ValueWithLabel {

	private String label;
	private String value;
	// private JacksonDBObject<Object> value;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean belongs(String value) {
		return label.toLowerCase().trim().equals(value.toLowerCase().trim());
	}

}
