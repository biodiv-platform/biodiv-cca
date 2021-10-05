package com.strandls.cca.pojo.fields.value;

import com.strandls.cca.pojo.CCAFieldValue;

public class HeaderFieldValue extends CCAFieldValue<Object> {

	public HeaderFieldValue(String dataValue) {
	}

	public Object getValue() {
		return null;
	}

	public void setValue(Object t) {
		throw new UnsupportedOperationException("Setting header value is not allowed");
	}

}
