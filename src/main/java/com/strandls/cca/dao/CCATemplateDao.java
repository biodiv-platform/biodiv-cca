package com.strandls.cca.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;

import com.google.inject.Inject;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.DeleteResult;
import com.strandls.cca.pojo.CCATemplate;
import com.strandls.cca.pojo.Platform;
import com.strandls.cca.service.impl.CCATemplateServiceImpl;

public class CCATemplateDao extends AbstractDao<CCATemplate> {

	@Inject
	public CCATemplateDao(MongoDatabase db) {
		super(CCATemplate.class, db);
	}

	public CCATemplate removeByShortName(String shortName) {
		CCATemplate template = findByProperty(CCATemplateServiceImpl.SHORT_NAME, shortName);
		DeleteResult dResult = dbCollection.deleteOne(Filters.eq(CCATemplateServiceImpl.SHORT_NAME, template.getId()));
		if (dResult.getDeletedCount() == 0) {
			throw new IllegalArgumentException("Can't delete object, it is not existing the system");
		}
		return template;
	}

	public List<CCATemplate> getAllCCATemplateWithoutFields(Platform platform) {
		// Get all the document with id
		Bson filters = Filters.exists("_id");

		// Add filter for the platform
		if (platform != null)
			filters = Filters.and(filters, Filters.eq("platform", platform.name()));

		return dbCollection.find(filters).projection(Projections.exclude("fields")).into(new ArrayList<CCATemplate>());
	}
}
