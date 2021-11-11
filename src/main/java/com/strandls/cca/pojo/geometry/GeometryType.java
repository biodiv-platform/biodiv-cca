package com.strandls.cca.pojo.geometry;

public enum GeometryType {
	POINT("Point"), MULTI_POINT("MultiPoint"), LINE_STRING("LineString"), MULTI_LINE_STRING("MultiLineString"),
	POLYGON("Polygon"), MULTI_POLYGON("MultiPolygon"), GEOMETRY_COLLECTION("GeometryCollection");

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
