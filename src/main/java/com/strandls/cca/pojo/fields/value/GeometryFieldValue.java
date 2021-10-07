package com.strandls.cca.pojo.fields.value;

import java.util.Arrays;

import com.mongodb.client.model.geojson.Geometry;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import com.strandls.cca.pojo.CCAFieldValue;

public class GeometryFieldValue extends CCAFieldValue {

	private Geometry value;

	public GeometryFieldValue() {
	}

	public GeometryFieldValue(String dataValue) {
		String[] points = dataValue.split(",");
		Double point1 = Double.parseDouble(points[0]);
		Double point2 = Double.parseDouble(points[0]);
		this.value = new Point(new Position(Arrays.asList(point1, point2)));
	}

	public Geometry getValue() {
		return value;
	}

	public void setValue(Geometry geometry) {
		this.value = geometry;
	}

}
