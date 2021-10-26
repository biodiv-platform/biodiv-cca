package com.strandls.cca.pojo.geometry;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.model.geojson.Geometry;
import com.mongodb.client.model.geojson.Polygon;
import com.mongodb.client.model.geojson.Position;

public class GeoJsonPolygon extends GeoJsonGeometry {

	private List<List<Double>> coordinates;

	@SuppressWarnings("unchecked")
	@Override
	public Geometry getGeometry() {
		List<Position> exterior = new ArrayList<>();
		List<Position> interior = new ArrayList<>();

		for (List<Double> point : coordinates) {
			Position position = new Position(point);
			exterior.add(position);
		}

		return new Polygon(exterior, interior);
	}

	public List<List<Double>> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<List<Double>> coordinates) {
		this.coordinates = coordinates;
	}

}
