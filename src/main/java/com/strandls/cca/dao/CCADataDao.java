package com.strandls.cca.dao;

import java.util.ArrayList;
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
import com.strandls.cca.CCAConstants;
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.filter.IFilter;
import com.strandls.cca.pojo.filter.OperatorType;
import com.strandls.cca.util.BsonFilterUtil;
import com.strandls.cca.util.BsonProjectionUtil;

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
			projections = BsonProjectionUtil.getProjectionsForListPage(templateDao, shortName);

		return dbCollection.find(filters).projection(projections).into(new ArrayList<CCAData>());
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
	public List<CCAData> getAll(UriInfo uriInfo, boolean projectAll, String userId) throws JsonProcessingException {

		MultivaluedMap<String, String> queryParameter = uriInfo.getQueryParameters();

		// Add all the filter here
		JSONArray filterArray = BsonFilterUtil.getFilterFromFields(queryParameter, templateDao);
		filterArray.addAll(BsonFilterUtil.getShortNameFilter(queryParameter));
		filterArray.addAll(BsonFilterUtil.getUserIdFilter(userId));
		filterArray.addAll(BsonFilterUtil.getDataIdsFilter(queryParameter));

		// Create the And filter for all the filter.
		JSONObject filterObject = new JSONObject();
		filterObject.appendField(CCAConstants.TYPE, OperatorType.AND);
		filterObject.appendField("filters", filterArray);

		// Generate the IFilter from the jackson.
		IFilter filter = objectMapper.readValue(filterObject.toJSONString(), IFilter.class);
		
		String viewTemplate;
		if (queryParameter.containsKey(CCAConstants.VIEW_TEMPLATE)) {
			viewTemplate = queryParameter.getFirst(CCAConstants.VIEW_TEMPLATE);
		} else
			viewTemplate = CCAConstants.MASTER;

		return getAll(filter, viewTemplate, projectAll);
	}

	public CCAData restore(Long id) {
		CCAData data = dbCollection.find(getIdFilter(id)).first();
		data.setIsDeleted(true);
		return replaceOne(data);
	}

	public CCAData remove(Long id) {
		CCAData data = dbCollection.find(getIdFilter(id)).first();
		data.setIsDeleted(false);
		return replaceOne(data);
	}

	public CCAData deepRemove(Long id) {
		CCAData data = dbCollection.find(getIdFilter(id)).first();
		return remove(data);
	}
}
