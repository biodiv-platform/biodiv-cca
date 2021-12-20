package com.strandls.cca.pojo.geometry;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.model.geojson.Geometry;
import com.mongodb.client.model.geojson.GeometryCollection;

public class GeoJsonGeometryCollection extends GeoJsonGeometry {

	private List<GeoJsonGeometry> geometries;

	@Override
	public Geometry getGeometry() {
		List<Geometry> geometriesList = new ArrayList<Geometry>();
		for (GeoJsonGeometry geoJsonGeometry : geometries) {
			geometriesList.add(geoJsonGeometry.getGeometry());
		}
		return new GeometryCollection(geometriesList);
	}

	@Override
	public List<Double> getCentroid() {
		List<Double> centroid = new ArrayList<>();
		Double x = 0.0;
		Double y = 0.0;
		boolean coordinateFound = false;
		Long n = 0L;
		for (GeoJsonGeometry geoJsonGeometry : geometries) {
			List<Double> c = geoJsonGeometry.getCentroid();
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

}
