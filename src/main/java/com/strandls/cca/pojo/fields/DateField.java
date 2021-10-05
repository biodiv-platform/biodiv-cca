package com.strandls.cca.pojo.fields;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import com.strandls.cca.pojo.CCAFieldValue;

public class DateField extends RangableField<Date> {

	private static final String[] ALLOWED_DATE_FORMAT = { "YYYY", "MM/YYYY", "MM-YYYY", "DD/MM/YYYY", "DD-MM-YYYY",
			"DD/MM/YYYY HH:mm:ss:ms", "dd-MM-yyyy HH:mm:ss:ms" };

	@Override
	public void setMin(Date min) {
		if (min == null)
			super.setMin(Date.from(Instant.EPOCH));
	}

	@Override
	public void setMax(Date max) {
		if (max == null)
			super.setMax(new Date(Long.MAX_VALUE));
	}

	@Override
	public boolean validate(CCAFieldValue fieldValue) {
		if (!super.validate(fieldValue))
			return false;

		List<String> values = fieldValue.getValue();

		return isOptional(fieldValue) || (values.size() == 1 && isRanged(values));
	}

	@Override
	public Date parseTo(String value) {
		for (String allowedFormat : ALLOWED_DATE_FORMAT) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(allowedFormat);
			dateFormat.setLenient(false);
			try {
				return dateFormat.parse(value.replaceAll("\\s*", ""));
			} catch (ParseException pe) {
			}
		}
		return null;
	}

}
