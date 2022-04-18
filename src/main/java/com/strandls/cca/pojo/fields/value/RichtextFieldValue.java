package com.strandls.cca.pojo.fields.value;

import com.strandls.cca.pojo.CCAFieldValue;

public class RichtextFieldValue extends CCAFieldValue {

	private String value;

	public RichtextFieldValue() {
	}

	public RichtextFieldValue(String dataValue) {
		this.value = dataValue;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String computeDiff(CCAFieldValue value) {
		RichtextFieldValue inputValue = (RichtextFieldValue) value;
		if (!this.value.equals(inputValue.getValue())) {
			return "Updated";
		}
		return null;
	}

	@Override
	public boolean validateValue(CCAFieldValue value) {
		RichtextFieldValue inputValue = (RichtextFieldValue) value;
		if(inputValue.getValue() != null && inputValue.getValue().equals("") && inputValue.getValue().equals("<p></p>")) {
			return true;
		}
		return false;
	}
}
