package com.strandls.cca.pojo;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "dataType")
@XmlEnum
public enum DataType {

	@XmlEnumValue("TEXT")
	TEXT("TEXT"),
	@XmlEnumValue("GEOMETRY")
	GEOMETRY("GEOMETRY"),
	@XmlEnumValue("NUMBER")
	NUMBER("NUMBER"),
	@XmlEnumValue("RADIO")
	RADIO("RADIO"),
	@XmlEnumValue("CHECKBOX")
	CHECKBOX("CHECKBOX"),
	@XmlEnumValue("DATE")
	DATE("DATE"),
	@XmlEnumValue("EMBEDDED")
	EMBEDDED("EMBEDDED"),
	@XmlEnumValue("UPLOAD")
	UPLOAD("UPLOAD"),
	@XmlEnumValue("LINK")
	LINK("LINK");
	
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
}
