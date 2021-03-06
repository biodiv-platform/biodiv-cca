package com.strandls.cca.pojo;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.strandls.cca.ApiConstants;
import com.strandls.cca.CCAConfig;

public class ValueWithLabel {

	//private static final String OTHER_VALUE = "others|?";

	private String valueId;
	private String label;
	private String value;

	@JsonIgnore
	private Map<String, String> translations = new HashMap<>();

	public ValueWithLabel translate(String language) {
		this.label = translations.get(language);
		if (this.label == null)
			this.label = translations.get(CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE));
		return this;
	}

	public ValueWithLabel addUpdateTranslation(ValueWithLabel valueWithLabel, String language) {
		if (valueWithLabel != null)
			getTranslations().putAll(valueWithLabel.getTranslations());
		getTranslations().put(language, label);
		return translate(language);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ValueWithLabel))
			return false;

		ValueWithLabel field = (ValueWithLabel) obj;

		return getValue().equals(field.getValue());
	}

	@Override
	public String toString() {
		return this.label;
	}

	public void setValueId(String valueId) {
		this.valueId = valueId;
	}

	public String getValueId() {
		return valueId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Map<String, String> getTranslations() {
		return translations;
	}

	public void setTranslations(Map<String, String> labelTranslations) {
		this.translations = labelTranslations;
	}

	public boolean belongs(ValueWithLabel valueWithLabel) {
		return valueId.equals(valueWithLabel.getValueId());
	}

}
