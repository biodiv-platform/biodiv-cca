package com.strandls.cca.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.bson.conversions.Bson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.strandls.cca.CCAConstants;
import com.strandls.cca.FieldConstants;
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.CCAField;
import com.strandls.cca.pojo.CCATemplate;
import com.strandls.cca.pojo.FieldType;
import com.strandls.cca.pojo.filter.CompareOperator;
import com.strandls.cca.pojo.filter.IFilter;
import com.strandls.cca.pojo.filter.OperatorType;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class CCADataDao extends AbstractDao<CCAData> {

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private CCATemplateDao templateDao;

	@Inject
	public CCADataDao(MongoDatabase db) {
		super(CCAData.class, db);
	}

	private List<CCAData> getAll(IFilter ccaFilters, String shortName, boolean projectAll) {

		// Add isDeleted filter here
		Bson isDeleted = Filters.or(Filters.exists(CCAConstants.IS_DELETED, false),
				Filters.eq(CCAConstants.IS_DELETED, false));

		Bson filters = ccaFilters.getFilter();
		filters = Filters.and(filters, isDeleted);

		Bson projections = null;
		if (!projectAll)
			projections = getProjectionsForListPage(shortName);

		return dbCollection.find(filters).projection(projections).into(new ArrayList<CCAData>());
	}

	/**
	 * This method gives projection for the list page of master
	 * 
	 * @return
	 */
	private Bson getProjectionsForListPage(String shortName) {
		List<String> fieldNames = new ArrayList<>();

		// Compulsory field from the CCA Data - Need to change if there is modification
		// in the Model
		fieldNames.add("shortName");
		fieldNames.add("userId");
		fieldNames.add("createdOn");
		fieldNames.add("updatedOn");

		// Take a master template as reference and get all the isSummary column to be
		// projected.
		if (shortName == null)
			shortName = CCAConstants.MASTER;
		CCATemplate ccaTemplate = templateDao.findByProperty(CCAConstants.SHORT_NAME, shortName);
		Iterator<CCAField> it = ccaTemplate.iterator();
		while (it.hasNext()) {
			CCAField ccaField = it.next();
			if (ccaField.getIsSummaryField().booleanValue()) {
				String fieldName = "ccaFieldValues" + "." + ccaField.getFieldId();
				fieldNames.add(fieldName);
			}
		}

		return Projections.include(fieldNames);
	}

	/**
	 * Create the filter based on query parameter given
	 * 
	 * filterTemplate - This is used as reference to create all the filter. (Default
	 * to master) viewTemplate - This is used as reference to view the list data.
	 * (Default to master) shortName - User can pass the short name as well. We'll
	 * get only data from the given short Name
	 * 
	 * @param uriInfo
	 * @return
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	public List<CCAData> getAll(UriInfo uriInfo, boolean projectAll) throws JsonProcessingException {

		MultivaluedMap<String, String> queryParameter = uriInfo.getQueryParameters();

		String viewTemplate;
		if (queryParameter.containsKey(CCAConstants.VIEW_TEMPLATE)) {
			viewTemplate = queryParameter.getFirst(CCAConstants.VIEW_TEMPLATE);
		} else
			viewTemplate = CCAConstants.MASTER;

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

		if (queryParameter.containsKey(CCAConstants.SHORT_NAME)) {
			JSONObject filter = getShortNameFilter(queryParameter);
			filterArray.add(filter);
		}

		// Create the And filter for all the filter.
		JSONObject filterObject = new JSONObject();
		filterObject.appendField(CCAConstants.TYPE, OperatorType.AND);
		filterObject.appendField("filters", filterArray);

		IFilter filter = objectMapper.readValue(filterObject.toJSONString(), IFilter.class);
		return getAll(filter, viewTemplate, projectAll);
	}

	private JSONObject getShortNameFilter(MultivaluedMap<String, String> queryParameter) {
		JSONObject filter = new JSONObject();
		filter.appendField(CCAConstants.TYPE, FieldConstants.GENERIC);
		filter.appendField(CCAConstants.FIELD_NAME, CCAConstants.SHORT_NAME);
		filter.appendField(CCAConstants.VALUE, queryParameter.get(CCAConstants.SHORT_NAME).get(0));
		return filter;
	}

	/**
	 * Creating filter based on its type
	 * 
	 * @param values
	 * @param field
	 * @return
	 */
	private List<JSONObject> getFilters(List<String> values, CCAField field) {

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

			break;
		case HEADING:
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
	private List<JSONObject> getNumberFilters(String fieldId, FieldType fieldType, String v) {

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
	private JSONObject getFilter(String fieldId, FieldType type, CompareOperator op, Object value) {
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
