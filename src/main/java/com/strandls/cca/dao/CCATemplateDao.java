package com.strandls.cca.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;

import com.google.inject.Inject;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.strandls.cca.CCAConstants;
import com.strandls.cca.pojo.CCATemplate;
import com.strandls.cca.pojo.Platform;
import com.strandls.cca.util.Permissions;

public class CCATemplateDao extends AbstractDao<CCATemplate> {

	@Inject
	public CCATemplateDao(MongoDatabase db) {
		super(CCATemplate.class, db);
	}

	public CCATemplate restore(String shortName) {
		CCATemplate template = dbCollection.find(Filters.eq(CCAConstants.SHORT_NAME, shortName)).first();
		template.setIsDeleted(false);
		return replaceOne(template);
	}

	public CCATemplate remove(String shortName) {
		CCATemplate template = dbCollection.find(Filters.eq(CCAConstants.SHORT_NAME, shortName)).first();
		template.setIsDeleted(true);
		return replaceOne(template);
	}

	public CCATemplate deepRemoveByShortName(String shortName) {
		CCATemplate template = dbCollection.find(Filters.eq(CCAConstants.SHORT_NAME, shortName)).first();
		return remove(template);
	}

	public List<CCATemplate> getAllCCATemplateWithoutFields(List<Permissions> permissions, Platform platform,
			Boolean excludeFields) {
		// Get all the document with is deleted as false
		List<Bson> filters = new ArrayList<>();

		Bson isDeleteFilter = Filters.eq(CCAConstants.IS_DELETED, false);
		Bson permissionFilter = Filters.in("permissions", permissions);

		filters.add(isDeleteFilter);
		filters.add(permissionFilter);

		// Add filter for the platform
		if (platform != null)
			filters.add(Filters.eq("platform", platform.name()));

		Bson bsonFilter = Filters.and(filters);
		if (excludeFields.booleanValue())
			return dbCollection.find(bsonFilter).projection(Projections.exclude("fields"))
					.into(new ArrayList<CCATemplate>());
		else
			return dbCollection.find(bsonFilter).into(new ArrayList<CCATemplate>());
	}

}
