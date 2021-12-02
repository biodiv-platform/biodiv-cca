package com.strandls.cca.pojo.fields.value;

import java.util.Arrays;
import java.util.List;

import com.strandls.cca.pojo.CCAFieldValue;

public class FileFieldValue extends CCAFieldValue {

	private List<String> value;

	public FileFieldValue() {
	}

	public FileFieldValue(String dataValue) {
		List<String> files = Arrays.asList(dataValue.split(","));
		this.value = files;
	}

	public List<String> getValue() {
		return value;
	}

	public void setValue(List<String> value) {
		this.value = value;
	}

}
