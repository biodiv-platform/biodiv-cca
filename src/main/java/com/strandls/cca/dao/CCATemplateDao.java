package com.strandls.cca.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.google.inject.Inject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
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

	public List<CCATemplate> getAllCCATemplateWithoutFields(List<String> permissions, Platform platform,
			Boolean excludeFields) {
		// Get all the document with is deleted as false
		List<Bson> filters = new ArrayList<>();

		Bson isDeleteFilter = Filters.eq(CCAConstants.IS_DELETED, false);
		filters.add(isDeleteFilter);

		if (!permissions.contains(Permissions.ROLE_ADMIN.name())) {
			Bson permissionFilter = Filters.in("permissions", permissions);
			filters.add(permissionFilter);
		}

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

	public List<String> getAllCCAFieldIds() {
	    List<String> fieldIds = new ArrayList<>();
	    try {
	        // Connect to the database

	        // Construct the aggregation pipeline
	        List<Bson> pipeline = Arrays.asList(
	            // Match the desired documents
	            Aggregates.match(Filters.exists("fields.fieldId")),
	            // Unwind the fields array to get individual field objects
	            Aggregates.unwind("$fields"),
	            // Group by the fieldId and add the fieldId to the group
	            Aggregates.group("$fields.fieldId"),
	            // Project the fieldId as output
	            Aggregates.project(Projections.fields(Projections.excludeId(), Projections.computed("_id", "$_id.fieldId")))
	        );

	        // Execute the aggregation pipeline
	        AggregateIterable<Document> result = dbCollection.aggregate(pipeline, Document.class);

	        // Iterate over the aggregation result and add the fieldIds to the list
	        for (Document document : result) {
	            String fieldId = document.getString("_id");
	            fieldIds.add(fieldId);
	            System.out.println(fieldId);
	        }

	        // Get the children's field IDs recursively
	        List<String> childFieldIds = getChildFieldIds(fieldIds);
	        fieldIds.addAll(childFieldIds);
	    } catch (Exception e) {
	        e.printStackTrace();
	        // Handle the exception
	    }
	    return fieldIds;
	}

	public List<String> getChildFieldIds(List<String> parentFieldIds) {
	    List<String> childFieldIds = new ArrayList<>();
	    try {
	        // Construct the aggregation pipeline to get children's field IDs
	        List<Bson> pipeline = Arrays.asList(
	            // Match the desired documents
	            Aggregates.match(Filters.in("parentId", parentFieldIds)),
	            // Group by the fieldId and add the fieldId to the group
	            Aggregates.group("$fieldId")
	        );

	        // Execute the aggregation pipeline
	        AggregateIterable<Document> result = dbCollection.aggregate(pipeline, Document.class);

	        // Iterate over the aggregation result and add the fieldIds to the list
	        for (Document document : result) {
	            String fieldId = document.getString("_id");
	            childFieldIds.add(fieldId);
	            System.out.println(fieldId);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        // Handle the exception
	    }
	    return childFieldIds;
	}

}
