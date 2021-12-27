package com.strandls.cca.pojo.fields.value;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCAFieldValue;
import com.strandls.cca.pojo.fields.RangableField;
import com.strandls.cca.util.CCAUtil;

public class DateRangeFieldValue extends CCAFieldValue {

	private List<Date> value;

	public DateRangeFieldValue() {
	}

	public List<Date> getValue() {
		return value;
	}

	public void setValue(List<Date> dates) {
		this.value = dates;
	}

	@Override
	public String computeDiff(CCAFieldValue value) {
		DateRangeFieldValue inputValue = (DateRangeFieldValue) value;
		int dbSize = this.value.size();
		int inSize = inputValue.getValue().size();

		if (dbSize != inSize) {
			if (dbSize > inSize) {
				return "Value deleted : " + this.value.get(0) + " - " + this.value.get(1);
			} else {
				return "Value Added : " + inputValue.getValue().get(0) + " - " + inputValue.getValue().get(1);
			}
		} else if(dbSize == 0) {
			// Do nothing
		} 
		else if (!this.value.get(0).equals(inputValue.getValue().get(0))
				|| !this.value.get(1).equals(inputValue.getValue().get(1))) {
			return "[" + this.value.get(0) + "-" + this.value.get(1) + "] → [" + inputValue.getValue().get(0) + "-"
					+ inputValue.getValue().get(1) + "]";
		}

		return null;
	}

	public DateRangeFieldValue(String dataValue) {
		if (dataValue == null || "".equals(dataValue))
			this.value = new ArrayList<>();
		else {
			String[] values = dataValue.split(CCAUtil.COLUMN_SEPARATOR);
			if (values.length != 2)
				throw new IllegalArgumentException("Range should have 2 values");
			List<Date> minMax = new ArrayList<>();
			minMax.add(CCAUtil.parseDate(values[0].trim()));
			minMax.add(CCAUtil.parseDate(values[1].trim()));
			this.value = minMax;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean validate(CCAField field) {
		super.validate(field);

		if (field.getIsRequired().booleanValue() && getValue() == null)
			throw new IllegalArgumentException("Field is required");

		if (!(field instanceof RangableField<?>))
			return false;

		if ((!field.getIsRequired().booleanValue() && (value == null || value.isEmpty())))
			return true;

		RangableField<Date> f = (RangableField<Date>) field;

		return isRanged(f);
	}

	private boolean isRanged(RangableField<Date> f) {
		if (value.size() != 2)
			return false;
		Date min = value.get(0);
		Date max = value.get(1);
		return min.compareTo(max) < 0 && CCAUtil.isRanged(min, f.fetchMinRange(), f.fetchMaxRange())
				&& CCAUtil.isRanged(max, f.fetchMinRange(), f.fetchMaxRange());
	}

}
