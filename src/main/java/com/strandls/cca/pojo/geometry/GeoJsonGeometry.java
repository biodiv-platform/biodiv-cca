package com.strandls.cca.pojo.geometry;

import com.mongodb.client.model.geojson.Geometry;

public abstract class GeoJsonGeometry {

	protected GeometryType type;
	protected Geometry geometry;

	public abstract Geometry getGeometry();

	public GeometryType getType() {
		return type;
	}

	public void setType(GeometryType type) {
		this.type = type;
	}
}
