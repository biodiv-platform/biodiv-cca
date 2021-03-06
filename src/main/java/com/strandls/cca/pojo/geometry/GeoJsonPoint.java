package com.strandls.cca.pojo.geometry;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.client.model.geojson.Geometry;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;

public class GeoJsonPoint extends GeoJsonGeometry {

	private List<Double> coordinates;

	@Override
	public Geometry getGeometry() {
		return new Point(new Position(coordinates));
	}

	public List<Double> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<Double> coordinates) {
		this.coordinates = coordinates;
	}

	@Override
	@JsonIgnore
	public List<Double> getCentroid() {
		return getCoordinates();
	}

}
