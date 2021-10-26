package com.strandls.cca.pojo.geometry;

public enum GeometryType {
	POINT("POINT"), Polygon("Polygon");

	private String value;

	private GeometryType(String value) {
		this.value = value;
	}

	public static GeometryType fromString(final String value) {
		for (GeometryType dataType : GeometryType.values()) {
			if (dataType.value.equals(value))
				return dataType;
		}
		throw new IllegalArgumentException(value);
	}

	public String getValue() {
		return value;
	}
}
