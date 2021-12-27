package com.strandls.cca.pojo.fields;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.conversions.Bson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Facet;
import com.strandls.cca.dao.CCATemplateDao;
import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.ValueWithLabel;
import com.strandls.cca.util.CCAFilterUtil;

public abstract class ValueOptionsField extends CCAField {

	private List<ValueWithLabel> valueOptions;

	@Override
	@JsonIgnore
	@BsonIgnore
	public Facet getGroupAggregation(MultivaluedMap<String, String> queryParameter, CCATemplateDao templateDao,
			ObjectMapper objectMapper, String userId) throws JsonProcessingException {
		String fieldHierarchy = getFieldHierarchy();
		Bson match = Aggregates.match(CCAFilterUtil.getAllFilters(queryParameter, templateDao, objectMapper, userId,
				new HashSet<>(Arrays.asList(getFieldId()))));
		Bson unwind = Aggregates.unwind("$" + fieldHierarchy);
		Bson group = Aggregates.group("$" + fieldHierarchy + ".value", Accumulators.sum("count", 1));
		return new Facet(getFieldId(), match, unwind, group);
	}

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

	@Override
	public String equals(Object obj, String language) {
		String diff = super.equals(obj, language);

		if (!(obj instanceof ValueOptionsField))
			return null;

		ValueOptionsField field = (ValueOptionsField) obj;

		int dbSize = this.valueOptions.size();
		int inSize = field.getValueOptions().size();

		if (dbSize != inSize) {
			diff += "Value : " + getValueOptions() + "→" + field.getValueOptions() + "\n";
		} else if (dbSize == 0) {
			// NO nothing
		} else {
			if (!getValueOptions().equals(field.getValueOptions()))
				diff += "Value : " + getValueOptions() + "→" + field.getValueOptions() + "\n";
		}

		return diff.equals("") ? null : diff;
	}

	public List<ValueWithLabel> getValueOptions() {
		return valueOptions;
	}

	public void setValueOptions(List<ValueWithLabel> valueOptions) {
		this.valueOptions = valueOptions;
	}

}
