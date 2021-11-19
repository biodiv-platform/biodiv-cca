package com.strandls.cca.pojo.fields.value;

import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCAFieldValue;
import com.strandls.cca.pojo.ValueWithLabel;
import com.strandls.cca.pojo.fields.ValueOptionsField;
import com.strandls.cca.util.CCAUtil;

public class SingleSelectFieldValue extends CCAFieldValue {

	private ValueWithLabel value;

	public SingleSelectFieldValue() {
	}

	public SingleSelectFieldValue(String dataValue) {
		if (dataValue != null && !"".equals(dataValue)) {
			String[] values = dataValue.split(CCAUtil.COLUMN_SEPARATOR);
			if (values.length != 1)
				throw new IllegalArgumentException("Single selection is requied");

			ValueWithLabel valueWithLabel = new ValueWithLabel();
			valueWithLabel.setLabel(values[0].trim());
			this.value = valueWithLabel;
		}
	}

	@Override
	public boolean validate(CCAField field) {
		super.validate(field);

		if (field.getIsRequired().booleanValue() && getValue() == null)
			throw new IllegalArgumentException("Field is required");

		if (!field.getIsRequired().booleanValue() && (value == null || "".equals(value.getLabel())))
			return true;

		if (!(field instanceof ValueOptionsField))
			return false;

		return ((ValueOptionsField) field).contains(value);
	}

	public ValueWithLabel getValue() {
		return value;
	}

	public void setValue(ValueWithLabel value) {
		this.value = value;
	}

}
