package com.strandls.cca.pojo.fields;

import java.util.Date;

public class DateField extends RangableField<Date> {

	@Override
	public Date fetchMinRange() {
		if (!isMinMaxSet() ||  getMinMax().get(0) == null)
			return new Date(Long.MIN_VALUE);
		return getMinMax().get(0);
	}

	@Override
	public Date fetchMaxRange() {
		if (!isMinMaxSet() || getMinMax().size() < 2 || getMinMax().get(1) == null)
			return new Date(Long.MAX_VALUE - 1);
		return getMinMax().get(1);
	}

}
