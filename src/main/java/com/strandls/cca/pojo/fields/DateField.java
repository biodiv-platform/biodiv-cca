package com.strandls.cca.pojo.fields;

import java.time.Instant;
import java.util.Date;

import com.strandls.cca.util.CCAUtil;

public class DateField extends RangableField<Date> {

	@Override
	public Date fetchMinValue() {
		return Date.from(Instant.EPOCH);
	}

	@Override
	public Date fetchMaxValue() {
		return new Date(Long.MAX_VALUE);
	}

	@Override
	public Date parseTo(String value) {
		return CCAUtil.parseDate(value);
	}

}
