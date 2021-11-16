package com.strandls.cca.pojo.fields;

public class NumberField extends RangableField<Double> {

	@Override
	public Double fetchMinRange() {
		if (!isMinMaxSet())
			return Double.MIN_VALUE;
		return getMinMax().get(0);
	}

	@Override
	public Double fetchMaxRange() {
		if (!isMinMaxSet() || getMinMax().size() < 2)
			return Double.MAX_VALUE;
		return getMinMax().get(1);
	}
}
