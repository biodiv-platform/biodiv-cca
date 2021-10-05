package com.strandls.cca.pojo.fields;

import java.util.List;

import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCAFieldValue;

public abstract class RangableField<T extends Comparable<T>> extends CCAField {

	private T min;
	private T max;

	@Override
	public boolean validate(CCAFieldValue fieldValue) {
		return super.validate(fieldValue) && (min == null || max == null || min.compareTo(max) <= 0);
	}

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

	public abstract T parseTo(String value);

	public boolean isRanged(List<String> ranged) {
		T maxObject = getMax();
		T minObject = getMin();

		if (maxObject == null || minObject == null)
			return true;

		if (ranged.size() == 1) {
			T value = parseTo(ranged.get(0));
			if (value.compareTo(minObject) < 0 || value.compareTo(maxObject) > 0)
				return false;
		}

		if (ranged.size() == 2) {
			T minValue = parseTo(ranged.get(0));
			T maxVlaue = parseTo(ranged.get(1));

			if (minValue.compareTo(maxVlaue) > 0)
				return false;

			if (minValue.compareTo(min) < 0 || minValue.compareTo(max) > 0 || maxVlaue.compareTo(max) > 0)
				return false;
		}

		return true;
	}
}
