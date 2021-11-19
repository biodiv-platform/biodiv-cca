package com.strandls.cca.pojo;

public class ValueWithLabel {

	private static final String OTHER_VALUE = "others|?";
	private String label;
	private String value;

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

	public boolean belongs(ValueWithLabel valueWithLabel) {
		if (OTHER_VALUE.equals(valueWithLabel.getValue().toLowerCase().replaceAll("\\s", ""))
				&& OTHER_VALUE.equals(getValue().toLowerCase().replaceAll("\\s", "")))
			return true;
		return label.toLowerCase().trim().equals(valueWithLabel.getLabel().toLowerCase().trim());
	}

}
