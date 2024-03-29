package com.strandls.cca.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.core.MultivaluedMap;

import org.bson.conversions.Bson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Facet;
import com.mongodb.client.model.Filters;
import com.strandls.cca.CCAConstants;
import com.strandls.cca.dao.CCATemplateDao;
import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCATemplate;
import com.strandls.cca.pojo.FieldType;
import com.strandls.cca.pojo.filter.CompareOperator;
import com.strandls.cca.pojo.filter.IFilter;
import com.strandls.cca.pojo.filter.OperatorType;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class CCAFilterUtil {

	private CCAFilterUtil() {
	}

	public static Bson getAllFilters(MultivaluedMap<String, String> queryParameter, CCATemplateDao templateDao,
			ObjectMapper objectMapper, String userId, Set<String> excludeFieldFromFilter, Boolean isDeletedData)
			throws JsonProcessingException {
		List<Bson> filters = new ArrayList<>();
		filters.addAll(getShortNameFilter(queryParameter));
		filters.addAll(getUserIdFilter(userId));
		filters.addAll(getDataIdsFilter(queryParameter));
		filters.addAll(getIsDeleteFilter(isDeletedData));
		filters.addAll(getFieldCountFilters(queryParameter));
		filters.addAll(getUserGroupFilter(queryParameter));

		// Create the And filter from all the fields.
		List<Bson> filter = CCAFilterUtil.getFilterFromFields(queryParameter, templateDao, objectMapper,
				excludeFieldFromFilter);
		filters.addAll(filter);

		return Filters.and(filters);
	}

	public static Bson getAllFilters(MultivaluedMap<String, String> queryParameter, CCATemplateDao templateDao,
			ObjectMapper objectMapper, String userId, Boolean isDeleteData) throws JsonProcessingException {
		return getAllFilters(queryParameter, templateDao, objectMapper, userId, new HashSet<>(), isDeleteData);

	}

	private static List<Bson> getIsDeleteFilter(Boolean isDeletedData) {
		// Add isDeleted filter here
		List<Bson> filters = new ArrayList<>();
		Bson isDeleted = Filters.or(Filters.exists(CCAConstants.IS_DELETED, false),
				Filters.eq(CCAConstants.IS_DELETED, isDeletedData));
		filters.add(isDeleted);
		return filters;
	}

	private static List<Bson> getFieldCountFilters(MultivaluedMap<String, String> queryParameter) {
		List<Bson> filters = new ArrayList<>();
		if (queryParameter.containsKey(CCAConstants.RICH_TEXT_COUNT)) {
			filters.add(Filters.gte(CCAConstants.RICH_TEXT_COUNT,
					Integer.parseInt(queryParameter.get(CCAConstants.RICH_TEXT_COUNT).get(0))));
		}

		// In future, will add filter condition for traits and text field filter.

		return filters;
	}

	public static List<Bson> getDataIdsFilter(MultivaluedMap<String, String> queryParameter) {
		List<Bson> filters = new ArrayList<>();
		if (queryParameter.containsKey(CCAConstants.ID)) {
			String[] stringIds = (queryParameter.get(CCAConstants.ID).get(0)).split(",");
			List<Long> ids = Arrays.stream(stringIds).map(Long::parseLong).collect(Collectors.toList());
			Bson idFilter = Filters.in(CCAConstants.ID, ids);
			filters.add(idFilter);
		}
		return filters;
	}

	public static List<Bson> getUserIdFilter(String userId) {
		List<Bson> filters = new ArrayList<>();
		if (userId != null) {
			filters.add(Filters.eq("userId", userId));
		}
		return filters;
	}

	public static List<Bson> getUserGroupFilter(MultivaluedMap<String, String> queryParameter) {
		List<Bson> filters = new ArrayList<>();

		if (queryParameter.containsKey(CCAConstants.USER_GROUPS)) {
			List<String> usergroupIds = queryParameter.get(CCAConstants.USER_GROUPS);

			if (usergroupIds != null && !usergroupIds.isEmpty()) {
				filters.add(Filters.in("usergroups", usergroupIds));
			}
		}
		return filters;

	}

	public static List<Bson> getShortNameFilter(MultivaluedMap<String, String> queryParameter) {
		List<Bson> filters = new ArrayList<>();
		if (queryParameter.containsKey(CCAConstants.SHORT_NAME)) {
			String shortName = queryParameter.get(CCAConstants.SHORT_NAME).get(0);
			filters.add(Filters.eq(CCAConstants.SHORT_NAME, shortName));
		}
		return filters;
	}

	public static List<Bson> getFilterFromFields(MultivaluedMap<String, String> queryParameter,
			CCATemplateDao templateDao, ObjectMapper objectMapper, Set<String> excludeFieldFromFilter)
			throws JsonProcessingException {
		// Take a master template as reference and create the filter
		String filterTemplate;
		if (queryParameter.containsKey(CCAConstants.FILTER_TEMPLATE)) {
			filterTemplate = queryParameter.getFirst(CCAConstants.FILTER_TEMPLATE);
		} else
			filterTemplate = CCAConstants.MASTER;

		CCATemplate ccaTemplate = templateDao.findByProperty(CCAConstants.SHORT_NAME, filterTemplate, false);

		JSONArray filterArray = new JSONArray();
		Iterator<CCAField> templateIt = ccaTemplate.iterator();
		while (templateIt.hasNext()) {

			CCAField field = templateIt.next();
			String fieldId = field.getFieldId();

			if (queryParameter.containsKey(fieldId) && !excludeFieldFromFilter.contains(fieldId)) {
				List<String> value = queryParameter.get(fieldId);
				filterArray.addAll(getFilters(value, field));
			}
		}

		JSONObject filterObject = new JSONObject();
		filterObject.appendField(CCAConstants.TYPE, OperatorType.AND);
		filterObject.appendField("filters", filterArray);
		IFilter filter = objectMapper.readValue(filterObject.toJSONString(), IFilter.class);

		List<Bson> filters = new ArrayList<>();
		filters.add(filter.getFilter());
		return filters;
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
		case MULTI_SELECT_CHECKBOX:
		case MULTI_SELECT_DROPDOWN:
		case SINGLE_SELECT_RADIO:
		case SINGLE_SELECT_DROPDOWN:
			filter = getFilter(fieldId, fieldType, CompareOperator.IN, values);
			filterArray.add(filter);
			break;
		case GEOMETRY:
			filter = getFilter(fieldId, fieldType, null, values.get(0));
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

	public static Bson getFacetListForFilterableFields(MultivaluedMap<String, String> queryParameter,
			CCATemplateDao templateDao, ObjectMapper objectMapper, String userId) throws JsonProcessingException {
		// Take a master template as reference and create the filter
		String filterTemplate;
		if (queryParameter.containsKey(CCAConstants.FILTER_TEMPLATE)) {
			filterTemplate = queryParameter.getFirst(CCAConstants.FILTER_TEMPLATE);
		} else
			filterTemplate = CCAConstants.MASTER;

		CCATemplate ccaTemplate = templateDao.findByProperty(CCAConstants.SHORT_NAME, filterTemplate, false);

		List<Facet> facets = new ArrayList<>();
		Iterator<CCAField> templateIt = ccaTemplate.iterator();
		while (templateIt.hasNext()) {

			CCAField field = templateIt.next();
			if (field.getIsFilterable().booleanValue()) {
				Facet facet = field.getGroupAggregation(queryParameter, templateDao, objectMapper, userId);
				facets.add(facet);
			}
		}
		return Aggregates.facet(facets);
	}

	public static Bson getFacetListForChartFields(MultivaluedMap<String, String> queryParameter,
			CCATemplateDao templateDao, ObjectMapper objectMapper, String userId) throws JsonProcessingException {
		// Take a master template as reference and create the filter
		String filterTemplate;
		if (queryParameter.containsKey(CCAConstants.FILTER_TEMPLATE)) {
			filterTemplate = queryParameter.getFirst(CCAConstants.FILTER_TEMPLATE);
		} else
			filterTemplate = CCAConstants.MASTER;

		CCATemplate ccaTemplate = templateDao.findByProperty(CCAConstants.SHORT_NAME, filterTemplate, false);

		List<Facet> facets = new ArrayList<>();
		Iterator<CCAField> templateIt = ccaTemplate.iterator();

		// hardcoded to get the needed aggregate
		List<String> filteredIds = Arrays.asList("f4d0c3af-02ba-4891-b1cb-bd1a81bd0ff4",
				"d80a1180-98e8-4722-90ab-6115d453d909", "1368d834-f297-428c-9aa6-52896834f3d4",
				"a188d345-a4a3-4d03-bfe8-0596374ae2a5", "41440a76-d80f-4531-b43b-f48b9c72ab2b");

		while (templateIt.hasNext()) {
			CCAField field = templateIt.next();
			if (filteredIds.contains(field.getFieldId())) {
				Facet facet = field.getGroupAggregation(queryParameter, templateDao, objectMapper, userId);
				if (facet != null) {
					facets.add(facet);
				}
			}
		}
		return Aggregates.facet(facets);
	}

}
