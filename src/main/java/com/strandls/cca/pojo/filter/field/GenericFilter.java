package com.strandls.cca.pojo.filter.field;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;
import com.strandls.cca.pojo.filter.IFilter;

public class GenericFilter implements IFilter {

	private String type;
	private String fieldName;
	private String value;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public Bson getFilter() {
		return Filters.eq(fieldName, value);
	}

}
