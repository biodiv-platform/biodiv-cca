package com.strandls.cca.pojo.fields.value;

import java.util.Date;

import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCAFieldValue;
import com.strandls.cca.pojo.fields.RangableField;
import com.strandls.cca.util.CCAUtil;

public class DateFieldValue extends CCAFieldValue {

	private Date value;

	public DateFieldValue() {
	}

	public DateFieldValue(String dataValue) {
		if (dataValue != null && !"".equals(dataValue))
			this.value = CCAUtil.parseDate(dataValue.trim());
	}

	public Date getValue() {
		return value;
	}

	public void setValue(Date date) {
		this.value = date;
	}

	@Override
	public String computeDiff(CCAFieldValue value) {
		DateFieldValue inputFieldValue = (DateFieldValue) value;
		Date inputValue = inputFieldValue.getValue();

		String diff = null;
		if (this.value == null) {
			if (inputValue != null)
				diff = "" + "→" + inputValue.toString();
		} else {
			if (inputValue == null) {
				diff = this.value.toString() + "→" + "";
			} else if (!this.value.equals(inputValue)) {
				diff = this.value.toString() + "→" + inputValue.toString();
			}
		}

		return diff;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean validate(CCAField field) {
		super.validate(field);

		if (field.getIsRequired().booleanValue() && getValue() == null)
			throw new IllegalArgumentException("Field is required");

		if (!(field instanceof RangableField<?>))
			return false;

		if ((!field.getIsRequired().booleanValue() && value == null))
			return true;

		RangableField<Date> f = (RangableField<Date>) field;

		return CCAUtil.isRanged(value, f.fetchMinRange(), f.fetchMaxRange());
	}

}
