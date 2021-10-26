package com.strandls.cca.pojo.geometry;

import java.util.Map;

public class Feature {

	private String type;
	private Map<String, String> properties;
	private GeoJsonGeometry geometry;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public GeoJsonGeometry getGeometry() {
		return geometry;
	}

	public void setGeometry(GeoJsonGeometry geometry) {
		this.geometry = geometry;
	}

}
