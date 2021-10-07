package com.strandls.cca.pojo.fields.value;

import java.util.ArrayList;
import java.util.List;

import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCAFieldValue;
import com.strandls.cca.pojo.ValueWithLabel;
import com.strandls.cca.pojo.fields.ValueOptionsField;
import com.strandls.cca.util.CCAUtil;

public class MultiSelectFieldValue extends CCAFieldValue {

	private List<ValueWithLabel> value;

	public MultiSelectFieldValue() {
	}

	public List<ValueWithLabel> getValue() {
		return value;
	}

	public void setValue(List<ValueWithLabel> value) {
		this.value = value;
	}

	public MultiSelectFieldValue(String dataValue) {
		List<ValueWithLabel> output = new ArrayList<>();
		if (dataValue == null || "".equals(dataValue)) {
			this.value = output;
		} else {
			String[] values = dataValue.split(CCAUtil.COLUMN_SEPARATOR);
			for (String v : values) {
				ValueWithLabel valueWithLabel = new ValueWithLabel();
				valueWithLabel.setLabel(v);
				output.add(valueWithLabel);
			}
			this.value = output;
		}

	}

	@Override
	public boolean validate(CCAField field) {
		super.validate(field);

		if (field.getIsRequired().booleanValue() && getValue() == null)
			throw new IllegalArgumentException("Field is required");

		if (!field.getIsRequired().booleanValue() && (value == null || value.isEmpty()))
			return true;

		if (!(field instanceof ValueOptionsField))
			return false;

		for (ValueWithLabel v : value) {
			if (!((ValueOptionsField) field).contains(v.getLabel()))
				return false;
		}

		return true;
	}

}
