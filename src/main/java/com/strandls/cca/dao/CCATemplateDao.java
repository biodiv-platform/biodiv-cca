package com.strandls.cca.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;

import com.google.inject.Inject;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.DeleteResult;
import com.strandls.cca.CCAConstants;
import com.strandls.cca.pojo.CCATemplate;
import com.strandls.cca.pojo.Platform;

public class CCATemplateDao extends AbstractDao<CCATemplate> {

	@Inject
	public CCATemplateDao(MongoDatabase db) {
		super(CCATemplate.class, db);
	}

	public CCATemplate revoke(String shortName) {
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
		DeleteResult dResult = dbCollection.deleteOne(Filters.eq(CCAConstants.SHORT_NAME, shortName));
		if (dResult.getDeletedCount() == 0) {
			throw new IllegalArgumentException("Can't delete object, it is not existing the system");
		}
		return template;
	}

	public List<CCATemplate> getAllCCATemplateWithoutFields(Platform platform, Boolean excludeFields) {
		// Get all the document with is deleted as false
		Bson filters = Filters.eq(CCAConstants.IS_DELETED, false);

		// Add filter for the platform
		if (platform != null)
			filters = Filters.and(filters, Filters.eq("platform", platform.name()));

		if (excludeFields.booleanValue())
			return dbCollection.find(filters).projection(Projections.exclude("fields"))
					.into(new ArrayList<CCATemplate>());
		else
			return dbCollection.find(filters).into(new ArrayList<CCATemplate>());
	}

}
