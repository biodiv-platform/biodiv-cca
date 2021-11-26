package com.strandls.cca.pojo.fields;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.ValueWithLabel;

public abstract class ValueOptionsField extends CCAField {

	private List<ValueWithLabel> valueOptions;

	/**
	 * Translating extra attribute (value options) for the traits.
	 */
	@Override
	public CCAField translate(String language) {
		for (ValueWithLabel valueWithLabel : valueOptions) {
			valueWithLabel.translate(language);
		}
		super.translate(language);
		return this;
	}

	/**
	 * Update all the label before doing the field translation. This method take
	 * care translation for all the value option fields.
	 */
	@Override
	public CCAField addUpdateTranslation(CCAField ccaField, String language) {
		ValueOptionsField valueOptionsField = (ValueOptionsField) ccaField;

		// take previous translations
		Map<String, ValueWithLabel> valueOptionsMap = new HashMap<>();
		if (valueOptionsField != null)
			for (ValueWithLabel valueWithLabel : valueOptionsField.getValueOptions()) {
				valueOptionsMap.put(valueWithLabel.getValue(), valueWithLabel);
			}

		for (ValueWithLabel valueWithLabel : getValueOptions()) {
			valueWithLabel.addUpdateTranslation(valueOptionsMap.get(valueWithLabel.getValue()), language);
		}
		super.addUpdateTranslation(ccaField, language);
		return translate(language);
	}

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
