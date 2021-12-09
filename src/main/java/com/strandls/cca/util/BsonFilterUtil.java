package com.strandls.cca.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import com.strandls.cca.CCAConstants;
import com.strandls.cca.FieldConstants;
import com.strandls.cca.dao.CCATemplateDao;
import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCATemplate;
import com.strandls.cca.pojo.FieldType;
import com.strandls.cca.pojo.filter.CompareOperator;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class BsonFilterUtil {

	private BsonFilterUtil() {
	}

	public static JSONArray getUserIdFilter(String userId) {
		JSONArray filterArray = new JSONArray();
		if (userId == null)
			return filterArray;
		JSONObject filter = new JSONObject();
		filter.appendField(CCAConstants.TYPE, FieldConstants.GENERIC);
		filter.appendField(CCAConstants.FIELD_NAME, CCAConstants.USER_ID);
		filter.appendField(CCAConstants.VALUE, userId);
		filterArray.add(filter);
		return filterArray;
	}

	public static JSONArray getShortNameFilter(MultivaluedMap<String, String> queryParameter) {
		JSONArray filterArray = new JSONArray();
		if (!queryParameter.containsKey(CCAConstants.SHORT_NAME))
			return filterArray;
		JSONObject filter = new JSONObject();
		filter.appendField(CCAConstants.TYPE, FieldConstants.GENERIC);
		filter.appendField(CCAConstants.FIELD_NAME, CCAConstants.SHORT_NAME);
		filter.appendField(CCAConstants.VALUE, queryParameter.get(CCAConstants.SHORT_NAME).get(0));
		filterArray.add(filter);
		return filterArray;
	}

	public static JSONArray getFilterFromFields(MultivaluedMap<String, String> queryParameter,
			CCATemplateDao templateDao) {
		// Take a master template as reference and create the filter
		String filterTemplate;
		if (queryParameter.containsKey(CCAConstants.FILTER_TEMPLATE)) {
			filterTemplate = queryParameter.getFirst(CCAConstants.FILTER_TEMPLATE);
		} else
			filterTemplate = CCAConstants.MASTER;

		CCATemplate ccaTemplate = templateDao.findByProperty(CCAConstants.SHORT_NAME, filterTemplate);

		JSONArray filterArray = new JSONArray();
		Iterator<CCAField> templateIt = ccaTemplate.iterator();
		while (templateIt.hasNext()) {

			CCAField field = templateIt.next();
			String fieldId = field.getFieldId();

			if (queryParameter.containsKey(fieldId)) {
				List<String> value = queryParameter.get(fieldId);
				filterArray.addAll(getFilters(value, field));
			}
		}
		return filterArray;
	}

	/**
	 * Creating filter based on its type
	 * 
	 * @param values
	 * @param field
	 * @return
	 */
	private static List<JSONObject> getFilters(List<String> values, CCAField field) {

		List<JSONObject> filterArray = new ArrayList<>();

		String fieldId = field.getFieldId();
		FieldType fieldType = field.getType();

		JSONObject filter;
		switch (fieldType) {
		case CHECKBOX:
		case MULTI_SELECT:
		case RADIO:
		case SINGLE_SELECT:
			filter = getFilter(fieldId, fieldType, CompareOperator.IN, values);
			filterArray.add(filter);
			break;
		case DATE:
		case NUMBER:
		case DATE_RANGE:
		case NUMBER_RANGE:
			if (values.size() == 1) {
				String v = values.get(0);
				filterArray.addAll(getNumberFilters(fieldId, fieldType, v));
			}
			break;
		case TEXT:
		case RICHTEXT:
			filter = getFilter(fieldId, fieldType, null, values.get(0));
			filter.appendField("isExactMatch", false);
			filterArray.add(filter);
			break;
		case GEOMETRY:
			filter = getFilter(fieldId, fieldType, null, values);
			filterArray.add(filter);
			break;
		case HEADING:
		case FILE:
		default:
			break;
		}

		return filterArray;
	}

	/**
	 * Number filter with various cases
	 * 
	 * 1. Less than / less than or equal to. 2. Greater than / greater than or equal
	 * to. 3. Range filter.
	 * 
	 * @param fieldId
	 * @param fieldType
	 * @param v
	 * @return
	 */
	private static List<JSONObject> getNumberFilters(String fieldId, FieldType fieldType, String v) {

		List<JSONObject> filterArray = new ArrayList<>();

		JSONObject filter;
		if ('<' == v.charAt(0)) {
			if ('=' == v.charAt(1)) {
				filter = getFilter(fieldId, fieldType, CompareOperator.LTE, v.substring(2));
				filterArray.add(filter);
			} else {
				filter = getFilter(fieldId, fieldType, CompareOperator.LT, v.substring(1));
				filterArray.add(filter);
			}
		} else if ('>' == v.charAt(0)) {
			if ('=' == v.charAt(1)) {
				filter = getFilter(fieldId, fieldType, CompareOperator.GTE, v.substring(2));
				filterArray.add(filter);
			} else {
				filter = getFilter(fieldId, fieldType, CompareOperator.GT, v.substring(1));
				filterArray.add(filter);
			}
		} else if ('=' == v.charAt(0)) {
			filter = getFilter(fieldId, fieldType, CompareOperator.EQ, v.substring(1));
			filterArray.add(filter);
		} else {
			String[] range = v.split("-");
			if (range.length != 2) {
				throw new IllegalArgumentException("Range should have two values");
			}
			filter = getFilter(fieldId, fieldType, CompareOperator.GTE, range[0]);
			filterArray.add(filter);

			filter = getFilter(fieldId, fieldType, CompareOperator.LTE, range[1]);
			filterArray.add(filter);
		}

		return filterArray;
	}

	/**
	 * Creation of the filter in general for various type
	 * 
	 * @param fieldId
	 * @param type
	 * @param op
	 * @param value
	 * @return
	 */
	private static JSONObject getFilter(String fieldId, FieldType type, CompareOperator op, Object value) {
		JSONObject filter = new JSONObject();
		filter.appendField("fieldId", fieldId);
		filter.appendField("type", type);
		if (op != null) {
			filter.appendField("op", op);
		}
		filter.appendField("value", value);
		return filter;
	}
}
