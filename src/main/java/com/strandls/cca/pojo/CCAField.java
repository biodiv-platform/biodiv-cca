package com.strandls.cca.pojo;

import java.util.List;

public class CCAField {

	private String name;
	private Integer displayOrder;
	private Boolean isRequired;
	private String question;
	private String type;
	private List<String> values;
	private List<CCAField> childrens;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public Boolean getIsRequired() {
		return isRequired;
	}

	public void setIsRequired(Boolean isRequired) {
		this.isRequired = isRequired;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public List<CCAField> getChildrens() {
		return childrens;
	}

	public void setChildrens(List<CCAField> childrens) {
		this.childrens = childrens;
	}
}
