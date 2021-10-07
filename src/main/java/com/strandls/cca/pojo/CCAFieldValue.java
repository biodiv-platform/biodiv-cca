package com.strandls.cca.pojo;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.strandls.cca.FieldConstants;
import com.strandls.cca.pojo.fields.value.CheckboxFieldValue;
import com.strandls.cca.pojo.fields.value.DateFieldValue;
import com.strandls.cca.pojo.fields.value.DateRangeFieldValue;
import com.strandls.cca.pojo.fields.value.FileFieldValue;
import com.strandls.cca.pojo.fields.value.GeometryFieldValue;
import com.strandls.cca.pojo.fields.value.HeaderFieldValue;
import com.strandls.cca.pojo.fields.value.MultiSelectFieldValue;
import com.strandls.cca.pojo.fields.value.NumberFieldValue;
import com.strandls.cca.pojo.fields.value.NumberRangeFieldValue;
import com.strandls.cca.pojo.fields.value.RadioFieldValue;
import com.strandls.cca.pojo.fields.value.RichtextFieldValue;
import com.strandls.cca.pojo.fields.value.SingleSelectFieldValue;
import com.strandls.cca.pojo.fields.value.TextFieldValue;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({ @JsonSubTypes.Type(value = CheckboxFieldValue.class, name = FieldConstants.CHECKBOX),
		@JsonSubTypes.Type(value = DateFieldValue.class, name = FieldConstants.DATE),
		@JsonSubTypes.Type(value = DateRangeFieldValue.class, name = FieldConstants.DATE_RANGE),
		@JsonSubTypes.Type(value = FileFieldValue.class, name = FieldConstants.FILE),
		@JsonSubTypes.Type(value = GeometryFieldValue.class, name = FieldConstants.GEOMETRY),
		@JsonSubTypes.Type(value = HeaderFieldValue.class, name = FieldConstants.HEADING),
		@JsonSubTypes.Type(value = MultiSelectFieldValue.class, name = FieldConstants.MULTI_SELECT),
		@JsonSubTypes.Type(value = NumberFieldValue.class, name = FieldConstants.NUMBER),
		@JsonSubTypes.Type(value = NumberRangeFieldValue.class, name = FieldConstants.NUMBER_RANGE),
		@JsonSubTypes.Type(value = RadioFieldValue.class, name = FieldConstants.RADIO),
		@JsonSubTypes.Type(value = RichtextFieldValue.class, name = FieldConstants.RICHTEXT),
		@JsonSubTypes.Type(value = SingleSelectFieldValue.class, name = FieldConstants.SINGLE_SELECT),
		@JsonSubTypes.Type(value = TextFieldValue.class, name = FieldConstants.TEXT) })
@BsonDiscriminator()
public abstract class CCAFieldValue {

	private String fieldId;
	private String name;
	private FieldType type;

	/**
	 * Always validating to true. If some validation are required. Then implement it
	 * in derived classes
	 */
	public boolean validate(CCAField field) {
		String fieldValueId = getFieldId();
		if (fieldValueId == null)
			throw new IllegalArgumentException("FieldId can't be null");

		if (!fieldValueId.equals(field.getFieldId()))
			throw new IllegalArgumentException("Invalid template mapping");

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

	public FieldType getType() {
		return type;
	}

	public void setType(FieldType type) {
		this.type = type;
	}
}
