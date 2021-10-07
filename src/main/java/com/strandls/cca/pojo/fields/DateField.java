package com.strandls.cca.pojo.fields;

import java.time.Instant;
import java.util.Date;

public class DateField extends RangableField<Date> {

	@Override
	public Date fetchMinRange() {
		if (getMin() == null)
			return Date.from(Instant.EPOCH);
		return getMin();
	}

	@Override
	public Date fetchMaxRange() {
		if (getMax() == null)
			return new Date(Long.MAX_VALUE - 1);
		return getMax();
	}

}
