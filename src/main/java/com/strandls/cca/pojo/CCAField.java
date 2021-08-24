package com.strandls.cca.pojo;

import java.util.Date;
import java.util.List;

//@MongoCollection(name = "cca_field")
public class CCAField {

	// @Id
	// @ObjectId
	private String fieldId;

	private String name;
	private CCAFieldDataValidation validation;
	private String question;
	private String type;
	private List<ValueWithLabel> valueOptions;
	private Date createOn;
	private Date updatedOn;
	private List<CCAField> children;

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

	public CCAFieldDataValidation getValidation() {
		return validation;
	}

	public void setValidation(CCAFieldDataValidation validation) {
		this.validation = validation;
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

	public List<ValueWithLabel> getValueOptions() {
		return valueOptions;
	}

	public void setValueOptions(List<ValueWithLabel> valueOptions) {
		this.valueOptions = valueOptions;
	}

	public Date getCreateOn() {
		return createOn;
	}

	public void setCreateOn(Date createOn) {
		this.createOn = createOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public List<CCAField> getChildren() {
		return children;
	}

	public void setChildren(List<CCAField> children) {
		this.children = children;
	}

}
