package com.strandls.cca.pojo.filter;

import org.bson.conversions.Bson;

import com.strandls.cca.pojo.FieldType;

public abstract class Filter implements IFilter {

	private String fieldId;
	private FieldType type;

	@Override
	public Bson getFilter() {
		throw new UnsupportedOperationException("Don't have filter for the " + type.name() + " field");
	}

	public String getFieldHierarchy() {
		return getCcaFieldValuesString() + "." + getFieldId() + ".value";
	}

	public String getFieldId() {
		return fieldId;
	}

	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

	public FieldType getType() {
		return type;
	}

	public void setType(FieldType type) {
		this.type = type;
	}

	protected static String getCcaFieldValuesString() {
		return CCA_FIELD_VALUES;
	}

}
