package com.strandls.cca.pojo;

import net.vz.mongodb.jackson.internal.stream.JacksonDBObject;

public class CCAFieldDataValidation {

	private Boolean isRequired;
	private JacksonDBObject<Object> min;
	private JacksonDBObject<Object> max;

	public Boolean getIsRequired() {
		return isRequired;
	}

	public void setIsRequired(Boolean isRequired) {
		this.isRequired = isRequired;
	}

	public JacksonDBObject<Object> getMin() {
		return min;
	}

	public void setMin(JacksonDBObject<Object> min) {
		this.min = min;
	}

	public JacksonDBObject<Object> getMax() {
		return max;
	}

	public void setMax(JacksonDBObject<Object> max) {
		this.max = max;
	}

}
