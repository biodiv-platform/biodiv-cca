package com.strandls.cca.pojo.fields;

import java.util.List;

import com.strandls.cca.pojo.CCAFieldValue;

public class DateRangeField extends DateField {

	@Override
	public boolean validate(CCAFieldValue fieldValue) {
		if (!super.validate(fieldValue))
			return false;

		List<String> values = fieldValue.getValue();

		return isOptional(fieldValue) || (values.size() == 2 && isRanged(values));
	}
}
