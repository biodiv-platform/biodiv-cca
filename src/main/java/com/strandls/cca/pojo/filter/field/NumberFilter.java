package com.strandls.cca.pojo.filter.field;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;
import com.strandls.cca.pojo.filter.Filter;
import com.strandls.cca.pojo.filter.CompareOperator;

public class NumberFilter extends Filter {

	private Double value;
	private CompareOperator op;

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public CompareOperator getOp() {
		return op;
	}

	public void setOp(CompareOperator op) {
		this.op = op;
	}
	
	@Override
	public Bson getFilter() {

		if (op == null)
			throw new IllegalArgumentException("Comparator operation is required");

		String fieldHierarchy = getFieldHierarchy();

		switch (op) {
		case EQ:
			return Filters.eq(fieldHierarchy, value);
		case GT:
			return Filters.gt(fieldHierarchy, value);
		case LT:
			return Filters.lt(fieldHierarchy, value);
		case GTE:
			return Filters.gte(fieldHierarchy, value);
		case LTE:
			return Filters.lte(fieldHierarchy, value);
		default:
			break;
		}

		throw new UnsupportedOperationException("No comparator registered with name : " + op.name());
	}

}
