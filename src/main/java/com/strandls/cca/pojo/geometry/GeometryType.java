package com.strandls.cca.pojo.geometry;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GeometryType {
	Point("Point"), MultiPoint("MultiPoint"), LineString("LineString"), MultiLineString("MultiLineString"),
	Polygon("Polygon"), MultiPolygon("MultiPolygon"), GeometryCollection("GeometryCollection");

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

	@JsonValue
	public String getValue() {
		return value;
	}
}
