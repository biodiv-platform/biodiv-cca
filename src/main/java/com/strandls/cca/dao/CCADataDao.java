package com.strandls.cca.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.bson.conversions.Bson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.strandls.cca.CCAConstants;
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.util.BsonProjectionUtil;
import com.strandls.cca.util.CCAFilterUtil;

public class CCADataDao extends AbstractDao<CCAData> {

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private CCATemplateDao templateDao;

	@Inject
	public CCADataDao(MongoDatabase db) {
		super(CCAData.class, db);
	}

	public AggregateIterable<Map> getAggregation(UriInfo uriInfo, String userId) throws JsonProcessingException {
		MultivaluedMap<String, String> queryParameter = uriInfo.getQueryParameters();

		Bson filters = CCAFilterUtil.getAllFilters(queryParameter, templateDao, objectMapper, userId);
		Bson facet = CCAFilterUtil.getFacetListForFilterableFields(queryParameter, templateDao);

		// Bson facet = Aggregates.facet(new Facet("a1", Aggregates.group("$userId",
		// Accumulators.sum("count", 1))));

		Bson match = Aggregates.match(filters);
		return dbCollection.aggregate(Arrays.asList(match, facet), Map.class);
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

		Bson filters = CCAFilterUtil.getAllFilters(queryParameter, templateDao, objectMapper, userId);

		Bson projections = null;
		if (!projectAll) {
			String viewTemplate;
			if (queryParameter.containsKey(CCAConstants.VIEW_TEMPLATE)) {
				viewTemplate = queryParameter.getFirst(CCAConstants.VIEW_TEMPLATE);
			} else
				viewTemplate = CCAConstants.MASTER;
			projections = BsonProjectionUtil.getProjectionsForListPage(templateDao, viewTemplate);
		}

		return dbCollection.find(filters).projection(projections).into(new ArrayList<CCAData>());
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
