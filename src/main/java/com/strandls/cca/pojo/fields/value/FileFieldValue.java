package com.strandls.cca.pojo.fields.value;

import com.strandls.cca.pojo.CCAFieldValue;

public class FileFieldValue extends CCAFieldValue {

	private String value;

	public FileFieldValue(String dataValue) {
		this.value = dataValue;
	}
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
