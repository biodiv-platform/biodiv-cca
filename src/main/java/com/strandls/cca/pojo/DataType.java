package com.strandls.cca.pojo;

public enum DataType {

	TEXT("TEXT"),
	RICHTEXT("RICHTEXT"),
	NUMBER("NUMBER"),
	NUMBER_RANGE("NUMBER_RANGE"),
	DATE("DATE"),
	DATE_RANGE("DATE_RANGE"),
	RADIO("RADIO"),
	CHECKBOX("CHECKBOX"),
	SINGLE_SELECT("SINGLE_SELECT"),
	MULTI_SELECT("MULTI_SELECT"),
	GEOMETRY("GEOMETRY"),
	FILE("FILE"),
	HEADING("HEADING");
	
	private String value;
	
	private DataType(String value) {
		this.value = value;
	}
	
	public static DataType fromValue(String value) {
		for(DataType dataType : DataType.values()) {
			if(dataType.value.equals(value))
				return dataType;
		}
		throw new IllegalArgumentException(value);
	}
	
	public String getValue() {
		return value;
	}
}
