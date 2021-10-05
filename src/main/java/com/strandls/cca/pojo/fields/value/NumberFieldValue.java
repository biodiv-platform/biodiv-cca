package com.strandls.cca.pojo.fields.value;

import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCAFieldValue;
import com.strandls.cca.pojo.fields.RangableField;
import com.strandls.cca.util.CCAUtil;

public class NumberFieldValue extends CCAFieldValue<Double> {

	private Double value;

	public NumberFieldValue(String dataValue) {
		if (dataValue != null && !"".equals(dataValue))
			this.value = Double.parseDouble(dataValue);
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean validate(CCAField field) {
		if (!super.validate(field))
			return false;

		if (!field.getIsRequired().booleanValue() && value == null)
			return true;

		if (!(field instanceof RangableField<?>))
			return false;

		RangableField<Double> f = (RangableField<Double>) field;

		return CCAUtil.isRanged(value, f.getMin(), f.getMax());
	}

}
