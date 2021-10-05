package com.strandls.cca.pojo.fields;

import java.util.List;

import com.strandls.cca.pojo.CCAFieldValue;

public class NumberField extends RangableField<Double> {

	@Override
	public boolean validate(CCAFieldValue fieldValue) {
		if (!super.validate(fieldValue))
			return false;

		if (isOptional(fieldValue))
			return true;
		
		List<String> values = fieldValue.getValue();
		return values.size() == 1 && isRanged(values);
	}

	@Override
	public Double parseTo(String value) {
		return Double.parseDouble(value);
	}

}
