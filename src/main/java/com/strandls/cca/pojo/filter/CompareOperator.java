package com.strandls.cca.pojo.filter;

public enum CompareOperator {
	EQ("EQ"), LT("LT"), GT("GT"), LTE("LTE"), GTE("GTE"), NE("NE"), IN("IN"), ALL("ALL");

	private String value;

	private CompareOperator(String value) {
		this.value = value;
	}

	public static CompareOperator fromString(final String value) {
		for (CompareOperator dataType : CompareOperator.values()) {
			if (dataType.value.equals(value))
				return dataType;
		}
		throw new IllegalArgumentException(value);
	}

	public String getValue() {
		return value;
	}
}
