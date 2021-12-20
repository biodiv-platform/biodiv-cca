package com.strandls.cca.pojo.fields;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.strandls.cca.pojo.CCAField;

public abstract class RangableField<T extends Comparable<T>> extends CCAField {

	private List<T> minMax = new ArrayList<>();

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
