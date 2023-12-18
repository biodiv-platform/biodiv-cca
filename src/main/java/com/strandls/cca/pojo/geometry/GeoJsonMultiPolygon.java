package com.strandls.cca.pojo.geometry;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.client.model.geojson.Geometry;
import com.mongodb.client.model.geojson.MultiPolygon;
import com.mongodb.client.model.geojson.PolygonCoordinates;
import com.mongodb.client.model.geojson.Position;
import com.strandls.cca.util.GeometryUtil;

public class GeoJsonMultiPolygon extends GeoJsonGeometry {

	private List<List<List<List<Double>>>> coordinates;

	@SuppressWarnings("unchecked")
	@Override
	public Geometry getGeometry() {
		List<PolygonCoordinates> polygonCoordinates = new ArrayList<>();
		for (List<List<List<Double>>> polygon : coordinates) {
			if (polygon.isEmpty())
				continue;
			List<Position> exterior = new ArrayList<>();
			List<Position> interior = new ArrayList<>();

			for (List<Double> point : polygon.get(0)) {
				exterior.add(new Position(point));
			}

			if (polygon.size() == 2)
				for (List<Double> point : polygon.get(1)) {
					interior.add(new Position(point));
				}

            if(polygon.size()>1)
	             polygonCoordinates.add(new PolygonCoordinates(exterior, interior));
            else
            	polygonCoordinates.add(new PolygonCoordinates(exterior));

	    }
	    return new MultiPolygon(polygonCoordinates);
	}

	public List<List<List<List<Double>>>> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<List<List<List<Double>>>> coordinates) {
		this.coordinates = coordinates;
	}

	@Override
	@JsonIgnore
	public List<Double> getCentroid() {
		return GeometryUtil.computeCentroid4D(coordinates);
	}

}
