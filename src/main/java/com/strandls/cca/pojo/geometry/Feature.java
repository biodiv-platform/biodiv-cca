package com.strandls.cca.pojo.geometry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Feature {

	private String type = "Feature";
	private Map<String, String> properties = new HashMap<>();
	private GeoJsonGeometry geometry;

	public List<Double> getCentroid() {
		return geometry.getCentroid();
	}

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
