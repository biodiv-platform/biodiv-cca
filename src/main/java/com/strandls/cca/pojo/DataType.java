package com.strandls.cca.pojo;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "dataType")
@XmlEnum
public enum DataType {

	@XmlEnumValue("TEXT")
	TEXT("TEXT"),
	@XmlEnumValue("RICHTEXT")
	RICHTEXT("RICHTEXT"),
	
	@XmlEnumValue("NUMBER")
	NUMBER("NUMBER"),
	@XmlEnumValue("NUMBER_RANGE")
	NUMBER_RANGE("NUMBER_RANGE"),
	
	@XmlEnumValue("DATE")
	DATE("DATE"),
	@XmlEnumValue("DATE_RANGE")
	DATE_RANGE("DATE_RANGE"),

	@XmlEnumValue("RADIO")
	RADIO("RADIO"),
	@XmlEnumValue("CHECKBOX")
	CHECKBOX("CHECKBOX"),
	
	@XmlEnumValue("SELECT")
	SINGLE_SELECT("SELECT"),
	@XmlEnumValue("MULTI_SELECT")
	MULTI_SELECT("MULTI_SELECT"),
	
	@XmlEnumValue("GEOMETRY")
	GEOMETRY("GEOMETRY"),
	
	@XmlEnumValue("FILE")
	FILE("FILE"),
	
	@XmlEnumValue("EMBEDDED")
	EMBEDDED("EMBEDDED"),

	@XmlEnumValue("Heading")
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
