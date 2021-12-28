package com.strandls.cca.pojo.geometry;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.client.model.geojson.Geometry;

public class FeatureCollection {

	private String type = "FeatureCollection";
	private List<Feature> features;

	public FeatureCollection() {
		this.features = new ArrayList<>();
	}

	@JsonIgnore
	public List<Double> getCentroid() {
		List<Double> centroid = new ArrayList<>();
		Double x = 0.0;
		Double y = 0.0;
		boolean coordinateFound = false;
		Long n = 0L;
		for (Feature feature : features) {
			List<Double> c = feature.getCentroid();
			if (!c.isEmpty()) {
				coordinateFound = true;
				x += c.get(0);
				y += c.get(1);
				n++;
			}
		}
		if (!coordinateFound)
			return centroid;
		x /= n;
		y /= n;
		centroid.add(x);
		centroid.add(y);
		return centroid;
	}

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
