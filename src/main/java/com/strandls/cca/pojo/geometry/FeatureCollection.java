package com.strandls.cca.pojo.geometry;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.client.model.geojson.Geometry;

public class FeatureCollection {

	private String type = "FeatureCollection";
	private List<Feature> features;
	
	// Supporting only one features
	// TODO : Need to support across multiple values in the geometry
	@JsonIgnore
	public Geometry getGeometry() {
		return features.get(0).getGeometry().getGeometry();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Feature> getFeatures() {
		return features;
	}

	public void setFeatures(List<Feature> features) {
		this.features = features;
	}

}
