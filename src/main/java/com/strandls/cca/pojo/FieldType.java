package com.strandls.cca.pojo;

public enum FieldType {

	TEXT("TEXT"), TEXT_AREA("TEXT_AREA"), RICHTEXT("RICHTEXT"), NUMBER("NUMBER"), NUMBER_RANGE("NUMBER_RANGE"),
	DATE("DATE"), YEAR("YEAR"), DATE_RANGE("DATE_RANGE"), SINGLE_SELECT_RADIO("SINGLE_SELECT_RADIO"), MULTI_SELECT_CHECKBOX("MULTI_SELECT_CHECKBOX"), 
	SINGLE_SELECT_DROPDOWN("SINGLE_SELECT_DROPDOWN"), MULTI_SELECT_DROPDOWN("MULTI_SELECT_DROPDOWN"), GEOMETRY("GEOMETRY"), FILE("FILE"), HEADING("HEADING");

	private String value;

	private FieldType(String value) {
		this.value = value;
	}

	public static FieldType fromString(final String value) {
		for (FieldType dataType : FieldType.values()) {
			if (dataType.value.equals(value))
				return dataType;
		}
		throw new IllegalArgumentException(value);
	}

	public String getValue() {
		return value;
	}

}
