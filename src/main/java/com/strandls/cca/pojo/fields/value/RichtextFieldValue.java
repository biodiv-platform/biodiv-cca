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

}
