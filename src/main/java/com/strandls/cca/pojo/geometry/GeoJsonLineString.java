package com.strandls.cca.pojo.geometry;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.client.model.geojson.Geometry;
import com.mongodb.client.model.geojson.LineString;
import com.mongodb.client.model.geojson.Position;
import com.strandls.cca.util.GeometryUtil;

public class GeoJsonLineString extends GeoJsonGeometry {

	private List<List<Double>> coordinates;

	@Override
	public Geometry getGeometry() {
		List<Position> lineString = new ArrayList<>();

		for (List<Double> point : coordinates) {
			Position position = new Position(point);
			lineString.add(position);
		}

		return new LineString(lineString);
	}

	public List<List<Double>> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<List<Double>> coordinates) {
		this.coordinates = coordinates;
	}

	@Override
	@JsonIgnore
	public List<Double> getCentroid() {
		return GeometryUtil.computeCentroid2D(coordinates);
	}

}
