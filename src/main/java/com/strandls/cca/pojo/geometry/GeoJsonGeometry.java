package com.strandls.cca.pojo.geometry;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.mongodb.client.model.geojson.Geometry;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({ @JsonSubTypes.Type(value = GeoJsonPoint.class, name = GeometryConstants.POINT),
		@JsonSubTypes.Type(value = GeoJsonMultiPoint.class, name = GeometryConstants.MULTI_POINT),
		@JsonSubTypes.Type(value = GeoJsonLineString.class, name = GeometryConstants.LINE_STRING),
		@JsonSubTypes.Type(value = GeoJsonMultiLineString.class, name = GeometryConstants.MULTI_LINE_STRING),
		@JsonSubTypes.Type(value = GeoJsonPolygon.class, name = GeometryConstants.POLYGON),
		@JsonSubTypes.Type(value = GeoJsonMultiPolygon.class, name = GeometryConstants.MULTI_POLYGON),
		@JsonSubTypes.Type(value = GeoJsonGeometryCollection.class, name = GeometryConstants.GEOMETRY_COLLECTION) })
@BsonDiscriminator()
public abstract class GeoJsonGeometry {

	protected GeometryType type;

	@JsonIgnore
	public abstract Geometry getGeometry();

	public GeometryType getType() {
		return type;
	}

	public void setType(GeometryType type) {
		this.type = type;
	}
}
