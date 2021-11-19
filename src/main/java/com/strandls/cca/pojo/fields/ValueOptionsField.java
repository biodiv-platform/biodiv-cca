package com.strandls.cca.pojo.fields;

import java.util.List;

import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.ValueWithLabel;

public abstract class ValueOptionsField extends CCAField {

	private List<ValueWithLabel> valueOptions;

	@Override
	public void validate() {
		super.validate();
		if (valueOptions == null || valueOptions.isEmpty())
			throw new IllegalArgumentException("Value options not provided");
	}

	public boolean contains(ValueWithLabel value) {
		for (ValueWithLabel valueWithLabel : valueOptions) {
			if (valueWithLabel.belongs(value))
				return true;
		}
		return false;
	}

	public List<ValueWithLabel> getValueOptions() {
		return valueOptions;
	}

	public void setValueOptions(List<ValueWithLabel> valueOptions) {
		this.valueOptions = valueOptions;
	}

}
