package com.strandls.cca.pojo.fields.value;

import com.strandls.cca.pojo.CCAFieldValue;

public class TextFieldValue extends CCAFieldValue {

	private String value;

	public TextFieldValue() {
	}

	public TextFieldValue(String dataValue) {
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
		TextFieldValue inputValue = (TextFieldValue) value;
		if (!this.value.equals(inputValue.getValue())) {
			return "Text updated";
		}
		return null;
	}
}
