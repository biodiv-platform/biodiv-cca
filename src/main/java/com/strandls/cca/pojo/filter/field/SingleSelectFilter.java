package com.strandls.cca.pojo.filter.field;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;
import com.strandls.cca.pojo.filter.Filter;

public class SingleSelectFilter extends Filter {

	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String getFieldHierarchy() {
		return getCcaFieldValuesString() + "." + getFieldId() + ".value.value";
	}

	@Override
	public Bson getFilter() {
		String fieldHierarchy = getFieldHierarchy();
		return Filters.in(fieldHierarchy, value);
	}

}
