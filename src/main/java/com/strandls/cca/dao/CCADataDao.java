package com.strandls.cca.dao;

import java.util.ArrayList;
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
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
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

	@SuppressWarnings("rawtypes")
	public AggregateIterable<Map> getAggregation(UriInfo uriInfo, String userId, Bson searchQuery)
			throws JsonProcessingException {

		MultivaluedMap<String, String> queryParameter = uriInfo.getQueryParameters();
		Boolean allFields = false;
		if (queryParameter.containsKey("allFields")) {
			allFields = Boolean.parseBoolean(queryParameter.get("allFields").get(0));
		}

		// Create the facet pipeline
		List<Bson> facetPipeline = new ArrayList<>();
		facetPipeline.add(Aggregates.match(searchQuery));
		facetPipeline.add(allFields
				? CCAFilterUtil.getFacetListForAllFields(queryParameter, templateDao, objectMapper, userId)
				: CCAFilterUtil.getFacetListForFilterableFields(queryParameter, templateDao, objectMapper, userId));
		return dbCollection.aggregate(facetPipeline, Map.class);

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
	public List<CCAData> getAll(UriInfo uriInfo, boolean projectAll, String userId, Boolean isDeletedData)
			throws JsonProcessingException {

		MultivaluedMap<String, String> queryParameter = uriInfo.getQueryParameters();

		Bson filters = CCAFilterUtil.getAllFilters(queryParameter, templateDao, objectMapper, userId, isDeletedData);

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

	public List<CCAData> getAll(UriInfo uriInfo, boolean projectAll, String userId, Boolean isDeletedData, int limit,
			int offset) throws JsonProcessingException {

		MultivaluedMap<String, String> queryParameter = uriInfo.getQueryParameters();

		Bson filters = CCAFilterUtil.getAllFilters(queryParameter, templateDao, objectMapper, userId, isDeletedData);

		Bson projections = null;
		if (!projectAll) {
			String viewTemplate;
			if (queryParameter.containsKey(CCAConstants.VIEW_TEMPLATE)) {
				viewTemplate = queryParameter.getFirst(CCAConstants.VIEW_TEMPLATE);
			} else
				viewTemplate = CCAConstants.MASTER;
			projections = BsonProjectionUtil.getProjectionsForListPage(templateDao, viewTemplate);
		}

		return dbCollection.find(filters).projection(projections).skip(offset).limit(limit)
				.into(new ArrayList<CCAData>());
	}

	public CCAData restore(Long id) {
		CCAData data = dbCollection.find(getIdFilter(id)).first();
		data.setIsDeleted(false);
		return replaceOne(data);
	}

	public CCAData remove(Long id) {
		CCAData data = dbCollection.find(getIdFilter(id)).first();
		data.setIsDeleted(true);
		return replaceOne(data);
	}

	public void removeOrRestoreManyCCDataByShortName(String shortName, Boolean isDeleted) {
		Bson update = Updates.set(CCAConstants.IS_DELETED, isDeleted);
		Bson filter = Filters.eq(CCAConstants.SHORT_NAME, shortName);
		dbCollection.updateMany(filter, update);
	}

	public CCAData deepRemove(Long id) {
		CCAData data = dbCollection.find(getIdFilter(id)).first();
		return remove(data);
	}

	public long totalDataCount(UriInfo uriInfo) throws JsonProcessingException {
		MultivaluedMap<String, String> queryParameter = uriInfo.getQueryParameters();
		Bson filters = CCAFilterUtil.getAllFilters(queryParameter, templateDao, objectMapper, null, false);
		return dbCollection.countDocuments(filters);
	}

	public Bson getIntersectionQuery(UriInfo uriInfo, Bson searchQuery) throws JsonProcessingException {
		Boolean isDeletedData = false;
		MultivaluedMap<String, String> queryParameter = uriInfo.getQueryParameters();
		Bson filters = CCAFilterUtil.getAllFilters(queryParameter, templateDao, objectMapper, null, isDeletedData);
		return Filters.and(filters, searchQuery);
	}

	public List<CCAData> getSearchCCAData(UriInfo uriInfo, Bson searchQuery, int limit, int offset)
			throws JsonProcessingException {
		Bson intersectionQuery = getIntersectionQuery(uriInfo, searchQuery);
		Bson projections = BsonProjectionUtil.getProjectionsForListPage(templateDao, CCAConstants.MASTER);
		return dbCollection.find(intersectionQuery).projection(projections).skip(offset).limit(limit)
				.into(new ArrayList<>());
	}

	public List<CCAData> getSearchMapCCAData(UriInfo uriInfo, Bson searchQuery) throws JsonProcessingException {
		Bson intersectionQuery = getIntersectionQuery(uriInfo, searchQuery);
		Bson projections = BsonProjectionUtil.getProjectionsForListPage(templateDao, CCAConstants.MASTER);
		return dbCollection.find(intersectionQuery).projection(projections).into(new ArrayList<>());
	}

}
