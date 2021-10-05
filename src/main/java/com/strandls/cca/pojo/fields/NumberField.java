package com.strandls.cca.pojo.fields;

public class NumberField extends RangableField<Double> {

	@Override
	public Double fetchMinValue() {
		return Double.MIN_VALUE;
	}

	@Override
	public Double fetchMaxValue() {
		return Double.MAX_VALUE;
	}

	@Override
	public Double parseTo(String value) {
		return Double.parseDouble(value);
	}

}
