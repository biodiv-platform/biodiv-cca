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

}
