package com.strandls.cca.pojo.fields;

import java.util.List;

import com.strandls.cca.pojo.CCAFieldValue;

public class MultiSelectField extends ValueOptionsField {

	@Override
	public boolean validate(CCAFieldValue fieldValue) {
		if (!super.validate(fieldValue))
			return false;

		if (isOptional(fieldValue))
			return true;

		List<String> values = fieldValue.getValue();
		for (String value : values) {
			if (!contains(value))
				return false;
		}
		return true;
	}

}
