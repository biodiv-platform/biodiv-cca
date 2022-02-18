package com.strandls.cca.pojo.fields.value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCAFieldValue;
import com.strandls.cca.pojo.ValueWithLabel;
import com.strandls.cca.pojo.fields.MultiSelectField;
import com.strandls.cca.pojo.fields.ValueOptionsField;
import com.strandls.cca.util.CCAUtil;

public class MultiSelectFieldValue extends CCAFieldValue {

	private List<ValueWithLabel> value = new ArrayList<>();

	public MultiSelectFieldValue() {
	}

	public List<ValueWithLabel> getValue() {
		return value;
	}

	public void setValue(List<ValueWithLabel> value) {
		this.value = value;
	}

	@Override
	public void translate(CCAField translatedField) {
		super.translate(translatedField);

		if (this.value == null || this.value.isEmpty())
			return;

		MultiSelectField field = (MultiSelectField) translatedField;

		Map<String, String> valueToLabel = new HashMap<>();
		for (ValueWithLabel valueWithLabel : field.getValueOptions()) {
			valueToLabel.put(valueWithLabel.getValue(), valueWithLabel.getLabel());
		}
		for (ValueWithLabel valueWithLabel : getValue()) {
			String v = valueWithLabel.getValue();
			if (valueToLabel.containsKey(v)) {
				String label = v.contains("?") ? valueWithLabel.getLabel() : valueToLabel.get(valueWithLabel.getValue());
				valueWithLabel.setLabel(label);
			}
		}
	}

	@Override
	public String computeDiff(CCAFieldValue value) {
		MultiSelectFieldValue inputValue = (MultiSelectFieldValue) value;

		int inSize = inputValue.getValue().size();
		int dbSize = this.getValue().size();

		String diff = "";
		if (inSize != dbSize) {
			diff += "Before : ";
			diff += this.getValue();
			diff += "\nAfter : ";
			diff += inputValue.getValue();
		} else if (dbSize == 0) {
			// Do nothing
		} else {
			if (!this.value.equals(inputValue.getValue())) {
				diff += "Before : ";
				diff += this.getValue();
				diff += "\nAfter : ";
				diff += inputValue.getValue();
			}
		}

		return "".equals(diff) ? null : diff;
	}

	public MultiSelectFieldValue(String dataValue) {
		List<ValueWithLabel> output = new ArrayList<>();
		if (dataValue == null || "".equals(dataValue)) {
			this.value = output;
		} else {
			String[] values = dataValue.split(CCAUtil.COLUMN_SEPARATOR);
			for (String v : values) {
				ValueWithLabel valueWithLabel = new ValueWithLabel();
				valueWithLabel.setLabel(v.trim());
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
			if (!((ValueOptionsField) field).contains(v))
				return false;
		}

		return true;
	}

}
