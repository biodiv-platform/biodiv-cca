package com.strandls.cca.pojo;

import java.util.List;

public class CCAFieldValues {

	private String fieldId;
	private String name;
	private List<String> value;

	private List<CCAFieldValues> ccaFieldValues;

	public String getFieldId() {
		return fieldId;
	}

	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getValue() {
		return value;
	}

	public void setValue(List<String> value) {
		this.value = value;
	}

	public List<CCAFieldValues> getCcaFieldValues() {
		return ccaFieldValues;
	}

	public void setCcaFieldValues(List<CCAFieldValues> ccaFieldValues) {
		this.ccaFieldValues = ccaFieldValues;
	}

}
