package com.strandls.cca.pojo.geometry;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.client.model.geojson.Geometry;
import com.mongodb.client.model.geojson.Polygon;
import com.mongodb.client.model.geojson.Position;
import com.strandls.cca.util.GeometryUtil;

public class GeoJsonPolygon extends GeoJsonGeometry {

	private List<List<List<Double>>> coordinates;

	@SuppressWarnings("unchecked")
	@Override
	public Geometry getGeometry() {
		if (coordinates.isEmpty())
			return null;

		List<Position> exterior = new ArrayList<>();

		for (List<Double> point : coordinates.get(0)) {
			exterior.add(new Position(point));
		}

		if (coordinates.size() == 2) {
			List<Position> interior = new ArrayList<>();
			for (List<Double> point : coordinates.get(1)) {
				interior.add(new Position(point));
			}
			return new Polygon(exterior, interior);
		}

		return new Polygon(exterior);
	}

	public List<List<List<Double>>> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<List<List<Double>>> coordinates) {
		this.coordinates = coordinates;
	}

	@Override
	@JsonIgnore
	public List<Double> getCentroid() {
		return GeometryUtil.computeCentroid3D(coordinates);
	}

}
