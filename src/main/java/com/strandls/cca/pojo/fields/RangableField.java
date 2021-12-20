package com.strandls.cca.pojo.fields;

import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Facet;
import com.strandls.cca.pojo.CCAField;

public abstract class RangableField<T extends Comparable<T>> extends CCAField {

	private List<T> minMax = new ArrayList<>();

	@Override
	public Facet getGroupAggregation() {
		String fieldHierarchy = "$" + getFieldHierarchy();
		Bson group = Aggregates.group(null, Accumulators.min("min", fieldHierarchy),
				Accumulators.max("max", fieldHierarchy));
		return new Facet(getFieldId(), group);
	}

	/**
	 * This method is for getting minimum and maximum for the generic type T Used
	 * only for the validation purpose.
	 * 
	 * @return
	 */
	public abstract T fetchMaxRange();

	public abstract T fetchMinRange();

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj, String language) {
		if (!super.equals(obj, language))
			return false;

		if (!(obj instanceof RangableField<?>))
			return false;

		RangableField<T> field = (RangableField<T>) obj;
		return getMinMax().equals(field.getMinMax());
	}

	@JsonIgnore
	public boolean isMinMaxSet() {
		return minMax != null && !minMax.isEmpty();
	}

	public List<T> getMinMax() {
		return minMax;
	}

	public void setMinMax(List<T> minMax) {
		this.minMax = minMax;
	}

}
