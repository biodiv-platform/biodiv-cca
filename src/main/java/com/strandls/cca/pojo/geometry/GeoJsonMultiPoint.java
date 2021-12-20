package com.strandls.cca.pojo.geometry;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.model.geojson.Geometry;
import com.mongodb.client.model.geojson.MultiPoint;
import com.mongodb.client.model.geojson.Position;
import com.strandls.cca.util.GeometryUtil;

public class GeoJsonMultiPoint extends GeoJsonGeometry {

	private List<List<Double>> coordinates;

	@Override
	public Geometry getGeometry() {
		List<Position> multiPoint = new ArrayList<>();
		for (List<Double> point : coordinates) {
			multiPoint.add(new Position(point));
		}
		return new MultiPoint(multiPoint);
	}

	public List<List<Double>> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<List<Double>> coordinates) {
		this.coordinates = coordinates;
	}

	@Override
	public List<Double> getCentroid() {
		return GeometryUtil.computeCentroid2D(coordinates);
	}

}
