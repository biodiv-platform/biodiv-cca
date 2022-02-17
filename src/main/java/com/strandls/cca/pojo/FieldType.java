package com.strandls.cca.pojo;

public enum FieldType {

	TEXT("TEXT"), TEXT_AREA("TEXT_AREA"), RICHTEXT("RICHTEXT"), NUMBER("NUMBER"), NUMBER_RANGE("NUMBER_RANGE"),
	DATE("DATE"), YEAR("YEAR"), DATE_RANGE("DATE_RANGE"), RADIO("RADIO"), CHECKBOX("CHECKBOX"), SINGLE_SELECT("SINGLE_SELECT"),
	MULTI_SELECT("MULTI_SELECT"), GEOMETRY("GEOMETRY"), FILE("FILE"), HEADING("HEADING");

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
