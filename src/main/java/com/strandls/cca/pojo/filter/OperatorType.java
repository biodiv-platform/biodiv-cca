package com.strandls.cca.pojo.filter;


public enum OperatorType {
	AND("AND"), OR("OR");
	
	private String value;

	private OperatorType(String value) {
		this.value = value;
	}

	public static OperatorType fromString(final String value) {
		for (OperatorType dataType : OperatorType.values()) {
			if (dataType.value.equals(value))
				return dataType;
		}
		throw new IllegalArgumentException(value);
	}

	public String getValue() {
		return value;
	}
}
