package com.strandls.cca.pojo;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.strandls.cca.ApiConstants;
import com.strandls.cca.CCAConfig;

public class ValueWithLabel {

	private static final String OTHER_VALUE = "others|?";

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
	public boolean equals(Object obj) {
		if (!(obj instanceof ValueWithLabel))
			return false;

		ValueWithLabel field = (ValueWithLabel) obj;

		return getValue().equals(field.getValue());
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

	// TODO : Need to make change for take into consideration the othe language as
	// well.
	public boolean belongs(ValueWithLabel valueWithLabel) {
		if (OTHER_VALUE.equals(valueWithLabel.getValue().toLowerCase().replaceAll("\\s", ""))
				&& OTHER_VALUE.equals(getValue().toLowerCase().replaceAll("\\s", "")))
			return true;
		String labelInDB = getTranslations().get(CCAConfig.getProperty(ApiConstants.DEFAULT_LANGUAGE));
		return labelInDB.toLowerCase().trim().equals(valueWithLabel.getLabel().toLowerCase().trim());
	}

}
