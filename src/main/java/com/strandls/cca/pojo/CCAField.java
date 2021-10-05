package com.strandls.cca.pojo;

import java.util.Date;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.strandls.cca.FieldConstants;
import com.strandls.cca.pojo.fields.CheckboxField;
import com.strandls.cca.pojo.fields.DateField;
import com.strandls.cca.pojo.fields.DateRangeField;
import com.strandls.cca.pojo.fields.FileField;
import com.strandls.cca.pojo.fields.GeometryField;
import com.strandls.cca.pojo.fields.HeaderField;
import com.strandls.cca.pojo.fields.MultiSelectField;
import com.strandls.cca.pojo.fields.NumberField;
import com.strandls.cca.pojo.fields.NumberRangeField;
import com.strandls.cca.pojo.fields.RadioField;
import com.strandls.cca.pojo.fields.RichtextField;
import com.strandls.cca.pojo.fields.SingleSelectField;
import com.strandls.cca.pojo.fields.TextField;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({ @JsonSubTypes.Type(value = CheckboxField.class, name = FieldConstants.CHECKBOX),
		@JsonSubTypes.Type(value = DateField.class, name = FieldConstants.DATE),
		@JsonSubTypes.Type(value = DateRangeField.class, name = FieldConstants.DATE_RANGE),
		@JsonSubTypes.Type(value = FileField.class, name = FieldConstants.FILE),
		@JsonSubTypes.Type(value = GeometryField.class, name = FieldConstants.GEOMETRY),
		@JsonSubTypes.Type(value = HeaderField.class, name = FieldConstants.HEADING),
		@JsonSubTypes.Type(value = MultiSelectField.class, name = FieldConstants.MULTI_SELECT),
		@JsonSubTypes.Type(value = NumberField.class, name = FieldConstants.NUMBER),
		@JsonSubTypes.Type(value = NumberRangeField.class, name = FieldConstants.NUMBER_RANGE),
		@JsonSubTypes.Type(value = RadioField.class, name = FieldConstants.RADIO),
		@JsonSubTypes.Type(value = RichtextField.class, name = FieldConstants.RICHTEXT),
		@JsonSubTypes.Type(value = SingleSelectField.class, name = FieldConstants.SINGLE_SELECT),
		@JsonSubTypes.Type(value = TextField.class, name = FieldConstants.TEXT) })
@BsonDiscriminator()
public abstract class CCAField implements IChildable<CCAField>, IFieldValidator {

	private String fieldId;

	private String name;
	private Boolean isRequired;
	private String question;
	private String helpText;
	private Boolean isMasterField;
	private FieldType type;
	private Date createOn;
	private Date updatedOn;
	private List<CCAField> children;

	/*
	 * Not required and empty. Validation completed with respect to value.
	 */
	public boolean isOptional(CCAFieldValue fieldValue) {
		return !getIsRequired() && (fieldValue.getValue() == null || fieldValue.getValue().isEmpty());
	}

	@Override
	public void validate() {
		// Nothing to do here
	}

	/**
	 * Always validating to true. If some validation are required. Then implement it
	 * in derived classes
	 */
	@Override
	public boolean validate(CCAFieldValue fieldValue) {
		String fieldValueId = fieldValue.getFieldId();
		if (fieldValueId == null)
			throw new IllegalArgumentException("FieldId can't be null");

		if (!fieldValueId.equals(getFieldId()))
			throw new IllegalArgumentException("Invalid template mapping");

		List<String> value = fieldValue.getValue();
		if (getIsRequired().booleanValue() && (value == null || value.isEmpty()))
			throw new IllegalArgumentException("Field is required");

		return true;
	}

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

	public String getHelpText() {
		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

	public Boolean getIsMasterField() {
		return isMasterField;
	}

	public void setIsMasterField(Boolean isMasterField) {
		this.isMasterField = isMasterField;
	}

	public FieldType getType() {
		return type;
	}

	public void setType(FieldType type) {
		this.type = type;
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
