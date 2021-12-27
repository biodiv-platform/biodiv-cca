package com.strandls.cca.pojo.fields.value;

import com.strandls.cca.pojo.CCAFieldValue;

public class HeaderFieldValue extends CCAFieldValue {

	public HeaderFieldValue() {
	}

	public HeaderFieldValue(String dataValue) {
	}

	public Object getValue() {
		return null;
	}

	@Override
	public String computeDiff(CCAFieldValue value) {
		return null;
	}

	public void setValue(Object t) {
		throw new UnsupportedOperationException("Setting header value is not allowed");
	}

}
