package com.strandls.cca.pojo.geometry;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.model.geojson.Geometry;
import com.mongodb.client.model.geojson.Polygon;
import com.mongodb.client.model.geojson.Position;

public class GeoJsonPolygon extends GeoJsonGeometry {

	private List<List<List<Double>>> coordinates;

	@SuppressWarnings("unchecked")
	@Override
	public Geometry getGeometry() {
		if (coordinates.isEmpty())
			return null;

		List<Position> exterior = new ArrayList<>();
		List<Position> interior = new ArrayList<>();

		for (List<Double> point : coordinates.get(0)) {
			exterior.add(new Position(point));
		}

		if (coordinates.size() == 2)
			for (List<Double> point : coordinates.get(1)) {
				interior.add(new Position(point));
			}

		return new Polygon(exterior, interior);
	}

	public List<List<List<Double>>> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<List<List<Double>>> coordinates) {
		this.coordinates = coordinates;
	}

}
