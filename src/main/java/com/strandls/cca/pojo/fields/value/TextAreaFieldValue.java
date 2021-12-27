package com.strandls.cca.pojo.fields.value;

import com.strandls.cca.pojo.CCAFieldValue;

public class TextAreaFieldValue extends CCAFieldValue {

	private String value;

	public TextAreaFieldValue() {
	}

	public TextAreaFieldValue(String dataValue) {
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
		TextAreaFieldValue inputValue = (TextAreaFieldValue) value;
		if (!this.value.equals(inputValue.getValue())) {
			return "Updated";
		}
		return null;
	}
}
