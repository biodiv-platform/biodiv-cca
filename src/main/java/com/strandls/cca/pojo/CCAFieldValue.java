package com.strandls.cca.pojo;

import java.util.List;

public class CCAFieldValue implements IChildable<CCAFieldValue> {

	private String fieldId;
	private String name;
	private List<String> value;

	private List<CCAFieldValue> children;

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

	public List<CCAFieldValue> getChildren() {
		return children;
	}

	public void setChildren(List<CCAFieldValue> children) {
		this.children = children;
	}

}
