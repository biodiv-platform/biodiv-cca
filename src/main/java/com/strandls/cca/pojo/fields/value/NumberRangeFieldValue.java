package com.strandls.cca.pojo.fields.value;

import java.util.ArrayList;
import java.util.List;

import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCAFieldValue;
import com.strandls.cca.pojo.fields.RangableField;
import com.strandls.cca.util.CCAUtil;

public class NumberRangeFieldValue extends CCAFieldValue {

	private List<Double> value;

	public NumberRangeFieldValue() {
	}

	public NumberRangeFieldValue(String dataValue) {
		if (dataValue == null || "".equals(dataValue))
			this.value = new ArrayList<>();
		else {
			String[] values = dataValue.split(CCAUtil.COLUMN_SEPARATOR);
			if (values.length != 2)
				throw new IllegalArgumentException("Range should have 2 values");
			List<Double> minMax = new ArrayList<>();
			minMax.add(Double.parseDouble(values[0]));
			minMax.add(Double.parseDouble(values[1]));
			this.value = minMax;
		}

	}

	public List<Double> getValue() {
		return value;
	}

	public void setValue(List<Double> value) {
		this.value = value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean validate(CCAField field) {
		super.validate(field);

		if (field.getIsRequired().booleanValue() && getValue() == null)
			throw new IllegalArgumentException("Field is required");

		if (!(field instanceof RangableField<?>))
			return false;

		if (!field.getIsRequired().booleanValue() && (value == null || value.isEmpty()))
			return true;

		RangableField<Double> f = (RangableField<Double>) field;

		return isRanged(f);
	}

	private boolean isRanged(RangableField<Double> f) {
		if (value.size() != 2)
			return false;
		Double min = value.get(0);
		Double max = value.get(1);
		return min < max && CCAUtil.isRanged(min, f.getMin(), f.getMax())
				&& CCAUtil.isRanged(max, f.getMin(), f.getMax());
	}

}
