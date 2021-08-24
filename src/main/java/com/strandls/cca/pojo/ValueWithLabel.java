package com.strandls.cca.pojo;

import net.vz.mongodb.jackson.internal.stream.JacksonDBObject;

public class ValueWithLabel {

	private String label;
	private JacksonDBObject<Object>	value;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Object getValue() {
		return value.getObject();
	}

	public void setValue(Object value) {
		this.value = new JacksonDBObject<>(value);
	}

}
