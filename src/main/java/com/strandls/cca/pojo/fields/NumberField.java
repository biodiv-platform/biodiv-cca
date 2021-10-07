package com.strandls.cca.pojo.fields;

public class NumberField extends RangableField<Double> {

	@Override
	public Double fetchMinRange() {
		if (getMin() == null)
			return Double.MIN_VALUE;
		return getMin();
	}

	@Override
	public Double fetchMaxRange() {
		if (getMax() == null)
			return Double.MAX_VALUE;
		return getMax();
	}
}
