package com.strandls.cca.pojo.fields.value;

import com.strandls.cca.CCAConstants;
import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCAFieldValue;
import com.strandls.cca.pojo.fields.RangableField;
import com.strandls.cca.util.CCAUtil;

public class NumberFieldValue extends CCAFieldValue {

	private Double value;

	public NumberFieldValue() {
	}

	public NumberFieldValue(String dataValue) {
		if (dataValue != null && !"".equals(dataValue))
			this.value = Double.parseDouble(dataValue.trim());
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	@Override
	public String computeDiff(CCAFieldValue value) {
		NumberFieldValue inputFieldValue = (NumberFieldValue) value;
		Double inputValue = inputFieldValue.getValue();
		String diff = "";
		if (this.value == null) {
			if (inputValue != null)
				diff += CCAConstants.BEFORE + CCAConstants.AFTER + this.value;
		} else {
			if (inputValue == null) {
				diff += CCAConstants.BEFORE + this.value + CCAConstants.AFTER;
			} else if (!this.value.equals(inputValue)) {
				diff += CCAConstants.BEFORE + this.value + CCAConstants.AFTER + inputValue;
			}
		}

		return "".equals(diff) ? null : diff;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean validate(CCAField field) {
		super.validate(field);

		if (field.getIsRequired().booleanValue() && getValue() == null)
			throw new IllegalArgumentException("Field is required");

		if (!field.getIsRequired().booleanValue() && value == null)
			return true;

		if (!(field instanceof RangableField<?>))
			return false;

		RangableField<Double> f = (RangableField<Double>) field;

		return CCAUtil.isRanged(value, f.fetchMinRange(), f.fetchMaxRange());
	}

}
