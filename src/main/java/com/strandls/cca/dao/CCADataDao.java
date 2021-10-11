package com.strandls.cca.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;

import org.bson.conversions.Bson;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.util.CCAUtil;

public class CCADataDao extends AbstractDao<CCAData> {

	@Inject
	public CCADataDao(MongoDatabase db) {
		super(CCAData.class, db);
	}

	public List<CCAData> getAll(MultivaluedMap<String, String> queryParameters, String shortName) {
		// Get all the document with id
		Bson filters = Filters.exists("_id");

		for(Entry<String, List<String>> e : queryParameters.entrySet()) {
			String key = e.getKey();
			List<String> value = e.getValue();
			
			if("shortName".equals(key)) {
				filters = Filters.and(filters, Filters.eq(key, value.get(0)));
			} else {
				String valueString = String.join(CCAUtil.COLUMN_SEPARATOR, value);
				filters = Filters.and(filters, Filters.eq(key, valueString));
			}
			
		}
		
		return dbCollection.find(filters).projection(Projections.exclude("fields")).into(new ArrayList<CCAData>());
	}

}
