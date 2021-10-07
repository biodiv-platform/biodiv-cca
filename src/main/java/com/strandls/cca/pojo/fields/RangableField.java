package com.strandls.cca.pojo.fields;

import com.strandls.cca.pojo.CCAField;

public abstract class RangableField<T extends Comparable<T>> extends CCAField {

	private T min;
	private T max;

	/** 
	 * This method is for getting minimum and maximum for the generic type T
	 * Used only for the validation purpose.
	 * @return
	 */
	public abstract T fetchMaxRange();

	public abstract T fetchMinRange();

	public T getMin() {
		return min;
	}

	public void setMin(T min) {
		this.min = min;
	}

	public T getMax() {
		return max;
	}

	public void setMax(T max) {
		this.max = max;
	}

}
