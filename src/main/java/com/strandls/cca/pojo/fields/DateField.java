package com.strandls.cca.pojo.fields;

import java.time.Instant;
import java.util.Date;

public class DateField extends RangableField<Date> {

	@Override
	public Date fetchMinRange() {
		if (!isMinMaxSet())
			return Date.from(Instant.EPOCH);
		return getMinMax().get(0);
	}

	@Override
	public Date fetchMaxRange() {
		if (!isMinMaxSet() || getMinMax().size() < 2)
			return new Date(Long.MAX_VALUE - 1);
		return getMinMax().get(1);
	}

}
