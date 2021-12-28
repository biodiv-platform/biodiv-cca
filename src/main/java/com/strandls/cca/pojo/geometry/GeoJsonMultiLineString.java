package com.strandls.cca.pojo.geometry;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.client.model.geojson.Geometry;
import com.mongodb.client.model.geojson.MultiLineString;
import com.mongodb.client.model.geojson.Position;
import com.strandls.cca.util.GeometryUtil;

public class GeoJsonMultiLineString extends GeoJsonGeometry {

	private List<List<List<Double>>> coordinates;

	@Override
	public Geometry getGeometry() {
		List<List<Position>> multiLineString = new ArrayList<>();

		for (List<List<Double>> line : coordinates) {
			List<Position> lineString = new ArrayList<>();
			for (List<Double> point : line) {
				Position position = new Position(point);
				lineString.add(position);
			}
			multiLineString.add(lineString);
		}

		return new MultiLineString(multiLineString);
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
