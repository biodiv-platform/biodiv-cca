package com.strandls.cca.pojo.filter.field;

import java.util.List;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;
import com.strandls.cca.pojo.filter.CompareOperator;
import com.strandls.cca.pojo.filter.Filter;

public class MultiSelectFilter extends Filter {

	private List<String> value;
	private CompareOperator op;

	public List<String> getValue() {
		return value;
	}

	public void setValue(List<String> value) {
		this.value = value;
	}

	public CompareOperator getOp() {
		return op;
	}

	public void setOp(CompareOperator op) {
		this.op = op;
	}

	@Override
	public String getFieldHierarchy() {
		return getCcaFieldValuesString() + "." + getFieldId() + ".value.value";
	}

	@Override
	public Bson getFilter() {
		if (op == null)
			throw new IllegalArgumentException("Comparator operation is required");

		String fieldHierarchy = getFieldHierarchy();

		switch (op) {
		case IN:
			return Filters.in(fieldHierarchy, value);
		case ALL:
			return Filters.all(fieldHierarchy, value);
		default:
			throw new UnsupportedOperationException("No comparator registered with name : " + op.name());
		}
	}

}
