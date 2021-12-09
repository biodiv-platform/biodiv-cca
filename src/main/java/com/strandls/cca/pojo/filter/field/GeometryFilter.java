package com.strandls.cca.pojo.filter.field;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;
import com.strandls.cca.pojo.filter.Filter;

public class GeometryFilter extends Filter {

	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public Bson getFilter() {

		String fieldHierarchy = getFieldHierarchy();

		String[] bbox = value.split(",");

		if (bbox.length != 4) {
			throw new IllegalArgumentException(
					"Please specify bbox in term of lowerLeftX, lowerLeftY, upperRightX, uppperRightY");
		}

		Double lowerLeftX = Double.parseDouble(bbox[0]);
		Double lowerLeftY = Double.parseDouble(bbox[1]);
		Double upperRightX = Double.parseDouble(bbox[2]);
		Double upperRightY = Double.parseDouble(bbox[3]);

		return Filters.geoWithinBox(fieldHierarchy, lowerLeftX, lowerLeftY, upperRightX, upperRightY);
	}
}
