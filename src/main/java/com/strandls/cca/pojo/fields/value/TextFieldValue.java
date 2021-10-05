package com.strandls.cca.pojo.fields.value;

import com.strandls.cca.pojo.CCAFieldValue;

public class TextFieldValue extends CCAFieldValue<String> {

	private String value;

	public TextFieldValue(String dataValue) {
		this.value = dataValue;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
