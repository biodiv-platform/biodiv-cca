package com.strandls.cca.pojo.fields.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonProperty;

import com.mongodb.client.model.geojson.Geometry;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import com.strandls.cca.pojo.CCAFieldValue;
import com.strandls.cca.pojo.geometry.Feature;
import com.strandls.cca.pojo.geometry.FeatureCollection;
import com.strandls.cca.pojo.geometry.GeoJsonGeometry;

public class GeometryFieldValue extends CCAFieldValue {

	@BsonProperty
	private FeatureCollection value;

	public GeometryFieldValue() {
		this.value = new FeatureCollection();
	}

	public List<Double> getCentroid() {
		return value.getCentroid();
	}

	@Override
	public String computeDiff(CCAFieldValue value) {
		return null;
	}

	public GeometryFieldValue(String dataValue) {
		String[] points = dataValue.split(",");
		Double point1 = Double.parseDouble(points[0]);
		Double point2 = Double.parseDouble(points[0]);

		List<Feature> features = new ArrayList<>();
		Feature feature = new Feature();
		GeoJsonGeometry geometry = new GeoJsonGeometry() {
			@Override
			public Geometry getGeometry() {
				return new Point(new Position(Arrays.asList(point1, point2)));
			}

			@Override
			public List<Double> getCentroid() {
				return Arrays.asList(point1, point2);
			}
		};
		feature.setGeometry(geometry);
		features.add(feature);
		FeatureCollection featureCollection = new FeatureCollection();
		featureCollection.setFeatures(features);
		this.value = featureCollection;
	}

	public FeatureCollection getValue() {
		return this.value;
	}

	public void setValue(FeatureCollection geometry) {
		this.value = geometry;
	}

}
