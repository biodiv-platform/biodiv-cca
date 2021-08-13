package com.strandls.cca.pojo;

import java.sql.Timestamp;
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
	private List<String> valueOptions;
	private Timestamp createOn;
	private Timestamp updatedOn;
	private List<CCAField> childrens;

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

	public List<String> getValueOptions() {
		return valueOptions;
	}

	public void setValueOptions(List<String> valueOptions) {
		this.valueOptions = valueOptions;
	}

	public Timestamp getCreateOn() {
		return createOn;
	}

	public void setCreateOn(Timestamp createOn) {
		this.createOn = createOn;
	}

	public Timestamp getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Timestamp updatedOn) {
		this.updatedOn = updatedOn;
	}

	public List<CCAField> getChildrens() {
		return childrens;
	}

	public void setChildrens(List<CCAField> childrens) {
		this.childrens = childrens;
	}

}
