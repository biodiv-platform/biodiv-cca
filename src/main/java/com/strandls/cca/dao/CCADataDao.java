package com.strandls.cca.dao;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bson.conversions.Bson;

import com.mongodb.client.MongoDatabase;
import com.strandls.cca.pojo.CCAData;
import com.strandls.cca.pojo.filter.IFilter;

public class CCADataDao extends AbstractDao<CCAData> {

	@Inject
	public CCADataDao(MongoDatabase db) {
		super(CCAData.class, db);
	}

	public List<CCAData> getAll(IFilter ccaFilters) {

		Bson filters = ccaFilters.getFilter();

		return dbCollection.find(filters).into(new ArrayList<CCAData>());
	}

}
